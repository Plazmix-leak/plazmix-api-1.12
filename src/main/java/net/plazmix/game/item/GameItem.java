package net.plazmix.game.item;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.plazmix.game.event.GameItemApplyEvent;
import net.plazmix.game.event.GameItemCancelEvent;
import net.plazmix.game.item.parameter.BasedGameItemParameter;
import net.plazmix.game.user.GameUser;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.LinkedList;

@Getter
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class GameItem implements Listener {

    @NonNull
    int id;

    @NonNull
    GameItemPrice price;

    @NonNull
    String itemName;

    @NonNull
    ItemStack iconItem;


    @Setter(AccessLevel.PACKAGE)
    @NonNull
    GameItemsCategory itemCategory;

    @Setter
    @NonNull
    Collection<String> description = new LinkedList<>();

    @Setter
    @NonNull
    GameItemParameter parameter;


    public GameItem(@NonNull int id,
                    @NonNull GameItemPrice price,

                    @NonNull String itemName,
                    @NonNull ItemStack iconItem,

                    GameItemsCategory itemCategory) {

        this.id = id;
        this.price = price;

        this.itemName = itemName;

        this.iconItem = iconItem;
        this.itemCategory = itemCategory;

        if (itemCategory != null) {
            itemCategory.addItem(this);
        }

        setParameter(new BasedGameItemParameter(this));
    }

    public GameItem(@NonNull int id,
                    @NonNull GameItemPrice price,

                    @NonNull String itemName,
                    @NonNull ItemStack iconItem) {

        this(id, price, itemName, iconItem, null);
    }


    protected void onApply(@NonNull GameUser gameUser) {
    }

    protected void onCancel(@NonNull GameUser gameUser) {
    }

    public void onSelect(@NonNull GameUser gameUser) {
    }


    public void applyItem(@NonNull GameUser gameUser) {
        Bukkit.getPluginManager().callEvent(new GameItemApplyEvent(gameUser, this));

        if (parameter != null) {
            parameter.onDefaultApply(gameUser);
        }

        onApply(gameUser);
    }

    public void cancelItem(@NonNull GameUser gameUser) {
        Bukkit.getPluginManager().callEvent(new GameItemCancelEvent(gameUser, this));

        if (parameter != null) {
            parameter.onDefaultCancel(gameUser);
        }

        onCancel(gameUser);
    }

    public boolean isSimilar(@NonNull ItemStack itemStack) {
        if (itemStack.isSimilar(iconItem) || iconItem.isSimilar(itemStack))
            return true;

        return itemStack.getType().equals(iconItem.getType()) && itemStack.getDurability() == iconItem.getDurability()
                && itemStack.getAmount() == iconItem.getAmount();
    }

}
