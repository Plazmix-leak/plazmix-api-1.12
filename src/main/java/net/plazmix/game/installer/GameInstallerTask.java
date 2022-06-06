package net.plazmix.game.installer;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import lombok.*;
import lombok.experimental.FieldDefaults;
import net.plazmix.configuration.BaseConfiguration;
import net.plazmix.game.GamePlugin;
import net.plazmix.utility.location.LocationUtil;
import net.plazmix.utility.location.region.CuboidRegion;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.material.MaterialData;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public abstract class GameInstallerTask implements Runnable {

    public static void executeInstall(@NonNull GameInstallerTask installerTask) {
        installerTask.run();
    }

    protected final @NonNull GamePlugin plugin;

    private final Settings settings = new Settings();
    private final Actions actions   = new Actions();

    protected abstract void handleExecute(@NonNull Actions actions, @NonNull Settings settings);

    @Override
    public void run() {

        // Init template settings.
        YamlInstallSettingsStorage settingsStorage = new YamlInstallSettingsStorage();
        settingsStorage.createIfNotExists();

        // Handle executors.
        handleExecute(actions, settings);

        // Init default settings.
        if (settings.center == null) {
            Preconditions.checkArgument(plugin.getService().getMapName() != null, "map name");

            World mapWorld = plugin.getService().getMapWorld();

            Preconditions.checkArgument(mapWorld != null, "map world");
            Preconditions.checkArgument(mapWorld.getSpawnLocation() != null, "map spawn location");

            settings.center = mapWorld.getSpawnLocation();
        }

        // Execute entities.
        for (Entity entity : settings.center.getWorld().getNearbyEntities(settings.center, settings.radius, settings.radius, settings.radius)) {
            actions.executeEntityActions(entity);
        }

        Bukkit.getScheduler().runTask(plugin, () -> {

            // Execute blocks.
            CuboidRegion cuboidRegion = new CuboidRegion(
                    settings.center.clone().add(settings.radius, settings.radius, settings.radius),
                    settings.center.clone().subtract(settings.radius, settings.radius, settings.radius)
            );

            // Check template settings.
            if (settingsStorage.blocks.isEmpty()) {

                // Run Map Analyze.
                if (settings.isUseOnlyTileBlocks()) {

                    for (Chunk chunk : cuboidRegion.getChunks()) {
                        for (BlockState tileBlock : chunk.getTileEntities()) {

                            actions.executeBlockActions(tileBlock.getBlock());

                            if (actions.blockActionsMap.containsKey(tileBlock.getType())) {
                                settingsStorage.put(tileBlock.getBlock());
                            }
                        }
                    }

                } else {
                    cuboidRegion.forEachBlock(block -> {

                        actions.executeBlockActions(block);

                        if (actions.blockActionsMap.containsKey(block.getType())) {
                            settingsStorage.put(block);
                        }
                    });
                }

                settingsStorage.saveConfiguration();
            }

            // If template settings contains blocks.
            else {
                settingsStorage.blocks.values().forEach(location ->
                        actions.executeBlockActions(location.getBlock()));
            }

            // Execute other actions.
            actions.executeOtherActions(this);
            actions.clearAll();
        });
    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static final class YamlInstallSettingsStorage extends BaseConfiguration {

        @NonNull
        Multimap<Material, Location> blocks = HashMultimap.create();

        private YamlInstallSettingsStorage() {
            super(GamePlugin.getInstance(), "installSettings.yml");
        }

        @Override
        protected void onInstall(@NonNull FileConfiguration fileConfiguration) {
            ConfigurationSection blocksSection = fileConfiguration.getConfigurationSection("blocks");

            if (blocksSection != null) {

                for (String blockType : blocksSection.getKeys(false)) {
                    Material material = Material.matchMaterial(blockType);

                    for (String stringLocation : blocksSection.getStringList(blockType)) {
                        Location location = LocationUtil.stringToLocation(stringLocation);

                        blocks.put(material, location);
                    }
                }
            }

            // TODO: Add entities.
        }

        @Override
        public void saveConfiguration() {
            FileConfiguration fileConfiguration = getLoadedConfiguration();

            // Add locations list.
            blocks.keySet().forEach(material -> {

                Collection<Location> locations = blocks.get(material);
                fileConfiguration.set("blocks." + material, locations.stream().map(LocationUtil::locationToString).collect(Collectors.toList()));
            });

            // Save configuration data.
            super.saveConfiguration();
        }

        public void put(@NonNull Block block) {
            blocks.put(block.getType(), block.getLocation());
        }
    }

    @Getter
    @Setter
    protected static class Settings {

        private int radius = 100;

        private Location center;

        // Если будет стоять true, то это сильно ускорит алгоритм
        private boolean useOnlyTileBlocks;
    }

    @Getter
    protected static class Actions {

        private final Collection<Consumer<GameInstallerTask>> otherActionsCollection    = new LinkedList<>();

        private final Multimap<EntityType, Consumer<Entity>> entityActionsMap           = LinkedHashMultimap.create();
        private final Multimap<Material, Consumer<Block>> blockActionsMap               = LinkedHashMultimap.create();


        private void executeOtherActions(@NonNull GameInstallerTask installerTask) {
            otherActionsCollection.forEach(consumer -> consumer.accept(installerTask));
        }

        private void executeEntityActions(@NonNull Entity entity) {
            entityActionsMap.get(entity.getType()).forEach(entityConsumer -> entityConsumer.accept(entity));
        }

        private void executeBlockActions(@NonNull Block block) {
            blockActionsMap.get(block.getType()).forEach(blockConsumer -> blockConsumer.accept(block));
        }


        public void addOtherAction(@NonNull Consumer<GameInstallerTask> taskConsumer) {
            otherActionsCollection.add(taskConsumer);
        }

        public void addEntity(@NonNull EntityType entityType, @NonNull Consumer<Entity> entityConsumer) {
            entityActionsMap.put(entityType, entityConsumer);
        }

        public void addBlock(@NonNull Material material, @NonNull Consumer<Block> blockConsumer) {
            blockActionsMap.put(material, blockConsumer);
        }

        public void addBlock(@NonNull MaterialData materialData, @NonNull Consumer<Block> blockConsumer) {
            blockActionsMap.put(materialData.getItemType(), blockConsumer);
        }


        public void clearAll() {
            entityActionsMap.clear();
            blockActionsMap.clear();
            otherActionsCollection.clear();
        }

    }

}
