package net.plazmix.utility.quest;

public final class QuestManager {

    public static final QuestManager INSTANCE = new QuestManager();

    public QuestTask getQuestTask(int questID, int taskID) {
        return new QuestTask(questID, taskID);
    }

}
