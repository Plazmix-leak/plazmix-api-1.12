package net.plazmix.command.impl;

import net.plazmix.command.BaseCommand;
import net.plazmix.coreconnector.core.group.Group;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GivePassCommand extends BaseCommand<Player> {

    public GivePassCommand() {
        super("givepass");

        setMinimalGroup(Group.ADMIN);
    }

    @Override
    protected void onExecute(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Юзай так - /givepass <check/give> <ник игрока>");
            return;
        }

        switch (args[0].toLowerCase()) {

            case "check": {
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Ну ник игрока то укажи уж");
                    break;
                }

                PlazmixUser plazmixUser = PlazmixUser.of(args[1]);

                if (plazmixUser.getPlayerId() <= 0) {
                    player.sendMessage(ChatColor.RED + "ну такого тут ни разу не было на туниксе... давай другого");
                    break;
                }

                player.sendMessage(ChatColor.YELLOW + "статус пропуска у " + plazmixUser.getDisplayName() + ": " + (plazmixUser.getPass().isActivated() ? "§aесть" : "§cнету"));
                break;
            }

            case "give": {
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Ну ник игрока то укажи уж");
                    break;
                }

                PlazmixUser plazmixUser = PlazmixUser.of(args[1]);

                if (plazmixUser.getPlayerId() <= 0) {
                    player.sendMessage(ChatColor.RED + "ну такого тут ни разу не было на плазмиксе... давай другого");
                    break;
                }

                if (plazmixUser.getPass().isActivated()) {
                    player.sendMessage(ChatColor.RED + "Окуп, этот чел уже покупал pass");
                    break;
                }

                plazmixUser.getPass().purchaseActivation();
                player.sendMessage(ChatColor.GREEN + "Окуп, pass текущего сезона был выдан " + plazmixUser.getDisplayName());
                break;
            }
        }
    }

}
