package net.plazmix.skin.command;

import net.plazmix.command.BaseCommand;
import net.plazmix.coreconnector.core.group.Group;
import net.plazmix.coreconnector.utility.mojang.MojangApi;
import net.plazmix.skin.PlayerSkinManager;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SkinSetCommand extends BaseCommand<Player> {

    public SkinSetCommand() {
        super("skin", "setskin", "skinset");

        setMinimalGroup(Group.STAR);
    }

    @Override
    protected void onExecute(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage("§cОшибка, пишите - /skin <ник скина>");
            return;
        }

        String skin = args[0];

        if (MojangApi.getUserUUID(skin) == null) {
            player.sendMessage("§cОшибка, лицензионный аккаунт с указанным ником не существует!");
            return;
        }

        String originalSkinName = MojangApi.getOriginalName(skin);
        player.sendMessage("§6§lСкины §8:: §fПроизводится установка скина §e" + originalSkinName);

        PlayerSkinManager.INSTANCE.setSkin(true, player, originalSkinName);

        player.playSound(player.getLocation(), Sound.ENTITY_CAT_AMBIENT, 1, 2);
        player.sendMessage("§6§lСкины §8:: §fЛицензионный скин §e" + originalSkinName + " §fбыл найден и §aуспешно §fустановлен для Вас!");
    }

}
