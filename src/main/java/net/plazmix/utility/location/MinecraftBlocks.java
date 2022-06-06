package net.plazmix.utility.location;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.plazmix.utility.ItemUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@UtilityClass
public class MinecraftBlocks {

    public BlockFace getEmptyRelative(@NonNull Block block, boolean checkObliquely) {
        return getEmptyRelative(block, 1, checkObliquely);
    }

    public BlockFace getEmptyRelative(@NonNull Block block, int distance, boolean checkObliquely) {
        return getAvailableRelativeByType(block, ItemUtil.EMPTY_ITEM_TYPE, distance, checkObliquely);
    }

    public BlockFace getEmptyRelative(@NonNull Block block, int distance) {
        return getAvailableRelativeByType(block, ItemUtil.EMPTY_ITEM_TYPE, distance, true);
    }

    public BlockFace getEmptyRelative(@NonNull Block block) {
        return getAvailableRelativeByType(block, ItemUtil.EMPTY_ITEM_TYPE, 1, true);
    }

    public BlockFace getAvailableRelativeByType(@NonNull Block block, @NonNull Material material,
                                                int distance, boolean checkObliquely) {

        Collection<BlockFace> blockFaceCollection = !checkObliquely ? Arrays.asList(BlockFace.UP, BlockFace.NORTH, BlockFace.EAST,
                BlockFace.SOUTH, BlockFace.WEST, BlockFace.DOWN) : Arrays.asList(BlockFace.values());

        for (BlockFace blockFace : blockFaceCollection) {
            if (!block.getRelative(blockFace, distance).getType().equals(material)) {
                continue;
            }

            return blockFace;
        }

        return BlockFace.SELF;
    }

    public BlockFace getAvailableRelativeByType(@NonNull Block block, @NonNull Material material,
                                                int distance) {

        return getAvailableRelativeByType(block, material, distance, true);
    }

    public BlockFace getAvailableRelativeByType(@NonNull Block block, @NonNull Material material) {

        return getAvailableRelativeByType(block, material, 1, true);
    }

    public Collection<Location> find(@NonNull Location startLocation, @NonNull Material... typesToFind) {
        List<Material> typesList = Arrays.asList(typesToFind);

        Collection<Location> locationsCollection = new ArrayList<>();
        Collection<Location> currentLoopLocations = new ArrayList<>();

        currentLoopLocations.add(startLocation);

        while (true) {

            Location[] previousLoopLocations = currentLoopLocations.toArray(new Location[0]);
            currentLoopLocations.clear();

            // Подсчитываем количество добавленных блоков
            int blockCounter = 0;

            for (Location blockLocation : previousLoopLocations) {
                for (BlockFace blockFace : BlockFace.values()) {

                    if (blockFace.equals(BlockFace.SELF))
                        continue;

                    Block relativeBlock = blockLocation.getBlock().getRelative(blockFace);
                    if (locationsCollection.contains(relativeBlock.getLocation()))
                        continue;

                    if (typesList.contains(relativeBlock.getType())) {

                        locationsCollection.add(relativeBlock.getLocation());
                        currentLoopLocations.add(relativeBlock.getLocation());

                        blockCounter++;
                    }
                }
            }

            // Если есть блоки, которые были добавленны в данном цикле,
            // то пойдем искать дальнейшие блоки
            if (blockCounter == 0) {
                break;
            }
        }

        return locationsCollection;
    }

}
