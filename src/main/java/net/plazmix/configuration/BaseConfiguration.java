package net.plazmix.configuration;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Getter
public abstract class BaseConfiguration {

    protected Plugin plugin;

    protected Path configurationPath;
    protected FileConfiguration loadedConfiguration;


    private BaseConfiguration(@NonNull Plugin plugin, @NonNull Path configurationPath) {
        this.plugin = plugin;
        this.configurationPath = configurationPath;
    }

    private BaseConfiguration(@NonNull Plugin plugin, @NonNull File configurationFile) {
        this (plugin, configurationFile.toPath());
    }

    public BaseConfiguration(@NonNull Plugin plugin, @NonNull String resourceName) {
        this (plugin, plugin.getDataFolder().toPath().resolve(resourceName));
    }


    @SneakyThrows
    public void createIfNotExists(boolean canInstall) {
        if (!Files.exists(configurationPath)) {

            if (this.plugin.getClass().getClassLoader().getResourceAsStream(configurationPath.toFile().getName()) != null) {
                this.plugin.saveResource(configurationPath.toFile().getName(), false);

            } else {

                Files.createDirectories(Paths.get( new File(configurationPath.toString().substring(0, configurationPath.toString().lastIndexOf(File.separator))).toURI() ));
                Files.createFile(configurationPath);
            }
        }

        this.loadedConfiguration = YamlConfiguration.loadConfiguration(configurationPath.toFile());

        if (canInstall) {
            install();
        }
    }

    public void createIfNotExists() {
        createIfNotExists(true);
    }

    public void install() {
        Preconditions.checkArgument(Files.exists(configurationPath), "Configuration '%s' is`nt exists", configurationPath.toFile().getName());

        onInstall(loadedConfiguration);
    }

    protected abstract void onInstall(@NonNull FileConfiguration fileConfiguration);


    public String getColoredString(@NonNull String configPath) {
        Preconditions.checkArgument(loadedConfiguration != null, "Configuration '%s' can`t be null", configurationPath.toFile().getName());

        return ChatColor.translateAlternateColorCodes('&', loadedConfiguration.getString(configPath));
    }

    public List<String> getColoredList(@NonNull String configPath) {
        Preconditions.checkArgument(loadedConfiguration != null, "Configuration '%s' can`t be null", configurationPath.toFile().getName());

        return Arrays.asList(ChatColor.translateAlternateColorCodes('&', Joiner.on('\n')
                .join(loadedConfiguration.getStringList(configPath))).split("\n"));
    }

    @SneakyThrows
    public void saveConfiguration() {
        Preconditions.checkArgument(Files.exists(configurationPath), "Configuration '%s' is`nt exists", configurationPath.toFile().getName());
        Preconditions.checkArgument(loadedConfiguration != null, "Configuration '%s' can`t be null", configurationPath.toFile().getName());

        this.loadedConfiguration.save(configurationPath.toFile());
    }

    public void reloadConfiguration() {
        Preconditions.checkArgument(Files.exists(configurationPath), "Configuration '%s' is`nt exists", configurationPath.toFile().getName());

        this.loadedConfiguration = YamlConfiguration.loadConfiguration(configurationPath.toFile());
    }

}
