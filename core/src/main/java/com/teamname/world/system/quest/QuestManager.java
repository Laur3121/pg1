package com.teamname.world.system.quest;

import com.teamname.world.system.DataLoader;
import java.util.HashMap;
import java.util.Map;

public class QuestManager {
    // Master data
    private Map<Integer, QuestData> questDatabase;

    // Active progress
    // We map Quest ID -> Progress
    private Map<Integer, QuestProgress> activeQuests;

    public QuestManager() {
        this.activeQuests = new HashMap<>();
        this.questDatabase = new HashMap<>(); // Will be populated from DataLoader
    }

    public void initialize(DataLoader dataLoader) {
        if (dataLoader != null && dataLoader.questData != null) {
            this.questDatabase = dataLoader.questData;
        }
    }

    public void startQuest(int questId) {
        if (!questDatabase.containsKey(questId)) {
            System.err.println("Quest ID " + questId + " not found in database.");
            return;
        }
        if (activeQuests.containsKey(questId)) {
            System.out.println("Quest " + questId + " is already active.");
            return;
        }

        QuestData data = questDatabase.get(questId);
        QuestProgress progress = new QuestProgress(questId);
        activeQuests.put(questId, progress);

        System.out.println("Started Quest: " + data.title);
    }

    public void checkProgress(String condition) {
        for (QuestProgress progress : activeQuests.values()) {
            if (progress.isCompleted) continue;

            QuestData data = questDatabase.get(progress.questId);
            if (data == null || data.steps == null) continue;

            if (progress.currentStepIndex < data.steps.size()) {
                QuestStepData step = data.steps.get(progress.currentStepIndex);
                if (step.condition != null && step.condition.equals(condition)) {
                     advanceStep(progress, data);
                }
            }
        }
    }

    private void advanceStep(QuestProgress progress, QuestData data) {
        System.out.println("Quest " + data.title + ": Step " + progress.currentStepIndex + " completed!");
        
        // Give rewards here if needed
        QuestStepData step = data.steps.get(progress.currentStepIndex);
        // (Reward logic would go here, e.g. adding gold to GameState)

        int nextIndex = step.nextStepIndex;
        if (nextIndex == -1) {
            progress.isCompleted = true;
            System.out.println("Quest " + data.title + " COMPLETED!");
            // Final reward?
        } else {
            progress.currentStepIndex = nextIndex;
        }
    }

    public Map<Integer, QuestProgress> getActiveQuests() {
        return activeQuests;
    }

    // For saving
    public void setActiveQuests(Map<Integer, QuestProgress> savedData) {
        this.activeQuests = savedData;
    }
}
