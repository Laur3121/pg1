package com.teamname.world.system;

import java.util.ArrayList;

/**
 * お店の機能を管理するクラス。
 * プレイヤーの所持金（GameState）とインベントリ（Inventory）を操作します。
 */
public class Shop {

    private ArrayList<ItemData> shopItems;

    public Shop() {
        this.shopItems = new ArrayList<>();
    }

    /**
     * お店に並ぶ商品を登録します。
     */
    public void addShopItem(ItemData item) {
        shopItems.add(item);
    }

    public ArrayList<ItemData> getShopItems() {
        return shopItems;
    }

    /**
     * アイテムを購入します。
     * 
     * @param playerState プレイヤーの状態（所持金）
     * @param inventory   プレイヤーのインベントリ
     * @param itemIndex   商品リストのインデックス
     * @return 購入できた場合は true
     */
    public boolean buyItem(GameState playerState, Inventory inventory, int itemIndex) {
        if (itemIndex < 0 || itemIndex >= shopItems.size()) {
            return false;
        }

        ItemData targetItem = shopItems.get(itemIndex);
        int price = targetItem.value; // ItemDataにvalue（価格）があると仮定

        // お金が足りるか確認
        if (playerState.gold >= price) {
            playerState.gold -= price;
            inventory.addItem(targetItem, 1);
            System.out.println(targetItem.name + "を購入しました。残金: " + playerState.gold);
            return true;
        } else {
            System.out.println("お金が足りません！");
            return false;
        }
    }

    /**
     * アイテムを売却します。
     * 
     * @param playerState プレイヤーの状態
     * @param inventory   プレイヤーのインベントリ
     * @param item        売りたいアイテムオブジェクト
     */
    public void sellItem(GameState playerState, Inventory inventory, Item item) {
        int sellPrice = item.data.value / 2; // 売値は買値の半額とする

        // インベントリから削除（未実装機能ですが、あると仮定して減らす処理）
        // 現状のInventoryにはremoveItemがないため、まずは個数を減らすか、メッセージのみとします
        // インベントリから削除
        int removedCount = inventory.removeItem(item.data, 1);
        if (removedCount > 0) {
            playerState.gold += sellPrice;
            System.out.println(item.data.name + "を売却しました。獲得: " + sellPrice + "円");
        } else {
            System.out.println("売却に失敗しました（アイテムがありません）。");
        }
    }
}
