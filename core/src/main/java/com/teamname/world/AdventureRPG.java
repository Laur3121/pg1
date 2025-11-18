package com.teamname.world;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input; // テスト用のキー入力に必要
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.teamname.world.combat.CombatScreen;

// --- ▼ここから追加（DataLoaderとInventory関連）▼ ---
import com.teamname.world.DataLoader;
import com.teamname.world.Inventory;
import com.teamname.world.ItemData;
// --- ▲ここまで追加▲ ---

public class AdventureRPG extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;
    private CombatScreen combatScreen;    // 戦闘画面
    // フラグ変数 (0: マップ/通常, 1: 戦闘)
    public int battleflag = 0;

    // --- ▼ここから追加（DataLoaderとInventory関連）▼ ---
    /**
     * ゲーム全体のデータを管理するローダーです。
     */
    private DataLoader dataLoader;

    /**
     * プレイヤーのもちものぶくろです。
     */
    private Inventory inventory;
    // --- ▲ここまで追加▲ ---


    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("libgdx.png");
        combatScreen = new CombatScreen();  // 戦闘画面の初期化

        // --- ▼ここから追加（DataLoaderとInventory関連）▼ ---

        // 1. DataLoaderをインスタンス化
        dataLoader = new DataLoader();

        // 2. すべてのデータをロード！
        try {
            dataLoader.loadAllData();
            System.out.println("DataLoaderのテスト: 読み込み成功！");

            // 3. Inventoryをインスタンス化
            inventory = new Inventory();

            // 4. 読み込んだデータを使って、アイテムをインベントリに追加するテスト
            if (dataLoader.allItems != null && dataLoader.allItems.size() >= 3) {
                // dataLoaderが読み込んだ「やくそう」(ID:1)の設計図を取得
                ItemData herbData = dataLoader.allItems.get(0);
                // 「やくそう」を5個追加
                inventory.addItem(herbData, 5);

                // 「ひのきのぼう」(ID:3)の設計図を取得
                ItemData stickData = dataLoader.allItems.get(2);
                // 「ひのきのぼう」を1個追加
                inventory.addItem(stickData, 1);

                // 5. インベントリの現在の中身をコンソールに出力して確認
                inventory.printInventory();

            } else {
                System.err.println("インベントリテスト: スキップ (DataLoaderがアイテムを読み込めなかったため)");
            }

        } catch (Exception e) {
            System.err.println("DataLoaderのテスト: 読み込み失敗...");
            e.printStackTrace(); // エラーの詳細を出力
        }
        // --- ▲ここまで追加▲ ---
    }

    @Override
    public void render() {
        // フラグによる分岐処理
        if (battleflag == 0) {
            ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
            batch.begin();
            batch.draw(image, 140, 210);
            batch.end();

        }else if (battleflag == 1) {
            combatScreen.render(Gdx.graphics.getDeltaTime());
        }
    }

    @Override
    public void dispose() {
        // プログラム1のリソース破棄
        batch.dispose();
        image.dispose();

        // プログラム2のリソース破棄
        if (combatScreen != null) {
            combatScreen.dispose();
        }
    }
}
