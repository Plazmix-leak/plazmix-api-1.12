package net.plazmix.game.installer;

import lombok.*;
import net.plazmix.PlazmixApi;
import net.plazmix.game.GamePlugin;

import java.nio.file.Files;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GameInstaller {

    public static GameInstaller create() {
        return new GameInstaller();
    }

    public void executeInstall(@NonNull GameInstallerTask installerTask) {
        GameInstallerTask.executeInstall(installerTask);
    }

    private void executeInstallByConfig(@NonNull GameInstallerConfiguration installerConfiguration) {
        installerConfiguration.reloadConfiguration();
        installerConfiguration.install();
    }

    @SneakyThrows
    public GameInstallerConfiguration executeInstallByCommand(@NonNull GamePlugin gamePlugin) {
        GameInstallerConfiguration installerConfiguration = new GameInstallerConfiguration(this, gamePlugin);

        if (!gamePlugin.getDataFolder().exists()) {
            Files.createDirectories(gamePlugin.getDataFolder().toPath());
        }

        if (!Files.exists(installerConfiguration.getConfigurationPath())) {
            Files.createFile(installerConfiguration.getConfigurationPath());

            PlazmixApi.registerCommand(gamePlugin, new GameInstallCommand(installerConfiguration));

        } else {

            executeInstallByConfig(installerConfiguration);
        }

        return installerConfiguration;
    }

}
