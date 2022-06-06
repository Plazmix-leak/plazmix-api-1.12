package net.plazmix.utility.quest;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.plazmix.coreconnector.CoreConnector;
import net.plazmix.utility.JsonUtil;

import java.sql.ResultSet;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class QuestTask {

    public static final String SELECT_QUERY = "SELECT * FROM `PlayerQuests` WHERE `Id`=? AND `QuestId`=? AND `TaskId`=?";
    public static final String UPDATE_QUERY = "UPDATE `PlayerQuests` SET `TaskJson` WHERE `Id`=? AND `QuestId`=? AND `TaskId`=?";


    int questID;

    int taskID;

    private QuestTaskData getPlayerData(int playerId) {
        return CoreConnector.getInstance()
                .getMysqlConnection().executeQuery(false, SELECT_QUERY,
                        resultSet -> {

                            if (!resultSet.next()) {
                                return null;
                            }

                            return JsonUtil.fromJson(resultSet.getString("TaskJson"), QuestTaskData.class);

                        }, playerId, questID, taskID);
    }

    public boolean hasPlayerData(int playerId) {
        return CoreConnector.getInstance()
                .getMysqlConnection().executeQuery(false, SELECT_QUERY,
                        ResultSet::next, playerId, questID, taskID);
    }

    public void change(int playerId, int newProgressValue) {
        QuestTaskData questTaskData = getPlayerData(playerId);

        if (questTaskData == null) {
            return;
        }

        CoreConnector.getInstance().getMysqlConnection().execute(true, UPDATE_QUERY,
                questTaskData.toJson(), playerId, questID, taskID);

        questTaskData.setProgress(newProgressValue);
    }

    public void increment(int playerId) {
        change(playerId, getPlayerData(playerId).getProgress() + 1);
    }
}