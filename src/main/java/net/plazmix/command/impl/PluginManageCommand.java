package net.plazmix.command.impl;

import com.google.common.base.Joiner;
import net.plazmix.command.BaseMegaCommand;
import net.plazmix.command.annotation.CommandArgument;
import net.plazmix.coreconnector.core.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;

import java.util.Arrays;
import java.util.stream.Collectors;

public class PluginManageCommand extends BaseMegaCommand<CommandSender> {

    public PluginManageCommand() {
        super("pluginmanager", "plugin", "plug", "pmanager", "plugins", "plugman");

        setMinimalGroup(Group.ADMIN);
    }

    @Override
    protected void onUsage(CommandSender commandSender) {
        commandSender.sendMessage("§d§lPlazmixAPI §8:: §fСписок команд:");
        commandSender.sendMessage(" §7Список плагинов - §e/pluginmanager list");
        commandSender.sendMessage(" §7Информация о плагине - §e/pluginmanager info <плагин>");
        commandSender.sendMessage(" §cВыключить §7плагин - §e/pluginmanager disable/off <плагин>");
        commandSender.sendMessage(" §aВключить §7плагин - §e/pluginmanager enable/on <плагин>");
        commandSender.sendMessage(" §7Перезапустить плагин - §e/pluginmanager reload <плагин>");
    }

    @CommandArgument
    protected void list(CommandSender commandSender, String[] args) {
        PluginManager pluginManager = Bukkit.getPluginManager();

        commandSender.sendMessage("§d§lPlazmixAPI §8:: §fСписок плагинов (" + pluginManager.getPlugins().length + "): "
                + Joiner.on("§f, ")
                .join( Arrays.stream(pluginManager.getPlugins()).map(plugin -> (plugin.isEnabled() ? "§a" : "§c") + plugin.getName()).collect(Collectors.toSet()) ));
    }

    @CommandArgument(aliases = "stats")
    protected void info(CommandSender commandSender, String[] args) {
        if (args.length == 0) {
            commandSender.sendMessage("§cОшибка, пишите - /pluginmanager info <плагин>");
            return;
        }

        Plugin plugin = Bukkit.getPluginManager().getPlugin(args[0]);

        if (plugin == null) {
            commandSender.sendMessage("§cОшибка, плагина " + args[0] + " не существует или не подгружен ядром!");
            return;
        }

        commandSender.sendMessage("§d§lPlazmixAPI §8:: §fСтатистика о плагине §e" + plugin.getName());

        commandSender.sendMessage(" §7Статус: " + (plugin.isEnabled() ? "§aвключен" : "§cвыключен"));
        commandSender.sendMessage(" §7Директория: " + (plugin.getDataFolder().exists() ? "§aесть" : "§cнет"));
        commandSender.sendMessage(" §7Конфигурация: " + (plugin.getDataFolder().toPath().resolve("config.yml").toFile().exists() ? "§aесть" : "§cнет"));
        commandSender.sendMessage("\n");

        PluginDescriptionFile pluginDescription = plugin.getDescription();
        commandSender.sendMessage(" §fОписание:");

        commandSender.sendMessage("  §7Название: §e" + pluginDescription.getName());
        commandSender.sendMessage("  §7Версия: §e" + pluginDescription.getVersion());
        commandSender.sendMessage("  §7Главный класс: §e" + pluginDescription.getMain());

        if (!pluginDescription.getAuthors().isEmpty()) {

            if (pluginDescription.getAuthors().size() < 2) {
                commandSender.sendMessage("  §7Автор: §e" + pluginDescription.getAuthors().stream().findFirst().orElse("<нет>"));

            } else {

                commandSender.sendMessage("  §7Авторы: §e" + Joiner.on("§7, §e").join(pluginDescription.getAuthors()));
            }
        }

        if (pluginDescription.getWebsite() != null) {
            commandSender.sendMessage("  §7Сайт: §e" + pluginDescription.getWebsite());
        }

        if (pluginDescription.getDepend() != null && !pluginDescription.getDepend().isEmpty()) {
            commandSender.sendMessage("  §7Зависимости: §a" + Joiner.on("§7, §a").join(pluginDescription.getDepend()));
        }
    }

    @CommandArgument(aliases = "on")
    protected void enable(CommandSender commandSender, String[] args) {
        if (args.length == 0) {
            commandSender.sendMessage("§cОшибка, пишите - /pluginmanager enable <плагин>");
            return;
        }

        Plugin plugin = Bukkit.getPluginManager().getPlugin(args[0]);

        if (plugin == null) {
            commandSender.sendMessage("§cОшибка, плагина " + args[0] + " не сущесвтует или не подгружен ядром!");
            return;
        }

        if (plugin.isEnabled()) {
            commandSender.sendMessage("§cОшибка, плагин " + plugin.getName() + " уже запущен!");
            return;
        }

        Bukkit.getPluginManager().enablePlugin(plugin);
        commandSender.sendMessage("§d§lPlazmixAPI §8:: §fПлагин §e" + plugin.getName() + " §fуспешно запущен!");
    }

    @CommandArgument(aliases = "off")
    protected void disable(CommandSender commandSender, String[] args) {
        if (args.length == 0) {
            commandSender.sendMessage("§cОшибка, пишите - /pluginmanager disable <плагин>");
            return;
        }

        Plugin plugin = Bukkit.getPluginManager().getPlugin(args[0]);

        if (plugin == null) {
            commandSender.sendMessage("§cОшибка, плагина " + args[0] + " не существует или не подгружен ядром!");
            return;
        }

        if (!plugin.isEnabled()) {
            commandSender.sendMessage("§cОшибка, плагин " + plugin.getName() + " уже выключен!");
            return;
        }

        Bukkit.getPluginManager().disablePlugin(plugin);
        commandSender.sendMessage("§d§lPlazmixAPI §8:: §fПлагин §e" + plugin.getName() + " §fуспешно выключен!");
    }

    @CommandArgument(aliases = "restart")
    protected void reload(CommandSender commandSender, String[] args) {
        if (args.length == 0) {
            commandSender.sendMessage("§cОшибка, пишите - /pluginmanager reload <плагин>");
            return;
        }

        Plugin plugin = Bukkit.getPluginManager().getPlugin(args[0]);

        if (plugin == null) {
            commandSender.sendMessage("§cОшибка, плагина " + args[0] + " не существует или не подгружен ядром!");
            return;
        }

        if (!plugin.isEnabled()) {
            Bukkit.getPluginManager().enablePlugin(plugin);

        } else {

            Bukkit.getPluginManager().disablePlugin(plugin);
            Bukkit.getPluginManager().enablePlugin(plugin);
        }

        commandSender.sendMessage("§d§lPlazmixAPI §8:: §fПлагин §e" + plugin.getName() + " §fуспешно перезапущен!");
    }

}
