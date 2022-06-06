package net.plazmix.inventory.updater;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.plazmix.inventory.BaseInventory;
import net.plazmix.inventory.addon.BaseInventoryUpdater;
import net.plazmix.inventory.manager.BukkitInventoryManager;
import org.bukkit.entity.Player;

@Getter
@Setter
@RequiredArgsConstructor
public abstract class SimpleInventoryUpdater implements BaseInventoryUpdater {

    private BaseInventory inventory;

    private final Player player;

    private boolean enable;
    private boolean cancelled;


    @Override
    public void startUpdater(long periodTicks) {
        this.cancelled = !cancelled;

        if (isCancelled()) {
            BukkitInventoryManager.INSTANCE.addInventoryUpdater(this, periodTicks);
        }
    }

    @Override
    public void cancelUpdater() {
        this.cancelled = !cancelled;

        if (!isCancelled()) {
            BukkitInventoryManager.INSTANCE.removeInventoryUpdater(this);
        }
    }

}
