package com.teamname.world.system;

public class GameState {
    // プレイヤーのステータス（本来はもっと詳しく作りますが、まずはHPから）
    public int currentHp;
    public int maxHp;
    public int gold;

    // ゲーム開始時の初期状態
    public GameState() {
        this.maxHp = 50;
        this.currentHp = 30; // 減っている状態からスタート（回復のテスト用）
        this.gold = 100;
    }

    // HPを回復するメソッド
    public void heal(int amount) {
        currentHp += amount;
        if (currentHp > maxHp) {
            currentHp = maxHp;
        }
        System.out.println("HPが " + currentHp + "/" + maxHp + " になった！");
    }
}
