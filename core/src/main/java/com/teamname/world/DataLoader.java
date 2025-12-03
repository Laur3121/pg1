package com.teamname.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Array; // libGDX独自のリスト

import java.util.ArrayList; // Java標準のリスト

public class DataLoader {

    // 読み込んだ全アイテムデータを保持するリスト
    public ArrayList<ItemData> allItems;

    // ゲーム起動時に一度だけ呼ぶことを想定したメソッド
    public void loadAllData() {
        Json json = new Json();

        // 1. assets/items.json ファイルを見つける
        FileHandle itemFile = Gdx.files.internal("data/items.json");

        // 2. ファイルを読み込み、ItemDataの「リスト」として解釈する
        //    (Array.class は libGDX 独自のリスト型, ItemData.class は中身の型)
        this.allItems = json.fromJson(ArrayList.class, ItemData.class, itemFile);

        // --- デバッグ（確認）用 ---
        // ちゃんと読み込めたかコンソールに出力してみる
        System.out.println("--- アイテムデータの読み込み完了 ---");
        for (ItemData item : allItems) {
            System.out.println("ID: " + item.id + ", 名前: " + item.name);
        }
        System.out.println("---------------------------------");
    }
}
