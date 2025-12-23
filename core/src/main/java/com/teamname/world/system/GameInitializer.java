package com.teamname.world.system;

import com.badlogic.gdx.Gdx;
import com.teamname.world.AdventureRPG;
import com.teamname.world.TitleScreen;
import com.teamname.world.combat.VisualCombatScreen;

public class GameInitializer {
    /**
     * ニューゲーム時（セーブデータがない時）に、初期アイテムをインベントリに追加します。
     * @param inventory 空のインベントリ
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

        // 2. まずは空っぽの状態で初期化
        Inventory inventory = new Inventory(); // 中身なし
        game.setInventory(inventory);

        game.setGameState(new GameState()); // HP:50, Gold:100 (デフォルト)

        // 3. セーブデータがあるかチェックして分岐
        if (Gdx.files.local("save.json").exists()) {
            // --- 続きから ---
            System.out.println("セーブデータを発見。ロードします...");
            // SaveManagerを使って、GameState(HPなど) と Inventory(アイテム) を一気にロード
            SaveManager.loadGame(game);

        } else {
            // --- はじめから ---
            System.out.println("セーブデータなし。ニューゲームを開始します。");
            // 自分自身（GameInitializer）のメソッドを呼び出して初期アイテムを配る
            setupNewGameInventory(game.getInventory(), game.getDataLoader());
        }

        // 4. メニュータブの初期化
        game.setMenuTab(new MenuTab(game));

        // 5. 戦闘画面の初期化
        game.combatScreen = new VisualCombatScreen();

        // 6. タイトル画面へ
        game.setScreen(new TitleScreen(game));
    }
}
