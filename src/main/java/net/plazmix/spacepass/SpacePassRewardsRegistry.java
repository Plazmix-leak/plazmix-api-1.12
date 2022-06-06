package net.plazmix.spacepass;

import lombok.NonNull;
import net.plazmix.inventory.impl.BasePaginatedInventory;
import net.plazmix.inventory.impl.BaseSimpleInventory;
import net.plazmix.utility.DateUtil;
import net.plazmix.utility.ItemUtil;
import net.plazmix.utility.NumberUtil;
import net.plazmix.utility.PercentUtil;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public final class SpacePassRewardsRegistry extends BasePaginatedInventory {

    public static final LinkedList<SpacePassReward> purchasableRewardsList  = new LinkedList<>();
    public static final LinkedList<SpacePassReward> bonusRewardsList = new LinkedList<>();

    public static void registerReward(boolean isBonus, int maxExperience,

                                      @NonNull String name,
                                      @NonNull MaterialData icon,

                                      @NonNull Consumer<Player> playerConsumer) {

        SpacePassReward passReward = new SpacePassReward(maxExperience, name, icon, playerConsumer);

        if (isBonus) {
            bonusRewardsList.add(passReward);

        } else {

            purchasableRewardsList.add(passReward);
        }
    }

    public static void openPassMenu(@NonNull Player player) {
        new SpacePassRewardsRegistry().openInventory(player);
    }

    private final LinkedList<Integer> progressSlots = new LinkedList<>(
            Arrays.asList(14, 15, 16, 17, 18, 27, 36, 35, 34, 33, 32, 31, 30, 29, 28, 19, 10, 11, 12, 13)
    );

    private SpacePassRewardsRegistry() {
        super("SpacePass :: Сезонные награды", 6);
    }

    @Override
    public void drawInventory(Player player) {
        SpacePass playerPass = SpacePassSqlHandler.INSTANCE.getPlayerPass(player);

        addRowToMarkup(3, 1);

        int currentExperience = playerPass.getExperience();
        int bufferedExp = 0;

        AtomicReference<SpacePassReward> currentPurchasableItem = new AtomicReference<>();

        LinkedList<String> informationLore = new LinkedList<>();
        informationLore.add("");
        informationLore.add("§fСостояние пропуска: " + (playerPass.isActivated() ? "§d§l§oПРИОБРЕТЕНО" : "§cНе приобретено"));

        if (SpacePass.START_SEASON_MILLIS > System.currentTimeMillis()) {
            informationLore.add("§fСезон начнется: §a" + DateUtil.formatTime(SpacePass.START_SEASON_MILLIS, "dd MMM yyyy"));
        }

        else if (SpacePass.END_SEASON_MILLIS > System.currentTimeMillis()) {
            informationLore.add("§fКонец сезона: §c" + DateUtil.formatTime(SpacePass.END_SEASON_MILLIS, "dd MMM yyyy"));
            informationLore.add("§fДо конца осталоьсь: §e" + NumberUtil.formatting((int) Math.max(1, TimeUnit.MILLISECONDS.toDays(SpacePass.END_SEASON_MILLIS - System.currentTimeMillis())), NumberUtil.NumberTimeUnit.DAYS));

        } else {

            informationLore.add("§c§lВНИМАНИЕ! §cСезон завершен, награды");
            informationLore.add("§cбольше нельзя будет получить!");
        }

        informationLore.add("");

        if (!playerPass.isActivated()) {

            informationLore.add("§7Прибрести статус §d§lSpacePass §7можно здесь,");
            informationLore.add("§7нажав по ЛКМ по этой табличке!");
            informationLore.add("");
            informationLore.add("§7Стоимость покупки: §e60 плазмы");
        } else {

            informationLore.add("§7Статус §d§lSpacePass §7уже §aприобретен §7для вас,");
            informationLore.add("§7играйте и получайте награды на §dPlazmix");
        }

        // Вывод общей статистики
        setClickItem(5, ItemUtil.newBuilder(Material.SIGN)
                        .setName(SpacePass.SEASON_TITLE + (!playerPass.isActivated() ? "" : " §7(✬)"))
                        .setLore(informationLore)
                        .build(),

                (player1, event) -> {

                    if (!playerPass.isActivated()) {
                        new TyPassConfirmationPurchase().openInventory(player);
                    }
                });

        // Платные награды пропуска
        boolean isStageEnded = false;
        int purchasableCounter = 0;

        for (SpacePassReward passReward : purchasableRewardsList) {
            purchasableCounter++;

            if (currentExperience > 0) {
                if (currentExperience >= passReward.getMaxExperience()) {
                    currentExperience -= passReward.getMaxExperience();

                } else {

                    currentPurchasableItem.set(passReward);

                    bufferedExp = currentExperience;
                    currentExperience = 0;

                    isStageEnded = purchasableCounter - (currentPage * 7) >= 7;
                }
            }

            // Отрисовать платную основу
            addOriginalItemToMarkup(ItemUtil.newBuilder(passReward.getIcon())
                    .setGlowing(currentExperience > passReward.getMaxExperience())

                    .setAmount(purchasableCounter - (currentPage * 7))

                    .setName(passReward.getName())
                    .addLore("")
                    .addLore("§7Собрано:")
                    .addLore(" §b" + NumberUtil.spaced(currentExperience >= passReward.getMaxExperience() ? passReward.getMaxExperience() : currentExperience) + " из " + NumberUtil.spaced(passReward.getMaxExperience()) + " опыта")

                    .build());
        }

        currentExperience = bufferedExp;

        // Бесплатные награды пропуска
        int[] freeSlots = {40, 41, 42};

        int freeCounter = 0;
        int freeCurrentMaxIndex = (currentPage + 1) * 3;

        for (SpacePassReward passReward : bonusRewardsList) {
            freeCounter++;

            if (freeCurrentMaxIndex - 3 <= freeCounter && freeCounter <= freeCurrentMaxIndex) {
                int slotIndex = (freeCounter - 1) % 3;

                setOriginalItem(freeSlots[slotIndex], ItemUtil.newBuilder(passReward.getIcon())
                        .setAmount(slotIndex + 1)
                        .setGlowing(isStageEnded && currentPurchasableItem.get() != null && currentExperience > currentPurchasableItem.get().getMaxExperience())

                        .setName("§bБонусная награда:")
                        .addLore(" " + passReward.getName())
                        .addLore("")
                        .addLore(" §7Будет получено только после")
                        .addLore(" §7завершения " + (currentPage + 1) + " стадии")

                        .build());
            }
        }

        // Прогресс платной основы в виде стекл
        if (currentPurchasableItem.get() == null) {
            return;
        }

        int progress = (int) (PercentUtil.getPercent(currentExperience, currentPurchasableItem.get().getMaxExperience()) / (100d / progressSlots.size()));
        int currentIndex = 0;

        for (int progressSlot : progressSlots) {
            currentIndex++;

            setOriginalItem(progressSlot, ItemUtil.newBuilder(Material.STAINED_GLASS_PANE)
                    .setDurability(currentIndex <= progress ? 5 : 7)

                    .setName(currentIndex <= progress ? "§a" + (currentIndex * 5) + "%" : ChatColor.RESET.toString())
                    .build());
        }
    }

    private static class TyPassConfirmationPurchase extends BaseSimpleInventory {

        public TyPassConfirmationPurchase() {
            super("Подтверждение покупки SpacePass", 3);
        }

        @Override
        public void drawInventory(Player player) {
            PlazmixUser plazmixUser = PlazmixUser.of(player);

            setClickItem(13, ItemUtil.newBuilder(Material.STAINED_GLASS)

                            .setDyeColor(DyeColor.GREEN)
                            .setName("§aПодтвердить")

                            .addLore("§7Нажав на данную кнопку вы подтвердите")
                            .addLore("§7покупку статуса §d§lSpacePass§7, тем самым")
                            .addLore("§7с Вашего счета спишется §e60 плазмы")

                            .build(),

                    (player1, event) -> {
                        player.closeInventory();

                        if (plazmixUser.getGolds() < 60) {
                            player.sendMessage("§d§lPlazmix §8:: §cОшибка, для покупки статуса §d§lSpacePass §cнеобходимо иметь §e60 плазмы");
                            return;
                        }

                        plazmixUser.getPass().purchaseActivation();

                        player.sendMessage("§d§lPlazmix §8:: §fСтатус §b§lSpacePass §fбыл успешно приобретен!");
                        player.sendMessage(" §fПриносим огромную §aблагодарность §fза покупку и желаем §eприятной игры§f!");

                        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
                    });

            setClickItem(15, ItemUtil.newBuilder(Material.STAINED_GLASS)

                            .setDyeColor(DyeColor.RED)
                            .setName("§cОтменить")

                            .addLore("§7Нажав на данную кнопку вы отмените")
                            .addLore("§7покупку статуса §d§lSpacePass§7, после")
                            .addLore("§7обратно перейдете в предыдущее меню")

                            .build(),

                    (player1, event) -> openPassMenu(player1));
        }

    }

}
