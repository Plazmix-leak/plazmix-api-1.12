package net.plazmix.command.impl;

import lombok.NonNull;
import net.plazmix.PlazmixApi;
import net.plazmix.command.ContextCommand;
import net.plazmix.command.annotation.CommandArgument;
import net.plazmix.core.PlazmixCoreApi;
import net.plazmix.utility.PlayerUtil;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

public class TestContextCommand extends ContextCommand<Player> {

    public TestContextCommand() {
        super("ctest", "contest", "contexttest");
    }

    @Override
    protected void onUsage(@NonNull Player sender) {
        sender.sendMessage(ChatColor.RED + "PlazmixTest :: Usage - /contest online (имя сервера, а можно и без этого)");
        sender.sendMessage(ChatColor.RED + "PlazmixTest :: Usage - /contest getId (ник игрока)");
    }

    @CommandArgument(aliases = {"list", "players"})
    protected void online(CommandContext<Player> context) {
        Player player = context.getSender();

        if (context.argumentsIsEmpty()) {
            player.sendMessage(ChatColor.GREEN + "PlazmixTest :: Общий онлайн данного сервера: " + Bukkit.getOnlinePlayers().size());

        } else {

            player.sendMessage(ChatColor.GREEN + "PlazmixTest :: Онлайн серверов " + context.firstArgument() + " - "
                    + PlazmixCoreApi.getOnlineByServersPrefixes(context.firstArgument()));
        }
    }

    @CommandArgument(aliases = {"getid"})
    protected void checkId(CommandContext<Player> context) {
        Player player = context.getSender();

        if (context.argumentsIsEmpty()) {
            player.sendMessage(ChatColor.RED + "PlazmixTest :: Нужно указать еще ник игрока");
            return;
        }

        PlazmixUser user = context.map(0, PlazmixUser::of);

        if (user.handle() == null) {
            player.sendMessage(ChatColor.RED + "PlazmixTest :: Такого игрока нет в сети :((");

        } else {

            player.sendMessage(ChatColor.GREEN + "PlazmixTest :: ID данного игрока - " + user.getPlayerId());
        }
    }


    @Override
    protected TabCompleteContext onTabComplete(@NonNull Player sender) {
        return TabCompleteContext.newContext()

                .<Player>getSuggestionsMap("online", 0)
                .setCanComplete(player -> PlayerUtil.getGroup(player).isAdmin())
                .setSuggestions(PlazmixCoreApi::getConnectedServers)
                .toContext()

                .<Player>getSuggestionsMap("checkId", 0)
                .setSuggestions(line -> PlazmixApi.getOnlinePlayers(line).stream().map(Player::getName).collect(Collectors.toList()))
                .toContext();
    }

}
