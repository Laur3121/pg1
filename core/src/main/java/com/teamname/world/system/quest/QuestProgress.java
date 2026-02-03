package com.teamname.world.system.quest;

public class QuestProgress {
    public int questId;
    public int currentStepIndex;
    public boolean isCompleted;

    public QuestProgress() {
    }

    public QuestProgress(int questId) {
        this.questId = questId;
        this.currentStepIndex = 0;
        this.isCompleted = false;
    }
}
