package com.teamname.world.system;

import com.badlogic.gdx.Gdx;
import com.teamname.world.AdventureRPG;
import com.teamname.world.GameScreen; // 追加
import com.teamname.world.TitleScreen;
import com.teamname.world.combat.VisualCombatScreen;
import com.teamname.world.system.UIManager;

public class GameInitializer {
    /**
     * ニューゲーム時（セーブデータがない時）に、初期アイテムをインベントリに追加します。
     * 
     * @param inventory  空のインベントリ
     * @param dataLoader アイテムデータの参照元
     */
    public static void setupNewGameInventory(Inventory inventory, DataLoader dataLoader) {
        // データの読み込みが成功しているかチェック
        if (dataLoader == null || dataLoader.allItems == null || dataLoader.allItems.isEmpty()) {
            System.err.println("エラー: アイテムデータがないため初期アイテムを配れません。");
            return;
        }

        try {
            // テスト用に「やくそう(Index:0)」と「ひのきのぼう(Index:2)」を追加
            if (dataLoader.allItems.size() >= 3) {
                ItemData herb = dataLoader.allItems.get(0);
                ItemData stick = dataLoader.allItems.get(2);

                inventory.addItem(herb, 5);
                inventory.addItem(stick, 1);

                System.out.println("ニューゲーム設定: 初期アイテムを追加しました。");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initialize(AdventureRPG game) {
        // 1. データの読み込み
        DataLoader dataLoader = new DataLoader();
        try {
            dataLoader.loadAllData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        game.setDataLoader(dataLoader);

        // 2. 仮のGameStateとInventoryを作成（UI初期化でのNPE防止のため）
        game.setInventory(new Inventory());
        game.setGameState(new GameState());

        // 3. UIマネージャーの初期化
        game.setUIManager(new UIManager(game));

        // 3. 戦闘画面の初期化
        game.combatScreen = new VisualCombatScreen(game);

        // 4. オーディオマネージャーの初期化
        game.setAudioManager(new AudioManager());

        // 5. タイトル画面へ (ここではまだゲームデータはロードしない)
        game.setScreen(new TitleScreen(game));
    }

    /**
     * ゲーム本編を開始する（タイトル画面から呼ばれる）
     * 
     * @param game    ゲームインスタンス
     * @param isDebug デバッグモードならTrue
     */
    public static void startGame(AdventureRPG game, boolean isDebug) {
        // GameStateとInventoryを初期化
        Inventory inventory = new Inventory();
        game.setInventory(inventory);

        GameState newState = new GameState();
        game.setGameState(newState);

        if (!isDebug && Gdx.files.local("data/save.dat").exists()) {
            // --- 続きから ---
            System.out.println("セーブデータを発見。ロードします...");
            SaveManager.loadGame(game);
        } else {
            // --- はじめから（またはデバッグ開始） ---
            System.out.println("ニューゲーム(Debug: " + isDebug + ") を開始します。");

            // 4人パーティ作成
            Character hero = new Character("Hero", 100, 30, 15, 10);
            hero.setDataLoaderProvider(game);
            newState.addMember(hero);

            Character warrior = new Character("Warrior", 120, 10, 20, 15);
            warrior.setDataLoaderProvider(game);
            newState.addMember(warrior);

            Character mage = new Character("Mage", 60, 50, 5, 5);
            mage.setDataLoaderProvider(game);
            newState.addMember(mage);

            Character priest = new Character("Priest", 70, 40, 8, 8);
            priest.setDataLoaderProvider(game);
            newState.addMember(priest);

            // Debugなら所持金ボーナス
            if (isDebug) {
                newState.gold = 5000;
            }

            // 初期アイテム配布
            setupNewGameInventory(game.getInventory(), game.getDataLoader());
        }

        // ゲーム画面へ遷移
        game.setScreen(new GameScreen(game));
    }
}
