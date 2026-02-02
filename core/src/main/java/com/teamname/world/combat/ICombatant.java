package com.teamname.world.combat;

/**
 * 戦闘参加者のインターフェース
 * プレイヤーキャラクターと敵キャラクターの両方がこのインターフェースを実装する
 */
public interface ICombatant {

    /**
     * 戦闘参加者の名前を取得
     * 
     * @return 名前
     */
    String getName();

    /**
     * 現在のHPを取得
     * 
     * @return 現在HP
     */
    int getCurrentHP();

    /**
     * 最大HPを取得
     * 
     * @return 最大HP
     */
    int getMaxHP();

    /**
     * 生存状態を確認
     * 
     * @return 生きている場合true
     */
    boolean isAlive();

    /**
     * ダメージを受ける
     * 
     * @param damage ダメージ量
     */
    void takeDamage(int damage);

    /**
     * 回復する
     * 
     * @param amount 回復量
     */
    void heal(int amount);

    /**
     * 素早さを取得(ターン順決定に使用)
     * 
     * @return 素早さ
     */
    int getSpeed();

    /**
     * 攻撃力を取得
     * 
     * @return 攻撃力
     */
    int getAttackPower();

    /**
     * 防御力を取得
     * 
     * @return 防御力
     */
    int getDefense();

    /**
     * 描画用のテクスチャキーを取得
     * 
     * @return テクスチャキー (例: "warrior", "1", "2")
     */
    String getTextureKey();
}
