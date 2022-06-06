package net.plazmix.utility.achievement;

public final class AchievementManager {

    public static final AchievementManager INSTANCE = new AchievementManager();

    public AchievementTask getAchievementTask(int achievementID, int taskID) {
        return new AchievementTask(achievementID, taskID);
    }

}
