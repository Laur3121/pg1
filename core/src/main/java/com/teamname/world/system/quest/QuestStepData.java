package com.teamname.world.system.quest;

public class QuestStepData {
    public String condition; // e.g., "TALK_KING", "KILL_BOSS"
    public String description;
    public int nextStepIndex; // -1 if this is the final step
    public int rewardGold;
    public int rewardExp;
}
