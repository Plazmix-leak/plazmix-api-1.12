package net.plazmix.inventory.addon;

import net.plazmix.inventory.BasePaginatedInventory;
import net.plazmix.utility.query.ResponseHandler;
import org.bukkit.inventory.ItemStack;

public interface BasePaginatedInventorySorting {

    /**
     * Получить инвентарь, в котором сортируем
     * выставленные в разметку предметы
     */
    BasePaginatedInventory getInventory();


    /**
     * Отсортировать предмет, получив его
     * индекс в списке предметов
     *
     * @param responseHandler - функция сортировки
     */
    BasePaginatedInventorySorting sortItem(ResponseHandler<Integer, ItemStack> responseHandler);

    /**
     * Перевернуть список отсортированных предметов,
     * выставив их таким образом, чтобы в инвентаре
     * они показывались от большего к меньшему
     */
    BasePaginatedInventorySorting reversed();


    /**
     * Перестраиваем положение предметов
     */
    void rebuildInventory();

}
