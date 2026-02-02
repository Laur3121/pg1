package com.teamname.world.system;

import com.teamname.world.combat.ICombatant;

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

    public Character() {
        // デフォルトコンストラクタ（ロード用）
    }

    public Character(String name, int hp, int mp, int str, int def) {
        this.name = name;
        this.maxHp = hp;
        this.currentHp = hp;
        this.maxMp = mp;
        this.currentMp = mp;
        this.str = str;
        this.def = def;
        this.level = 1;
        this.exp = 0;
        this.equippedWeaponId = -1;
        this.equippedArmorId = -1;
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

    @Override
    public int getSpeed() {
        // 素早さは仮実装（レベルベースなど）
        return 10 + level;
    }

    @Override
    public int getAttackPower() {
        // DataLoaderが必要だが、インターフェースには引数がないため、
        // 簡易的にフィールドのstrを返すか、別途注入されたloaderを使う
        if (dataLoaderProvider != null) {
            return getAttack(dataLoaderProvider.getDataLoader());
        }
        return str;
    }

    @Override
    public int getDefense() {
        if (dataLoaderProvider != null) {
            return getDefense(dataLoaderProvider.getDataLoader());
        }
        return def;
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
}
