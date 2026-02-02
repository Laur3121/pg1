package com.teamname.world.system;

/**
 * モンスターデータを保持するクラス。
 * monsters.json の内容と対応します。
 */
public class MonsterData {
    public int id;
    public String name;
    public int hp;
    public int mp;
    public int attack;
    public int defense;
    public int exp;
    public int gold;
    public String texturePath; // 画像パス

    // JSON読み込み用コンストラクタ
    public MonsterData() {
    }
}
