package net.plazmix.game.state.type;

import lombok.NonNull;
import net.plazmix.PlazmixApi;
import net.plazmix.coreconnector.module.type.NetworkModule;
import net.plazmix.coreconnector.utility.server.ServerSubModeType;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.setting.GameSetting;
import net.plazmix.game.state.GameState;
import net.plazmix.game.utility.GameSchedulers;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class StandardEndingState extends GameState {

    public StandardEndingState(@NonNull GamePlugin plugin, @NonNull String stateName) {
        super(plugin, stateName, false);
    }


    protected abstract String getWinnerPlayerName();

    protected abstract void handleStart();
    protected abstract void handleScoreboardSet(@NonNull Player player);

    protected abstract Location getTeleportLocation();

    @Override
    protected void onStart() {
        plugin.getGamesData().setWinner(getWinnerPlayerName());
        plugin.getGamesData().setEndTimeMillis(System.currentTimeMillis());

        // Handle game settings;
        GameSetting.resetDefaults(plugin.getService());

        for (Player player : Bukkit.getOnlinePlayers()) {

            // Handle teleport location.
            Location teleportLocation = getTeleportLocation();

            if (teleportLocation != null) {
                player.teleport(teleportLocation);
            }

            // Handle scoreboard.
            handleScoreboardSet(player);
        }

        handleStart();

        // Через 1 минуту завершаем это состояние
        GameSchedulers.runLater(20 * 10, this::onShutdown);
    }

    @Override
    protected void onShutdown() {

        // Кидаем всех игроков на лобби сервер
        for (Player player : Bukkit.getOnlinePlayers()) {
            NetworkModule.getInstance().redirectToBest(player.getName(), PlazmixApi.getCurrentServerMode(), ServerSubModeType.GAME_LOBBY);
        }

        // И еще через 5 секунд перезапускаем арену
        GameSchedulers.runLater(100, Bukkit::shutdown);
    }

    @EventHandler
    public void onPlayerFall(PlayerMoveEvent event) {

        if (event.getTo().getY() < getFallingMinY()) {
            Location spawnLocation = getTeleportLocation();

            if (spawnLocation != null) {
                event.getPlayer().teleport(spawnLocation);
            }
        }
    }

    private final List<Player> alreadyGgSendPlayers = new ArrayList<>();

    @EventHandler
    public void onChatSendGG(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (!alreadyGgSendPlayers.contains(player) && event.getMessage().equalsIgnoreCase("GG")) {

            PlazmixUser plazmixUser = PlazmixUser.of(player);
            plazmixUser.addExperience(2);

            player.sendMessage("§b+2 опыта");
            alreadyGgSendPlayers.add(player);
        }
    }

    protected int getFallingMinY() {
        return 0;
    }

}
