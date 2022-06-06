package net.plazmix.game.item;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.plazmix.game.GamePlugin;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

@Getter
@RequiredArgsConstructor
public abstract class GameItemsCategory {

    protected final int id;
    protected final int inventorySlot;

    protected final String categoryName;
    protected final ItemStack iconStack;

    protected final TIntObjectMap<GameItem> gameItemsMap = new TIntObjectHashMap<>();

    @Setter
    protected Collection<String> description = new LinkedList<>();

// =================================================== // CATEGORY FACTORY // =================================================== //


    public void addItem(@NonNull GameItem gameItem) {
        gameItem.setItemCategory(this);
        gameItemsMap.put(gameItem.getId(), gameItem);

        // Для подгрузки базы данных предмета
        if (gameItem.getParameter() != null) {
            gameItem.getParameter().getDatabase();
        }

        Bukkit.getPluginManager().registerEvents(gameItem, GamePlugin.getInstance());
    }

    public GameItem getItem(int itemId) {
        return gameItemsMap.get(itemId);
    }

    public Collection<GameItem> getMappedItems() {
        return Collections.unmodifiableCollection(gameItemsMap.valueCollection());
    }

    public int getMappedItemsCount() {
        return gameItemsMap.size();
    }

}
