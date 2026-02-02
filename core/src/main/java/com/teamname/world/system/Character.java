package com.teamname.world.system;

public class Character {
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
}
