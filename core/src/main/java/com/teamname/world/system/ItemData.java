package com.teamname.world.system;

// このクラスは、JSONのデータを読み込むためだけの「入れ物」です
public class ItemData {

    // JSONのキーと名前を一致させる
    public int id;
    public String name;
    public String description;
    public String type; // "POTION" や "WEAPON" など
    public int value; // 売買価格など
    public int power; // 攻撃力 または 回復量
    public int defense; // 防御力

    // (重要) libGDXのJsonライブラリが使うため、空っぽのコンストラクタが必須です
    public ItemData() {
    }
}
