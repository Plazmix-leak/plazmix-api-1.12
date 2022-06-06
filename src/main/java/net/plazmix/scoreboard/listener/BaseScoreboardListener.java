package net.plazmix.scoreboard.listener;

import net.plazmix.scoreboard.BaseScoreboard;
import net.plazmix.scoreboard.BaseScoreboardScope;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BaseScoreboardListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();

        for (BaseScoreboard baseScoreboard : BaseScoreboardScope.SCOPING_SCOREBOARD_MAP.get(BaseScoreboardScope.PUBLIC)) {
            baseScoreboard.setScoreboardToPlayer(player);
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent playerQuitEvent) {
        Player player = playerQuitEvent.getPlayer();

        for (BaseScoreboard baseScoreboard
                : BaseScoreboardScope.SCOPING_SCOREBOARD_MAP.values()) {

            baseScoreboard.getPlayerReceiverCollection().remove(player);
            baseScoreboard.getScoreboardQueueMap().remove(player);
        }
    }

}
