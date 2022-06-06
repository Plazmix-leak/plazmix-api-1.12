package net.plazmix.lobby.npc.listener;

import net.plazmix.PlazmixApiPlugin;
import net.plazmix.lobby.npc.ServerNPC;
import net.plazmix.lobby.npc.manager.ServerNPCManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public final class ServerNPCListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        new BukkitRunnable() {

            @Override
            public void run() {
                for (ServerNPC<?> serverNPC : ServerNPCManager.INSTANCE.getServerNpcsCollection()) {
                    serverNPC.addReceivers(player);
                }
            }

        }.runTaskLater(PlazmixApiPlugin.getPlugin(PlazmixApiPlugin.class), 15);
    }

}
