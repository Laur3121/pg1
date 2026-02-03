package com.teamname.world.combat;

public class Monster implements ICombatant {
    private String name;
    private int maxHp;
    private int currentHp;
    private int attack;
    private int defense;
    private int speed;
    private int exp;
    private int gold;
    private String textureKey;

    // New stats
    private int currentMp;
    private int maxMp;
    private int level;
    private int lucky;

    public Monster(String name, int hp, int attack, int defense, int speed, int exp, int gold) {
        this(name, hp, attack, defense, speed, exp, gold, "1"); // デフォルトキー
    }

    public Monster(String name, int hp, int attack, int defense, int speed, int exp, int gold, String textureKey) {
        this.name = name;
        this.maxHp = hp;
        this.currentHp = hp;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.exp = exp;
        this.gold = gold;
        this.textureKey = textureKey;

        // Default values for new stats
        this.currentMp = 0;
        this.maxMp = 0;
        this.level = 1;
        this.lucky = 0;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getCurrentHP() {
        return currentHp;
    }

    @Override
    public int getMaxHP() {
        return maxHp;
    }

    @Override
    public boolean isAlive() {
        return currentHp > 0;
    }

    @Override
    public void takeDamage(int damage) {
        currentHp -= damage;
        if (currentHp < 0)
            currentHp = 0;
    }

    @Override
    public void heal(int amount) {
        currentHp += amount;
        if (currentHp > maxHp)
            currentHp = maxHp;
    }

    // Legacy methods removed or kept as internal getters without Override
    public int getSpeed() {
        return speed;
    }

    public int getAttackPower() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    // Removed strict getExp() without override to avoid ambiguity/duplication if
    // logic is same
    // But kept getters for internal use if needed, but renamed or just direct
    // access since fields are private.
    // Actually, ICombatant requires getExp(), so I will implement it below.

    public int getGold() {
        return gold;
    }

    @Override
    public String getTextureKey() {
        return textureKey;
    }

    // ICombatant New Methods
    @Override
    public int getCurrentMP() {
        return currentMp;
    }

    @Override
    public int getMaxMP() {
        return maxMp;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public int getPower() {
        return attack;
    } // Map attack to power

    @Override
    public int getBlock() {
        return defense;
    } // Map defense to block

    @Override
    public int getLucky() {
        return lucky;
    }

    @Override
    public int getQuick() {
        return speed;
    } // Map speed to quick

    @Override
    public int getExp() {
        return exp;
    } // Implement interface method

    @Override
    public void gainExp(int exp) {
        // Managers usually don't gain EXP, but interface requires it
    }
}
