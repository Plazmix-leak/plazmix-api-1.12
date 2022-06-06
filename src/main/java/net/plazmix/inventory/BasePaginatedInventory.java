package net.plazmix.inventory;

import net.plazmix.inventory.addon.BasePaginatedInventorySorting;
import net.plazmix.inventory.button.BaseInventoryButton;
import net.plazmix.inventory.button.action.impl.ClickableButtonAction;
import net.plazmix.inventory.button.action.impl.DraggableButtonAction;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface BasePaginatedInventory extends BaseInventory {

    List<BaseInventoryButton> getPageButtons();


    BasePaginatedInventorySorting getInventorySort();

    void setInventorySort(BasePaginatedInventorySorting inventorySort);


    /**
     * Добавить предмет в разметку страницы
     *
     * @param inventoryButton - предмет
     */
    void addItemToMarkup(BaseInventoryButton inventoryButton);

    /**
     * Добавить предмет в разметку страницы
     *
     * @param itemStack - предмет
     */
    void addOriginalItemToMarkup(ItemStack itemStack);

    /**
     * Добавить предмет в разметку страницы
     *
     * @param itemStack - предмет
     */
    void addClickItemToMarkup(ItemStack itemStack, ClickableButtonAction buttonAction);

    /**
     * Добавить предмет в разметку страницы
     *
     * @param itemStack - предмет
     */
    void addDragItemToMarkup(ItemStack itemStack, DraggableButtonAction buttonAction);


    /**
     * Установить количество используемых слотов на страницу
     *
     * @param slotArray - слоты
     */
    void setMarkupSlots(Integer... slotArray);

    /**
     * Установить количество используемых слотов на страницу
     *
     * @param slotList - слоты
     */
    void setMarkupSlots(List<Integer> slotList);

    /**
     * Добавить линию в разметку страницы
     *
     * @param rowIndex    - индекс линии
     * @param sideTab     - отступ по слотам для боков линии
     */
    void addRowToMarkup(int rowIndex, int sideTab);


    /**
     * На страницу назад
     *
     * @param player - игрок
     */
    void backwardPage(Player player);

    /**
     * На страницу вперед
     *
     * @param player - игрок
     * @param allPagesCount - всего страниц
     */
    void forwardPage(Player player, int allPagesCount);
}
