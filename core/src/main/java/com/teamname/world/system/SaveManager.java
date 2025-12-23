package com.teamname.world.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.teamname.world.AdventureRPG;

import java.util.ArrayList;

public class SaveManager {

    // セーブデータの構造（保存したいものをここにまとめる）
    public static class SaveData {
        public GameState gameState;
        public ArrayList<Item> inventoryItems;
    }

    // セーブを実行するメソッド
    public static void saveGame(AdventureRPG game) {
        SaveData data = new SaveData();
        data.gameState = game.getGameState();
        data.inventoryItems = game.getInventory().getItems();

        Json json = new Json();
        // 見やすく整形してJSONにする
        String jsonText = json.prettyPrint(data);

        // ローカルストレージに "save.json" という名前で保存
        FileHandle file = Gdx.files.local("save.json");
        file.writeString(jsonText, false);

        System.out.println("セーブしました: " + file.file().getAbsolutePath());
    }

    // ロードを実行するメソッド
    public static void loadGame(AdventureRPG game) {
        FileHandle file = Gdx.files.local("save.json");
        if (!file.exists()) {
            System.out.println("セーブデータがありません。");
            return;
        }

        Json json = new Json();
        try {
            SaveData data = json.fromJson(SaveData.class, file);

            // 読み込んだデータをゲームに反映させる
            // 注意: GameStateの中身を書き換える処理が必要です
            // ここでは簡易的に参照を入れ替えるのではなく、値をコピーする形が安全ですが
            // まずは単純に値をセットしなおすイメージで

            // 1. HPなどを復元
            GameState currentState = game.getGameState();
            currentState.currentHp = data.gameState.currentHp;
            currentState.maxHp = data.gameState.maxHp;
            currentState.gold = data.gameState.gold;

            // 2. アイテムを復元
            game.getInventory().getItems().clear();
            game.getInventory().getItems().addAll(data.inventoryItems);

            System.out.println("ロードしました！");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ロードに失敗しました。load fail");
        }
    }
}
