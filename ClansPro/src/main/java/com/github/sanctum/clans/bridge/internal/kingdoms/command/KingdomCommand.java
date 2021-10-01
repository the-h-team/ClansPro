package com.github.sanctum.clans.bridge.internal.kingdoms.command;

import com.github.sanctum.clans.bridge.ClanAddonQuery;
import com.github.sanctum.clans.bridge.ClanVentBus;
import com.github.sanctum.clans.bridge.internal.KingdomAddon;
import com.github.sanctum.clans.bridge.internal.kingdoms.Kingdom;
import com.github.sanctum.clans.bridge.internal.kingdoms.Quest;
import com.github.sanctum.clans.bridge.internal.kingdoms.RoundTable;
import com.github.sanctum.clans.bridge.internal.kingdoms.event.KingdomCreatedEvent;
import com.github.sanctum.clans.bridge.internal.kingdoms.event.KingdomCreationEvent;
import com.github.sanctum.clans.construct.api.Clan;
import com.github.sanctum.clans.construct.api.ClanSubCommand;
import com.github.sanctum.clans.construct.api.ClansAPI;
import com.github.sanctum.clans.construct.extra.MessagePrefix;
import com.github.sanctum.labyrinth.formatting.Message;
import com.github.sanctum.labyrinth.formatting.PaginatedList;
import com.github.sanctum.labyrinth.formatting.string.DefaultColor;
import com.github.sanctum.labyrinth.formatting.string.RandomHex;
import com.github.sanctum.labyrinth.library.Mailer;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.labyrinth.library.TextLib;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KingdomCommand extends ClanSubCommand implements Message.Factory {

	private final KingdomAddon addon;

	public KingdomCommand(KingdomAddon addon, String label) {
		super(label);
		this.addon = addon;
	}

	@Override
	public boolean player(Player p, String label, String[] args) {

		if (args.length == 0) {
			// TODO: send help menu
			Clan.Associate associate = ClansAPI.getInstance().getAssociate(p).orElse(null);
			message()
					.append(text(" "))
					.send(p);
			if (associate != null) {
				if (associate.getClan().getValue(String.class, "kingdom") != null) {
					message()
							.append(text(" "))
							.append(text("[").color(Color.OLIVE))
							.append(text("Start").color(Color.MAROON).style(ChatColor.STRIKETHROUGH).bind(hover("You are already in a kingdom").style(DefaultColor.VELVET)))
							.append(text("]").color(Color.OLIVE))
							.send(p);
					message()
							.append(text(" "))
							.send(p);
					message()
							.append(text(" "))
							.append(text("[").color(Color.OLIVE))
							.append(text("Leave").color(Color.FUCHSIA).bind(hover("Click to leave your current kingdom").style(new RandomHex())).bind(command("/c kingdom leave")))
							.append(text("]").color(Color.OLIVE))
							.send(p);
					message()
							.append(text(" "))
							.send(p);
					message()
							.append(text(" "))
							.append(text("[").color(Color.OLIVE))
							.append(text("Jobs").color(Color.FUCHSIA).bind(hover("Click to view the jobs your kingdom has available.").style(new RandomHex())).bind(command("/c kingdom jobs")))
							.append(text("]").color(Color.OLIVE))
							.send(p);

				} else {
					message()
							.append(text(" "))
							.append(text("[").color(Color.OLIVE))
							.append(text("Start").color(Color.FUCHSIA).bind(hover("Click to start a kingdom").style(new RandomHex())).bind(suggest("/c kingdom start ")))
							.append(text("]").color(Color.OLIVE))
							.send(p);
					message()
							.append(text(" "))
							.send(p);
					message()
							.append(text(" "))
							.append(text("[").color(Color.OLIVE))
							.append(text("Join").style(new RandomHex()).bind(hover("Click to join a kingdom").style(new RandomHex())).bind(suggest("/c kingdom join ")))
							.append(text("]").color(Color.OLIVE))
							.send(p);
				}
			} else {
				message()
						.append(text("You must own a clan to contribute to kingdom services.").style(DefaultColor.VELVET))
						.send(p);
			}
			message()
					.append(text(" "))
					.send(p);
		}

		if (args.length == 1) {

			if (args[0].equalsIgnoreCase("start")) {
				Clan.ACTION.sendMessage(p, "&cInvalid usage: &6/clan &7kingdom start <name>");
			}

			if (args[0].equalsIgnoreCase("leave")) {

				Clan.Associate associate = ClansAPI.getInstance().getAssociate(p).orElse(null);

				if (associate != null) {

					Clan c = associate.getClan();

					String kindom = c.getValue(String.class, "kingdom");

					if (kindom != null) {

						if (associate.getPriority().toInt() < 3) {
							Clan.ACTION.sendMessage(p, Clan.ACTION.noClearance());
							return true;
						}

						Kingdom k = Kingdom.getKingdom(kindom);

						k.getMembers().remove(c);

						c.removeValue("kingdom");

						Clan.ACTION.sendMessage(p, "&cYour clan is no longer a member of the kingdom.");

						if (k.getMembers().size() == 0) {
							// TODO: announce kingdom fallen
							k.remove(ClanAddonQuery.getAddon("Kingdoms"));
						}

					}
				}

			}

			if (args[0].equalsIgnoreCase("jobs")) {

				Clan.Associate associate = ClansAPI.getInstance().getAssociate(p).orElse(null);

				if (associate != null) {

					Clan c = associate.getClan();

					String kingdom = c.getValue(String.class, "kingdom");

					if (kingdom != null) {

						Kingdom k = Kingdom.getKingdom(kingdom);

						PaginatedList<Quest> help = new PaginatedList<>(new ArrayList<>(k.getQuests()))
								.limit(6)
								.start((pagination, page, max) -> {
									MessagePrefix prefix = ClansAPI.getInstance().getPrefix();
									message().append(text(prefix.getPrefix())).append(text(prefix.getText()).style(new RandomHex())).append(text(prefix.getSuffix())).append(text(" ")).append(text("|").style(ChatColor.BOLD)).append(text(" ")).append(text("Jobs").style(DefaultColor.MANGO)).send(p);
								});

						help.finish(builder -> {
							builder.setPrefix("&7&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
							builder.setSuffix("&7&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
							builder.setPlayer(p);
						}).decorate((pagination, quest, page, max, placement) -> {

							if (quest.activated(p)) {
								message().append(text("[").color(Color.MAROON))
										.append(text("#").color(Color.GRAY))
										.append(text(String.valueOf(placement)).color(Color.ORANGE).style(ChatColor.BOLD))
										.append(text("]").color(Color.MAROON))
										.append(text(" "))
										.append(text(quest.getTitle()).style(new RandomHex()))
										.append(text(" "))
										.append(text("(").color(Color.MAROON))
										.append(text(String.valueOf(quest.getPercentage())).color(Color.AQUA).bind(hover("&cClick to quit job &3&l" + quest.getTitle())).bind(command("/c kingdom quit " + quest.getTitle())))
										.append(text("%"))
										.append(text(")").color(Color.MAROON))
										.append(text(":"))
										.append(text(" "))
										.append(text(quest.getDescription()).style(new RandomHex()))
										.send(p);
							} else {
								message().append(text("[").color(Color.MAROON))
										.append(text("#").color(Color.GRAY))
										.append(text(String.valueOf(placement)).color(Color.ORANGE).style(ChatColor.BOLD))
										.append(text("]").color(Color.MAROON))
										.append(text(" "))
										.append(text(quest.getTitle()).style(new RandomHex()))
										.append(text(" "))
										.append(text("(").color(Color.MAROON))
										.append(text(String.valueOf(quest.getPercentage())).color(Color.AQUA).bind(hover("&aClick to accept job &3&l" + quest.getTitle())).bind(command("/c kingdom work " + quest.getTitle())))
										.append(text("%"))
										.append(text(")").color(Color.MAROON))
										.append(text(":"))
										.append(text(" "))
										.append(text(quest.getDescription()).style(new RandomHex()))
										.send(p);
							}

						}).get(1);

					}


				}

			}


			if (args[0].equalsIgnoreCase("roundtable")) {
				// TODO: send rountable help menu
			}

		}

		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("join")) {

				Kingdom k = Kingdom.getKingdom(args[1]);

				if (k != null) {

					ClansAPI API = ClansAPI.getInstance();

					Clan.Associate associate = API.getAssociate(p).orElse(null);

					if (associate != null) {

						Clan c = associate.getClan();

						if (c.getValue(String.class, "kingdom") != null) {
							Clan.ACTION.sendMessage(p, "&cYou are already in a kingdom.");
							return true;
						}

						if (associate.getPriority().toInt() == 3) {

							k.getMembers().add(c);

						} else {
							Clan.ACTION.sendMessage(p, Clan.ACTION.noClearance());
						}

					}

				} else {
					Clan.ACTION.sendMessage(p, "&cThis kingdom doesn't exist!");
				}

			}


			if (args[0].equalsIgnoreCase("start")) {

				String name = args[1];

				ClansAPI API = ClansAPI.getInstance();

				Clan.Associate associate = API.getAssociate(p).orElse(null);

				if (associate != null) {


					Clan c = associate.getClan();

					Kingdom k = Kingdom.getKingdom(name);

					if (k == null) {

						if (associate.getPriority().toInt() < 3) {
							Clan.ACTION.sendMessage(p, Clan.ACTION.noClearance());
							return true;
						}

						String kingdom = c.getValue(String.class, "kingdom");

						if (kingdom != null) {
							Clan.ACTION.sendMessage(p, "&cYou are already apart of a kingdom!");
							return true;
						}

						KingdomCreationEvent event = ClanVentBus.call(new KingdomCreationEvent(associate, name));
						if (!event.isCancelled()) {
							Kingdom create = new Kingdom(event.getKingdomName(), addon);
							ClanVentBus.call(new KingdomCreatedEvent(p, create));
							c.setValue("kingdom", event.getKingdomName(), false);
							create.getMembers().add(c);

							addon.getMailer().prefix().start(Clan.ACTION.getPrefix()).finish().announce(player -> true, p.getName() + " started a new kingdom called &6" + event.getKingdomName()).deploy();
						}

					} else {

						if (k.getMembers().contains(c)) {
							Clan.ACTION.sendMessage(p, "&cYou're already a member.");
						} else {
							Clan.ACTION.sendMessage(p, "&cThis kingdom already exists!");
						}

					}


				}

			}

			if (args[0].equalsIgnoreCase("roundtable")) {

				RoundTable table = KingdomAddon.getRoundTable();

				if (args[1].equalsIgnoreCase("jobs")) {

					PaginatedList<Quest> help = new PaginatedList<>(new ArrayList<>(table.getQuests()))
							.limit(6)
							.start((pagination, page, max) -> {
								if (Bukkit.getVersion().contains("1.17") || Bukkit.getVersion().contains("1.16")) {
									Mailer.empty(p).chat("&7&m------------&7&l[&#ff7700&oRoundtable Jobs&7&l]&7&m------------").deploy();
								} else {
									Mailer.empty(p).chat("&7&m------------&7&l[&6&oRoundtable Jobs&7&l]&7&m------------").deploy();
								}
							});

					help.finish(builder -> {
						builder.setPrefix("&7&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
						builder.setSuffix("&7&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
						builder.setPlayer(p);
					}).decorate((pagination, achievement, page, max, placement) -> {

						if (achievement.activated(p)) {
							Mailer.empty(p).chat(TextLib.getInstance().textRunnable("", " &7# &6&l" + placement + " &c&o" + achievement.getTitle() + " &r(&d" + achievement.getPercentage() + "%&r) " + " &e: &b&l" + achievement.getDescription(), "&6Click to quit job &3&l" + achievement.getTitle(), "c kingdom roundtable quit " + achievement.getTitle())).deploy();
						} else {
							Mailer.empty(p).chat(TextLib.getInstance().textRunnable("", " &7# &6&l" + placement + " &3&o" + achievement.getTitle() + " &r(&d" + achievement.getPercentage() + "%&r) " + " &e: &b&l" + achievement.getDescription(), "&6Click to start job &3&l" + achievement.getTitle(), "c kingdom roundtable work " + achievement.getTitle())).deploy();
						}

					}).get(1);

				}

				if (args[0].equalsIgnoreCase("join")) {

					if (table.getUsers().isEmpty()) {

						table.take(p.getUniqueId(), RoundTable.Rank.HIGHEST);
						addon.getMailer().prefix().start(Clan.ACTION.getPrefix()).finish().announce(player -> true, p.getName() + " is now among the most powerful on the server.").deploy();

					} else {

						if (!table.join(p.getUniqueId())) {

							if (table.isMember(p.getUniqueId())) {
								Clan.ACTION.sendMessage(p, "&cYou are already a member.");
							} else {
								Clan.ACTION.sendMessage(p, "&cYou are not invited.");
							}

						} else {

							Clan.ACTION.sendMessage(p, "&a&lWelcome to the round table.");

						}
					}
				}


			}

			if (args[0].equalsIgnoreCase("name")) {

				ClansAPI API = ClansAPI.getInstance();

				Clan.Associate associate = API.getAssociate(p).orElse(null);

				if (associate != null) {

					Clan c = associate.getClan();

					String kingdom = c.getValue(String.class, "kingdom");

					if (kingdom != null) {

						Kingdom k = Kingdom.getKingdom(kingdom);

						k.setName(args[1]);

						Clan.ACTION.sendMessage(p, "&aKingdom name changed to &b" + args[2]);

					}
				}

			}

			if (args[0].equalsIgnoreCase("work")) {


				ClansAPI API = ClansAPI.getInstance();

				Clan.Associate associate = API.getAssociate(p).orElse(null);

				if (associate != null) {


					Clan c = associate.getClan();

					String kingdom = c.getValue(String.class, "kingdom");

					if (kingdom != null) {

						Kingdom k = Kingdom.getKingdom(kingdom);

						Quest achievement = k.getQuest(args[1]);

						if (achievement != null) {

							if (achievement.activate(p)) {

								p.sendTitle(StringUtils.use(achievement.getTitle()).translate(), StringUtils.use(achievement.getDescription()).translate(), 60, 10, 60);


							} else {

								Clan.ACTION.sendMessage(p, "&cYou are already working job &e" + achievement.getTitle());

							}

						}
					}
				}

			}

			if (args[0].equalsIgnoreCase("quit")) {

				ClansAPI API = ClansAPI.getInstance();

				Clan.Associate associate = API.getAssociate(p).orElse(null);

				if (associate != null) {


					Clan c = associate.getClan();

					String kingdom = c.getValue(String.class, "kingdom");

					if (kingdom != null) {

						Kingdom k = Kingdom.getKingdom(kingdom);

						Quest achievement = k.getQuest(args[1]);

						if (achievement != null) {

							if (achievement.deactivate(p)) {

								Clan.ACTION.sendMessage(p, "&cYou are no longer working job &e" + achievement.getTitle());


							} else {

								Clan.ACTION.sendMessage(p, "&cYou aren't currently working job &e" + achievement.getTitle());

							}

						}
					}
				}

			}
		}

		if (args.length == 3) {
			if (args[0].equalsIgnoreCase("roundtable")) {

				RoundTable table = KingdomAddon.getRoundTable();

				if (table != null) {

					if (!table.isMember(p.getUniqueId())) {
						Clan.ACTION.sendMessage(p, "&cYou are not a member of the roundtable.");
						return true;
					}

					if (args[1].equalsIgnoreCase("work")) {


						Quest achievement = table.getQuest(args[2]);

						if (achievement != null) {

							if (achievement.activate(p)) {

								p.sendTitle(StringUtils.use(achievement.getTitle()).translate(), StringUtils.use(achievement.getDescription()).translate(), 60, 10, 60);


							} else {

								Clan.ACTION.sendMessage(p, "&cYou are already working job &e" + achievement.getTitle());

							}

						}

					}

					if (args[1].equalsIgnoreCase("quit")) {


						Quest achievement = table.getQuest(args[2]);

						if (achievement != null) {

							if (achievement.deactivate(p)) {

								Clan.ACTION.sendMessage(p, "&cYou are no longer working job &e" + achievement.getTitle());


							} else {

								Clan.ACTION.sendMessage(p, "&cYou aren't currently working job &e" + achievement.getTitle());

							}

						}

					}

				}

			}
		}

		return true;
	}

	@Override
	public boolean console(CommandSender sender, String label, String[] args) {
		return false;
	}

	@Override
	public List<String> tab(Player player, String label, String[] args) {
		return null;
	}
}
