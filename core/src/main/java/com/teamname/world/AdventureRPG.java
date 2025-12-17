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

        // 2. まずは空っぽの状態で初期化
        gameState = new GameState(); // HP:50, Gold:100 (デフォルト)
        inventory = new Inventory(); // 中身なし

        // 3. セーブデータがあるかチェックして分岐
        if (com.badlogic.gdx.Gdx.files.local("save.json").exists()) {
            // --- 続きから ---
            System.out.println("セーブデータを発見。ロードします...");
            // SaveManagerを使って、GameState(HPなど) と Inventory(アイテム) を一気にロード
            com.teamname.world.system.SaveManager.loadGame(this);

        } else {
            // --- はじめから ---
            System.out.println("セーブデータなし。ニューゲームを開始します。");
            // GameInitializerを使って初期アイテムを配る
            GameInitializer.setupNewGameInventory(inventory, dataLoader);
        }

        // 4. メニュータブの初期化
        menuTab = new MenuTab(this);

        // 5. 戦闘画面の初期化
        combatScreen = new VisualCombatScreen();

        // 6. タイトル画面へ
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
