package net.plazmix.game.utility.hotbar;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

public final class GameHotbarListener
        implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        GameHotbar gameHotbar = GameHotbar.getPlayerHotbar(player);

        if (gameHotbar != null) {
            gameHotbar.removeHotbar(player);
        }
    }

    @EventHandler
    public void onMoveItems(InventoryClickEvent event) {
        Player player = ((Player) event.getWhoClicked());
        Inventory inventory = event.getClickedInventory();

        if (inventory == null) {
            return;
        }

        if (!inventory.getType().equals(InventoryType.PLAYER)) {
            return;
        }

        GameHotbar gameHotbar = GameHotbar.getPlayerHotbar(player);

        if (gameHotbar == null) {
            return;
        }

        if (gameHotbar.getHotbarItem(event.getSlot() + 1) == null) {
            return;
        }

        event.setCancelled(!gameHotbar.isMoveItems());
    }

    @EventHandler
    public void onMoveItems(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        GameHotbar gameHotbar = GameHotbar.getPlayerHotbar(player);

        if (gameHotbar == null) {
            return;
        }

        if (gameHotbar.getHotbarItem(player.getInventory().getHeldItemSlot() + 1) == null) {
            return;
        }

        event.setCancelled(!gameHotbar.isMoveItems());
    }

    @EventHandler
    public void onHotbarClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!event.hasItem()) {
            return;
        }

        GameHotbar gameHotbar = GameHotbar.getPlayerHotbar(player);

        if (gameHotbar == null) {
            return;
        }

        if (gameHotbar.handleClick(player)) {
            event.setCancelled(!gameHotbar.isInteractionAllowed());
        }
    }

}
