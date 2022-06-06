package net.plazmix.holographic.updater;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.plazmix.PlazmixApiPlugin;
import net.plazmix.holographic.ProtocolHolographic;
import net.plazmix.holographic.ProtocolHolographicUpdater;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Consumer;

@Getter
@Setter
@RequiredArgsConstructor
public abstract class SimpleHolographicUpdater extends BukkitRunnable
        implements ProtocolHolographicUpdater, Consumer<ProtocolHolographic> {

    private final ProtocolHolographic holographic;

    private boolean enable;

    @Override
    public void run() {
        if (holographic.getViewers().isEmpty()) {
            return;
        }

        accept(holographic);
    }

    @Override
    public void startUpdater(long periodTick) {
        PlazmixApiPlugin plazmixApiPlugin = PlazmixApiPlugin.getPlugin(PlazmixApiPlugin.class);
        runTaskTimerAsynchronously(plazmixApiPlugin, 0, periodTick);
    }

    @Override
    public void cancelUpdater() {
        cancel();
    }

}
