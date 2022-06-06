package net.plazmix.game.team;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.ChatColor;

public final class GameTeamManager {

    @Getter
    private final TIntObjectMap<GameTeam> loadedTeamMap = new TIntObjectHashMap<>();
    private final Multimap<ChatColor, Integer> internalColorIdMap = HashMultimap.create();

    /**
     * Получить загруженную игровую команду
     * по ее ID
     *
     * @param teamId - id команды
     */
    public GameTeam getTeam(int teamId) {
        return loadedTeamMap.get(teamId);
    }

    /**
     * Получить все загруженные игровые команды
     * по цвету
     *
     * @param color - цвет команды
     */
    public GameTeam[] getTeamsByColor(ChatColor color) {
        if (!internalColorIdMap.containsKey(color)) {
            return null;
        }

        return internalColorIdMap.get(color).stream().map(this::getTeam).toArray(GameTeam[]::new);
    }

    /**
     * Создать и загрузить игровую команду
     * по ее цвету
     *
     * @param teamId - id игровой команды
     * @param gameTeam - игровая команда
     */
    public void registerTeam(int teamId, @NonNull GameTeam gameTeam) {
        loadedTeamMap.put(teamId, gameTeam);
    }

    /**
     * Получить количество загруженных
     * игровых команд
     *
     * @return - количество игровых команд
     */
    public int getMappedTeamsCount() {
        return loadedTeamMap.size();
    }

}
