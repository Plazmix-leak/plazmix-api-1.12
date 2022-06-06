package net.plazmix.inventory;

import net.plazmix.inventory.addon.BaseInventoryUpdater;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface BaseInventory extends BaseInventoryContainer {

    /**
     * Открыть инвентарь игроку
     *
     * @param player - игрок
     */
    void openInventory(Player player);

    /**
     * Отрисовать инвентарь для игрока
     *
     * @param player - игрок
     */
    void drawInventory(Player player);

    /**
     * Обновить инвентарь для игрока
     *
     * @param player - игрока
     */
    void updateInventory(Player player);

    /**
     * Очистить инвентарь
     *
     * @param player - игрок
     */
    void clearInventory(Player player);

    /**
     * Инициализировать и создать
     * все предметы в инвентаре
     *
     * @param player - игрок
     * @param inventoryInitialize - инициализировать ли инвентарь
     */
    void create(Player player, boolean inventoryInitialize);

    /**
     * Включить автообновление инвентаря
     *
     * @param updateTicks - период обновления
     * @param inventoryUpdater - что будет происходить при обновлении
     */
    void setInventoryUpdater(long updateTicks, BaseInventoryUpdater inventoryUpdater);

    /**
     * Получить объект баккитовского инвентаря
     *  (говно параша, но вдруг нада))0)))0)))
     */
    Inventory getBukkitInventory();


    /**
     * Получить менеджер автообноления инвентаря
     */
    BaseInventoryUpdater getInventoryUpdater();


    /**
     * Выполняется при открытии инвентаря игроку
     *
     * @param player - игрок
     */
    void onOpen(Player player);

    /**
     * Выполняется при закрытии инвентаря
     *
     * @param player - игрок
     */
    void onClose(Player player);

}
