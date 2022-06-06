package net.plazmix.game.utility.hotbar;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

@RequiredArgsConstructor
@Getter
public class GameHotbarItem {

    public static GameHotbarItem create(int inventorySlot, @NonNull ItemStack itemStack, @NonNull Consumer<Player> clickAction) {
        return new GameHotbarItem(inventorySlot, itemStack, clickAction);
    }

    public static GameHotbarItem create(int inventorySlot, @NonNull ItemStack itemStack) {
        return create(inventorySlot, itemStack, player -> {});
    }


    private final int inventorySlot;
    private final ItemStack itemStack;

    private final Consumer<Player> clickAction;


    public void handleGive(@NonNull Player player) {
        player.getInventory().setItem(inventorySlot - 1, itemStack);
    }

    public void handleClick(@NonNull Player player) {
        if (!player.getInventory().getItemInHand().equals(itemStack))
            return;

        clickAction.accept(player);
    }

}
