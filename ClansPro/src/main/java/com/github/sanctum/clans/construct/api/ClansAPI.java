package com.github.sanctum.clans.construct.api;

import com.github.sanctum.clans.ClansJavaPlugin;
import com.github.sanctum.clans.bridge.ClanAddon;
import com.github.sanctum.clans.construct.ArenaManager;
import com.github.sanctum.clans.construct.ClaimManager;
import com.github.sanctum.clans.construct.ClanManager;
import com.github.sanctum.clans.construct.CommandManager;
import com.github.sanctum.clans.construct.DataManager;
import com.github.sanctum.clans.construct.ShieldManager;
import com.github.sanctum.clans.construct.extra.MessagePrefix;
import com.github.sanctum.labyrinth.data.FileList;
import com.github.sanctum.labyrinth.data.LabyrinthUser;
import com.github.sanctum.labyrinth.data.container.KeyedServiceManager;
import com.github.sanctum.labyrinth.gui.unity.construct.Menu;
import com.github.sanctum.labyrinth.library.NamespacedKey;
import com.github.sanctum.labyrinth.task.TaskScheduler;
import com.github.sanctum.panther.annotation.Experimental;
import com.github.sanctum.panther.annotation.Note;
import com.github.sanctum.panther.paste.PasteManager;
import com.github.sanctum.panther.paste.operative.PasteResponse;
import com.github.sanctum.panther.paste.type.Hastebin;
import com.github.sanctum.panther.util.Check;
import com.github.sanctum.panther.util.HUID;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <pre>
 *     ▄▄▄·▄▄▄        ▄▄
 *    ▐█ ▄█▀▄ █·▪     ██▌
 *     ██▀·▐▀▀▄  ▄█▀▄ ▐█·
 *    ▐█▪·•▐█•█▌▐█▌.▐▌.▀
 *   .▀   .▀  ▀ ▀█▄▀▪ ▀
 * </pre>
 * <pre>
 * <h3>MIT License</h2>
 * Copyright (c) 2021 Sanctum
 *
 * <pre>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * <strong>You will be required to publicly display credit to the original authors in any postings regarding both "remastering" or
 * forking of this project. While not enforced what so ever, if you decide on forking + re-selling under
 * modified circumstances that you pay us a royalty fee of $4.50 USD per sale to respect our side of the work involved.</strong>
 * <pre>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <pre>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public interface ClansAPI {

	static ClansAPI getInstance() {
		return Bukkit.getServicesManager().load(ClansAPI.class);
	}

	static BanksAPI getBankInstance() {
		return BanksAPI.getInstance();
	}

	@Note("This is accessible but you should almost never need to use it directly")
	static DataManager getDataInstance() {
		return JavaPlugin.getPlugin(ClansJavaPlugin.class).dataManager;
	}

	/**
	 * Get this server's unique session id.
	 *
	 * @return the persistently unique id for this session.
	 * @apiNote This id is no longer unique after a single game session.
	 */
	@NotNull UUID getSessionId();

	/**
	 * @return Gets the prefix object for the plugin.
	 */
	@NotNull MessagePrefix getPrefix();

	/**
	 * The plugin instance for the api. Try not to use this!
	 *
	 * @return The primary plugin instance.
	 */
	@NotNull Plugin getPlugin();

	/**
	 * Gets a clan associate by their player object.
	 *
	 * @param player The player to use.
	 * @return A clan associate with properties such as nickname, bio etc.
	 */
	Optional<Clan.Associate> getAssociate(OfflinePlayer player);

	/**
	 * Gets a clan associate by their player idd.
	 *
	 * @param uuid The player to use.
	 * @return A clan associate with properties such as nickname, bio etc.
	 */
	Optional<Clan.Associate> getAssociate(UUID uuid);

	/**
	 * Gets a clan associate by their player name.
	 *
	 * @param playerName The player to use.
	 * @return A clan associate with properties such as nickname, bio etc.
	 */
	Optional<Clan.Associate> getAssociate(String playerName);

	/**
	 * Get the ClansPro file listing.
	 *
	 * @return Get's the file collection for the given plugin.
	 */
	@NotNull FileList getFileList();

	/**
	 * Gets the service manager for event cycles.
	 *
	 * @return The event cycle services manager.
	 */
	@NotNull KeyedServiceManager<ClanAddon> getServiceManager();

	/**
	 * Get the manager for clan war arenas.
	 *
	 * @return The arena manager.
	 */
	@NotNull ArenaManager getArenaManager();

	/**
	 * Get the manager for clans to load/delete from.
	 *
	 * @return The clan manager.
	 */
	@NotNull ClanManager getClanManager();

	/**
	 * Get the manager for clan claims.
	 *
	 * @return The claim manager.
	 */
	@NotNull ClaimManager getClaimManager();

	/**
	 * Get the manger for the raid-shield
	 *
	 * @return The raid shield manager.
	 */
	@NotNull ShieldManager getShieldManager();

	/**
	 * Get the manager for clan sub commands.
	 *
	 * @return The sub command manager.
	 */
	@NotNull CommandManager getCommandManager();

	/**
	 * Get the logo gallery. A public place for local users to upload their 8-bit art work.
	 *
	 * @return The public logo gallery
	 */
	@NotNull LogoGallery getLogoGallery();

	/**
	 * Get the object dedicated to managing information relays to/from web services.
	 *
	 * @return A paste manager.
	 */
	@NotNull PasteManager getPasteManager();

	/**
	 * Get the locally cached hastebin api instance.
	 *
	 * @return the local hastebin api instance.
	 */
	@NotNull Hastebin getLocalHastebinInstance();

	/**
	 * Check if pro needs to be updated.
	 *
	 * @return true if the plugin has an update.
	 */
	boolean isUpdated();

	/**
	 * Check if a clan contains the target UUID
	 *
	 * @param target The target "Member"
	 * @param clanID The target clan to search
	 * @return true if the given clan's members contains the given uuid
	 */
	boolean isClanMember(UUID target, HUID clanID);

	/**
	 * Check if a target player is currently a member of a clan.
	 *
	 * @param target The target uuid to search for.
	 * @return result = true if the target player is in a clan.
	 */
	boolean isInClan(UUID target);

	/**
	 * Check if a specified clan name is black-listed
	 *
	 * @param name The clan name in question
	 * @return result = true if the clan name is not allowed.
	 */
	boolean isNameBlackListed(String name);

	/**
	 * Search and automatically register all found pro addons in a given package location
	 *
	 * @param packageName The package location to browse for addons.
	 */
	void registerAddons(Plugin plugin, String packageName);

	/**
	 * Automatically hook a specific addon via class instantiation.
	 * <p>
	 * Desired class must inherit ClanAddon.
	 *
	 * @param cycle The class that extends EventCycle functionality
	 */
	void registerAddon(Class<? extends ClanAddon> cycle);

	/**
	 * Kick a specified user from a clan they might be in.
	 *
	 * @param uuid The user to kick from their clan.
	 * @return true if the user was kicked otherwise false if the user isn't in a clan.
	 */
	boolean kickUser(UUID uuid);

	/**
	 * Onboard a specified user to a clan of specification.
	 *
	 * @param uuid The user to invite
	 * @return an associate object if the user was able to join otherwise empty.
	 */
	Optional<Clan.Associate> obtainUser(UUID uuid, String clanName);

	/**
	 * Gets an addon by its name.
	 *
	 * @param name The name of the addon.
	 * @return A clan addon.
	 */
	@Nullable ClanAddon getAddon(String name);

	@Nullable Menu getMenu(GUI gui, InvasiveEntity entity);

	default NamespacedKey getLocalPrintKey() {
		return new NamespacedKey(getPlugin(), "reload_data");
	}

	/**
	 * Debug an invasive entity to check all parameters for stability.
	 * The option of creating a link will generate a <strong>hastebin</strong> link instead
	 * of sending all the information in console.
	 *
	 * @param createLink whether to create a link.
	 * @param entity     The entity to debug.
	 */
	default void debugConsole(InvasiveEntity entity, boolean createLink) {
		if (!createLink) {
			if (entity.isClan()) {
				Clan c = entity.getAsClan();
				getPlugin().getLogger().warning("|==============================================|");
				getPlugin().getLogger().warning("|==============================================|");
				getPlugin().getLogger().warning("|==============================================|");
				getPlugin().getLogger().warning(MessageFormat.format("|           Debug run for clan {0}              ", Check.forNull(c.getName(), "The clan's name is null!")));
				getPlugin().getLogger().warning("|==============================================|");
				getPlugin().getLogger().warning("- Members = [ size: " + c.getMembers().size() + ", visual: " + c.getMembers().stream().map(InvasiveEntity::getName).collect(Collectors.joining(", ")) + " ]");
				getPlugin().getLogger().warning(MessageFormat.format("- Id = [{0}]", c.getId()));
				getPlugin().getLogger().warning(MessageFormat.format("- Type = [{0}]", c.isConsole() ? "SERVER" : "PLAYER"));
				getPlugin().getLogger().warning(MessageFormat.format("- Mode = [{0}]", c.isPeaceful() ? "PEACE" : "WAR"));
				getPlugin().getLogger().warning(MessageFormat.format("- Password = [{0}]", c.getPassword() != null ? c.getPassword() : "N/A"));
				getPlugin().getLogger().warning(MessageFormat.format("- Power = [{0}]", c.getPower()));
				getPlugin().getLogger().warning(MessageFormat.format("- Claims = [{0}/{1}]", c.getClaims().length, c.getClaimLimit()));
				getPlugin().getLogger().warning("|==============================================|");
				getPlugin().getLogger().warning("|==============================================|");
				getPlugin().getLogger().warning("|==============================================|");
				getPlugin().getLogger().warning("|==============================================|");
			}
			if (entity.isAssociate()) {
				Clan.Associate a = entity.getAsAssociate();
				getPlugin().getLogger().warning("|==============================================|");
				getPlugin().getLogger().warning("|==============================================|");
				getPlugin().getLogger().warning("|==============================================|");
				getPlugin().getLogger().warning(MessageFormat.format("|        Debug run for associate {0}              ", a.getName()));
				getPlugin().getLogger().warning("|==============================================|");
				getPlugin().getLogger().warning(MessageFormat.format("- Id = [{0}]", a.getId().toString()));
				getPlugin().getLogger().warning(MessageFormat.format("- Clan = [{0}]", a.getClan().getName()));
				getPlugin().getLogger().warning(MessageFormat.format("- Bio = [{0}]", a.getBiography()));
				getPlugin().getLogger().warning(MessageFormat.format("- Chat = [{0}]", a.getChannel()));
				getPlugin().getLogger().warning(MessageFormat.format("- Joined = [{0}]", a.getJoinDate().toLocaleString()));
				getPlugin().getLogger().warning(MessageFormat.format("- Nickname = [{0}]", a.getNickname()));
				getPlugin().getLogger().warning(MessageFormat.format("- Rank = [{0}]", a.getPriority()));
				getPlugin().getLogger().warning(MessageFormat.format("- KD = [{0}]", a.getKD()));
				getPlugin().getLogger().warning("|==============================================|");
				getPlugin().getLogger().warning("|==============================================|");
				getPlugin().getLogger().warning("|==============================================|");
				getPlugin().getLogger().warning("|==============================================|");
			}
		} else {
			if (entity.isClan()) {
				TaskScheduler.of(() -> {
					Date now = new Date();
					Hastebin bin = getLocalHastebinInstance();
					PasteResponse response;
					boolean isConsole = entity.getAsClan().isConsole();
					Clan.Implementation implementation = entity.getAsClan().getImplementation();
					if (entity.isValid()) {
						boolean doubleCheck = true;
						for (InvasiveEntity e : entity.getAsClan()) {
							if (!e.isValid()) {
								doubleCheck = false;
								break;
							}
						}
						if (doubleCheck) {
							String json = entity.getAsClan().write(entity.getAsClan()).toString();
							response = bin.write("/**",
									" * Debugged: " + now.toLocaleString() + "",
									" * Object: " + json,
									" * Grade: PASS",
									" * Server: " + isConsole,
									" * Implementation: " + implementation,
									" * Comment: This clan object from members to self is fully valid!",
									" */");
						} else {
							response = bin.write("/**",
									" * Debugged: " + now.toLocaleString() + "",
									" * Grade: FAIL",
									" * Server: " + isConsole,
									" * Implementation: " + implementation,
									" * Comment: This clan object can't be used, one or more members are invalid.",
									" */");
						}
					} else {
						response = bin.write("/**",
								" * Debugged: " + now.toLocaleString() + "",
								" * Grade: FAIL",
								" * Server: " + isConsole,
								" * Implementation: " + implementation,
								" * Comment: This clan object can't be used.",
								" */");
					}
					String link = response.get();
					getPlugin().getLogger().warning(entity.getName() + " debug: " + link);
				}).scheduleAsync();
			}
		}
	}

	default Optional<Clan.Associate> getAssociate(LabyrinthUser user) {
		return getAssociate(user.getId());
	}

	/**
	 * Get the server consultant object if one has been provided.
	 *
	 * @return the server consultant object if provided or null.
	 * @apiNote The server consultant is also an {@link com.github.sanctum.clans.construct.api.Clan.Associate}!
	 */
	default @Nullable Consultant getConsultant() {
		return (Consultant) getAssociate(getSessionId()).orElse(null);
	}

	@Experimental(dueTo = "Involving usage of the brand new api! Use at your own risk.")
	default List<InvasiveEntity> getEntities() {
		List<InvasiveEntity> list = InoperableSpecialMemory.ENTITY_MAP.values().stream().collect(Collectors.toList());
		getClanManager().getClans().forEach(c -> {
			list.addAll(c.getMembers());
			list.add(c);
		});
		return Collections.unmodifiableList(list);
	}


}
