package net.plazmix.utility.custom;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.plazmix.utility.ItemUtil;
import net.plazmix.utility.MetadataUtil;
import net.plazmix.utility.PercentUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Getter
@Deprecated
public abstract class CustomBlock {

    public static final Map<ItemStack, CustomBlock> CUSTOM_BLOCK_MAP = new HashMap<>();

    public static CustomBlock toCustomBlock(@NonNull Block bukkitBlock) {
        if (!MetadataUtil.hasMetadata(bukkitBlock, "custom")) {
            return null;
        }

        return MetadataUtil.getMetadata(bukkitBlock, "custom", CustomBlock.class);
    }

    public static CustomBlock valueOf(@NonNull ItemStack itemStack) {
        return CUSTOM_BLOCK_MAP.get(itemStack);
    }


    protected final String displayName;

    protected final Material material;

    protected final int chancePercent;
    protected final int durability;

    protected final Map<ItemStack, Integer> itemDropCollection = new HashMap<>();


// ================================================== // Передающиеся методы // ================================================ //

    public abstract void onBlockCreate(@NonNull Location location);

// ============================================== // Переопределяющиеся методы // ============================================== //

    public void onPlayerPlace(@NonNull BlockPlaceEvent blockPlaceEvent) { }
    public void onPlayerBreak(@NonNull BlockBreakEvent blockBreakEvent) { }

    public void onCustomPlace(@NonNull Location location) { }
    public void onCustomBreak(@NonNull Location location) { }

    public void onDropItem(@NonNull Location location, @NonNull ItemStack itemStack) { }

    public void onPlayerInteract(@NonNull Player player, @NonNull Action action, Location location,
                                 @NonNull PlayerInteractEvent event) { }

// ============================================================================================================================= //

    public ItemStack toItemStack() {
        return ItemUtil.newBuilder(material)
                .setDurability(durability)

                .setName(displayName)
                .build();
    }

    public void addItemDrop(@NonNull ItemStack itemStack, int dropPercent) {
        if (dropPercent > 100) {
            return;
        }

        itemDropCollection.put(itemStack, dropPercent);
    }

    public void placeBlock(@NonNull Location location) {
        onBlockCreate(location);

        location.getBlock().setTypeIdAndData(material.getId(), (byte) durability, true);
        MetadataUtil.setMetadata(location.getBlock(), "custom", this);

        onCustomPlace(location);
    }

    public void breakBlock(@NonNull Location location) {
        drop(location);

        location.getBlock().setType(Material.AIR);
        location.getWorld().playSound(location, Sound.BLOCK_STONE_BREAK, 1, 1);

        onCustomBreak(location);
    }

    public final void drop(@NonNull Location location) {
        itemDropCollection.forEach((itemStack, percent) -> {

            if (!PercentUtil.acceptRandomPercent(percent)) {
                return;
            }

            location.getWorld().dropItemNaturally(location, itemStack);
            onDropItem(location, itemStack);
        });
    }

    public void register() {
        CUSTOM_BLOCK_MAP.put(this.toItemStack(), this);
    }

}
