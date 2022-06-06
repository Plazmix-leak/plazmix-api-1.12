package net.plazmix.command.impl;

import lombok.NonNull;
import net.plazmix.command.BaseMegaCommand;
import net.plazmix.command.annotation.CommandArgument;
import net.plazmix.coreconnector.CoreConnector;
import net.plazmix.coreconnector.core.group.Group;
import net.plazmix.coreconnector.protocol.server.SPlayerLevelUpdatePacket;
import net.plazmix.utility.NumberUtil;
import net.plazmix.utility.ValidateUtil;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class ExperienceCommand extends BaseMegaCommand<Player> {

    public ExperienceCommand() {
        super("level", "lvl", "exp", "experience");
    }

    @Override
    protected void onUsage(Player player) {
        PlazmixUser plazmixUser = PlazmixUser.of(player);

        player.sendMessage("§d§lLeveling §8:: §fИнформация по игровому уровню:");
        player.sendMessage(" §7На данный момент у Вас §d" + plazmixUser.getLevel() + " §7уровень,");
        player.sendMessage(" §7До следующего уровня необходимо собрать §b" + NumberUtil.spaced(plazmixUser.getMaxExperience() - plazmixUser.getExperience()) + " EXP");
    }


    @CommandArgument(aliases = "admin")
    protected void help(Player player, String[] args) {
        checkPermission(player, plazmixUser -> {
            player.sendMessage("§d§lLeveling §8:: §fУправление уровнями игроков:");
            player.sendMessage(" §7Узнать уровень игрока - §e/exp get <ник игрока>");
            player.sendMessage(" §7Установить количество опыта - §e/exp set <ник игрока> <опыт>");
            player.sendMessage(" §7Добавить опыт - §e/exp add <ник игрока> <опыт>");
            player.sendMessage(" §7Отнять опыт - §e/exp take <ник игрока> <опыт>");
        });
    }

    @CommandArgument(aliases = "check")
    protected void get(Player player, String[] args) {
        checkPermission(player, plazmixUser -> {

            if (args.length == 0) {
                player.sendMessage("§d§lLeveling §8:: §fИспользуйте - §d/exp get <ник игрока>");
                return;
            }

            PlazmixUser target = PlazmixUser.of(args[0]);

            if (target.getPlayerId() <= 0) {
                player.sendMessage("§сИгрок не найден в базе данных :(");
                return;
            }

            player.sendMessage("§d§lLeveling §8:: §fИнформация по игровому уровню " + target.getDisplayName());
            player.sendMessage(" §7На данный момент у игрока §d" + target.getLevel() + " §7уровень,");
            player.sendMessage(" §7До следующего уровня ему необходимо собрать §b" + NumberUtil.spaced(target.getMaxExperience() - target.getExperience()) + " EXP");
        });
    }

    @CommandArgument(aliases = "add")
    protected void give(Player player, String[] args) {
        checkPermission(player, plazmixUser -> {

            if (args.length < 2) {
                player.sendMessage("§d§lLeveling §8:: §fИспользуйте - §d/exp add <ник игрока> <опыт>");
                return;
            }

            PlazmixUser target = PlazmixUser.of(args[0]);

            if (target.getPlayerId() <= 0) {
                player.sendMessage("§сИгрок не найден в базе данных :(");
                return;
            }

            if (!ValidateUtil.isNumber(args[1])) {
                player.sendMessage("§c" + args[1] + " не является числом :(");
                return;
            }

            int experience = Integer.parseInt(args[1]);

            target.addExperience(experience);
            player.sendMessage("§d§lLeveling §8:: §fОпыт игрока был изменен на §b" + NumberUtil.spaced(target.getExperience()) + " EXP");

            sendLevelUpdatePacket(target.getName());
        });
    }

    @CommandArgument(aliases = {"remove", "delete"})
    protected void take(Player player, String[] args) {
        checkPermission(player, plazmixUser -> {

            if (args.length < 2) {
                player.sendMessage("§d§lLeveling §8:: §fИспользуйте - §d/exp take <ник игрока> <опыт>");
                return;
            }

            PlazmixUser target = PlazmixUser.of(args[0]);

            if (target.getPlayerId() <= 0) {
                player.sendMessage("§d§lLeveling §8:: §сОшибка, игрок не найден в базе данных :(");
                return;
            }

            if (!ValidateUtil.isNumber(args[1])) {
                player.sendMessage("§c" + args[1] + " не является числом :(");
                return;
            }

            int experience = Integer.parseInt(args[1]);

            target.takeExperience(experience);
            player.sendMessage("§d§lLeveling §8:: §fОпыт игрока был изменен на §b" + NumberUtil.spaced(target.getExperience()) + " EXP");

            sendLevelUpdatePacket(target.getName());
        });
    }

    @CommandArgument(aliases = "update")
    protected void set(Player player, String[] args) {
        checkPermission(player, plazmixUser -> {

            if (args.length < 2) {
                player.sendMessage("§d§lLeveling §8:: §fИспользуйте - §d/exp set <ник игрока> <опыт>");
                return;
            }

            PlazmixUser target = PlazmixUser.of(args[0]);

            if (target.getPlayerId() <= 0) {
                player.sendMessage("§d§lLeveling §8:: §сОшибка, игрок не найден в базе данных :(");
                return;
            }

            if (!ValidateUtil.isNumber(args[1])) {
                player.sendMessage("§c" + args[1] + " не является числом :(");
                return;
            }

            int experience = Integer.parseInt(args[1]);

            target.setExperience(experience);
            player.sendMessage("§d§lLeveling §8:: §fОпыт игрока был изменен на §b" + NumberUtil.spaced(target.getExperience()) + " EXP");

            sendLevelUpdatePacket(target.getName());
        });
    }


    private void checkPermission(@NonNull Player player, @NonNull Consumer<PlazmixUser> onSuccess) {
        PlazmixUser plazmixUser = PlazmixUser.of(player);

        if (!plazmixUser.getGroup().isAdmin()) {
            plazmixUser.localization().sendMessage(localizationResource -> localizationResource.getMessage("MINIMAL_GROUP")
                    .replace("%group%", Group.DEVELOPER.getColouredName())
                    .toText());
            return;
        }

        onSuccess.accept(plazmixUser);
    }

    private void sendLevelUpdatePacket(@NonNull String playerName) {
        PlazmixUser plazmixUser = PlazmixUser.of(playerName);

        SPlayerLevelUpdatePacket levelUpdatePacket = new SPlayerLevelUpdatePacket(
                plazmixUser.getName(),

                plazmixUser.getLevel(),
                plazmixUser.getExperience(),
                plazmixUser.getMaxExperience()
        );

        CoreConnector.getInstance().sendPacket(levelUpdatePacket);
    }

}
