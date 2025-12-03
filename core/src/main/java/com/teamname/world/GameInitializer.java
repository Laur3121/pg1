package com.teamname.world;

public class GameInitializer {

    /**
     * ゲーム開始時のインベントリを作成し、初期アイテム（テストデータ）を追加して返します。
     * @param dataLoader アイテムデータの参照元
     * @return 初期化されたインベントリ
     */
    public static Inventory createInitialInventory(DataLoader dataLoader) {
        // 1. 空のインベントリを作る
        Inventory inventory = new Inventory();

        // 2. データの読み込みが成功しているかチェック
        if (dataLoader == null || dataLoader.allItems == null || dataLoader.allItems.isEmpty()) {
            System.err.println("エラー: アイテムデータが読み込まれていないため、初期アイテムを配れません。");
            return inventory; // 空のまま返す
        }

        // 3. テスト用データの追加
        try {
            // IDではなくリストの順番(index)で取っているので注意（0番目がitems.jsonの1つ目）
            if (dataLoader.allItems.size() >= 3) {
                ItemData herb = dataLoader.allItems.get(0);   // やくそう
                ItemData stick = dataLoader.allItems.get(2);  // ひのきのぼう

                inventory.addItem(herb, 5);
                inventory.addItem(stick, 1);

                System.out.println("デバッグ: テスト用初期アイテムを追加しました。");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return inventory;
    }
}
