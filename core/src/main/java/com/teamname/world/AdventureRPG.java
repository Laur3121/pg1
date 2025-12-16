package com.teamname.world;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.teamname.world.combat.VisualCombatScreen;
import com.teamname.world.system.DataLoader;
import com.teamname.world.system.GameInitializer;
import com.teamname.world.system.Inventory;
import com.teamname.world.system.MenuTab;
import com.teamname.world.system.GameState;

public class AdventureRPG extends Game {

    private SpriteBatch batch;

    // ゲーム全体の主要データ
    private DataLoader dataLoader;
    private Inventory inventory;
    private GameState gameState;

    // メニュータブ（画面に重ねて表示する部品）
    private MenuTab menuTab;

    // 戦闘画面（VisualCombatScreen を使用）
    public VisualCombatScreen combatScreen;

    // フラグ管理（0: 通常, 1: 戦闘）
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

        // 2. ゲーム状態（HPなど）の初期化
        gameState = new GameState();

        // 3. インベントリの初期化 (GameInitializerを使ってテストデータを投入)
        this.inventory = GameInitializer.createInitialInventory(dataLoader);

        // 4. メニュータブの初期化
        menuTab = new MenuTab(this);

        // 5. 戦闘画面の初期化（VisualCombatScreen）
        combatScreen = new VisualCombatScreen();

        // 6. 最初の画面へ移動（タイトル画面）
        setScreen(new TitleScreen(this));
    }

    @Override
    public void render() {
        // 各 Screen の render を呼び出す
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (batch != null) batch.dispose();
        if (menuTab != null) menuTab.dispose();
        if (combatScreen != null) combatScreen.dispose();
    }

    // --- ゲッター（他のクラスからこれらを使う） ---

    public SpriteBatch getBatch() {
        return batch;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public GameState getGameState() {
        return gameState;
    }

    public DataLoader getDataLoader() {
        return dataLoader;
    }

    public MenuTab getMenuTab() {
        return menuTab;
    }
}
