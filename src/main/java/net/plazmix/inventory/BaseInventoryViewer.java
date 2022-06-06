package net.plazmix.inventory;

import org.bukkit.entity.Player;

// в разработке...
public interface BaseInventoryViewer {

    Player getViewPlayer();

    BaseInventory getInventory();
}
