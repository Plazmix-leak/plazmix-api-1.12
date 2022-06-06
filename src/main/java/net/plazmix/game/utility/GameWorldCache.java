package net.plazmix.game.utility;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.plazmix.game.GameCache;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class GameWorldCache extends GameCache {

    private static final Map<String, GameWorldCache> WORLD_CACHE_MAP
            = new HashMap<>();

    public static GameWorldCache fromWorld(@NonNull World world) {
        return WORLD_CACHE_MAP.computeIfAbsent(world.getName().toLowerCase(), f -> new GameWorldCache(world));
    }

    public static GameWorldCache fromLocation(@NonNull Location location) {
        return fromWorld(location.getWorld());
    }

    public static GameWorldCache fromBlock(@NonNull Block block) {
        return fromWorld(block.getWorld());
    }

    public static GameWorldCache fromEntity(@NonNull Entity entity) {
        return fromWorld(entity.getWorld());
    }

    private final World world;

}
