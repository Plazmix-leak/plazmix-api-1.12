package net.plazmix.schematic.command;

import com.google.common.base.Joiner;
import lombok.NonNull;
import lombok.SneakyThrows;
import net.plazmix.PlazmixApiPlugin;
import net.plazmix.command.BaseCommand;
import net.plazmix.coreconnector.core.group.Group;
import net.plazmix.schematic.BaseSchematic;
import net.plazmix.utility.location.LocationUtil;
import net.plazmix.utility.location.region.CuboidRegion;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public final class SchematicCommand extends BaseCommand<Player> {

    private final Map<String, Location> playerFirstLocations    = new HashMap<>();
    private final Map<String, Location> playerSecondLocations   = new HashMap<>();

    public SchematicCommand() {
        super("bschem", "bschematic", "schems", "bschematics");

        setMinimalGroup(Group.DEVELOPER);
    }


    private Location getFirstPosition(@NonNull Player player) {
        return playerFirstLocations.get(player.getName().toLowerCase());
    }

    private Location getSecondPosition(@NonNull Player player) {
        return playerSecondLocations.get(player.getName().toLowerCase());
    }

    private void addFirstPosition(@NonNull Player player, @NonNull Location position) {
        playerFirstLocations.put(player.getName().toLowerCase(), position);

        player.sendMessage(ChatColor.GREEN + "BSchematics :: Первая позиция установлена: " + LocationUtil.locationToString(position));
    }

    private void addSecondPosition(@NonNull Player player, @NonNull Location position) {
        playerSecondLocations.put(player.getName().toLowerCase(), position);

        player.sendMessage(ChatColor.GREEN + "BSchematics :: Вторая позиция установлена: " + LocationUtil.locationToString(position));
    }

    private boolean hasFirstPosition(@NonNull Player player) {
        return getFirstPosition(player) != null;
    }

    private boolean hasSecondPosition(@NonNull Player player) {
        return getSecondPosition(player) != null;
    }

    private CuboidRegion getCuboid(@NonNull Player player) {
        return new CuboidRegion(getFirstPosition(player), getSecondPosition(player));
    }

    private BaseSchematic getSchematic(@NonNull String name, @NonNull Player player) {
        return new BaseSchematic(getCuboid(player), name);
    }

    public static List<BaseSchematic> getSchematicsList() {
        Path schematicsFolder = getSchematicsDirectory();

        if (!Files.exists(schematicsFolder)) {
            schematicsFolder.toFile().mkdirs();
        }

        return Arrays.stream(Objects.requireNonNull(schematicsFolder.toFile().list()))
                .map(file -> new BaseSchematic(file.replace(BaseSchematic.SCHEM_FILE_FORMAT, ""))).collect(Collectors.toList());
    }

    public static BaseSchematic schematicLoad(@NonNull String name) {
        if (!getSchematicsList().stream().map(BaseSchematic::getName).collect(Collectors.toList()).contains(name)) {
            return null;
        }

        return new BaseSchematic(name);
    }

    public static Path getSchematicsDirectory() {
        return PlazmixApiPlugin.getPlugin(PlazmixApiPlugin.class).getDataFolder()
                .toPath()
                .resolve("schematics");
    }


    @Override
    @SneakyThrows
    protected void onExecute(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(ChatColor.AQUA + "BaseSchematic :: Список доступных команд:");

            player.sendMessage(ChatColor.AQUA + " -> Список схематиков - /bschems list");

            player.sendMessage(ChatColor.AQUA + " -> Установить первую позицию - /bschems pos1");
            player.sendMessage(ChatColor.AQUA + " -> Установить вторую позицию - /bschems pos2");

            player.sendMessage(ChatColor.AQUA + " -> Сохранить схематик - /bschems save <название>");
            player.sendMessage(ChatColor.AQUA + " -> Выгрузить схематик - /bschems load <название>");
            return;
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "list": {
                player.sendMessage("Список схематиков: " + Joiner.on(", ").join(getSchematicsList()));
                break;
            }

            case "pos1": {
                addFirstPosition(player, player.getLocation());
                break;
            }

            case "pos2": {
                addSecondPosition(player, player.getLocation());
                break;
            }

            case "save": {
                if (args.length < 2) {
                    player.sendMessage("сука даун полупокерный, имя напиши, название схематика, имя, имяя, ИМЯЯЯ БЛЯТЬ");
                    break;
                }

                if (!Files.exists(getSchematicsDirectory())) {
                    Files.createDirectories(getSchematicsDirectory());
                }

                BaseSchematic baseSchematic = getSchematic(args[1], player);
                baseSchematic.save(player.getLocation());

                player.sendMessage("§eСхематик успешно сохранен от Вашей локации: " + LocationUtil.locationToString(player.getLocation()));
                break;
            }

            case "load": {
                if (args.length < 2) {
                    player.sendMessage("сука даун полупокерный, имя напиши, название схематика, имя, имяя, ИМЯЯЯ БЛЯТЬ");
                    break;
                }

                player.sendMessage("§eИдет загрузка схематика...");

                if (!Files.exists(getSchematicsDirectory())) {
                    Files.createDirectories(getSchematicsDirectory());
                }

                BaseSchematic baseSchematic = schematicLoad(args[1]);

                if (baseSchematic == null) {
                    player.sendMessage("§cА такого схематика нет, нахуй пошел, полупокер");
                    break;
                }

                baseSchematic.load(player.getLocation());
                player.sendMessage("§eСхематик успешно выгружен от Вашей локации: " + LocationUtil.locationToString(player.getLocation()));
                break;
            }

            default:
                player.sendMessage(ChatColor.AQUA + "BSchematics :: Список доступных команд:");
                player.sendMessage(ChatColor.AQUA + " -> Список схематиков - /bschems list");
                player.sendMessage(ChatColor.AQUA + " -> Установить первую позицию - /bschems pos1");
                player.sendMessage(ChatColor.AQUA + " -> Установить вторую позицию - /bschems pos2");
                player.sendMessage(ChatColor.AQUA + " -> Сохранить схематик - /bschems save <название>");
                player.sendMessage(ChatColor.AQUA + " -> Выгрузить схематик - /bschems load <название>");
        }

    }

}
