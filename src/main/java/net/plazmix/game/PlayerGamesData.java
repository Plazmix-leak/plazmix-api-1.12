package net.plazmix.game;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import net.plazmix.coreconnector.mysql.MysqlDatabaseConnection;
import net.plazmix.utility.JsonUtil;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class PlayerGamesData {

    public static PlayerGamesData create(@NonNull String game, @NonNull String map, @NonNull String server) {
        return new PlayerGamesData(game, map, server);
    }

    String game;
    String map;
    String server;

    @NonFinal String winner;

    @NonFinal long startTimeMillis;
    @NonFinal long endTimeMillis;

    @NonFinal List<Integer> playersIdsList;


    public boolean canInsert() {
        return playersIdsList != null && !playersIdsList.isEmpty() && endTimeMillis > 0 && startTimeMillis > 0 && winner != null && game != null && map != null && server != null;
    }

    public void insert(@NonNull MysqlDatabaseConnection connection) {
        connection.execute(true, "INSERT INTO `PlayerGamesData` VALUES (?, ?, ?, ?, ?, ?, ?)",
                game, new Timestamp(startTimeMillis), new Timestamp(endTimeMillis), map, server, JsonUtil.toJson(playersIdsList), winner);
    }

}
