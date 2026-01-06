package com.teamname.world.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

import java.util.ArrayList;

public class DataLoader {

    // 読み込んだ全アイテムデータを保持するリスト
    public ArrayList<ItemData> allItems;

    public void loadAllData() {
        Json json = new Json();

        // 1. assets/data/items.json ファイルを見つける
        FileHandle itemFile = Gdx.files.internal("data/items.json");

        // 2. ファイルを読み込み
        this.allItems = json.fromJson(ArrayList.class, ItemData.class, itemFile);

        System.out.println("--- アイテムデータの読み込み完了 ---");
        // デバッグ出力は少し省略しても良いでしょう
    }

    /**
     * 指定されたIDを持つItemDataを検索して返します。
     * セーブデータのロード時に使用します。
     */
    public ItemData getItemDataById(int id) {
        if (allItems == null) return null;

        for (ItemData data : allItems) {
            if (data.id == id) {
                return data;
            }
        }
        System.out.println("警告: ID " + id + " のアイテムデータが見つかりませんでした。");
        return null;
    }
}
