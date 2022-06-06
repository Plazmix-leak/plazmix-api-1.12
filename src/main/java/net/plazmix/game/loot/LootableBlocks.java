package net.plazmix.game.loot;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.plazmix.utility.MetadataUtil;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

@UtilityClass
public class LootableBlocks {

    private final String LOOTABLE_METADATA = "lootable_block_inventory";


    public void setLootableBlock(@NonNull Block block, @NonNull LootableInventory lootableInventory) {
        MetadataUtil.setMetadata(block, LOOTABLE_METADATA, lootableInventory);
    }

    public boolean isLootableBlock(@NonNull Block block) {
        return MetadataUtil.hasMetadata(block, LOOTABLE_METADATA);
    }

    public void openLootableInventory(@NonNull Block block, @NonNull Player player) {
        if (!isLootableBlock(block))
            return;

        LootableInventory lootableInventory = MetadataUtil.getMetadata(block, LOOTABLE_METADATA, LootableInventory.class);
        lootableInventory.openInventory(player);
    }

}
