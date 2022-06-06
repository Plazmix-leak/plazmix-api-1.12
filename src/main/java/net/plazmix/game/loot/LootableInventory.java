package net.plazmix.game.loot;

import gnu.trove.map.TObjectDoubleMap;
import lombok.NonNull;
import net.plazmix.inventory.impl.BaseSimpleInventory;
import net.plazmix.utility.CollectionUtil;
import net.plazmix.utility.NumberUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Comparator;

public final class LootableInventory extends BaseSimpleInventory {

    private final @NonNull TObjectDoubleMap<ItemStack> itemChanceMap;

    public LootableInventory(int inventoryRows, String inventoryTitle, @NonNull TObjectDoubleMap<ItemStack> itemChanceMap) {
        super(inventoryTitle, inventoryRows);

        this.itemChanceMap = itemChanceMap;
    }

    public LootableInventory(int inventoryRows, @NonNull TObjectDoubleMap<ItemStack> itemChanceMap) {
        this (inventoryRows, "Lootable Inventory", itemChanceMap);
    }

    @Override
    public void drawInventory(Player player) {
        int inventoryItemCount = NumberUtil.randomInt(inventoryRows, inventoryRows * 3);

        for (int index = 0; index < inventoryItemCount; index++) {
            int inventorySlot = NumberUtil.randomInt(1, inventoryRows * 9);

            setClickItem(inventorySlot, getRandomItemStack(), (player1, event) -> event.setCancelled(false));
        }
    }

    private boolean hasItem(ItemStack itemStack) {
        return buttons.valueCollection().stream().anyMatch(baseInventoryButton ->

                CollectionUtil.containsElements(
                        Arrays.asList(itemStack.getType().name().split("_")),
                        Arrays.asList(baseInventoryButton.getItemStack().getType().name().split("_"))
                ));
    }

    private ItemStack getRandomItemStack() {
        double randomChance = NumberUtil.randomDouble(1, 100);

        ItemStack itemStack = itemChanceMap.keySet().stream()

                .sorted(Comparator.comparing(item -> Math.abs(itemChanceMap.get(item) - randomChance)))
                .skip((long) (itemChanceMap.size() * Math.random()))

                .findFirst()
                .orElse(null);

        if (itemStack == null || hasItem(itemStack)) {
            return getRandomItemStack();
        }

        return itemStack;
    }

}
