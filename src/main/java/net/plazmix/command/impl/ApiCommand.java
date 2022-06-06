package net.plazmix.command.impl;

import net.plazmix.command.BaseCommand;
import net.plazmix.coreconnector.core.group.Group;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.lang.management.ManagementFactory;
import java.util.Arrays;

public class ApiCommand extends BaseCommand<Player> {

    public ApiCommand() {
        super("papi", "plazmixapi", "api");
        setMinimalGroup(Group.ADMIN);
    }

    @Override
    protected void onExecute(Player player, String[] args) {

        PlazmixUser plazmixUser = PlazmixUser.of(player);

        Runtime runtime = Runtime.getRuntime();

        long max = runtime.maxMemory() / 1024L / 1024L;
        long total = runtime.totalMemory() / 1024L / 1024L;
        long free = runtime.freeMemory() / 1024L / 1024L;
        long usages = (runtime.totalMemory() - runtime.freeMemory()) / 1024L / 1024L;

        String system = ManagementFactory.getOperatingSystemMXBean().getName();
        String version = ManagementFactory.getOperatingSystemMXBean().getVersion();

        plazmixUser.handle().sendMessage("§d§lPlazmixAPI §8:: §fПолная информация о сервере:");

        plazmixUser.handle().sendMessage("§fОперационка:");
        plazmixUser.handle().sendMessage(" §▪ §7Система: §e" + system + " §7Версии: §d" + version);
        plazmixUser.handle().sendMessage(" §▪ §7Ядер: §2" + (runtime.availableProcessors() / 2));
        plazmixUser.handle().sendMessage(" §▪ §7Потоков: §2" + runtime.availableProcessors());

        plazmixUser.handle().sendMessage("§7Память:");
        plazmixUser.handle().sendMessage(" §8▪ §7Максимально §a" + max + " МБ");
        plazmixUser.handle().sendMessage(" §8▪ §7Выделено §a" + total + " МБ");
        plazmixUser.handle().sendMessage(" §8▪ §7Свободно §a" + free + " МБ");
        plazmixUser.handle().sendMessage(" §8▪ §7Используется §a" + usages + " МБ");

        plazmixUser.handle().sendMessage("§fСервер:");
        plazmixUser.handle().sendMessage(" §8▪ §7ViewDistance §6" + Bukkit.getViewDistance());
        plazmixUser.handle().sendMessage(" §8▪ §7TPS §6" + Arrays.toString(Bukkit.getServer().getTPS()));

        for (World world : Bukkit.getWorlds()) {
            plazmixUser.handle().sendMessage(" §8▪ §7Мир: §6" + world.getName());
            plazmixUser.handle().sendMessage(" §8▪ §7Энтити: §6" + world.getEntities().size());
            plazmixUser.handle().sendMessage(" §8▪ §7Чанков: §6" + world.getLoadedChunks().length);
        }
    }
}