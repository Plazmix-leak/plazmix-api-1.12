package net.plazmix.inventory.button.action.impl;

import net.plazmix.inventory.button.BaseInventoryButton;
import net.plazmix.inventory.button.action.EnumButtonAction;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;

public interface DraggableButtonAction extends BaseInventoryButton.IInventoryButtonAction {

    /**
     * Произвести действие кнопки на предмет
     *
     * @param player - игрок
     * @param event  - ивент
     */
    void buttonDrag(Player player, InventoryDragEvent event);


    @Override
    default void buttonAction(Player player, EnumButtonAction buttonAction, InventoryInteractEvent event) {
        buttonDrag(player, (InventoryDragEvent) event);
    }

}