package net.plazmix.game.state;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.plazmix.coreconnector.utility.server.game.GameServerInfo;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.GamePluginService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

@RequiredArgsConstructor
@Getter
public abstract class GameState
        implements Listener {

    protected final @NonNull GamePlugin plugin;
    protected final @NonNull String stateName;

    private final boolean availableJoinPlayers;

    @Setter
    private boolean enabled;


    protected abstract void onStart();
    protected abstract void onShutdown();

    public void forceShutdown() {
        plugin.getService().getStateManager().shutdownState(this);
    }


    public void nextStage() {
        plugin.getService().getStateManager().nextStage();
    }

    public void backStage() {
        plugin.getService().getStateManager().backStage();
    }


    @EventHandler
    public void onServerPing(ServerListPingEvent event) {
        GamePluginService serverService = plugin.getService();

        GameServerInfo serverInfo = new GameServerInfo(

                serverService.getMapName(),
                serverService.getServerMode(),

                isAvailableJoinPlayers(),

                serverService.getAlivePlayers().size(),
                serverService.getMaxPlayers()
        );

        event.setMaxPlayers(serverService.getMaxPlayers());
        event.setMotd(serverInfo.toServerMotd());
    }

}
