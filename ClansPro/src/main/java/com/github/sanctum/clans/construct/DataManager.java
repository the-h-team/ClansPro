package com.github.sanctum.clans.construct;


import com.github.sanctum.clans.ClansJavaPlugin;
import com.github.sanctum.clans.construct.api.Clan;
import com.github.sanctum.clans.construct.api.ClanCooldown;
import com.github.sanctum.clans.construct.api.ClansAPI;
import com.github.sanctum.clans.construct.extra.FancyLogoAppendage;
import com.github.sanctum.clans.construct.impl.Resident;
import com.github.sanctum.labyrinth.data.DataTable;
import com.github.sanctum.labyrinth.data.FileList;
import com.github.sanctum.labyrinth.data.FileManager;
import com.github.sanctum.labyrinth.data.LabyrinthDataTable;
import com.github.sanctum.labyrinth.formatting.FancyMessage;
import com.github.sanctum.labyrinth.library.Items;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.labyrinth.task.TaskScheduler;
import com.github.sanctum.panther.file.Configurable;
import com.github.sanctum.skulls.CustomHead;
import com.github.sanctum.skulls.CustomHeadLoader;
import java.io.File;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;

public class DataManager {

	public final Map<Player, String> ID_MODE = new HashMap<>();
	private final Set<Player> CHAT_SPY = new HashSet<>();
	private final Set<Resident> RESIDENTS = new HashSet<>();
	private final Set<Player> INHABITANTS = new HashSet<>();
	private final List<ClanCooldown> COOLDOWNS = new LinkedList<>();
	private final DataTable resetTable = new LabyrinthDataTable();

	public static class Side {
		public static final int LEFT = 1;
		public static final int RIGHT = 2;
	}

	void create(FileManager manager) {
		if (!manager.getRoot().exists()) {
			InputStream is = ClansAPI.getInstance().getPlugin().getResource(manager.getRoot().getName() + manager.getRoot().getType().get());
			if (is == null) throw new IllegalStateException("Unable to load Config.yml from the jar!");
			FileList.copy(is, manager.getRoot().getParent());
			manager.getRoot().reload();
		}
	}

	public DataTable getResetTable() {
		return this.resetTable;
	}

	public boolean isSpy(Player player) {
		return CHAT_SPY.contains(player);
	}

	public boolean addSpy(Player spy) {
		return CHAT_SPY.add(spy);
	}

	public boolean removeSpy(Player spy) {
		return CHAT_SPY.remove(spy);
	}

	public Set<Player> getSpies() {
		return Collections.unmodifiableSet(CHAT_SPY);
	}

	public List<ClanCooldown> getCooldowns() {
		return Collections.unmodifiableList(COOLDOWNS);
	}

	public Set<Resident> getResidents() {
		return Collections.unmodifiableSet(RESIDENTS);
	}

	public Resident getResident(Player p) {
		return RESIDENTS.stream().filter(r -> r.getPlayer().getName().equals(p.getName())).findFirst().orElse(null);
	}

	public boolean isInWild(Player player) {
		return INHABITANTS.contains(player);
	}

	public boolean removeWildernessInhabitant(Player player) {
		return INHABITANTS.remove(player);
	}

	public boolean addWildernessInhabitant(Player player) {
		return INHABITANTS.add(player);
	}

	public boolean addClaimResident(Resident resident) {
		return RESIDENTS.add(resident);
	}

	public boolean removeClaimResident(Resident resident) {
		return RESIDENTS.remove(resident);
	}

	public @NotNull FileManager getConfig() {
		FileManager main = ClansAPI.getInstance().getFileList().get("Config", "Configuration");
		create(main);
		return main;
	}

	public @NotNull FileManager getMessages() {
		FileManager main = ClansAPI.getInstance().getFileList().get("Messages", "Configuration");
		create(main);
		return main;
	}

	public @NotNull FileManager getClanFile(Clan c) {
		return ClansAPI.getInstance().getFileList().get(c.getId().toString(), "Clans", JavaPlugin.getPlugin(ClansJavaPlugin.class).TYPE);
	}

	public String getConfigString(String path) {
		return getConfig().getRoot().getString(path);
	}

	public boolean isTrue(String path) {
		return getConfig().getRoot().getBoolean(path);
	}

	public String getMenuTitle(String menu) {
		FileManager main = ClansAPI.getInstance().getFileList().get("Messages", "Configuration");
		create(main);
		return main.getRoot().getString("menu-titles." + menu);
	}

	public String getMenuCategory(String menu) {
		FileManager main = ClansAPI.getInstance().getFileList().get("Messages", "Configuration");
		create(main);
		return main.getRoot().getString("menu-categories." + menu);
	}

	public String getMenuNavigation(String menu) {
		FileManager main = ClansAPI.getInstance().getFileList().get("Messages", "Configuration");
		create(main);
		return main.getRoot().getString("gui-navigation." + menu);
	}

	private ItemStack improvise(String value) {
		Material mat = Items.findMaterial(value);
		if (mat != null) {
			return new ItemStack(mat);
		} else {
			if (value.length() < 26) {
				return CustomHead.Manager.getHeads().stream().filter(h -> StringUtils.use(h.name()).containsIgnoreCase(value)).map(CustomHead::get).findFirst().orElse(null);
			} else {
				return CustomHeadLoader.provide(value);
			}
		}
	}

	public ItemStack getMenuItem(String object) {
		FileManager main = ClansAPI.getInstance().getFileList().get("Messages", "Configuration");
		create(main);
		return improvise(Objects.requireNonNull(main.getRoot().getString("menu-items." + object)));
	}

	public Material getMenuMaterial(String object) {
		FileManager main = ClansAPI.getInstance().getFileList().get("Messages", "Configuration");
		create(main);
		return Items.findMaterial(Objects.requireNonNull(main.getRoot().getString("menu-items." + object)));
	}

	public String getMessageString(String path) {
		FileManager main = ClansAPI.getInstance().getFileList().get("Messages", "Configuration");
		create(main);
		return main.getRoot().getString(path);
	}

	public int getConfigInt(String path) {
		return getConfig().getRoot().getInt(path);
	}

	public String getMessageResponse(String path) {
		FileManager main = ClansAPI.getInstance().getFileList().get("Messages", "Configuration");
		create(main);
		return main.getRoot().getString("Response." + path);
	}

	public FancyLogoAppendage appendStringsToLogo(List<String> logo, Consumer<FancyMessage> consumer) {
		return new FancyLogoAppendage().append(logo, consumer);
	}

	public String[] appendStringsToLogo(List<String> logo, @MagicConstant(valuesFromClass = Side.class) int side, String... text) {
		String[] ar = logo.toArray(new String[0]);
		for (int i = 0; i < ar.length; i++) {
			if (i > 0) {
				if ((Math.max(0, i - 1)) <= text.length - 1) {
					String m = text[Math.max(0, i - 1)];
					switch (side) {
						case 1:
							ar[i] = "&r" + m + "   &r" + ar[i];
							break;
						case 2:
							ar[i] = ar[i] + "   &r" + m;
							break;
					}
				}
			}
		}
		return ar;
	}

	public static boolean isTitlesAllowed() {
		FileManager main = ClansAPI.getInstance().getFileList().get("Config", "Configuration");
		return main.getRoot().getBoolean("Clans.land-claiming.send-titles");
	}

	public String formatDisplayTag(String color, String name) {
		return MessageFormat.format(Objects.requireNonNull(getConfig().getRoot().getString("Formatting.nametag-prefix.text")), color, name);
	}

	public boolean isDisplayTagsAllowed() {
		FileManager main = getConfig();
		return main.getRoot().getBoolean("Formatting.nametag-prefix.use");
	}

	public boolean isUpdate() {
		ClansAPI api = ClansAPI.getInstance();
		FileList list = api.getFileList();
		FileManager main = list.get("Config", "Configuration");
		FileManager messages = list.get("Messages", "Configuration");
		if (!ClansAPI.getInstance().getPlugin().getDescription().getVersion().equals(getConfig().getRoot().getString("Version"))) {
			FileManager mainOld = list.get("config_old", "Configuration", Configurable.Type.JSON);
			FileManager messOld = list.get("messages_old", "Configuration", Configurable.Type.JSON);
			if (mainOld.getRoot().exists()) {
				mainOld.getRoot().delete();
			}
			if (messOld.getRoot().exists()) {
				messOld.getRoot().delete();
			}
			final String type = main.read(c -> c.getNode("Formatting").getNode("file-type").toPrimitive().getString());
			main.toJSON("config_old", "Configuration");
			messages.toJSON("messages_old", "Configuration");

			TaskScheduler.of(() -> {
				InputStream mainGrab;
				if (type != null) {
					if (type.equals("JSON")) {
						mainGrab = api.getPlugin().getResource("Config_json.yml");
					} else {
						mainGrab = api.getPlugin().getResource("Config.yml");
					}
				} else {
					mainGrab = api.getPlugin().getResource("Config.yml");
				}
				InputStream msgGrab;
				msgGrab = api.getPlugin().getResource("Messages.yml");
				if (mainGrab == null) throw new IllegalStateException("Unable to load Config.yml from the jar!");
				if (msgGrab == null) throw new IllegalStateException("Unable to load Messages.yml from the jar!");
				FileList.copy(mainGrab, main.getRoot().getParent());
				FileList.copy(msgGrab, messages.getRoot().getParent());
				messages.getRoot().reload();
				main.getRoot().reload();
			}).scheduleLater(1);
			return true;
		}
		return false;
	}

	public void copyDefaults() {
		FileManager main = ClansAPI.getInstance().getFileList().get("Config", "Configuration");
		FileManager msg = ClansAPI.getInstance().getFileList().get("Messages", "Configuration");
		if (!main.getRoot().exists()) {
			InputStream mainGrab = ClansAPI.getInstance().getPlugin().getResource("Config.yml");
			if (mainGrab == null) throw new IllegalStateException("Unable to load Config.yml from the jar!");
			FileList.copy(mainGrab, main.getRoot().getParent());
		}
		if (!msg.getRoot().exists()) {
			InputStream mainGrab = ClansAPI.getInstance().getPlugin().getResource("Messages.yml");
			if (mainGrab == null) throw new IllegalStateException("Unable to load Messages.yml from the jar!");
			FileList.copy(mainGrab, msg.getRoot().getParent());
		}
	}

	public File getClanFolder() {
		final File dir = new File(FileManager.class.getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " "));
		File d = new File(dir.getParentFile().getPath(), ClansAPI.getInstance().getPlugin().getDescription().getName() + "/" + "Clans" + "/");
		if (!d.exists()) {
			d.mkdirs();
		}
		return d;
	}

	public boolean addCooldown(ClanCooldown instance) {
		if (COOLDOWNS.stream().anyMatch(c -> c.getId().equals(instance.getId()))) return false;
		return COOLDOWNS.add(instance);
	}

	public boolean removeCooldown(ClanCooldown instance) {
		return COOLDOWNS.remove(instance);
	}

	public static class Security {
		public static String getPermission(String command) {
			return ClansAPI.getDataInstance().getMessageString("Commands." + command + ".permission");
		}
	}
}
