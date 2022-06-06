package net.plazmix.spacepass;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.plazmix.coreconnector.CoreConnector;
import net.plazmix.coreconnector.module.type.NetworkModule;
import org.bukkit.entity.Player;

import java.sql.Timestamp;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SpacePassSqlHandler {

    public static final SpacePassSqlHandler INSTANCE
            = new SpacePassSqlHandler();

    public static final String SELECT_PASS_QUERY            = "SELECT * FROM `PlayerPass` WHERE `Id`=?";
    public static final String INSERT_PASS_QUERY            = "INSERT INTO `PlayerPass` VALUES (?, ?, ?, ?)";
    public static final String PURCHASE_ACTIVATION_QUERY    = "UPDATE `PlayerPass` SET `Activation`=? WHERE `Id`=?";
    public static final String ADD_EXP_PASS_QUERY           = "UPDATE `PlayerPass` SET `Experience`=(`Experience`+?) WHERE `Id`=?";

    private final TIntObjectMap<SpacePass> playerPassesMap = new TIntObjectHashMap<>();

    public void cleanDatabase() {
        try {

            if (System.currentTimeMillis() > SpacePass.END_SEASON_MILLIS) {
                CoreConnector.getInstance().getMysqlConnection().execute(true, "DELETE FROM `PlayerPass`");
            }

        } catch (Exception ignored) {
        }
    }

    public void purchaseActivation(int playerId) {
        CoreConnector.getInstance().getMysqlConnection().execute(true, PURCHASE_ACTIVATION_QUERY, true, playerId);
    }

    public SpacePass getPlayerPass(int playerId) {
        if (playerId < 0) {
            return null;
        }

        SpacePass pass = playerPassesMap.get(playerId);

        if (pass == null) {
            pass = CoreConnector.getInstance().getMysqlConnection().executeQuery(false, SELECT_PASS_QUERY, (resultSet) -> {

                if (resultSet.next()) {
                    return new SpacePass(playerId, resultSet.getTimestamp("Date"), resultSet.getInt("Experience"), resultSet.getBoolean("Activation"));
                }

                SpacePass sqlPass = new SpacePass(playerId, new Timestamp(System.currentTimeMillis()), 0, false);

                CoreConnector.getInstance().getMysqlConnection().execute(true, INSERT_PASS_QUERY,
                        playerId, sqlPass.getPurchaseDate(), sqlPass.getExperience(), sqlPass.isActivated());

                return sqlPass;
            }, playerId);

            playerPassesMap.put(playerId, pass);
        }

        return pass;
    }

    public SpacePass getPlayerPass(@NonNull String playerName) {
        return getPlayerPass(NetworkModule.getInstance().getPlayerId(playerName));
    }

    public SpacePass getPlayerPass(@NonNull Player player) {
        return getPlayerPass(player.getName());
    }

}
