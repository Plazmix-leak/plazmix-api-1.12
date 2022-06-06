package net.plazmix.game.mysql.type;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.NonNull;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.item.GameItem;
import net.plazmix.game.mysql.GameMysqlDatabase;
import net.plazmix.game.mysql.RemoteDatabaseRowType;
import net.plazmix.game.user.GameUser;

import java.util.List;

public class UpgradeGameItemsMysqlDatabase extends GameMysqlDatabase {

    public UpgradeGameItemsMysqlDatabase(String tablePrefix) {
        super(tablePrefix + "_Items", false);
    }

    @Override
    public void initialize() {
        addColumn("CategoryID", RemoteDatabaseRowType.INT);
        addColumn("ItemID", RemoteDatabaseRowType.INT);
        addColumn("Upgrade", RemoteDatabaseRowType.INT);
        addColumn("State", RemoteDatabaseRowType.BOOLEAN);
    }

    @Override
    public void onJoinLoad(@NonNull GamePlugin plugin, @NonNull GameUser gameUser) {
        loadAll(true, gameUser, resultSet -> {
            Multimap<Integer, GameItem> gameItemsMap = ArrayListMultimap.create();

            while (resultSet.next()) {
                boolean isSelected = resultSet.getBoolean("State");

                int upgradeLevel = resultSet.getInt("Upgrade");
                int categoryId = resultSet.getInt("CategoryID");
                int itemId = resultSet.getInt("ItemID");

                GameItem gameItem = plugin.getService().getItemsCategory(categoryId)
                        .getItem(itemId);

                if (gameItem == null) {
                    continue;
                }

                if (isSelected) {
                    List<GameItem> selectedItems = gameUser.getSelectedItems(categoryId);
                    selectedItems.add(gameItem);

                    gameUser.getCache().set("SItems" + categoryId, selectedItems);
                }

                gameUser.getCache().set("UItemsUpgrade" + categoryId + itemId, upgradeLevel);
                gameItemsMap.put(categoryId, gameItem);
            }

            gameItemsMap.keySet().forEach(categoryId ->
                    gameUser.getCache().set("BItems" + categoryId, gameItemsMap.get(categoryId)));
        });
    }

}
