package net.plazmix.listener;

import lombok.Getter;
import lombok.NonNull;
import net.plazmix.event.RegionEnterEvent;
import net.plazmix.event.RegionExitEvent;
import net.plazmix.utility.location.region.Region;
import net.plazmix.utility.location.region.RegionMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

public final class RegionEnterExitListener implements Listener {

	@Getter
	private static final RegionMap<Region> regionMap = new RegionMap<>();

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		handleReMoving(event.getPlayer(), event.getFrom(), event.getTo(),
				RegionExitEvent.ExitCause.MOVE, RegionEnterEvent.EnterCause.MOVE, event);
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		handleReMoving(event.getPlayer(), event.getFrom(), event.getTo(),
				RegionExitEvent.ExitCause.TELEPORT, RegionEnterEvent.EnterCause.TELEPORT, event);
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		handleExit(event.getPlayer(), RegionExitEvent.ExitCause.QUIT);
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		handleExit(event.getEntity(), RegionExitEvent.ExitCause.DEATH);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		handleEnter(event.getPlayer(), RegionEnterEvent.EnterCause.JOIN);
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		handleEnter(event.getPlayer(), RegionEnterEvent.EnterCause.RESPAWN);
	}

	private void handleReMoving(@NonNull Player player,
								@NonNull Location from, @NonNull Location to,
								@NonNull RegionExitEvent.ExitCause exitCause,
								@NonNull RegionEnterEvent.EnterCause enterCause,
								@NonNull Cancellable event) {

		regionMap.get(from).forEach(region -> {

			if (region.contains(from) && !region.contains(to)) {
				Bukkit.getPluginManager().callEvent(new RegionExitEvent(player, region, exitCause, event));
			}
		});

		regionMap.get(to).forEach(region -> {

			if (!region.contains(from) && region.contains(to)) {
				Bukkit.getPluginManager().callEvent(new RegionEnterEvent(player, region, enterCause, event));
			}
		});
	}

	private void handleExit(@NonNull Player player, @NonNull RegionExitEvent.ExitCause exitCause) {
		regionMap.get(player.getLocation()).forEach(region -> {

			if (region.contains(player.getLocation())) {
				Bukkit.getPluginManager().callEvent(new RegionExitEvent(player, region, exitCause, null));
			}
		});
	}

	private void handleEnter(@NonNull Player player, @NonNull RegionEnterEvent.EnterCause enterCause) {
		regionMap.get(player.getLocation()).forEach(region -> {

			if (region.contains(player.getLocation())) {
				Bukkit.getPluginManager().callEvent(new RegionEnterEvent(player, region, enterCause, null));
			}
		});
	}
	
}