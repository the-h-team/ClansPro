package com.github.sanctum.clans.listener;

import com.github.sanctum.clans.bridge.ClanVentBus;
import com.github.sanctum.clans.construct.Claim;
import com.github.sanctum.clans.construct.api.Clan;
import com.github.sanctum.clans.construct.api.ClansAPI;
import com.github.sanctum.clans.construct.api.Permission;
import com.github.sanctum.clans.construct.extra.ShieldTamper;
import com.github.sanctum.clans.events.core.ClaimInteractEvent;
import com.github.sanctum.clans.events.core.RaidShieldEvent;
import com.github.sanctum.labyrinth.event.custom.DefaultEvent;
import com.github.sanctum.labyrinth.event.custom.Subscribe;
import com.github.sanctum.labyrinth.event.custom.Vent;
import java.text.MessageFormat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

public class BlockEventListener implements Listener {


	@Subscribe
	public void onBreak(DefaultEvent.BlockBreak event) {
		final Block b = event.getBlock();
		ClaimInteractEvent e = ClanVentBus.call(new ClaimInteractEvent(event.getPlayer(), b, b.getLocation(), ClaimInteractEvent.InteractionType.BREAK));
		if (e.isCancelled()) {
			event.setCancelled(e.isCancelled());
		} else {
			if (Claim.getResident(event.getPlayer()) != null) {
				final Material type = b.getState().getType();
				final Byte data = b.getState().getRawData();
				Claim.getResident(event.getPlayer()).addBroken(b, type, data);
			}
		}
	}

	@Subscribe
	public void onBuild(DefaultEvent.BlockPlace event) {
		ClaimInteractEvent e = ClanVentBus.call(new ClaimInteractEvent(event.getPlayer(), event.getBlock(), event.getBlock().getLocation(), ClaimInteractEvent.InteractionType.BUILD));
		if (e.isCancelled()) {
			event.setCancelled(e.isCancelled());
		} else {
			if (Claim.getResident(event.getPlayer()) != null) {
				Claim.getResident(event.getPlayer()).addPlaced(event.getBlock());
			}
		}
	}

	@Subscribe(priority = Vent.Priority.LOW)
	public void onAdjust(RaidShieldEvent e) {
		ShieldTamper edit = ClansAPI.getInstance().getShieldManager().getTamper();
		if (edit.isOff()) {
			e.setCancelled(true);
		} else {
			if (edit.getUpTime() != 0) {
				e.setStartTime(edit.getUpTime());
				e.setStopTime(edit.getDownTime());
			}
		}
	}

	@Subscribe
	public void onShield(RaidShieldEvent e) {
		World world = Bukkit.getWorld(ClansAPI.getData().getMain().getRoot().getString("Clans.raid-shield.main-world"));
		if (world == null) {
			world = Bukkit.getWorlds().get(0);
		}
		if (Clan.ACTION.isNight(world, e.getStartTime(), e.getStopTime())) {
			if (ClansAPI.getInstance().getShieldManager().isEnabled()) {
				ClansAPI.getInstance().getShieldManager().setEnabled(false);
				if (e.getShieldOn().equals("{0} &a&lRAID SHIELD ENABLED")) {
					e.setShieldOff(ClansAPI.getData().getMain().getRoot().getString("Clans.raid-shield.messages.disabled"));
				}
				if (ClansAPI.getData().isTrue("Clans.raid-shield.send-messages")) {
					Bukkit.broadcastMessage(Clan.ACTION.color(MessageFormat.format(e.getShieldOff(), Clan.ACTION.getPrefix())));
				}
			}
		}
		if (!Clan.ACTION.isNight(world, e.getStartTime(), e.getStopTime())) {
			if (!ClansAPI.getInstance().getShieldManager().isEnabled()) {
				ClansAPI.getInstance().getShieldManager().setEnabled(true);
				if (e.getShieldOn().equals("{0} &a&lRAID SHIELD ENABLED")) {
					e.setShieldOn(ClansAPI.getData().getMain().getRoot().getString("Clans.raid-shield.messages.enabled"));
				}
				if (ClansAPI.getData().isTrue("Clans.raid-shield.send-messages")) {
					Bukkit.broadcastMessage(Clan.ACTION.color(MessageFormat.format(e.getShieldOn(), Clan.ACTION.getPrefix())));
				}
			}
		}
	}

	@Subscribe
	public void onClaimInteract(ClaimInteractEvent e) {
		if (ClansAPI.getInstance().getClaimManager().isInClaim(e.getLocation())) {
			Clan.Associate associate = ClansAPI.getInstance().getAssociate(e.getPlayer()).orElse(null);
			if (associate != null && associate.isValid()) {
				if (!e.getClaim().getOwner().equals(associate.getClan().getId().toString())) {
					if (!e.getPlayer().hasPermission("clanspro.claim.bypass")) {
						if (!e.getClaim().getClan().getAllyList().contains(associate.getClan().getId().toString())) {
							e.stringLibrary().sendMessage(e.getPlayer(), MessageFormat.format(e.stringLibrary().notClaimOwner(e.getClaim().getClan().getName()), e.getClaim().getClan().getName()));
							e.setCancelled(true);
						}
					}
				} else {
					if (e.getInteraction() == ClaimInteractEvent.InteractionType.USE) {
						if (!Permission.LAND_USE_INTRACTABLE.test(associate)) {
							Clan.ACTION.sendMessage(e.getPlayer(), Clan.ACTION.noClearance());
							e.setCancelled(true);
						}
					} else {
						if (!Permission.LAND_USE.test(associate)) {
							Clan.ACTION.sendMessage(e.getPlayer(), Clan.ACTION.noClearance());
							e.setCancelled(true);
						}
					}
				}
			} else {
				if (!e.getPlayer().hasPermission("clanspro.claim.bypass")) {
					e.stringLibrary().sendMessage(e.getPlayer(), MessageFormat.format(e.stringLibrary().notClaimOwner(e.getClaim().getClan().getName()), e.getClaim().getClan().getName()));
					e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onProjectileHit(ProjectileHitEvent event) {
		if (event.getEntity().getShooter() instanceof Player) {
			Player p = (Player) event.getEntity().getShooter();
			ClaimInteractEvent e = new Vent.Call<>(Vent.Runtime.Synchronous, new ClaimInteractEvent(p, event.getEntity().getLocation(), ClaimInteractEvent.InteractionType.USE)).run();
			if (e.isCancelled()) {
				if (event.getEntity().getType() != EntityType.TRIDENT) {
					e.stringLibrary().sendMessage(e.getPlayer(), MessageFormat.format(e.stringLibrary().notClaimOwner(e.getClaim().getClan().getName()), e.getClaim().getClan().getName()));
					event.getEntity().remove();
				}
			}
		}
	}


}
