package net.plazmix.lobby.playertop.database.type;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import lombok.RequiredArgsConstructor;
import net.plazmix.coreconnector.CoreConnector;
import net.plazmix.lobby.playertop.database.PlayerTopsDatabase;

@RequiredArgsConstructor
public class PlayerTopsMysqlDatabase implements PlayerTopsDatabase {

    private final String mysqlTable;
    private final String tableColumn;

    @Override
    public TIntIntMap find(int limit) {
        TIntIntMap playerTopsByIds = new TIntIntHashMap();

        CoreConnector.getInstance().getMysqlConnection().executeQuery(false,
                String.format("SELECT * FROM `%s` ORDER BY `%s` DESC LIMIT %s", mysqlTable, tableColumn, limit),
                resultSet -> {

                    while (resultSet.next()) {

                        int playerId = resultSet.getInt("Id");
                        int playerValue = resultSet.getInt(tableColumn);

                        playerTopsByIds.put(playerId, playerValue);
                    }

                    return null;
                });

        int standsCount = playerTopsByIds.size();
        if (standsCount < limit) {

            for (int i = 1 ; i <= (limit - standsCount); i++)
                playerTopsByIds.put(-i, 0);
        }

        return playerTopsByIds;
    }
}
