package net.plazmix.utility.achievement;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.plazmix.coreconnector.CoreConnector;
import net.plazmix.utility.JsonUtil;

import java.sql.ResultSet;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AchievementTask {

    public static final String SELECT_QUERY = "SELECT * FROM `PlayerAchievements` WHERE `Id`=? AND `AchievementId`=? AND `TaskId`=?";
    public static final String UPDATE_QUERY = "UPDATE `PlayerAchievements` SET `TaskJson` WHERE `Id`=? AND `AchievementId`=? AND `TaskId`=?";


    int questID;

    int taskID;

    private AchievementTaskData getPlayerData(int playerId) {
        return CoreConnector.getInstance()
                .getMysqlConnection().executeQuery(false, SELECT_QUERY,
                        resultSet -> {

                            if (!resultSet.next()) {
                                return null;
                            }

                            return JsonUtil.fromJson(resultSet.getString("TaskJson"), AchievementTaskData.class);

                        }, playerId, questID, taskID);
    }

    public boolean hasPlayerData(int playerId) {
        return CoreConnector.getInstance()
                .getMysqlConnection().executeQuery(false, SELECT_QUERY,
                        ResultSet::next, playerId, questID, taskID);
    }

    public void change(int playerId, int newProgressValue) {
        AchievementTaskData achievementTaskData = getPlayerData(playerId);

        if (achievementTaskData == null) {
            return;
        }

        CoreConnector.getInstance().getMysqlConnection().execute(true, UPDATE_QUERY,
                achievementTaskData.toJson(), playerId, questID, taskID);

        achievementTaskData.setProgress(newProgressValue);
    }

    public void increment(int playerId) {
        change(playerId, getPlayerData(playerId).getProgress() + 1);
    }
}