package net.plazmix.inventory.manager;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.plazmix.inventory.BaseInventory;
import net.plazmix.inventory.addon.BaseInventoryUpdater;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BukkitInventoryManager {

    public static final BukkitInventoryManager INSTANCE = new BukkitInventoryManager();


    @Getter
    private final Map<String, BaseInventory> playerInventoryMap = new HashMap<>();

    @Getter
    private final Map<BaseInventoryUpdater, Long> inventoryUpdaterMap = new HashMap<>();


    /**
     * Получить открытый самописный инвентарь игрока
     *
     * @param playerName - ник игрока
     */
    public BaseInventory getOpenInventory(String playerName) {
        return playerInventoryMap.get(playerName.toLowerCase());
    }

    /**
     * Получить открытый самописный инвентарь игрока
     *
     * @param player - игрок
     */
    public BaseInventory getOpenInventory(Player player) {
        return getOpenInventory(player.getName());
    }


    public void addOpenInventoryToPlayer(String playerName, BaseInventory bukkitInventory) {
        playerInventoryMap.put(playerName.toLowerCase(), bukkitInventory);
    }

    public void addOpenInventoryToPlayer(Player player, BaseInventory bukkitInventory) {
        addOpenInventoryToPlayer(player.getName(), bukkitInventory);
    }


    public void removeOpenInventoryToPlayer(String playerName) {
        playerInventoryMap.remove(playerName.toLowerCase());
    }

    public void removeOpenInventoryToPlayer(Player player) {
        removeOpenInventoryToPlayer(player.getName());
    }


    public void addInventoryUpdater(BaseInventoryUpdater inventoryUpdater, long periodTicks) {
        inventoryUpdaterMap.put(inventoryUpdater, periodTicks);
    }

    public void removeInventoryUpdater(BaseInventoryUpdater inventoryUpdater) {
        inventoryUpdaterMap.remove(inventoryUpdater);
    }

    public void startInventoryUpdaters(@NonNull Plugin plugin) {
        new BukkitRunnable() {

            private long currentTicks = 1;

            @Override
            public void run() {
                inventoryUpdaterMap.forEach((inventoryUpdater, periodTicks) -> {
                    if (currentTicks % periodTicks != 0) {
                        return;
                    }

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        BaseInventory playerInventory = getOpenInventory(player);

                        if (playerInventory != null && playerInventory.getInventoryUpdater().equals(inventoryUpdater)) {
                            inventoryUpdater.applyRunnable(player);
                        }
                    }
                });

                currentTicks++;
            }

        }.runTaskTimer(plugin, 1, 1);
    }

}
