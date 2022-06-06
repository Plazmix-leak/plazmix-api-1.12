package net.plazmix.game.utility.hotbar;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class GameHotbar {

    private static final Map<Player, GameHotbar> playerHotbarsMap = new HashMap<>();

    public static GameHotbar getPlayerHotbar(@NonNull Player player) {
        return playerHotbarsMap.get(player);
    }

    public static GameHotbar create() {
        return new GameHotbar();
    }


    private final TIntObjectMap<GameHotbarItem> itemsMap = new TIntObjectHashMap<>();
    private final Map<Player, PlayerInventory> previousPlayerInventoryMap = new HashMap<>();

    @Getter
    @Setter
    private boolean moveItems;
    @Getter
    @Setter
    private boolean interactionAllowed;


    public void addItem(int hotbarSlot, @NonNull GameHotbarItem hotbarItem) {
        itemsMap.put(hotbarSlot, hotbarItem);
    }

    public GameHotbarItem getHotbarItem(int hotbarSlot) {
        return itemsMap.get(hotbarSlot);
    }


    public boolean handleClick(@NonNull Player player) {
        int hotbarSlot = player.getInventory().getHeldItemSlot();

        GameHotbarItem hotbarItem = getHotbarItem(hotbarSlot + 1);

        if (hotbarItem == null) {
            hotbarItem = itemsMap.valueCollection()
                    .stream()
                    .filter(item -> (player.getInventory().getItemInMainHand() != null && player.getInventory().getItemInMainHand().getType() != Material.AIR) || (player.getInventory().getItemInOffHand() != null && player.getInventory().getItemInOffHand().getType() != Material.AIR))
                    .filter(item -> player.getInventory().getItem(hotbarSlot).equals(item.getItemStack()))
                    .findFirst()
                    .orElse(null);
        }

        if (hotbarItem != null) {
            hotbarItem.handleClick(player);

            return true;
        }

        return false;
    }

    public void setHotbarTo(@NonNull Player player) {
        playerHotbarsMap.put(player, this);

        previousPlayerInventoryMap.put(player, player.getInventory());
        player.getInventory().clear();

        itemsMap.valueCollection().forEach(hotbarItem -> hotbarItem.handleGive(player));
    }

    public void removeHotbar(@NonNull Player player, boolean clearInventory) {
        playerHotbarsMap.remove(player);
        PlayerInventory previousInventory = previousPlayerInventoryMap.get(player);

        if (clearInventory) {
            player.getInventory().clear();
        }

        if (previousInventory == null) {
            return;
        }

        int itemSlot = 0;
        for (ItemStack itemStack : previousInventory.getContents()) {

            player.getInventory().setItem(itemSlot, itemStack);

            itemSlot++;
        }
    }

    public void removeHotbar(@NonNull Player player) {
        removeHotbar(player, true);
    }

}
