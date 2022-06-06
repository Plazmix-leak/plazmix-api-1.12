package net.plazmix.skin.command;

import lombok.NonNull;
import net.plazmix.command.BaseCommand;
import net.plazmix.coreconnector.core.group.Group;
import net.plazmix.coreconnector.module.type.skin.PlayerSkin;
import net.plazmix.inventory.impl.BasePaginatedInventory;
import net.plazmix.skin.PlayerSkinManager;
import net.plazmix.utility.DateUtil;
import net.plazmix.utility.ItemUtil;
import net.plazmix.utility.mojang.MojangUtil;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Collection;

public class SkinHistoryCommand extends BaseCommand<Player> {

    public SkinHistoryCommand() {
        super("skins", "skinhistory");

        setMinimalGroup(Group.STAR);
    }

    @Override
    protected void onExecute(Player player, String[] args) {
        if (args.length == 0) {
            new SkinsHistoryInventory(player.getName()).openInventory(player);
            return;
        }

        PlazmixUser targetPlazmixUser = PlazmixUser.of(args[0]);

        if (targetPlazmixUser.getPlayerId() < 0) {
            PlazmixUser.of(player).localization().sendMessage("NO_PLAYER");

            return;
        }

        new SkinsHistoryInventory(targetPlazmixUser.getName()).openInventory(player);
    }

    private static class SkinsHistoryInventory extends BasePaginatedInventory {
        private final String targetName;

        public SkinsHistoryInventory(@NonNull String targetName) {
            super("История скинов", 6);

            this.targetName = targetName;
        }

        @Override
        public void drawInventory(Player player) {
            Collection<PlayerSkin> skinsHistory = PlayerSkinManager.INSTANCE.getSkinsHistory(targetName);
            PlayerSkin currentSkin = PlayerSkinManager.INSTANCE.getCurrentPlayerSkin(targetName);

            addRowToMarkup(3, 2);
            addRowToMarkup(4, 2);

            setOriginalItem(5, ItemUtil.newBuilder(Material.SIGN)
                    .setName("§aОбщая информация")

                    .addLore("§7Всего скинов: §f" + skinsHistory.size())
                    .addLore("§7Сейчас установлен: §f" + currentSkin.getSkinName())

                    .build());

            for (PlayerSkin playerSkinInfo : skinsHistory) {
                addClickItemToMarkup(ItemUtil.newBuilder(Material.SKULL_ITEM)
                                .setDurability(3)
                                .setName((!currentSkin.equals(playerSkinInfo)
                                        ? ChatColor.YELLOW
                                        : ChatColor.GREEN)

                                        + playerSkinInfo.getSkinName())

                                .addLore("")
                                .addLore("§7Был установлен:")
                                .addLore(" §f" + DateUtil.formatTime(playerSkinInfo.getDate().getTime(), DateUtil.DEFAULT_DATETIME_PATTERN))
                                .addLore("")

                                .addLore(!currentSkin.equals(playerSkinInfo)
                                        ? "§e▸ Нажмите, чтобы сменить!"
                                        : "§a▸ Скин сейчас установлен!")

                                .setMojangSkin(MojangUtil.getMojangSkinOrDefault(playerSkinInfo.getSkinName()))
                                .build(),

                        (player1, event) -> {

                            if (player.getName().equalsIgnoreCase(targetName) && currentSkin.equals(playerSkinInfo)) {
                                return;
                            }

                            Bukkit.dispatchCommand(player, "skinset " + playerSkinInfo.getSkinName());
                            player.closeInventory();
                        });
            }

            if (skinsHistory.isEmpty()) {
                setOriginalItem(23, ItemUtil.newBuilder(Material.GLASS_BOTTLE)
                        .setName("§cНичего не найдено!")
                        .addLore("§7История изменения скинов у данного игрока пуста")
                        .build());
            }
        }

    }

}
