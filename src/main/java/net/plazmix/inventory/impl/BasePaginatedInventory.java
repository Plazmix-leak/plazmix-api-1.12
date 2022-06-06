package net.plazmix.inventory.impl;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;
import lombok.NonNull;
import net.plazmix.inventory.addon.BaseInventoryUpdater;
import net.plazmix.inventory.addon.BasePaginatedInventorySorting;
import net.plazmix.inventory.button.BaseInventoryButton;
import net.plazmix.inventory.button.action.impl.ClickableButtonAction;
import net.plazmix.inventory.button.action.impl.DraggableButtonAction;
import net.plazmix.inventory.button.impl.ActionInventoryButton;
import net.plazmix.inventory.button.impl.DraggableInventoryButton;
import net.plazmix.inventory.button.impl.SimpleInventoryButton;
import net.plazmix.inventory.manager.BukkitInventoryManager;
import net.plazmix.utility.ItemUtil;
import net.plazmix.utility.NumberUtil;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Getter
public abstract class BasePaginatedInventory implements net.plazmix.inventory.BasePaginatedInventory {

    protected String inventoryTitle;

    protected int inventoryRows;
    protected int inventorySize;

    protected int currentPage;


    private final TIntObjectMap<BaseInventoryButton> buttons = new TIntObjectHashMap<>();

    protected final List<Integer> pageSlots = new LinkedList<>();
    protected final List<BaseInventoryButton> pageButtons = new LinkedList<>();


    protected BaseInventoryUpdater inventoryUpdater;
    protected BasePaginatedInventorySorting inventorySort;


    protected Inventory bukkitInventory;


    /**
     * Инициализировать данные инвентаря
     *
     * @param inventoryTitle - название инвентаря
     * @param inventoryRows  - количество линий в разметке инвентаря
     */
    public BasePaginatedInventory(String inventoryTitle, int inventoryRows) {
        this.inventoryTitle = inventoryTitle;
        this.inventoryRows = inventoryRows;

        this.inventorySize = inventoryRows * 9;
    }

    @Override
    public void create(Player player, boolean inventoryInitialize) {
        if (inventoryInitialize) {
            this.bukkitInventory = Bukkit.createInventory(player, inventorySize, inventoryTitle + " | " + (currentPage + 1));
        }

        if (player == null) {
            return;
        }

        buildPaginatedInventory(player);

        buttons.forEachEntry((buttonSlot, inventoryButton) -> {
            bukkitInventory.setItem(buttonSlot - 1, inventoryButton.getItemStack());
            return true;
        });
    }

    @Override
    public void openInventory(@NonNull Player player) {
        player.closeInventory();

        create(player, true);

        BukkitInventoryManager.INSTANCE.addOpenInventoryToPlayer(player, this);
        player.openInventory(getBukkitInventory());
    }

    @Override
    public void updateInventory(@NonNull Player player) {
        create(player, false);

        //player.openInventory(getBukkitInventory());
    }

    @Override
    public void clearInventory(@NonNull Player player) {
        getButtons().clear();

        getBukkitInventory().clear();
    }

    @Override
    public void setInventoryTitle(@NonNull String inventoryTitle) {
        Validate.isTrue(inventoryTitle.length() <= 32, "inventory title length cannot be > 32");

        this.inventoryTitle = inventoryTitle;

        create(null, true);
    }

    @Override
    public void setInventoryRows(int inventoryRows) {
        Validate.isTrue(inventoryRows <= 6, "inventory rows length cannot be > 6");

        this.inventoryRows = inventoryRows;
        this.inventorySize = inventoryRows * 9;

        create(null, true);
    }

    @Override
    public void setInventorySize(int inventorySize) {
        Validate.isTrue(inventoryRows % 9 == 0, "Inventory must have a size that is a multiple of 9!");

        this.inventorySize = inventorySize;
        this.inventoryRows = inventorySize / 9;

        create(null, true);
    }

    @Override
    public void setItem(int buttonSlot, @NonNull BaseInventoryButton inventoryButton) {
        buttons.put(buttonSlot, inventoryButton);
    }

    @Override
    public void setOriginalItem(int buttonSlot,
                                ItemStack itemStack) {

        BaseInventoryButton inventoryButton = new SimpleInventoryButton(itemStack);

        setItem(buttonSlot, inventoryButton);
    }

    @Override
    public void setClickItem(int buttonSlot,
                             ItemStack itemStack,
                             ClickableButtonAction buttonAction) {

        BaseInventoryButton inventoryButton = new ActionInventoryButton(
                itemStack, buttonAction
        );

        setItem(buttonSlot, inventoryButton);
    }

    @Override
    public void setDragItem(int buttonSlot,
                            ItemStack itemStack,
                            DraggableButtonAction buttonAction) {

        BaseInventoryButton inventoryButton = new DraggableInventoryButton(
                itemStack, buttonAction
        );

        setItem(buttonSlot, inventoryButton);
    }

    @Override
    public void setInventoryUpdater(long updateTicks, @NonNull BaseInventoryUpdater inventoryUpdater) {
        //check old updater
        if (inventoryUpdater != null) {
            inventoryUpdater.cancelUpdater();
        }

        this.inventoryUpdater = inventoryUpdater;

        getInventoryUpdater().setEnable(true);
        getInventoryUpdater().startUpdater(updateTicks);
    }

    @Override
    public void setInventorySort(@NonNull BasePaginatedInventorySorting inventorySort) {
        this.inventorySort = inventorySort;

        inventorySort.rebuildInventory();
    }

    @Override
    public void addItemToMarkup(@NonNull BaseInventoryButton inventoryButton) {
        pageButtons.add(inventoryButton);
    }

    @Override
    public void addOriginalItemToMarkup(ItemStack itemStack) {
        BaseInventoryButton inventoryButton = new SimpleInventoryButton(itemStack);

        pageButtons.add(inventoryButton);
    }

    @Override
    public void addClickItemToMarkup(ItemStack itemStack, ClickableButtonAction buttonAction) {
        BaseInventoryButton inventoryButton = new ActionInventoryButton(
                itemStack, buttonAction
        );

        pageButtons.add(inventoryButton);
    }

    @Override
    public void addDragItemToMarkup(ItemStack itemStack, DraggableButtonAction buttonAction) {
        BaseInventoryButton inventoryButton = new DraggableInventoryButton(
                itemStack, buttonAction
        );

        pageButtons.add(inventoryButton);
    }

    @Override
    public void setMarkupSlots(@NonNull Integer... slotArray) {
        pageSlots.clear();

        Collections.addAll(pageSlots, slotArray);
    }

    @Override
    public void setMarkupSlots(List<Integer> slotList) {
        setMarkupSlots(slotList.toArray(new Integer[0]));
    }

    @Override
    public void addRowToMarkup(int rowIndex, int sideTab) {
        if (rowIndex <= 0) {
            throw new IllegalArgumentException("row index must be > 0");
        }

        if (rowIndex >= 7) {
            throw new IllegalArgumentException("row index must be < 6");
        }

        int startSlotIndex = (rowIndex * 9) - 8;
        int endSlotIndex = startSlotIndex + 9;

        if (sideTab < 0) {
            throw new IllegalArgumentException("side tab must be > 0");
        }

        startSlotIndex += sideTab;
        endSlotIndex -= sideTab;

        for (int slotIndex : NumberUtil.toManyArray(startSlotIndex, endSlotIndex)) {
            pageSlots.add(slotIndex);
        }
    }

    @Override
    public void backwardPage(@NonNull Player player) {
        if (currentPage - 1 < 0) {
            throw new RuntimeException(String.format("Page cannot be < 0 (%s - 1 < 0)", currentPage));
        }

        this.currentPage--;

        create(player, true);
        openInventory(player);
    }

    @Override
    public void forwardPage(@NonNull Player player, int allPagesCount) {
        if (currentPage >= allPagesCount) {
            throw new RuntimeException(String.format("Page cannot be >= max pages count (%s >= %s)", currentPage, allPagesCount));
        }

        this.currentPage++;

        create(player, true);
        openInventory(player);
    }

    @Override
    public void onOpen(Player player) { }

    @Override
    public void onClose(Player player) { }

    /**
     * Построение страничного инвентаря
     *
     * @param player - игрок, для которого построить меню
     */
    private void buildPaginatedInventory(@NonNull Player player) {
        pageSlots.clear();
        pageButtons.clear();

        clearInventory(player);
        drawInventory(player);

        int allPagesCount = (pageSlots.isEmpty() ? 0 : Math.max(0, pageButtons.size() - 1) / pageSlots.size());

        if (!pageButtons.isEmpty() && pageSlots.isEmpty())
            throw new IllegalArgumentException("no markup set");


        if (!(currentPage >= allPagesCount)) {
            setClickItem(inventorySize - 3, ItemUtil.newBuilder(Material.SPECTRAL_ARROW)
                            .setName("§eСледующая страница")
                            .addLore("§7Нажмите, чтобы открыть следующую страницу!")

                            .build(),

                    (clickedPlayer, event) -> forwardPage(clickedPlayer, allPagesCount));
        }

        if (!(currentPage - 1 < 0)) {
            setClickItem(inventorySize - 5, ItemUtil.newBuilder(Material.SPECTRAL_ARROW)
                            .setName("§eПредыдущая страница")
                            .addLore("§7Нажмите, чтобы открыть предыдущую страницу!")

                            .build(),

                    (clickedPlayer, event) -> backwardPage(clickedPlayer));
        }

        for (int i = 0; i < pageSlots.size(); i++) {
            int itemIndex = currentPage * pageSlots.size() + i;

            if (pageButtons.size() <= itemIndex) {
                break;
            }

            int buttonSlot = pageSlots.get(i);

            setItem(buttonSlot, pageButtons.get(itemIndex));
        }
    }

}
