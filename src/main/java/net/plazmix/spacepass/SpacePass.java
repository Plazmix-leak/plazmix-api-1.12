package net.plazmix.spacepass;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.plazmix.coreconnector.CoreConnector;
import net.plazmix.coreconnector.module.type.NetworkModule;
import net.plazmix.utility.DateUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Timestamp;

@AllArgsConstructor
@Getter
public class SpacePass {

    public static final String SEASON_TITLE         = ("§b§lСЕЗОН 1: НОВОГОДНИЙ СЕЗОН");

    public static final long START_SEASON_MILLIS    = DateUtil.parsePatternToMillis("dd.MM.yy", "01.01.22");
    public static final long END_SEASON_MILLIS      = DateUtil.parsePatternToMillis("dd.MM.yy", "28.02.22");


    private final int playerId;

    private final Timestamp purchaseDate;

    private int experience;
    private boolean activated;


    public void purchaseActivation() {
        this.activated = true;

        SpacePassSqlHandler.INSTANCE.purchaseActivation(playerId);
    }

    public void addExperience(int experience) {
        if (System.currentTimeMillis() < START_SEASON_MILLIS || System.currentTimeMillis() > END_SEASON_MILLIS) {
            return;
        }

        Player player = Bukkit.getPlayerExact(NetworkModule.getInstance().getPlayerName(playerId));

        if (player == null || !player.isOnline()) {
            return;
        }

        int newExperience = Math.min(getMaxExperience(), this.experience + experience);
        CoreConnector.getInstance().getMysqlConnection().execute(true, SpacePassSqlHandler.ADD_EXP_PASS_QUERY, experience, playerId);

        if (isActivated()) {

            for (int i = getCurrentRewardId(this.experience); i <= getPassedRewardId(newExperience); i++) {
                SpacePassRewardsRegistry.purchasableRewardsList.get(i).getPlayerConsumer().accept(player);

                // Выдача бесплатных наград стадии
                if (i % 7 == 0) {

                    int freeCounter = 0;
                    for (SpacePassReward freeReward : SpacePassRewardsRegistry.bonusRewardsList) {
                        freeCounter++;

                        if (freeCounter > ((i / 7) - 1) * 3 && freeCounter <= (i / 7) * 3) {
                            freeReward.getPlayerConsumer().accept(player);
                        }
                    }
                }
            }
        }

        this.experience = newExperience;
    }

    public int getMaxExperience() {
        return SpacePassRewardsRegistry.purchasableRewardsList.stream().mapToInt(SpacePassReward::getMaxExperience).sum();
    }

    public SpacePassReward getCurrentReward(int experience) {
        return SpacePassRewardsRegistry.purchasableRewardsList.get(getCurrentRewardId(experience));
    }

    public SpacePassReward getPassedReward(int experience) {
        return SpacePassRewardsRegistry.purchasableRewardsList.get(getPassedRewardId(experience));
    }

    public int getCurrentRewardId(int experience) {

        int indexCounter = 0;
        int currentExp = experience;

        for (SpacePassReward passReward : SpacePassRewardsRegistry.purchasableRewardsList) {

            if (currentExp >= passReward.getMaxExperience()) {
                currentExp -= passReward.getMaxExperience();

            } else {

                return indexCounter;
            }

            indexCounter++;
        }

        return 0;
    }

    public int getPassedRewardId(int experience) {

        int indexCounter = 0;
        int currentExp = experience;

        for (SpacePassReward passReward : SpacePassRewardsRegistry.purchasableRewardsList) {

            if (currentExp >= passReward.getMaxExperience()) {
                currentExp -= passReward.getMaxExperience();

            } else {

                return Math.max(0, indexCounter - 1);
            }

            indexCounter++;
        }

        return 0;
    }


}
