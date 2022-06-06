package net.plazmix.game.state;

import lombok.Getter;
import lombok.NonNull;
import net.plazmix.coreconnector.CoreConnector;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.event.GameStateActivateEvent;
import net.plazmix.game.event.GameStateShutdownEvent;
import net.plazmix.game.event.GameStateSwitchEvent;
import net.plazmix.utility.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.LinkedList;

@Getter
public final class GameStateManager {

    private GameState currentState;
    private final LinkedList<GameState> gameStates = new LinkedList<>();

    public void registerState(@NonNull GameState gameState) {
        gameStates.add(gameState);
    }

    public void registerState(int stateElementId, @NonNull GameState gameState) {
        gameStates.set(stateElementId, gameState);
    }

    public void activateState(@NonNull GameState gameState) {
        if (currentState != null) {
            currentState.forceShutdown();
        }

        currentState = gameState;

        currentState.setEnabled(true);
        currentState.onStart();

        Bukkit.getPluginManager().registerEvents(gameState, gameState.getPlugin());
        Bukkit.getPluginManager().callEvent(new GameStateActivateEvent(gameState));

        if (CoreConnector.getInstance().isConnected() && CoreConnector.getInstance().getChannelWrapper() != null) {
            CoreConnector.getInstance().setMotd(GamePlugin.getInstance().getService().getGameServerInfo().toServerMotd());
        }
    }

    public void shutdownState(@NonNull GameState gameState) {
        currentState = null;

        gameState.setEnabled(false);
        gameState.onShutdown();

        Bukkit.getPluginManager().callEvent(new GameStateShutdownEvent(gameState));
        HandlerList.unregisterAll(gameState);

        // Full player clearing.
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerUtil.resetPlayer(player);
        }

        // Bukkit.reloadData();
    }


    public void nextStage() {
        if (gameStates.isEmpty()) {
            return;
        }

        if (currentState == null) {

            currentState = gameStates.getFirst();
            activateState(currentState);
            return;
        }

        int currentStateId = gameStates.indexOf(currentState);

        if (currentStateId + 1 >= gameStates.size()) {
            Bukkit.shutdown();
            return;
        }

        GameState nextStage = gameStates.get(currentStateId + 1);

        Bukkit.getPluginManager().callEvent(new GameStateSwitchEvent(currentState, nextStage));
        activateState(nextStage);
    }

    public void backStage() {
        if (gameStates.isEmpty()) {
            return;
        }

        if (currentState == null) {

            currentState = gameStates.getFirst();
            activateState(currentState);
            return;
        }

        int currentStateId = gameStates.indexOf(currentState);

        if (currentStateId <= 0) {
            return;
        }

        GameState previousStage = gameStates.get(currentStateId - 1);

        Bukkit.getPluginManager().callEvent(new GameStateSwitchEvent(currentState, previousStage));
        activateState(previousStage);
    }

}
