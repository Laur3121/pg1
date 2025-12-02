package com.teamname.world;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.teamname.world.combat.CombatScreen;

// DataLoaderとInventory関連
import com.teamname.world.DataLoader;
import com.teamname.world.Inventory;
import com.teamname.world.ItemData;

public class AdventureRPG extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;

    private CombatScreen combatScreen;    // 戦闘画面
    private MenuScreen menuScreen;        // ★追加: メニュー画面

    // フラグ変数 (0: マップ/通常, 1: 戦闘, 2: メニュー)
    public int battleflag = 0;

    private DataLoader dataLoader;
    private Inventory inventory;

    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("libgdx.png");

        // データロード処理
        dataLoader = new DataLoader();
        try {
            dataLoader.loadAllData();
            inventory = new Inventory();

            // テストデータの追加
            if (dataLoader.allItems != null && dataLoader.allItems.size() >= 3) {
                ItemData herbData = dataLoader.allItems.get(0);
                inventory.addItem(herbData, 5);
                ItemData stickData = dataLoader.allItems.get(2);
                inventory.addItem(stickData, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 画面の初期化 (Inventoryを作った後で行う)
        combatScreen = new CombatScreen();
        menuScreen = new MenuScreen(this); // ★追加: this(自分自身)を渡す
    }

    @Override
    public void render() {
        // --- 画面切り替えの入力処理 ---
        // 通常画面(0)のとき、Mキーでメニュー(2)へ
        if (battleflag == 0) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
                battleflag = 2;
                menuScreen.show(); // 表示時の処理を呼ぶ

                return;
            }
        }

        // --- 描画処理 ---
        if (battleflag == 0) {
            // 通常マップ画面（今は画像を表示するだけ）
            ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
            batch.begin();
            batch.draw(image, 140, 210);
            batch.end();

        } else if (battleflag == 1) {
            // 戦闘画面
            combatScreen.render(Gdx.graphics.getDeltaTime());

        } else if (battleflag == 2) {
            // ★追加: メニュー画面
            menuScreen.render(Gdx.graphics.getDeltaTime());
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
        if (combatScreen != null) combatScreen.dispose();
        if (menuScreen != null) menuScreen.dispose(); // ★追加
    }

    // ★追加: 他のクラスからInventoryを参照できるようにする
    public Inventory getInventory() {
        return inventory;
    }
}
