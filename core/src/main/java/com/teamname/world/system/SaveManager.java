package com.teamname.world.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.teamname.world.AdventureRPG;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.badlogic.gdx.utils.Json; // Import Json

public class SaveManager {

    // 保存先のファイルパス（拡張子は .dat や .bin にするのが一般的です）
    private static final String SAVE_FILE_PATH = "data/save.dat";

    // ▼ 内部クラス定義

    public static class SaveData {
        public int gold;
        public java.util.List<CharacterData> party;
        public java.util.List<InventoryItemData> inventory;
    }

    public static class CharacterData {
        public String name;
        public int level, exp;
        public int currentHp, maxHp;
        public int currentMp, maxMp;
        public int str, def;
        public int equippedWeaponId, equippedArmorId;

        public CharacterData() {
        }

        public CharacterData(Character c) {
            this.name = c.name;
            this.level = c.level;
            this.exp = c.exp;
            this.currentHp = c.currentHp;
            this.maxHp = c.maxHp;
            this.currentMp = c.currentMp;
            this.maxMp = c.maxMp;
            this.str = c.str;
            this.def = c.def;
            this.equippedWeaponId = c.equippedWeaponId;
            this.equippedArmorId = c.equippedArmorId;
        }

        public Character toCharacter() {
            Character c = new Character(name, maxHp, maxMp, str, def);
            c.level = this.level;
            c.exp = this.exp;
            c.currentHp = this.currentHp;
            c.currentMp = this.currentMp;
            c.equippedWeaponId = this.equippedWeaponId;
            c.equippedArmorId = this.equippedArmorId;
            return c;
        }
    }

    public static class InventoryItemData {
        public int id;
        public int quantity;

        public InventoryItemData() {
        }

        public InventoryItemData(int id, int quantity) {
            this.id = id;
            this.quantity = quantity;
        }
    }

    // セーブを実行するメソッド
    public static void saveGame(AdventureRPG game) {
        FileHandle file = Gdx.files.local(SAVE_FILE_PATH);

        // dataディレクトリがない場合は作成する必要があるが、
        // Gdx.files.local の挙動によっては自動で作られないことがあるため、念の為親ディレクトリ作成を試みる
        // （PC版などでは file.parent().mkdirs() が機能する）
        if (file.parent().exists() == false) {
            file.parent().mkdirs();
        }

        GameState state = game.getGameState();
        Inventory inventory = game.getInventory();

        SaveData data = new SaveData();
        data.gold = state.gold;

        // パーティ保存
        data.party = new ArrayList<>();
        if (state.partyMembers != null) {
            for (Character c : state.partyMembers) {
                data.party.add(new CharacterData(c));
            }
        }

        // アイテム保存
        data.inventory = new ArrayList<>();
        for (Item item : inventory.getItems()) {
            data.inventory.add(new InventoryItemData(item.data.id, item.quantity));
        }

        Json json = new Json();
        String jsonString = json.toJson(data);

        file.writeString(jsonString, false);
        System.out.println("セーブ完了: " + file.path());
    }

    // ロードを実行するメソッド
    public static void loadGame(AdventureRPG game) {
        FileHandle file = Gdx.files.local(SAVE_FILE_PATH);
        if (!file.exists()) {
            System.out.println("セーブデータがありません。 no save data");
            return;
        }

        Json json = new Json();
        try {
            SaveData data = json.fromJson(SaveData.class, file.readString());

            // GameState復元
            GameState state = game.getGameState(); // 既にnewされている前提
            state.gold = data.gold;
            // partyMembersがnullでないことを確認し、クリアしてから追加
            if (state.partyMembers == null) {
                state.partyMembers = new ArrayList<>();
            } else {
                state.partyMembers.clear();
            }
            if (data.party != null) {
                for (CharacterData cd : data.party) {
                    state.addMember(cd.toCharacter());
                }
            }

            // Inventory復元
            Inventory inventory = game.getInventory();
            inventory.getItems().clear();
            DataLoader loader = game.getDataLoader();

            if (data.inventory != null) {
                for (InventoryItemData iid : data.inventory) {
                    ItemData itemData = loader.getItemDataById(iid.id);
                    if (itemData != null) {
                        inventory.addItem(itemData, iid.quantity);
                    }
                }
            }

            System.out.println("ロード完了");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ロードに失敗しました。データが壊れている可能性があります。");
        }
    }
}
