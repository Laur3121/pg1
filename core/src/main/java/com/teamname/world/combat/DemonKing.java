package com.teamname.world.combat;

/**
 * 魔王クラス (Boss)
 * レベルに応じてステータスが変動するクラス
 */
public class DemonKing extends Monster {

    // Hero Base Stats (approx): HP 100, MP 30, Pow 10, Blk 10, Qck 5, Lck 10
    // Demon King = 3x Hero
    private static final int BASE_HP = 300; // 3x Hero HP
    private static final int BASE_MP = 90; // 3x Hero MP
    private static final int BASE_POWER = 30; // 3x Hero Power
    private static final int BASE_BLOCK = 30; // 3x Hero Block
    private static final int BASE_QUICK = 0; // Demon King is very slow (0 speed)
    private static final int BASE_LUCKY = 30; // 3x Hero Lucky

    private static final int BASE_EXP = 50000;
    private static final int BASE_GOLD = 100000;

    /**
     * コンストラクタ
     * 
     * @param level 魔王のレベル
     */
    public DemonKing(int level) {
        // Linear scaling: Base + (Base * Level)
        // At Lv 50: 300 * 50 = 15000 HP (vs Hero ~5000)
        // Power: 30 * 50 = 1500 (vs Hero ~500)
        super("Demon King",
                calcStat(BASE_HP, level),
                calcStat(BASE_POWER, level),
                calcStat(BASE_BLOCK, level),
                calcStat(BASE_QUICK, level),
                calcExp(BASE_EXP, level),
                calcGold(BASE_GOLD, level),
                "demon_king");

        // Set other specific ICombatant stats that Monster might handle differently or
        // not via constructor
        // Monster constructor sets hp, attack, defense, speed, exp, gold.
        // We need to set scaling MP, Lucky and ensure Level is set.

        setMaxMP(calcStat(BASE_MP, level));
        setCurrentMP(getMaxMP());
        setLevel(level);
        setLucky(calcStat(BASE_LUCKY, level));
    }

    private void setMaxMP(int mp) {
        // Monster doesn't expose public setter for maxMp/currentMp easily in
        // the constructor we called, so we might need a way to set them.
        // Assuming Monster has protected logic or we need to access fields?
        // Wait, Monster has private fields. We need to check if we can set them.
        // Actually Monster probably implements ICombatant getters.
        // But Monster class fields are private.
        // We should add protected setters or just override the getters from ICombatant
        // using our own fields in this subclass.
    }

    // Since Monster fields are private and no setters mentioned in snippet,
    // we will store our own scalable stats and override getters.

    private int myMaxMp;
    private int myCurrentMp;
    private int myLevel;
    private int myLucky;

    // Override getters to return correct values
    @Override
    public int getMaxMP() {
        return myMaxMp;
    }

    @Override
    public int getCurrentMP() {
        return myCurrentMp;
    }

    // @Override removed as it's not in ICombatant yet, or just a helper
    public void consumeMP(int value) {
        myCurrentMp = Math.max(0, myCurrentMp - value);
    }

    @Override
    public int getLevel() {
        return myLevel;
    }

    @Override
    public int getLucky() {
        return myLucky;
    }

    // Helpers
    private void setLevel(int lv) {
        this.myLevel = lv;
    }

    private void setLucky(int v) {
        this.myLucky = v;
    }

    private void setCurrentMP(int v) {
        this.myCurrentMp = v;
    }

    // Helper to calculate stats
    private static int calcStat(int base, int level) {
        // Simple linear scaling but steep: Base * Level
        return base * level;
    }

    private static int calcExp(int base, int level) {
        return base * level;
    }

    private static int calcGold(int base, int level) {
        return base * level;
    }
}
