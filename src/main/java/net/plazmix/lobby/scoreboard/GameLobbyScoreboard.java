package net.plazmix.lobby.scoreboard;

import lombok.NonNull;
import net.plazmix.coreconnector.CoreConnector;
import net.plazmix.coreconnector.mysql.MysqlDatabaseConnection;
import net.plazmix.scoreboard.BaseScoreboard;
import net.plazmix.scoreboard.BaseScoreboardBuilder;
import net.plazmix.scoreboard.BaseScoreboardScope;
import net.plazmix.scoreboard.animation.ScoreboardDisplayCustomAnimation;
import net.plazmix.utility.NumberUtil;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GameLobbyScoreboard {

    public static final String GET_STATS_VALUE_QUERY = "SELECT * FROM `%s` WHERE `Id`=?";
    public static final String PLACEHOLDER_SEPARATOR = "%";


    private final Map<String, Integer> placeholdersTempMap = new HashMap<>();

    public GameLobbyScoreboard(@NonNull MysqlDatabaseConnection databaseConnection,
                               @NonNull Player player, @NonNull String localizationKeyPrefix) {

        if (!player.isOnline()) {
            return;
        }

        PlazmixUser plazmixUser = PlazmixUser.of(player);

        Collection<String> displayNamesCollection = plazmixUser.localization().getLocalizationResource().getTextList(localizationKeyPrefix + "_BOARD_TITLE");
        Collection<String> displayLinesCollection = plazmixUser.localization().getLocalizationResource().getTextList(localizationKeyPrefix + "_BOARD_LINES");


        ScoreboardDisplayCustomAnimation displayAnimation = new ScoreboardDisplayCustomAnimation();

        for (String displayName : displayNamesCollection) {
            displayAnimation.addTextToAnimation(displayName);
        }

        // Создаем сам scoreboard
        BaseScoreboard baseScoreboard = BaseScoreboardBuilder.newScoreboardBuilder()
                .scoreboardScope(BaseScoreboardScope.PROTOTYPE)
                .scoreboardDisplay(displayAnimation)

                .scoreboardLine(displayLinesCollection.toArray(new String[0]))

                .scoreboardUpdater((scoreboard, player1) -> {

                    int lineCounter = displayLinesCollection.size();
                    for (String scoreboardLine : displayLinesCollection) {

                        if (scoreboardLine.contains(PLACEHOLDER_SEPARATOR)) {

                            String[] placeholdersData = scoreboardLine.split("\\" + PLACEHOLDER_SEPARATOR);
                            for (int i = 1; i < placeholdersData.length; i += 2) {

                                String placeholder = (PLACEHOLDER_SEPARATOR + placeholdersData[i] + PLACEHOLDER_SEPARATOR);
                                String[] placeholderSqlData = placeholder.substring(1, placeholder.length() - 1).split("_");

                                if (placeholderSqlData.length == 2) {

                                    String table = placeholderSqlData[0];
                                    String column = placeholderSqlData[1];

                                    // Будем кешировать данные тупа
                                    Integer columnValue = placeholdersTempMap.get(table + column);
                                    if (columnValue == null) {

                                        columnValue = databaseConnection.executeQuery(false, String.format(GET_STATS_VALUE_QUERY, table),
                                                resultSet -> resultSet.getInt(column), plazmixUser.getPlayerId());

                                        placeholdersTempMap.put(table + column, columnValue);
                                    }

                                    // и выводить их в строчку
                                    scoreboardLine = scoreboardLine.replace(placeholder, String.valueOf(columnValue));
                                }
                            }
                        }

                        scoreboard.updateScoreboardLine(lineCounter, player1, scoreboardLine
                                .replace("%coins%", NumberUtil.spaced(PlazmixUser.of(player1).getCoins()))
                                .replace("%golds%", NumberUtil.spaced(PlazmixUser.of(player1).getGolds()))

                                .replace("%online%", NumberUtil.spaced(CoreConnector.getInstance().getGlobalOnline()))
                                .replace("%server%", CoreConnector.getInstance().getServerName())
                        );

                        lineCounter--;
                    }

                }, 60).build();

        baseScoreboard.setScoreboardToPlayer(player);
    }
}
