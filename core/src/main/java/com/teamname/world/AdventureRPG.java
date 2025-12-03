package com.teamname.world;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.teamname.world.combat.VisualCombatScreen;
import com.teamname.world.combat.CombatScreen;

public class AdventureRPG extends Game {

    private SpriteBatch batch;

    // ゲーム全体の主要データ
    private DataLoader dataLoader;
    private Inventory inventory;

    // ★復活: メニュータブ（画面に重ねて表示する部品）
    private MenuTab menuTab;

    // 戦闘画面
    public CombatScreen combatScreen;

    // フラグ管理（戦闘中かどうかなど。0:通常, 1:戦闘）
    public int battleflag = 0;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // 1. データの読み込み
        dataLoader = new DataLoader();
        try {
            dataLoader.loadAllData();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. インベントリの初期化 (GameInitializerを使ってテストデータを投入)
        this.inventory = GameInitializer.createInitialInventory(dataLoader);

        // 3. ★復活: メニュータブの初期化
        menuTab = new MenuTab(this);

        // 4. 戦闘画面の初期化
        combatScreen = new CombatScreen();

        // 5. 最初の画面へ移動
        setScreen(new TitleScreen(this));
    }

    @Override
    public void render() {
        // 各Screenのrenderを呼び出す
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (batch != null) batch.dispose();
        if (menuTab != null) menuTab.dispose(); // ★追加
        if (combatScreen != null) combatScreen.dispose();
    }

    // --- ゲッター（他のクラスからこれらを使う） ---

    public SpriteBatch getBatch() {
        return batch;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public DataLoader getDataLoader() {
        return dataLoader;
    }

    public MenuTab getMenuTab() { // ★追加
        return menuTab;
    }
}
