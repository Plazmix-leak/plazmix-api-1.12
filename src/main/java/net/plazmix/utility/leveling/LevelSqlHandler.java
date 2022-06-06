package net.plazmix.utility.leveling;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.plazmix.coreconnector.CoreConnector;
import net.plazmix.coreconnector.core.network.NetworkManager;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LevelSqlHandler {

    public static final String INSERT_EXPERIENCE_QUERY  = "INSERT INTO `PlayerLeveling` VALUES (?,?) ON DUPLICATE KEY UPDATE `Experience`=?";
    public static final String GET_EXPERIENCE_QUERY     = "SELECT * FROM `PlayerLeveling` WHERE `Id`=?";

    public static final LevelSqlHandler INSTANCE = new LevelSqlHandler();


    public final TIntIntMap playerExperienceMap = new TIntIntHashMap();

    public int getPlayerExp(int playerId) {
        if (playerExperienceMap.containsKey(playerId)) {
            return playerExperienceMap.get(playerId);
        }

        int playerExperience = CoreConnector.getInstance().getMysqlConnection().executeQuery(false, GET_EXPERIENCE_QUERY, resultSet -> {

            if (!resultSet.next()) {
                CoreConnector.getInstance().getMysqlConnection().execute(true, INSERT_EXPERIENCE_QUERY, playerId, 0, 0);

                return 0;
            }

            return resultSet.getInt("Experience");
        }, playerId);

        playerExperienceMap.put(playerId, playerExperience);
        return playerExperience;
    }

    public int getPlayerExp(@NonNull String playerName) {
        int playerId = NetworkManager.INSTANCE.getPlayerId(playerName);
        return getPlayerExp(playerId);
    }


    public void setPlayerExp(int playerId, int experience) {
        playerExperienceMap.put(playerId, experience);

        CoreConnector.getInstance().getMysqlConnection().execute(true, INSERT_EXPERIENCE_QUERY, playerId, experience, experience);
    }

    public void setPlayerExp(@NonNull String playerName, int experience) {
        int playerId = NetworkManager.INSTANCE.getPlayerId(playerName);

        setPlayerExp(playerId, experience);
    }


    public void addPlayerExp(int playerId, int experience) {
        setPlayerExp(playerId, getPlayerExp(playerId) + experience);
    }

    public void addPlayerExp(@NonNull String playerName, int experience) {
        int playerId = NetworkManager.INSTANCE.getPlayerId(playerName);

        addPlayerExp(playerId, experience);
    }


    public void removePlayerExp(int playerId, int experience) {
        setPlayerExp(playerId, getPlayerExp(playerId) - experience);
    }

    public void removePlayerExp(@NonNull String playerName, int experience) {
        int playerId = NetworkManager.INSTANCE.getPlayerId(playerName);

        addPlayerExp(playerId, experience);
    }

}
