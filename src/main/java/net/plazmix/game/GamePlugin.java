package net.plazmix.game;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.plazmix.PlazmixApi;
import net.plazmix.core.PlazmixCoreApi;
import net.plazmix.coreconnector.CoreConnector;
import net.plazmix.game.command.LeaveCommand;
import net.plazmix.game.command.StartCommand;
import net.plazmix.game.installer.GameInstallerTask;
import net.plazmix.game.listener.GameSettingsListener;
import net.plazmix.game.listener.GhostListener;
import net.plazmix.game.listener.PlayerLoaderListener;
import net.plazmix.game.state.GameState;
import net.plazmix.utility.Directories;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public abstract class GamePlugin extends JavaPlugin {

    @Getter
    private static GamePlugin instance;


    GameCache cache             = new GameCache();
    GamePluginService service   = new GamePluginService();

    @NonFinal
    PlayerGamesData gamesData;


    public abstract GameInstallerTask getInstallerTask();

    protected abstract void handleEnable();
    protected abstract void handleDisable();

    @Override
    public void onDisable() {
        instance = null;

        // Backup files process.
        String mapName = service.getMapName();

        if (mapName != null) {
            loadBackupMap(mapName);
        }

        // Disable all stages.
        GameState currentState = service.getStateManager().getCurrentState();

        if (currentState != null && currentState.isEnabled()) {
            currentState.forceShutdown();
        }

        for (GameState gameState : service.getStateManager().getGameStates()) {
            if (!gameState.isEnabled())
                continue;

            gameState.forceShutdown();
        }

        // Game plugin disable process.
        handleDisable();

        // Initialize PlayerGamesData.
        if (gamesData != null && gamesData.canInsert()) {

            gamesData.insert(CoreConnector.getInstance().getMysqlConnection());
            gamesData = null;
        }
    }

    @Override
    public void onEnable() {
        instance = this;

        PlazmixApi.registerCommand(new LeaveCommand());
        PlazmixApi.registerCommand(new StartCommand());

        // Register listeners.
        getServer().getPluginManager().registerEvents(new PlayerLoaderListener(this), this);
        getServer().getPluginManager().registerEvents(new GameSettingsListener(this), this);
        getServer().getPluginManager().registerEvents(new GhostListener(this), this);

        // Game plugin enable process.
        handleEnable();

        // Initialize PlayerGamesData.
        gamesData = PlayerGamesData.create(service.getGameName(), service.getMapName(), PlazmixCoreApi.getCurrentServerName());

        // Activate of the stages
        service.getStateManager().nextStage();

        // Backup files process.
        createBackupFolder();
    }


    @SneakyThrows
    protected void createBackupFolder() {
        Path backupDirectory = getDataFolder().toPath().resolve("backup");

        if (!Files.exists(backupDirectory)) {
            Files.createDirectories(backupDirectory);

            getLogger().info(ChatColor.GREEN + "[Game Backup] Backup folder has been created!");
            getLogger().info(ChatColor.GREEN + "[Game Backup] Please, load the arena world file to the backup folder");
        }
    }

    @SneakyThrows
    protected void loadBackupMap(String arenaMap) {
        World world = Bukkit.getWorld(arenaMap);

        if (world == null) {
            getLogger().info(ChatColor.RED + "[Game Backup] Map '" + arenaMap + "' is`nt exists!");
            return;
        }

        Path backupDirectory = getDataFolder().toPath().resolve("backup");
        Path worldBackupPath = backupDirectory.resolve(world.getName());

        if (!Files.exists(worldBackupPath)) {
            getLogger().info(ChatColor.RED + "[Game Backup] Backup arena world folder is`nt exists");
            getLogger().info(ChatColor.RED + "[Game Backup] Copy backup process is cancelled!");

            return;
        }

        if (Objects.requireNonNull(worldBackupPath.toFile().list()).length != 0) {
            File worldFile = world.getWorldFolder();

            if (world.getWorldFolder().exists()) {
                Directories.clearDirectory(worldFile, true);
            }

            Directories.copyDirectory(worldBackupPath, worldFile.toPath());
            getLogger().info(ChatColor.GREEN + "[Game Backup] Backup of the world map '" + worldFile.getName() + "' has been loaded!");
        }
    }

    public void broadcastMessage(@NonNull ChatMessageType chatMessageType, @NonNull String message) {
        for (Player player : getServer().getOnlinePlayers()) {
            player.spigot().sendMessage(chatMessageType, TextComponent.fromLegacyText(message));
        }
    }

    public void broadcastMessage(@NonNull String message) {
        broadcastMessage(ChatMessageType.CHAT, message);
    }

    public void broadcastLangMessage(@NonNull ChatMessageType chatMessageType, @NonNull String langKey) {
        for (Player player : getServer().getOnlinePlayers()) {

            String text = PlazmixUser.of(player).localization().getLocalizationResource().getText(langKey);
            player.spigot().sendMessage(chatMessageType, TextComponent.fromLegacyText(text));
        }
    }

    public void broadcastLangMessage(@NonNull String langKey) {
        broadcastLangMessage(ChatMessageType.CHAT, langKey);
    }

}
