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

    @Override
    public int getSpeed() {
        return speed;
    }

    @Override
    public int getAttackPower() {
        return attack;
    }

    @Override
    public int getDefense() {
        return defense;
    }

    public int getExp() {
        return exp;
    }

    public int getGold() {
        return gold;
    }

    @Override
    public String getTextureKey() {
        return textureKey;
    }
}
