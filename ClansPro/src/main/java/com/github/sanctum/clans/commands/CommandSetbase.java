package com.github.sanctum.clans.commands;

import com.github.sanctum.clans.bridge.ClanVentBus;
import com.github.sanctum.clans.construct.DataManager;
import com.github.sanctum.clans.construct.api.Clan;
import com.github.sanctum.clans.construct.api.ClanSubCommand;
import com.github.sanctum.clans.construct.api.ClansAPI;
import com.github.sanctum.clans.construct.api.Clearance;
import com.github.sanctum.clans.construct.extra.StringLibrary;
import com.github.sanctum.clans.event.associate.AssociateUpdateBaseEvent;
import org.bukkit.entity.Player;

public class CommandSetbase extends ClanSubCommand {
	public CommandSetbase() {
		super("setbase");
	}

	@Override
	public boolean player(Player p, String label, String[] args) {
		StringLibrary lib = Clan.ACTION;
		Clan.Associate associate = ClansAPI.getInstance().getAssociate(p).orElse(null);

		if (args.length == 0) {
			if (!p.hasPermission(this.getPermission() + "." + DataManager.Security.getPermission("setbase"))) {
				lib.sendMessage(p, lib.noPermission(this.getPermission() + "." + DataManager.Security.getPermission("setbase")));
				return true;
			}

			if (associate == null) {
				lib.sendMessage(p, lib.notInClan());
				return true;
			}

			Clan clan = associate.getClan();
			if (Clearance.MANAGE_BASE.test(associate)) {
				if (!ClansAPI.getInstance().getClaimManager().isInClaim(p.getLocation())) {
					AssociateUpdateBaseEvent event = ClanVentBus.call(new AssociateUpdateBaseEvent(p, p.getLocation()));
					if (!event.isCancelled()) {
						clan.setBase(event.getLocation());
					}
				} else {
					if (ClansAPI.getInstance().getClaimManager().getClaim(p.getLocation()).getOwner().getTag().getId().equals(clan.getId().toString())) {
						AssociateUpdateBaseEvent event = ClanVentBus.call(new AssociateUpdateBaseEvent(p, p.getLocation()));
						if (!event.isCancelled()) {
							clan.setBase(event.getLocation());
						}
					} else {
						lib.sendMessage(p, lib.notClaimOwner(((Clan) ClansAPI.getInstance().getClaimManager().getClaim(p.getLocation()).getHolder()).getName()));
					}
				}
			} else {
				lib.sendMessage(p, lib.noClearance());
				return true;
			}
			return true;
		}

		if (args.length == 1) {
			if (!p.hasPermission(this.getPermission() + "." + DataManager.Security.getPermission("create"))) {
				lib.sendMessage(p, lib.noPermission(this.getPermission() + "." + DataManager.Security.getPermission("create")));
				return true;
			}
			if (!isAlphaNumeric(args[0])) {
				lib.sendMessage(p, lib.nameInvalid(args[0]));
				return true;
			}
			if (Clan.ACTION.getAllClanNames().contains(args[0])) {
				lib.sendMessage(p, lib.alreadyMade(args[0]));
				return true;
			}
			Clan.ACTION.create(p.getUniqueId(), args[0], null);
			return true;
		}


		return true;
	}
}