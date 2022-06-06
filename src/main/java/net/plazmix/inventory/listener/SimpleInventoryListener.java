package net.plazmix.inventory.listener;

import net.plazmix.inventory.BaseInventory;
import net.plazmix.inventory.button.BaseInventoryButton;
import net.plazmix.inventory.button.impl.ActionInventoryButton;
import net.plazmix.inventory.button.impl.DraggableInventoryButton;
import net.plazmix.inventory.manager.BukkitInventoryManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.PlayerInventory;

public class SimpleInventoryListener implements Listener {

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Player player = ((Player) event.getWhoClicked());
        BaseInventory bukkitInventory = BukkitInventoryManager.INSTANCE.getOpenInventory(player);

        if (bukkitInventory == null) {
            return;
        }

        for (int rawSlot : event.getRawSlots()) {
            BaseInventoryButton inventoryButton = bukkitInventory.getButtons().get(rawSlot + 1);

            if (!(inventoryButton instanceof DraggableInventoryButton)) {
                return;
            }

            event.setCancelled(true);

            ((DraggableInventoryButton) inventoryButton).getButtonAction().buttonDrag(player, event);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = ((Player) event.getWhoClicked());
        BaseInventory bukkitInventory = BukkitInventoryManager.INSTANCE.getOpenInventory(player);

        if (bukkitInventory == null || bukkitInventory.getBukkitInventory() instanceof PlayerInventory) {
            return;
        }

        BaseInventoryButton inventoryButton = bukkitInventory.getButtons().get(event.getSlot() + 1);

        //вдруг это OriginalItem? лучше на всякий отменить клик уж)
        event.setCancelled(true);

        if (!(inventoryButton instanceof ActionInventoryButton)) {
            return;
        }

        ((ActionInventoryButton) inventoryButton).getButtonAction().buttonClick(player, event);
    }


    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Player player = ((Player) event.getPlayer());
        BaseInventory bukkitInventory = BukkitInventoryManager.INSTANCE.getOpenInventory(player);

        if (bukkitInventory == null) {
            return;
        }

        bukkitInventory.onOpen(player);
    }


    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = ((Player) event.getPlayer());
        BaseInventory bukkitInventory = BukkitInventoryManager.INSTANCE.getOpenInventory(player);

        if (bukkitInventory == null) {
            return;
        }

        if (bukkitInventory.getInventoryUpdater() != null && bukkitInventory.getInventoryUpdater().isEnable()) {
            bukkitInventory.getInventoryUpdater().cancelUpdater();
        }

        BukkitInventoryManager.INSTANCE.removeOpenInventoryToPlayer(player);

        bukkitInventory.onClose(player);
    }

}
