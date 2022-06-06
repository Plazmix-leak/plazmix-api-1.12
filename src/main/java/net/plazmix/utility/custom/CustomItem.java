package net.plazmix.utility.custom;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.plazmix.utility.ItemUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Deprecated
public abstract class CustomItem {

    @Getter
    private final ItemStack itemStack;


    public abstract void onInteract(@NonNull Player player, @NonNull Action mouseAction, Location location,
                                    @NonNull PlayerInteractEvent event);

    public void onPickup(@NonNull PlayerPickupItemEvent event) { }
    public void onDrop(@NonNull PlayerDropItemEvent event) { }

    public void onBlockBreak(@NonNull BlockBreakEvent event) { }
    public void onBlockPlace(@NonNull BlockPlaceEvent event) { }

    public void onBowShoot(@NonNull EntityShootBowEvent event) { }

    public static final Map<ItemStack, CustomItem> CUSTOM_ITEM_MAP = new HashMap<>();

    public static CustomItem of(ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }

        for (CustomItem customItem : CUSTOM_ITEM_MAP.values()) {
            ItemStack customItemStack = customItem.itemStack;

            if (itemStack.getType().name().contains("POTION") || itemStack.getType().name().contains("ARROW")) {
                if (customItemStack.getType().equals(itemStack.getType()) && customItemStack.getEnchantments().equals(itemStack.getEnchantments())
                        && (customItemStack.hasItemMeta() && itemStack.hasItemMeta()
                        && (customItemStack.getItemMeta().hasDisplayName() && itemStack.getItemMeta().hasDisplayName() && customItemStack.getItemMeta().getDisplayName().equals(itemStack.getItemMeta().getDisplayName()))
                        && (customItemStack.getItemMeta().hasLore() && itemStack.getItemMeta().hasLore() && customItemStack.getItemMeta().getLore().equals(itemStack.getItemMeta().getLore())))) {

                    return customItem;
                }

            } else {

                if (ItemUtil.matchMeta(customItemStack, itemStack) && ItemUtil.matchType(customItemStack, itemStack)) {
                    return customItem;
                }
            }
        }

        return null;
    }


    public void give(@NonNull Player player) {
        player.getInventory().addItem(itemStack);
    }

    public void register() {
        CUSTOM_ITEM_MAP.put(itemStack, this);
    }

}
