package net.plazmix.game.installer;

import lombok.NonNull;
import net.plazmix.configuration.BaseConfiguration;
import net.plazmix.game.GamePlugin;
import org.bukkit.configuration.file.FileConfiguration;

public class GameInstallerConfiguration extends BaseConfiguration {

    private final GameInstaller gameInstaller;
    private final GamePlugin gamePlugin;

    public GameInstallerConfiguration(@NonNull GameInstaller gameInstaller, @NonNull GamePlugin gamePlugin) {
        super(gamePlugin, "settings.yml");

        this.gameInstaller = gameInstaller;
        this.gamePlugin = gamePlugin;
    }

    @Override
    protected void onInstall(@NonNull FileConfiguration fileConfiguration) {

        // Map name.
        if (fileConfiguration.isSet("Map")) {
            gamePlugin.getService().setMapName(fileConfiguration.getString("Map"));

        } else {

            fileConfiguration.set("Map", "unknown");
        }

        // Install radius.
        if (fileConfiguration.isSet("InstallRadius")) {
            String map = gamePlugin.getService().getMapName();

            if (map != null) {
                GameInstallerTask installerTask = gamePlugin.getInstallerTask();

                if (installerTask == null) {
                    return;
                }

                installerTask.getSettings().setRadius(fileConfiguration.getInt("InstallRadius"));
                gameInstaller.executeInstall(installerTask);
            }

        } else {

            fileConfiguration.set("InstallRadius", 250);
        }
    }

}
