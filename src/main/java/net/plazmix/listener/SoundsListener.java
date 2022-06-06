package net.plazmix.listener;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemHeldEvent;

public class SoundsListener implements Listener {

    @Setter
    @Getter
    private static boolean enable = true;

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!isEnable() || event.getClick() == null || event.getCurrentItem() == null || event.getClickedInventory() == null) {
            return;
        }

        if (event.getClickedInventory().getLocation() != null && !event.getClickedInventory().getLocation().getBlock().isEmpty()) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        if (event.getClickedInventory().getType() != null && !event.getClickedInventory().getType().equals(InventoryType.PLAYER) && event.getCurrentItem().getTypeId() > 0) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BELL, 1, 2);
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!isEnable()) {
            return;
        }

        if (event.getInventory().getLocation() != null && !event.getInventory().getLocation().getBlock().isEmpty()) {
            return;
        }

        Player player = (Player) event.getPlayer();
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BELL, 1, 1);
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        if (!isEnable()) {
            return;
        }

        Player player = event.getPlayer();
        player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 0.3f, 2);
    }

}
