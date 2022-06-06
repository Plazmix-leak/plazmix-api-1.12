package net.plazmix.inventory.button.action.impl;

import net.plazmix.inventory.button.BaseInventoryButton;
import net.plazmix.inventory.button.action.EnumButtonAction;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;

public interface ClickableButtonAction extends BaseInventoryButton.IInventoryButtonAction {

    /**
     * Произвести действие кнопки на предмет
     *
     * @param player - игрок
     * @param event  - ивент
     */
    void buttonClick(Player player, InventoryClickEvent event);


    @Override
    default void buttonAction(Player player, EnumButtonAction buttonAction, InventoryInteractEvent event) {
        buttonClick(player, (InventoryClickEvent) event);
    }

}