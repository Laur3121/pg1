package com.teamname.world;

import java.util.ArrayList;

/**
 * プレイヤーの「もちものぶくろ」を管理するクラス。
 * Itemのリストを保持し、アイテムの追加や削除を行います。
 */
public class Inventory {

    // 所持しているアイテムのリスト
    // （同じ種類のアイテムも、別のItemオブジェクトとしてリストに入れます。例：やくそうx3, やくそうx5）
    // ※将来的にはアイテムを「重ねる」処理を追加しますが、まずはシンプルにします。
    private ArrayList<Item> items;

    public Inventory() {
        this.items = new ArrayList<>();
    }

    /**
     * インベントリにアイテムを追加します。
     * @param data 追加するアイテムの ItemData
     * @param quantity 追加する個数
     */
    public void addItem(ItemData data, int quantity) {
        if (quantity <= 0) {
            return; // 0個以下は追加しない
        }

        // TODO: 本来は、すでに同じアイテム(data.idが同じ)を持っているか探し、
        //       持っていれば quantity を増やす（重ねる）処理を入れます。
        //       今は簡単にするため、常に新しいItemとしてリストに追加します。

        Item newItem = new Item(data, quantity);
        items.add(newItem);

        System.out.println("インベントリ: " + newItem.data.name + " を " + quantity + "個追加しました。");
    }

    /**
     * （デバッグ用）現在の所持アイテムをすべてコンソールに出力します。
     */
    public void printInventory() {
        System.out.println("--- もちもの ---");
        if (items.isEmpty()) {
            System.out.println("（なにも持っていない）");
        } else {
            for (Item item : items) {
                System.out.println(item.toString()); // ItemクラスのtoString()が呼ばれる
            }
        }
        System.out.println("-----------------");
    }

    // 将来的には以下のメソッドも必要になります
    // public void removeItem(int itemId, int quantity) { ... }
    // public boolean hasItem(int itemId) { ... }
    // public Item getItem(int itemId) { ... }
}
