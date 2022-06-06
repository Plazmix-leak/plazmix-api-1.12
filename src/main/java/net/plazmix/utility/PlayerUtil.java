package net.plazmix.utility;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.plazmix.PlazmixApiPlugin;
import net.plazmix.coreconnector.core.group.Group;
import net.plazmix.coreconnector.core.language.LanguageType;
import net.plazmix.game.utility.hotbar.GameHotbar;
import net.plazmix.protocollib.entity.FakeEntity;
import net.plazmix.protocollib.entity.FakeEntityRegistry;
import net.plazmix.protocollib.entity.FakeEntityScope;
import net.plazmix.scoreboard.BaseScoreboard;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.logging.Level;

@UtilityClass
public class PlayerUtil {

    public synchronized Player getOnlineRandom() {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();

        return onlinePlayers.stream().skip((long) (onlinePlayers.size() * Math.random()))
                .findFirst()
                .orElse(null);
    }

    public synchronized Player getOnlineRandom(@NonNull Predicate<Player> filter) {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();

        return onlinePlayers.stream().skip((long) (onlinePlayers.size() * Math.random()))
                .filter(filter)
                .findFirst()
                .orElse(null);
    }

    public synchronized String getDisplayName(@NonNull Player player) {
        return PlazmixUser.of(player).getDisplayName();
    }

    public synchronized Group getGroup(@NonNull Player player) {
        return PlazmixUser.of(player).getGroup();
    }

    public synchronized LanguageType getLanguage(@NonNull Player player) {
        return PlazmixUser.of(player).getLanguage();
    }

    public synchronized void redirect(@NonNull Player player, @NonNull String serverName) {
        ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput();

        dataOutput.writeUTF("ConnectOther");
        dataOutput.writeUTF(player.getName());
        dataOutput.writeUTF(serverName);

        Bukkit.getServer().sendPluginMessage(PlazmixApiPlugin.getPlugin(PlazmixApiPlugin.class), "BungeeCord", dataOutput.toByteArray());
    }

    public synchronized long getOfflinePlayerStatistic(OfflinePlayer player, Statistic statistic) {
        File worldFolder = new File(Bukkit.getServer().getWorlds().get(0).getWorldFolder(), "stats");
        File playerStatistics = new File(worldFolder, player.getUniqueId().toString() + ".json");

        if (playerStatistics.exists()) {

            JSONParser parser = new JSONParser();
            JSONObject jsonObject = null;

            try {
                jsonObject = (JSONObject) parser.parse(new FileReader(playerStatistics));
            } catch (IOException | ParseException e) {
                Bukkit.getLogger().log(Level.WARNING, "Falha ao ler o arquivo de estatisticas do jogador " + player.getName(), e);
            }

            StringBuilder statisticNmsName = new StringBuilder("stat.");

            for (char character : statistic.name().toCharArray()) {
                if (statisticNmsName.charAt(statisticNmsName.length() - 1) == '_') {
                    statisticNmsName.setCharAt(statisticNmsName.length() - 1, Character.toUpperCase(character));
                } else {
                    statisticNmsName.append(Character.toLowerCase(character));
                }
            }

            if (Objects.requireNonNull(jsonObject).containsKey(statisticNmsName.toString())) {
                return (long) jsonObject.get(statisticNmsName.toString());

            } else {

                return 0;
            }
        }

        return 0;
    }

    public void resetPlayer(@NonNull Player player) {
        player.closeInventory();
        player.setGameMode(Bukkit.getServer().getDefaultGameMode());

        // Hotbars.
        if (GameHotbar.getPlayerHotbar(player) != null) {
            GameHotbar.getPlayerHotbar(player).removeHotbar(player);
        }

        // Show all players
        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {

            otherPlayer.hidePlayer(player);
            otherPlayer.showPlayer(player);

            player.hidePlayer(otherPlayer);
            player.showPlayer(otherPlayer);
        }

        // Clear PotionEffects
        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(potionEffect.getType());
        }

        // Clear PlayerInventory
        player.getInventory().clear();

        // Clear Scoreboards
        BaseScoreboard.getCachedPlayerScoreboard(player)
                .forEach(baseScoreboard -> baseScoreboard.removeScoreboardToPlayer(player));

        // Reset levels
        player.setLevel(0);
        player.setExp(0);

        // Reset health & food
        player.setMaxHealth(20);
        player.setHealth(player.getMaxHealth());

        player.setFoodLevel(20);

        // Reset player tickers
        player.setFireTicks(0);
        player.setNoDamageTicks(0);

        player.resetTitle();
        player.resetPlayerTime();
        player.resetPlayerWeather();
        player.resetMaxHealth();

        // Remove fake entities
        for (FakeEntity fakeEntity : FakeEntityRegistry.INSTANCE.getReceivableEntities(player)) {
            if (fakeEntity.getEntityScope().equals(FakeEntityScope.PUBLIC)) {
                continue;
            }

            fakeEntity.removeReceivers(player);
        }

        for (FakeEntity fakeEntity : FakeEntityRegistry.INSTANCE.getViewableEntities(player)) {
            if (fakeEntity.getEntityScope().equals(FakeEntityScope.PUBLIC)) {
                continue;
            }

            fakeEntity.removeViewers(player);
        }

        try {
            MinecraftReflection.getCraftPlayerClass().getMethod("updateScaledHealth").invoke(player);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ignored) { }
    }
}
