package com.teamname.world.system;

/**
 * 装備品アイテムを表すクラス。
 * Itemを拡張し、装備スロット（剣・盾など）やステータス補正値を持ちます。
 */
public class Equipment extends Item {

    public enum Slot {
        WEAPON, ARMOR, HELMET, ACCESSORY
    }

    public Slot slot;
    public int attackBonus;
    public int defenseBonus;

    public Equipment() {
        super();
    }

    public Equipment(ItemData data, int quantity, Slot slot) {
        super(data, quantity);
        this.slot = slot;
        // ItemDataのtypeなどからステータス補正を決めるロジックをここに書くこともあります
        // 今回は単純化のため、固定値やdata.valueなどを流用して設定する例とします
        this.attackBonus = 0;
        this.defenseBonus = 0;
    }
}
