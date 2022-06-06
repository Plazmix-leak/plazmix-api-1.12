package net.plazmix.utility.player;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.plazmix.coreconnector.CoreConnector;
import net.plazmix.coreconnector.core.group.Group;
import net.plazmix.coreconnector.core.group.GroupManager;
import net.plazmix.coreconnector.core.language.LanguageManager;
import net.plazmix.coreconnector.core.language.LanguageType;
import net.plazmix.coreconnector.core.network.NetworkManager;
import net.plazmix.coreconnector.module.BaseModuleManager;
import net.plazmix.coreconnector.module.type.NetworkModule;
import net.plazmix.coreconnector.module.type.coloredprefix.ColoredPrefixModule;
import net.plazmix.coreconnector.utility.localization.LocalizedPlayer;
import net.plazmix.coreconnector.utility.server.ServerMode;
import net.plazmix.economy.EconomyMode;
import net.plazmix.economy.service.ModeEconomyService;
import net.plazmix.economy.service.PlazmaEconomyService;
import net.plazmix.event.PlazmixExperienceChangeEvent;
import net.plazmix.event.PlazmixLevelChangeEvent;
import net.plazmix.skin.PlayerSkinManager;
import net.plazmix.spacepass.SpacePass;
import net.plazmix.spacepass.SpacePassSqlHandler;
import net.plazmix.utility.leveling.LevelSqlHandler;
import net.plazmix.utility.leveling.LevelingUtil;
import net.plazmix.utility.map.MultikeyHashMap;
import net.plazmix.utility.map.MultikeyMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class PlazmixUser {

// ================================================================================================================== //

    public static final MultikeyMap<PlazmixUser> PLAZMIX_PLAYER_MAP = new MultikeyHashMap<PlazmixUser>()

            .register(String.class, plazmixUser -> plazmixUser.getName().toLowerCase())
            .register(int.class,    PlazmixUser::getPlayerId);


    public static PlazmixUser of(@NonNull String playerName) {
        return PLAZMIX_PLAYER_MAP.computeIfAbsent(String.class, playerName, () -> new PlazmixUser(playerName));
    }

    public static PlazmixUser of(int playerId) {
        return PLAZMIX_PLAYER_MAP.computeIfAbsent(int.class, playerId, () -> new PlazmixUser(NetworkManager.INSTANCE.getPlayerName(playerId)));
    }

    public static PlazmixUser of(@NonNull Player player) {
        return of(player.getName());
    }

// ================================================================================================================== //

    @Getter
    private final Table<String, String, Object> databasesValuesCacheTable = HashBasedTable.create();

    @Getter
    private final String name;


    public String getCurrentSkin() {
        return PlayerSkinManager.INSTANCE.getCurrentPlayerSkin(name).getSkinName();
    }

    public String getPrefix() {
        if (getGroup() == null) {
            return Group.DEFAULT.getPrefix() + (getPass().isActivated() ? "✬" : "");
        }

        ColoredPrefixModule prefixModule = BaseModuleManager.INSTANCE.find(ColoredPrefixModule.class);

        ChatColor prefixColor = prefixModule.getPrefixColor(getPlayerId());
        String prefix = prefixColor == null ? getGroup().getPrefix() : prefixModule.format(prefixColor, getGroup());

        return prefix + (getGroup().isDefault() ? "" : " ") + (getPass().isActivated() ? "✬" : "");
    }

    public String getDisplayName() {
        return getPrefix() + name + (getGroup() != null ? getGroup().getSuffix() : Group.DEFAULT.getSuffix());
    }

    public int getPlayerId() {
        return NetworkModule.getInstance().getPlayerId(name);
    }


    public int getCoins() {
        try {
            EconomyMode economyMode = EconomyMode.valueOf(ServerMode.getMode(CoreConnector.getInstance().getServerName()).name());
            return ModeEconomyService.init(economyMode).get(this.name);
        }
        catch (IllegalArgumentException exception) {
            return -1;
        }
    }

    public boolean hasCoins(int coins) {
        return getCoins() >= coins;
    }

    public void addCoins(int coins) {
        try {
            EconomyMode economyMode = EconomyMode.valueOf(ServerMode.getMode(CoreConnector.getInstance().getServerName()).name());
            ModeEconomyService.init(economyMode).add(this.name, coins);
        }
        catch (IllegalArgumentException ignored) {
        }
    }

    public void removeCoins(int coins) {
        try {
            EconomyMode economyMode = EconomyMode.valueOf(ServerMode.getMode(CoreConnector.getInstance().getServerName()).name());
            ModeEconomyService.init(economyMode).take(this.name, coins);
        }
        catch (IllegalArgumentException ignored) {
        }
    }

    public void setCoins(int coins) {
        try {
            EconomyMode economyMode = EconomyMode.valueOf(ServerMode.getMode(CoreConnector.getInstance().getServerName()).name());
            ModeEconomyService.init(economyMode).set(this.name, coins);
        }
        catch (IllegalArgumentException ignored) {
        }
    }

    public int getGolds() {
        return PlazmaEconomyService.getInstance().get(this.name);
    }

    public boolean hasGolds(int golds) {
        return this.getGolds() >= golds;
    }

    public void addGolds(int golds) {
        PlazmaEconomyService.getInstance().add(this.name, golds);
    }

    public void removeGolds(int golds) {
        PlazmaEconomyService.getInstance().take(this.name, golds);
    }

    public void setGolds(int golds) {
        PlazmaEconomyService.getInstance().set(this.name, golds);
    }

    public Group getGroup() {
        return GroupManager.INSTANCE.getPlayerGroup(getName());
    }

    public LanguageType getLanguage() {
        return LanguageManager.INSTANCE.getPlayerLanguage(getName());
    }

    public SpacePass getPass() {
        return SpacePassSqlHandler.INSTANCE.getPlayerPass(getPlayerId());
    }


    private LocalizationPlayer localizationPlayer;

    public LocalizationPlayer localization() {
        if (localizationPlayer == null) {
            localizationPlayer = new LocalizationPlayer(LocalizedPlayer.create(getName(), getLanguage().getResource()));
        }

        return localizationPlayer;
    }

    public Player handle() {
        return Bukkit.getPlayer(getName());
    }

    public int getTotalExperience() {
        return LevelSqlHandler.INSTANCE.getPlayerExp(getPlayerId());
    }

    public int getExperience() {
        int playerExperience = getTotalExperience();
        int totalExperienceToLevel = (int) LevelingUtil.getTotalExpToLevel(getLevel());

        return (getLevel() > 1 ? playerExperience - totalExperienceToLevel : playerExperience);
    }

    @SuppressWarnings("all")
    public void setExperience(int experience) {
        if (getExperience() == experience) {
            return;
        }

        // Применяем систему TyPass
        if (experience > getTotalExperience()) {
            SpacePassSqlHandler.INSTANCE.getPlayerPass(getName()).addExperience(experience - getTotalExperience());
        }

        int previousLevel = getLevel();

        // Change experience.
        Bukkit.getPluginManager().callEvent(new PlazmixExperienceChangeEvent(handle(), getExperience(), experience));
        LevelSqlHandler.INSTANCE.setPlayerExp(getPlayerId(), experience);

        // Change leveling.
        int newLevel = getLevel();

        if (previousLevel != newLevel) {
            Bukkit.getPluginManager().callEvent(new PlazmixLevelChangeEvent(handle(), previousLevel, newLevel));
        }
    }

    public void addExperience(int experience) {
        setExperience(getTotalExperience() + experience);
    }

    public void takeExperience(int experience) {
        setExperience(getTotalExperience() - experience);
    }


    public int getMaxExperience() {
        return (int) LevelingUtil.getExpFromLevelToNext(getLevel());
    }

    public int getLevel() {
        return LevelingUtil.getLevel(getTotalExperience());
    }

    @SuppressWarnings("all")
    public <T> T getDatabaseValue(@NonNull String table, @NonNull String column) {
        if (databasesValuesCacheTable.contains(table, column)) {
            return (T) databasesValuesCacheTable.get(table, column);
        }

        try {
            T value = (T) CoreConnector.getInstance().getMysqlConnection().executeQuery(false, "SELECT `" + column + "` FROM `" + table + "` WHERE `Id`=?",
                    resultSet -> {

                        if (!resultSet.next()) {
                            return 0;
                        }

                        return resultSet.getObject(column);

                    }, getPlayerId());

            databasesValuesCacheTable.put(table, column, value == null ? 0 : value);
            return (T) databasesValuesCacheTable.get(table, column);

        } catch (Exception ex) {
            return (T) ((Object) 0);
        }
    }

}
