package net.plazmix.game.team;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.plazmix.game.GameCache;
import net.plazmix.game.event.GameTeamDestroyEvent;
import net.plazmix.game.event.GameTeamPlayerAddEvent;
import net.plazmix.game.event.GameTeamPlayerRemoveEvent;
import net.plazmix.game.user.GameUser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Getter
public class GameTeam {

    private final int teamIndex;

    private final ChatColor chatColor;
    private final String teamName;

    private final GameCache cache = new GameCache();

    private final Map<String, GameUser> playerMap = new LinkedHashMap<>();


    /* ============================================== // DEFAULT TEAMS // ============================================== */

    public static final GameTeam DEFAULT_RED_TEAM = new GameTeam(1, ChatColor.RED, "Красные");
    public static final GameTeam DEFAULT_ORANGE_TEAM = new GameTeam(2, ChatColor.GOLD, "Оранжевые");
    public static final GameTeam DEFAULT_YELLOW_TEAM = new GameTeam(3, ChatColor.YELLOW, "Желтые");
    public static final GameTeam DEFAULT_GREEN_TEAM = new GameTeam(4, ChatColor.GREEN, "Лаймовые");
    public static final GameTeam DEFAULT_DARK_GREEN_TEAM = new GameTeam(5, ChatColor.DARK_GREEN, "Зеленые");
    public static final GameTeam DEFAULT_AQUA_TEAM = new GameTeam(6, ChatColor.AQUA, "Голубые");
    public static final GameTeam DEFAULT_DARK_AQUA_TEAM = new GameTeam(7, ChatColor.DARK_AQUA, "Бирюзовые");
    public static final GameTeam DEFAULT_BLUE_TEAM = new GameTeam(8, ChatColor.BLUE, "Синий");
    public static final GameTeam DEFAULT_PINK_TEAM = new GameTeam(9, ChatColor.LIGHT_PURPLE, "Розовые");
    public static final GameTeam DEFAULT_PURPLE_TEAM = new GameTeam(10, ChatColor.DARK_PURPLE, "Фиолетовые");
    public static final GameTeam DEFAULT_WHITE_TEAM = new GameTeam(11, ChatColor.WHITE, "Белые");
    public static final GameTeam DEFAULT_GRAY_TEAM = new GameTeam(12, ChatColor.GRAY, "Серые");

    private static final GameTeam[] DEFAULT_TEAMS = {
            DEFAULT_RED_TEAM, DEFAULT_ORANGE_TEAM, DEFAULT_YELLOW_TEAM, DEFAULT_GREEN_TEAM,
            DEFAULT_DARK_GREEN_TEAM, DEFAULT_AQUA_TEAM, DEFAULT_DARK_AQUA_TEAM, DEFAULT_BLUE_TEAM,
            DEFAULT_PINK_TEAM, DEFAULT_PURPLE_TEAM, DEFAULT_WHITE_TEAM, DEFAULT_GRAY_TEAM
    };

    public static GameTeam[] getDefaultTeams() {
        return DEFAULT_TEAMS;
    }

    public static Optional<GameTeam> getDefault(ChatColor color) {
        return Arrays.stream(DEFAULT_TEAMS).filter(defTeam -> defTeam.getChatColor() == color).findFirst();
    }

    /* ============================================== // DEFAULT TEAMS // ============================================== */


    public int getPlayersCount() {
        return playerMap.size();
    }

    public Collection<GameUser> getPlayers() {
        return playerMap.values();
    }

    /**
     * Добавить игрока в команду
     *
     * @param gameUser - игровой пользователь,
     *                 которого нужно добавить в команду
     */
    public void addPlayer(@NonNull GameUser gameUser) {
        GameTeamPlayerAddEvent teamPlayerAddEvent = new GameTeamPlayerAddEvent(gameUser, this);
        Bukkit.getPluginManager().callEvent(teamPlayerAddEvent);

        if (teamPlayerAddEvent.isCancelled()) {
            return;
        }

        playerMap.put(gameUser.getName().toLowerCase(), gameUser);
        gameUser.setCurrentTeam(this);
    }

    /**
     * Добавить игрока в команду
     *
     * @param player - Bukkit игрок,
     *               которого нужно добавить в команду
     */
    public void addPlayer(@NonNull Player player) {
        addPlayer(GameUser.from(player));
    }

    /**
     * Добавить игрока в команду
     *
     * @param playerName - ник игрока,
     *                   которого нужно добавить в команду
     */
    public void addPlayer(@NonNull String playerName) {
        addPlayer(GameUser.from(playerName));
    }


    /**
     * Удалить игрока из команды
     *
     * @param gameUser - игровой пользователь,
     *                 которого нужно удалить из команды
     */
    public void removePlayer(@NonNull GameUser gameUser) {
        GameTeamPlayerRemoveEvent teamPlayerRemoveEvent = new GameTeamPlayerRemoveEvent(gameUser, this);
        Bukkit.getPluginManager().callEvent(teamPlayerRemoveEvent);

        if (teamPlayerRemoveEvent.isCancelled()) {
            return;
        }

        playerMap.remove(gameUser.getName().toLowerCase());
        gameUser.setCurrentTeam(null);

        if (playerMap.isEmpty()) {
            Bukkit.getPluginManager().callEvent(new GameTeamDestroyEvent(this));
        }
    }

    /**
     * Удалить игрока из команды
     *
     * @param player - Bukkit игрок,
     *               которого нужно удалить из команды
     */
    public void removePlayer(@NonNull Player player) {
        removePlayer(GameUser.from(player));
    }

    /**
     * Удалить игрока из команды
     *
     * @param playerName - ник игрока,
     *                   которого нужно удалить из команды
     */
    public void removePlayer(@NonNull String playerName) {
        removePlayer(GameUser.from(playerName));
    }


    /**
     * Проверить наличие игрока в команде
     *
     * @param gameUser - игровой пользователь,
     *                 наличие которого нужно проверить
     */
    public boolean hasPlayer(@NonNull GameUser gameUser) {
        return playerMap.containsKey(gameUser.getName().toLowerCase());
    }

    /**
     * Проверить наличие игрока в команде
     *
     * @param player - Bukkit игрок,
     *               наличие которого нужно проверить
     */
    public boolean hasPlayer(@NonNull Player player) {
        return hasPlayer(GameUser.from(player));
    }

    /**
     * Проверить наличие игрока в команде
     *
     * @param playerName - ник игрока,
     *                   наличие которого нужно проверить
     */
    public boolean hasPlayer(@NonNull String playerName) {
        return hasPlayer(GameUser.from(playerName));
    }

    public void handleBroadcast(@NonNull Consumer<GameUser> broadcastConsumer) {

        for (GameUser gameUser : playerMap.values()) {
            broadcastConsumer.accept(gameUser);
        }
    }

}
