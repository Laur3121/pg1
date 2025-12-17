package com.teamname.world.system;

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
}
