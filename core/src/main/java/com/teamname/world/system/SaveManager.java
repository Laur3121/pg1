package com.teamname.world.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.teamname.world.AdventureRPG;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class SaveManager {

    // 保存先のファイルパス（拡張子は .dat や .bin にするのが一般的です）
    private static final String SAVE_FILE_PATH = "data/save.dat";

    // セーブを実行するメソッド
    public static void saveGame(AdventureRPG game) {
        FileHandle file = Gdx.files.local(SAVE_FILE_PATH);

        // dataディレクトリがない場合は作成する必要があるが、
        // Gdx.files.local の挙動によっては自動で作られないことがあるため、念の為親ディレクトリ作成を試みる
        // （PC版などでは file.parent().mkdirs() が機能する）
        if (file.parent().exists() == false) {
            file.parent().mkdirs();
        }

        // バイナリ書き込み用のストリームを作成
        // try-with-resources構文を使うと、自動でclose()してくれます
        try (DataOutputStream out = new DataOutputStream(file.write(false))) {

            // 1. GameStateの数値を書き込む
            GameState state = game.getGameState();
            out.writeInt(state.currentHp); // HP
            out.writeInt(state.maxHp); // MaxHP
            out.writeInt(state.currentMp); // MP
            out.writeInt(state.maxMp); // MaxMP
            out.writeInt(state.gold); // Gold
            out.writeInt(state.level);
            out.writeInt(state.exp);
            out.writeInt(state.str);
            out.writeInt(state.def);
            out.writeInt(state.equippedWeaponId);
            out.writeInt(state.equippedArmorId);

            // 2. インベントリのアイテムを書き込む
            // アイテムそのものではなく、「ID」と「個数」だけを記録する
            ArrayList<Item> items = game.getInventory().getItems();

            // まず「いくつアイテムを持っているか」の総数を書く
            out.writeInt(items.size());

            for (Item item : items) {
                // アイテムID
                out.writeInt(item.data.id);
                // 個数
                out.writeInt(item.quantity);
            }

            System.out.println("バイナリ形式でセーブしました: " + file.file().getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("セーブに失敗しました。");
        }
    }

    // ロードを実行するメソッド
    public static void loadGame(AdventureRPG game) {
        FileHandle file = Gdx.files.local(SAVE_FILE_PATH);
        if (!file.exists()) {
            System.out.println("セーブデータがありません。 no save data");
            return;
        }

        // バイナリ読み込み用のストリームを作成
        try (DataInputStream in = new DataInputStream(file.read())) {

            // 1. GameStateの数値を読み込む（書き込んだ順序通りに読む！）
            GameState state = game.getGameState();
            state.currentHp = in.readInt();
            state.maxHp = in.readInt();
            state.currentMp = in.readInt();
            state.maxMp = in.readInt();
            state.gold = in.readInt();
            state.level = in.readInt();
            state.exp = in.readInt();
            state.str = in.readInt();
            state.def = in.readInt();
            state.equippedWeaponId = in.readInt();
            state.equippedArmorId = in.readInt();

            // 2. インベントリを復元する
            // まず一度空にする
            Inventory inventory = game.getInventory();
            inventory.getItems().clear();

            // アイテム総数を読む
            int itemCount = in.readInt();

            for (int i = 0; i < itemCount; i++) {
                int id = in.readInt(); // IDを読む
                int quantity = in.readInt(); // 個数を読む

                // IDを使って、DataLoaderから本来のアイテム情報(名前や説明文)を取得する
                ItemData data = game.getDataLoader().getItemDataById(id);

                if (data != null) {
                    // インベントリに追加（※このaddItemメソッドがコンソールにログを出すかもしれません）
                    inventory.addItem(data, quantity);
                } else {
                    System.out.println("不明なアイテムID: " + id + " がスキップされました。");
                }
            }

            System.out.println("バイナリ形式のロードが完了しました！");

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ロードに失敗しました。データが壊れている可能性があります。");
        }
    }
}
