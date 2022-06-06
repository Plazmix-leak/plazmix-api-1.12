package net.plazmix.schematic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import net.plazmix.utility.JsonUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

@Data
@AllArgsConstructor
public class BaseSchematicBlock {

    private Vector offset;
    private MaterialData materialData;


    public Location getBaseLocation(@NonNull Location originLocation) {
        return originLocation.getBlock().getLocation().clone().add(offset.getBlockX() + 1, offset.getBlockY(), offset.getBlockZ() + 1);
    }

    public Block getBlock(@NonNull Location originLocation) {
        return getBaseLocation(originLocation).getBlock();
    }

    public boolean isEmpty(@NonNull Location originLocation) {
        return getBlock(originLocation).isEmpty();
    }

    public boolean isLiquid(@NonNull Location originLocation) {
        return getBlock(originLocation).isLiquid();
    }

    public void update(@NonNull Location originLocation) {
        Block block = getBlock(originLocation);

        block.setType(materialData.getItemType());
        block.setData(materialData.getData());

        block.getState().update();
    }


    @Override
    public String toString() {
        String offsetData = (offset.getX() + ", " + offset.getY() + ", " + offset.getZ());
        String stateData = JsonUtil.toJson(materialData);

        return offsetData + BaseSchematic.SCHEM_BLOCK_DATA_SPLITTER + stateData;
    }
}
