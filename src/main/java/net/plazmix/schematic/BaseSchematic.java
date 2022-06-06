package net.plazmix.schematic;

import lombok.*;
import net.plazmix.PlazmixApiPlugin;
import net.plazmix.utility.location.region.CuboidRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RequiredArgsConstructor
@AllArgsConstructor
public class BaseSchematic {

    public static final String SCHEM_BLOCK_DATA_SPLITTER    = "@";
    public static final String SCHEM_FILE_FORMAT            = ".bschem";


    private CuboidRegion cuboidRegion;

    @Getter
    private final String name;


    public BaseSchematicReader reader() {
        return new BaseSchematicReader(getFile());
    }

    public File getFile() {
        return PlazmixApiPlugin.getPlugin(PlazmixApiPlugin.class)
                .getDataFolder()
                .toPath()
                .resolve("schematics")
                .resolve(name + SCHEM_FILE_FORMAT)
                .toFile();
    }

    @SneakyThrows
    public void save(@NonNull Location originLocation) {
        File file = getFile();
        BaseSchematicReader schematicReader = reader();

        Files.deleteIfExists(file.toPath());
        Files.createFile(file.toPath());

        Bukkit.getScheduler().runTask(PlazmixApiPlugin.getPlugin(PlazmixApiPlugin.class), () ->
                cuboidRegion.forEachBlock(block -> {

            if (block.isEmpty()) {
                return;
            }

            Vector offset = block.getLocation().clone().subtract(originLocation).toVector();
            MaterialData materialData = block.getState().getData();

            try {
                BaseSchematicBlock baseSchematicBlock = new BaseSchematicBlock(offset, materialData);
                schematicReader.write(baseSchematicBlock);

            } catch (IOException exception) {
                exception.printStackTrace();
            }

        }));
    }

    public void load(@NonNull Location originLocation) {
        Bukkit.getScheduler().runTask(PlazmixApiPlugin.getPlugin(PlazmixApiPlugin.class), () -> {
            try {
                for (BaseSchematicBlock schematicBlock : reader().read())
                    schematicBlock.update(originLocation);

            } catch (IOException exception) {
                exception.printStackTrace();
            }

        });
    }

    public void loadFor(@NonNull Player player, @NonNull Location originLocation) {
        Bukkit.getScheduler().runTask(PlazmixApiPlugin.getPlugin(PlazmixApiPlugin.class), () -> {

            try {
                for (BaseSchematicBlock schematicBlock : reader().read()) {

                    Block block = schematicBlock.getBlock(originLocation);
                    player.sendBlockChange(block.getLocation(), block.getType(), block.getData());
                }

            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });
    }

    @Override
    public String toString() {
        return name;
    }

}
