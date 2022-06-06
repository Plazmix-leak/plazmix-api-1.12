package net.plazmix.lobby.playertop.database.type;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import lombok.RequiredArgsConstructor;
import net.plazmix.coreconnector.core.network.NetworkManager;
import net.plazmix.lobby.playertop.database.PlayerTopsDatabase;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PlayerTopsCacheDatabase implements PlayerTopsDatabase {

    private final Function<PlazmixUser, Integer> playerFunction;

    @Override
    public TIntIntMap find(int limit) {
        TIntIntMap playerTopsByIds = new TIntIntHashMap();

        for (OfflinePlayer offlinePlayer : Arrays.stream(Bukkit.getOfflinePlayers())
                .limit(limit)
                .collect(Collectors.toSet())) {

            int playerId = NetworkManager.INSTANCE.getPlayerId(offlinePlayer.getName());
            playerTopsByIds.put(playerId, playerFunction.apply(PlazmixUser.of(playerId)));
        }

        int standsCount = playerTopsByIds.size();
        if (standsCount < limit) {

            for (int i = 1 ; i <= (limit - standsCount); i++)
                playerTopsByIds.put(-i, 0);
        }

        return playerTopsByIds;
    }

}
