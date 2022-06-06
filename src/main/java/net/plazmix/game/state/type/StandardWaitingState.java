package net.plazmix.game.state.type;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.plazmix.coreconnector.module.type.NetworkModule;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.setting.GameSetting;
import net.plazmix.game.state.GameState;
import net.plazmix.game.utility.GameSchedulers;
import net.plazmix.utility.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.stream.Collectors;

public abstract class StandardWaitingState extends GameState {

    @Getter
    protected final TimerStatus timerStatus = new TimerStatus(this);

    public StandardWaitingState(@NonNull GamePlugin plugin, @NonNull String stateName) {
        super(plugin, stateName, true);
    }


    protected abstract Location getTeleportLocation();

    protected abstract void handleEvent(@NonNull PlayerJoinEvent event);
    protected abstract void handleEvent(@NonNull PlayerQuitEvent event);

    protected abstract void handleTimerUpdate(@NonNull TimerStatus timerStatus);

    @Override
    protected void onStart() {
        plugin.getGamesData().setPlayersIdsList(Bukkit.getOnlinePlayers().stream().map(player -> NetworkModule.getInstance().getPlayerId(player.getName())).collect(Collectors.toList()));
        plugin.getGamesData().setStartTimeMillis(System.currentTimeMillis());

        // Handle game settings
        GameSetting.setAll(plugin.getService(), false);
    }

    @Override
    protected void onShutdown() {
        // Starting game process states...
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        GameSchedulers.runLater(5, () -> PlayerUtil.resetPlayer(event.getPlayer()));

        Location teleportLocation = getTeleportLocation();

        if (teleportLocation != null) {
            event.getPlayer().teleport(teleportLocation);
        }

        handleEvent(event);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        handleEvent(event);
    }

    @EventHandler
    public void onPlayerFall(PlayerMoveEvent event) {

        if (event.getTo().getY() < getFallingMinY()) {
            Location teleportLocation = getTeleportLocation();

            if (teleportLocation != null) {
                event.getPlayer().teleport(teleportLocation);
            }
        }
    }

    protected int getFallingMinY() {
        return 0;
    }


    @RequiredArgsConstructor
    public static class TimerStatus {

        public static final int TIMER_PRE_START_SECONDS = 15;
        public static final int TIMER_MAX_SECONDS = 60;

        private final StandardWaitingState state;
        private BukkitTask task;

        private int startSeconds;

        @Getter
        private int leftSeconds;

        private void onSecondTick() {
            leftSeconds--;

            if (leftSeconds <= 0) {
                cancelTask();

                state.nextStage();
                return;
            }

            for (Player player : Bukkit.getOnlinePlayers()) {

                player.setLevel(leftSeconds);
                player.setExp((float) leftSeconds / startSeconds);

                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BELL, 1, 2f - (2f / startSeconds) * leftSeconds);
            }

            state.handleTimerUpdate(this);
        }

        public void runTask(int leftSeconds) {
            cancelTask();

            this.leftSeconds = this.startSeconds = (leftSeconds + 1);
            this.task = GameSchedulers.runTimer(0, 20, this::onSecondTick);
        }

        public void runTask() {
            runTask(TIMER_PRE_START_SECONDS);
        }

        public void cancelTask() {

            if (task != null) {
                task.cancel();
            }

            for (Player player : Bukkit.getOnlinePlayers()) {

                player.setExp(0);
                player.setLevel(0);

                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1, 1);
            }

            state.handleTimerUpdate(this);
        }

        public boolean isLived() {
            return task != null && leftSeconds > 0;
        }
    }

}
