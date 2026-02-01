package com.teamname.world.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

import java.util.ArrayList;

public class DataLoader {

    // 読み込んだ全アイテムデータを保持するリスト
    public ArrayList<ItemData> allItems;
    // スキルとモンスターのデータリストも追加
    public ArrayList<SkillData> allSkills;
    public ArrayList<MonsterData> allMonsters;

    public void loadAllData() {
        Json json = new Json();

        // 1. Items
        FileHandle itemFile = Gdx.files.internal("data/items.json");
        if (itemFile.exists()) {
            this.allItems = json.fromJson(ArrayList.class, ItemData.class, itemFile);
            System.out.println("--- アイテムデータの読み込み完了: " + allItems.size() + "個 ---");
        } else {
            System.err.println("警告: items.json が見つかりません。");
            this.allItems = new ArrayList<>();
        }

        // 2. Skills
        FileHandle skillFile = Gdx.files.internal("data/skills.json");
        if (skillFile.exists()) {
            this.allSkills = json.fromJson(ArrayList.class, SkillData.class, skillFile);
            System.out.println("--- スキルデータの読み込み完了: " + allSkills.size() + "個 ---");
        } else {
            System.err.println("警告: skills.json が見つかりません。");
            this.allSkills = new ArrayList<>();
        }

        // 3. Monsters
        FileHandle monsterFile = Gdx.files.internal("data/monsters.json");
        if (monsterFile.exists()) {
            this.allMonsters = json.fromJson(ArrayList.class, MonsterData.class, monsterFile);
            System.out.println("--- モンスターデータの読み込み完了: " + allMonsters.size() + "個 ---");
        } else {
            System.err.println("警告: monsters.json が見つかりません。");
            this.allMonsters = new ArrayList<>();
        }
    }

    /**
     * 指定されたIDを持つItemDataを検索して返します。
     * セーブデータのロード時に使用します。
     */
    public ItemData getItemDataById(int id) {
        if (allItems == null)
            return null;

        for (ItemData data : allItems) {
            if (data.id == id) {
                return data;
            }
        }
        System.out.println("警告: ID " + id + " のアイテムデータが見つかりませんでした。");
        return null;
    }
}
