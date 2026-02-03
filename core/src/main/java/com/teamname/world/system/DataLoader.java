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

    // Event & Quest System
    public java.util.Map<Integer, com.teamname.world.system.quest.QuestData> questData;
    public java.util.Map<String, com.teamname.world.system.event.DialogNode> dialogData;

    public void loadAllData() {
        Json json = new Json();
        json.setIgnoreUnknownFields(true);

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

        // 4. Quests
        this.questData = new java.util.HashMap<>();
        FileHandle questFile = Gdx.files.internal("data/quests.json");
        if (questFile.exists()) {
             ArrayList<com.teamname.world.system.quest.QuestData> list = json.fromJson(ArrayList.class, com.teamname.world.system.quest.QuestData.class, questFile);
             for(com.teamname.world.system.quest.QuestData q : list) {
                 this.questData.put(q.id, q);
             }
             System.out.println("--- クエストデータの読み込み完了: " + questData.size() + "個 ---");
        } else {
            System.out.println("クエストデータなし (data/quests.json)");
        }

        // 5. Dialogs
        this.dialogData = new java.util.HashMap<>();
        // 5. Dialogs
        this.dialogData = new java.util.HashMap<>();
        FileHandle dialogFile = Gdx.files.internal("data/dialogs.json");
        if (dialogFile.exists()) {
            try {
                 com.badlogic.gdx.utils.JsonReader reader = new com.badlogic.gdx.utils.JsonReader();
                 com.badlogic.gdx.utils.JsonValue root = reader.parse(dialogFile);
                 
                 for (com.badlogic.gdx.utils.JsonValue entry : root) {
                     com.teamname.world.system.event.DialogNode node = new com.teamname.world.system.event.DialogNode();
                     node.id = entry.getString("id", entry.name);
                     node.text = entry.getString("text", "");
                     node.speakerName = entry.getString("speakerName", "");
                     
                     if (entry.has("triggerEventId")) {
                         node.triggerEventId = entry.getString("triggerEventId");
                     }
                     
                     if (entry.has("options")) {
                         node.options = new ArrayList<>();
                         for (com.badlogic.gdx.utils.JsonValue optVal : entry.get("options")) {
                             com.teamname.world.system.event.DialogOption opt = new com.teamname.world.system.event.DialogOption();
                             opt.text = optVal.getString("text", "");
                             opt.nextNodeId = optVal.getString("nextNodeId", null);
                             node.options.add(opt);
                         }
                     }
                     
                     this.dialogData.put(entry.name, node);
                     
                     // Debug
                     if (entry.name.equals("king_intro")) {
                         System.out.println("DEBUG: Manual Loaded king_intro. Text=" + node.text);
                         if (node.text.length() > 0) {
                              System.out.println("DEBUG: First char: " + (int)node.text.charAt(0));
                         }
                     }
                 }
                 System.out.println("--- 会話データの読み込み完了: " + dialogData.size() + "個 ---");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
             System.out.println("会話データなし (data/dialogs.json)");
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
