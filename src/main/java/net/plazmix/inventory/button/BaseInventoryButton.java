package net.plazmix.inventory.button;

import net.plazmix.inventory.button.action.EnumButtonAction;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.ItemStack;

public interface BaseInventoryButton {

    /**
     * Получить объект предмета
     */
    ItemStack getItemStack();

    /**
     * Получить кликабельность объекта
     *
     * Если ее нет, то метод будет
     * возвращать null
     */
    IInventoryButtonAction getButtonAction();


    interface IInventoryButtonAction {

        /**
         * Произвести клик по кнопки
         *
         * @param player       - игрок, который кликает
         * @param buttonAction - действие клика
         * @param event        - ивент, когда происходит взаимодействие с прелметом
         */
        void buttonAction(Player player, EnumButtonAction buttonAction, InventoryInteractEvent event);
    }

}
