package com.teamname.world.system;

public class GameState {
    // プレイヤーのステータス（本来はもっと詳しく作りますが、まずはHPから）
    // 基礎ステータス
    public int level;
    public int exp;
    public int currentHp;
    public int maxHp;
    public int currentMp;
    public int maxMp;
    public int str; // Strength (攻撃力に影響)
    public int def; // Defense (防御力に影響)
    public int gold;

    // 装備スロット (ItemDataのIDを保存, 0なら装備なし)
    public int equippedWeaponId;
    public int equippedArmorId;

    // ゲーム開始時の初期状態
    public GameState() {
        this.level = 1;
        this.exp = 0;
        this.maxHp = 50;
        this.currentHp = 50;
        this.maxMp = 20;
        this.currentMp = 20;
        this.str = 10;
        this.def = 5;
        this.gold = 5000;

        this.equippedWeaponId = 0;
        this.equippedArmorId = 0;
    }

    // HPを回復するメソッド
    public void heal(int amount) {
        currentHp += amount;
        if (currentHp > maxHp) {
            currentHp = maxHp;
        }
        System.out.println("HPが " + currentHp + "/" + maxHp + " になった！");
    }

    // --- ステータス計算 ---

    public int getAttack(DataLoader loader) {
        int weaponPower = 0;
        if (equippedWeaponId > 0 && loader != null) {
            ItemData weapon = loader.getItemDataById(equippedWeaponId);
            if (weapon != null) {
                weaponPower = weapon.power;
            }
        }
        return str + weaponPower;
    }

    public int getDefense(DataLoader loader) {
        int armorDefense = 0;
        if (equippedArmorId > 0 && loader != null) {
            ItemData armor = loader.getItemDataById(equippedArmorId);
            if (armor != null) {
                armorDefense = armor.defense;
            }
        }
        return def + armorDefense;
    }

    // --- 装備変更 ---

    public void equip(int itemId, String type) {
        if ("WEAPON".equalsIgnoreCase(type)) {
            equippedWeaponId = itemId;
            System.out.println("武器を装備しました: ID " + itemId);
        } else if ("ARMOR".equalsIgnoreCase(type)) {
            equippedArmorId = itemId;
            System.out.println("防具を装備しました: ID " + itemId);
        }
    }
}
