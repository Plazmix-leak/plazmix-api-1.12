package net.plazmix.game.utility.worldreset;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class GameWorldReset {

    private final Map<Location, MaterialData> BLOCK_CACHE_MAP = new HashMap<>();

    public void addBlockCache(@NonNull Block block, @NonNull MaterialData previousBlockData) {
        BLOCK_CACHE_MAP.put(block.getLocation(), previousBlockData);
    }

    public void removeBlockCache(@NonNull Block block) {
        BLOCK_CACHE_MAP.remove(block.getLocation());
    }

    public MaterialData getPreviousBlockData(@NonNull Location location) {
        return BLOCK_CACHE_MAP.get(location);
    }


    public void resetWorld(@NonNull World world) {
        new HashMap<>(BLOCK_CACHE_MAP).forEach((location, materialData) -> {

            if (!location.getWorld().getName().equalsIgnoreCase(world.getName()))
                return;

            location.getBlock().setType(materialData.getItemType());
            location.getBlock().setData(materialData.getData());

            BLOCK_CACHE_MAP.remove(location);
        });
    }

    public void resetAllWorlds() {
        for (World world : Bukkit.getWorlds())
            resetWorld(world);

        BLOCK_CACHE_MAP.clear();
    }


    public boolean hasBlock(@NonNull Block block) {
        return BLOCK_CACHE_MAP.containsKey(block.getLocation());
    }

    public boolean hasBlock(@NonNull Location location) {
        return BLOCK_CACHE_MAP.containsKey(location);
    }

}
