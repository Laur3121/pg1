package com.teamname.world.system;

import com.teamname.world.combat.core.ICombatant;

public class Character implements ICombatant {
    public String name;
    public int level;
    public int exp;

    public int maxHp;
    public int currentHp;

    public int maxMp;
    public int currentMp;

    public int str;
    public int def;

    public int equippedWeaponId;
    public int equippedArmorId;

    // New Stats
    public int power;
    public int block;
    public int lucky;
    public int quick;
    public int nextExp; // EXP needed for next level

    public Character() {
        // デフォルトコンストラクタ（ロード用）
    }

    public Character(String name, int hp, int mp, int str, int def) {
        this(name, hp, mp, str, def, 10, 5, 10);
    }

    public Character(String name, int hp, int mp, int str, int def, int lucky, int quick, int power) {
        this.name = name;
        this.maxHp = hp;
        this.currentHp = hp;
        this.maxMp = mp;
        this.currentMp = mp;
        this.str = str;
        this.def = def;
        this.level = 1;
        this.exp = 0;
        this.nextExp = 100; // Initial next exp
        this.equippedWeaponId = -1;
        this.equippedArmorId = -1;

        // Initialize new stats
        this.power = power;
        this.block = def; // Default block to def
        this.lucky = lucky;
        this.quick = quick;
    }

    // ICombatant implementation
    private com.teamname.world.combat.DataLoaderProvider dataLoaderProvider;

    public void setDataLoaderProvider(com.teamname.world.combat.DataLoaderProvider provider) {
        this.dataLoaderProvider = provider;
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

    // Legacy method - Override removed
    public int getSpeed() {
        // Quick replaces speed
        return quick;
    }

    // Legacy method - Override removed
    public int getAttackPower() {
        // Power replaces attack power
        return power;
    }

    // Legacy method - Override removed
    public int getDefense() {
        // Block replaces defense
        return block;
    }

    // ステータス計算ヘルパー
    public int getAttack(DataLoader loader) {
        int totalAtk = this.str;
        if (equippedWeaponId != -1 && loader != null) {
            ItemData weapon = loader.getItemDataById(equippedWeaponId);
            if (weapon != null) {
                totalAtk += weapon.power;
            }
        }
        return totalAtk;
    }

    public int getDefense(DataLoader loader) {
        int totalDef = this.def;
        if (equippedArmorId != -1 && loader != null) {
            ItemData armor = loader.getItemDataById(equippedArmorId);
            if (armor != null) {
                totalDef += armor.defense;
            }
        }
        return totalDef;
    }

    @Override
    public String getTextureKey() {
        // 名前や職業に応じて返すキーを変える
        if (name.equalsIgnoreCase("Warrior") || name.equalsIgnoreCase("Hero"))
            return "warrior";
        if (name.equalsIgnoreCase("Mage"))
            return "evilmage"; // 味方メイジの画像があればそれに変える
        if (name.equalsIgnoreCase("Priest"))
            return "archer"; // 仮
        return "warrior"; // デフォルト
    }

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
        // Weapon bonus could be added here
        return power;
    }

    @Override
    public int getBlock() {
        // Armor bonus could be added here
        return block;
    }

    @Override
    public int getLucky() {
        return lucky;
    }

    @Override
    public int getQuick() {
        return quick;
    }

    @Override
    public int getExp() {
        return exp;
    }

    @Override
    public void gainExp(int amount) {
        this.exp += amount;
        while (this.exp >= this.nextExp) {
            levelUp();
        }
    }

    private void levelUp() {
        this.exp -= this.nextExp;
        this.level++;
        this.nextExp = (int) (this.nextExp * 1.5f); // Simple curve

        // Increase stats
        this.maxHp += 20;
        this.currentHp = this.maxHp;
        this.maxMp += 10;
        this.currentMp = this.maxMp;
        this.power += 2;
        this.block += 1;
        this.lucky += 1;
        this.quick += 1;

        System.out.println(name + " leveled up to " + level + "!");
    }
}
