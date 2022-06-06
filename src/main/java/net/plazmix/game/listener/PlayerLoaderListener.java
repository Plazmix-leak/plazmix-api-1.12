package net.plazmix.game.listener;

import lombok.NonNull;
import net.plazmix.PlazmixApi;
import net.plazmix.coreconnector.CoreConnector;
import net.plazmix.coreconnector.utility.server.ServerSubModeType;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.mysql.GameMysqlDatabase;
import net.plazmix.game.state.GameState;
import net.plazmix.game.user.GameUser;
import net.plazmix.utility.PlayerUtil;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collection;
import java.util.stream.Collectors;

public final class PlayerLoaderListener extends GameListener {

    public PlayerLoaderListener(@NonNull GamePlugin plugin) {
        super(plugin);
    }

    private void loadAllDatabases(@NonNull GamePlugin plugin, @NonNull GameUser gameUser) {
        for (GameMysqlDatabase mysqlDatabase : plugin.getService().getGameDatabasesMap().values()) {
            mysqlDatabase.onJoinLoad(plugin, gameUser);
        }
    }

    private void saveAllDatabases(@NonNull GamePlugin plugin, @NonNull GameUser gameUser) {
        for (GameMysqlDatabase mysqlDatabase : plugin.getService().getGameDatabasesMap().values()) {
            mysqlDatabase.onQuitSave(plugin, gameUser);
        }
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        PlazmixUser plazmixUser = PlazmixUser.of(event.getName());
        GameState currentState = plugin.getService().getCurrentState();

        if (!currentState.isAvailableJoinPlayers()) {
            return;
        }

        if (Bukkit.getOnlinePlayers().size() >= plugin.getService().getMaxPlayers()) {
            Collection<Player> canKickPlayers = Bukkit.getOnlinePlayers()
                    .stream()
                    .filter(player -> PlayerUtil.getGroup(player).getLevel() < plazmixUser.getGroup().getLevel())
                    .collect(Collectors.toList());

            if (canKickPlayers.isEmpty()) {

                event.setKickMessage(ChatColor.RED + "Данная арена переполнена");
                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                return;
            }

            Player replacementPlayer = canKickPlayers.stream().findFirst().get();

            replacementPlayer.sendMessage("§c§lВНИМАНИЕ! §cВаше место на арене " + CoreConnector.getInstance().getServerName() + " занял " + plazmixUser.getDisplayName());
            replacementPlayer.sendMessage("§cтак как его статус Выше вашего по уровню!");

            CoreConnector.getNetworkInstance().redirectToBest(replacementPlayer.getName(), PlazmixApi.getCurrentServerMode(), ServerSubModeType.GAME_LOBBY);
        }
    }

    @EventHandler
    public void handleJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        GameState currentState = plugin.getService().getCurrentState();

        GameUser gameUser = plugin.getService().getGameUser(player);
        gameUser.setGhost(!currentState.isAvailableJoinPlayers());

        if (gameUser.isGhost()) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {

                player.setAllowFlight(true);
                player.setFlying(true);

            }, 20);
        }

        loadAllDatabases(plugin, gameUser);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void handleUnavailableJoin(PlayerJoinEvent event) {
        GameState currentState = plugin.getService().getCurrentState();

        if (!currentState.isAvailableJoinPlayers()) {
            event.setJoinMessage(null);
        }
    }

    @EventHandler
    public void handleQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        GameUser gameUser = plugin.getService().getGameUser(player);
        saveAllDatabases(plugin, gameUser);
    }

}
