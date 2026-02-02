package com.teamname.world.system;

/**
 * スキルデータを保持するクラス。
 * skills.json の内容と対応します。
 */
public class SkillData {
    public int id;
    public String name;
    public String description;
    public int mpCost; // 消費MP
    public int power; // 威力
    public String targetType; // SINGLE, ALL, SELF など
    public String animationId; // 使用するエフェクトIDなど

    // JSON読み込み用コンストラクタ
    public SkillData() {
    }
}
