package net.plazmix.game.utility.hotbar;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameHotbarBuilder {

    public static GameHotbarBuilder newBuilder() {
        return new GameHotbarBuilder();
    }


    private final GameHotbar gameHotbar = new GameHotbar();

    public GameHotbarBuilder setMoveItems(boolean moveItems) {
        gameHotbar.setMoveItems(moveItems);

        return this;
    }

    public GameHotbarBuilder setAllowInteraction(boolean allowInteraction) {
        gameHotbar.setInteractionAllowed(allowInteraction);

        return this;
    }


    public GameHotbarBuilder addItem(@NonNull GameHotbarItem hotbarItem) {
        gameHotbar.addItem(hotbarItem.getInventorySlot(), hotbarItem);

        return this;
    }

    public GameHotbarBuilder addItem(int inventorySlot, @NonNull ItemStack itemStack) {
        return addItem(GameHotbarItem.create(inventorySlot, itemStack));
    }

    public GameHotbarBuilder addItem(int inventorySlot, @NonNull ItemStack itemStack, @NonNull Consumer<Player> clickAction) {
        return addItem(GameHotbarItem.create(inventorySlot, itemStack, clickAction));
    }


    public GameHotbar build() {
        return gameHotbar;
    }

}
