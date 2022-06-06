package net.plazmix.command.impl;

import net.plazmix.command.BaseCommand;
import net.plazmix.inventory.impl.BaseSimpleInventory;
import net.plazmix.utility.ItemUtil;
import net.plazmix.utility.NumberUtil;
import net.plazmix.utility.leveling.LevelingUtil;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LevelConvertCommand extends BaseCommand<Player> {

    public LevelConvertCommand() {
        super("lvlconvert");
    }

    @Override
    protected void onExecute(Player player, String[] args) {
        int currentLevel = PlazmixUser.of(player).getLevel();

        if (currentLevel <= 5) {
            player.sendMessage("§d§lLeveling §8:: §cОшибка, конвертация уровня доступна только от 5 уровня!");

            return;
        }

        new LevelConvertInventory(currentLevel).openInventory(player);
    }


    private static class LevelConvertInventory extends BaseSimpleInventory {
        private int levelCount = 5;

        public LevelConvertInventory(int levelCount) {
            super("Конвертация уровня", 5);

            this.levelCount = levelCount;
        }

        @Override
        public void drawInventory(Player player) {
            PlazmixUser plazmixUser = PlazmixUser.of(player);

            // Inventory frames.
            ItemStack frameItem = ItemUtil.newBuilder(Material.STAINED_GLASS_PANE)
                    .setDurability(15)

                    .setName(ChatColor.RESET.toString())
                    .build();

            for (int slot = 1; slot <= 9; slot++)
                setOriginalItem(slot, frameItem);

            setOriginalItem(13, frameItem);
            setOriginalItem(14, frameItem);
            setOriginalItem(15, frameItem);


            // Information items.
            setClickItem(5, ItemUtil.newBuilder(Material.EMERALD)
                            .setName("§aКонвертация уровня")

                            .addLore("")
                            .addLore("§7Ваш баланс: §6" + NumberUtil.formattingSpaced(plazmixUser.getCoins(), "коин", "коина", "коинов"))
                            .addLore("§7Ваш уровень: §b" + NumberUtil.spaced(plazmixUser.getLevel()))
                            .addLore("")
                            .addLore("§8Конвертация:")
                            .addLore(" §7Вы отдаете §b" + NumberUtil.spaced(levelCount) + " уровень§7, получая")
                            .addLore(" §7взамен §e" + NumberUtil.formattingSpaced(getConvertedCoinsByLevel(), "коин", "коина", "коинов") + " §7на свой баланс")
                            .addLore("")
                            .addLore("§a▸ Нажмите, чтобы конвертировать")

                            .build(),

                    (player1, event) -> {

                        // Add coins.
                        plazmixUser.addCoins(getConvertedCoinsByLevel());


                        // Change level.
                        int experiencePercent = NumberUtil.getIntPercent(plazmixUser.getExperience(), plazmixUser.getMaxExperience());
                        int playerExperience = plazmixUser.getTotalExperience() - plazmixUser.getExperience();

                        for (int level = plazmixUser.getLevel(); level > plazmixUser.getLevel() - levelCount; level--) {
                            playerExperience -= LevelingUtil.getExpFromLevelToNext(level - 1);
                        }

                        playerExperience += NumberUtil.getIntNumberByPercent(experiencePercent, (int) LevelingUtil.getExpFromLevelToNext(plazmixUser.getLevel() - levelCount));
                        plazmixUser.setExperience(Math.max(0, playerExperience));


                        // Announce.
                        player.sendMessage("§d§lLeveling §8:: §fВы успешно конвертировали §b" + levelCount + " уровень §fв §e"
                                + NumberUtil.formattingSpaced(getConvertedCoinsByLevel(), "коин", "коина", "коинов"));

                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_TRADING, 1, 1);
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

                        player.closeInventory();
                    });

            setOriginalItem(32, ItemUtil.newBuilder(Material.EXP_BOTTLE)
                    .setAmount(levelCount)

                    .setName("§e" + NumberUtil.spaced(levelCount) + " уровень §f-> §e" + NumberUtil.formattingSpaced(getConvertedCoinsByLevel(), "коин", "коина", "коинов"))
                    .build());

            // Change level items.
            if (levelCount + 1 <= plazmixUser.getLevel()) {
                setClickItem(31, ItemUtil.newBuilder(Material.STAINED_GLASS_PANE)
                                .setDurability(5)
                                .setAmount(1)

                                .setName("§a+1")
                                .build(),

                        (player1, inventoryClickEvent) -> {
                            levelCount++;

                            updateInventory(player);
                        });
            }

            if (levelCount + 5 <= plazmixUser.getLevel()) {
                setClickItem(30, ItemUtil.newBuilder(Material.STAINED_GLASS_PANE)
                                .setDurability(5)
                                .setAmount(5)

                                .setName("§a+5")
                                .build(),

                        (player1, inventoryClickEvent) -> {
                            levelCount += 5;

                            updateInventory(player);
                        });
            }

            if (levelCount + 10 <= plazmixUser.getLevel()) {
                setClickItem(29, ItemUtil.newBuilder(Material.STAINED_GLASS_PANE)
                                .setDurability(5)
                                .setAmount(10)

                                .setName("§a+10")
                                .build(),

                        (player1, inventoryClickEvent) -> {
                            levelCount += 10;

                            updateInventory(player);
                        });
            }

            // minus to item amount
            if (levelCount - 1 > 0) {
                setClickItem(33, ItemUtil.newBuilder(Material.STAINED_GLASS_PANE)
                                .setDurability(14)
                                .setAmount(1)

                                .setName("§c-1")
                                .build(),

                        (player1, inventoryClickEvent) -> {
                            levelCount--;

                            updateInventory(player);
                        });
            }

            if (levelCount - 5 > 0) {
                setClickItem(34, ItemUtil.newBuilder(Material.STAINED_GLASS_PANE)
                                .setDurability(14)
                                .setAmount(5)

                                .setName("§c-5")
                                .build(),

                        (player1, inventoryClickEvent) -> {
                            levelCount -= 5;

                            updateInventory(player);
                        });
            }

            if (levelCount - 10 > 0) {
                setClickItem(35, ItemUtil.newBuilder(Material.STAINED_GLASS_PANE)
                                .setDurability(14)
                                .setAmount(10)

                                .setName("§c-10")
                                .build(),

                        (player1, inventoryClickEvent) -> {
                            levelCount -= 10;

                            updateInventory(player);
                        });
            }
        }

        private int getConvertedCoinsByLevel() {
            return (levelCount * 1_000);
        }

    }

}
