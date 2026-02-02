package com.teamname.world.system;

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
     * 
     * @param data     追加するアイテムの ItemData
     * @param quantity 追加する個数
     */
    public void addItem(ItemData data, int quantity) {
        if (quantity <= 0) {
            return; // 0個以下は追加しない
        }

        // TODO: 本来は、すでに同じアイテム(data.idが同じ)を持っているか探し、
        // 持っていれば quantity を増やす（重ねる）処理を入れます。
        // 今は簡単にするため、常に新しいItemとしてリストに追加します。

        // アイテム種別によってクラスを使い分ける（簡易的なFactoryパターン）
        Item newItem;
        if ((data.type != null) && (data.type.equalsIgnoreCase("WEAPON") || data.type.equalsIgnoreCase("ARMOR"))) {
            // 装備品として作成（スロットは仮でWEAPON固定にしていますが、本来はデータから判定します）
            Equipment.Slot slot = Equipment.Slot.WEAPON;
            newItem = new Equipment(data, quantity, slot);
        } else {
            newItem = new Item(data, quantity);
        }

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

    public ArrayList<Item> getItems() {
        return items;
    }

    /**
     * 指定したアイテムをインベントリから削除します。
     * 
     * @param data     削除するアイテムの ItemData
     * @param quantity 削除する個数
     * @return 実際に削除できた個数（所持数より多く指定された場合は所持数分だけ削除）
     */
    public int removeItem(ItemData data, int quantity) {
        if (quantity <= 0)
            return 0;

        Item target = null;
        for (Item item : items) {
            if (item.data.id == data.id) {
                target = item;
                break;
            }
        }

        if (target != null) {
            if (target.quantity > quantity) {
                target.quantity -= quantity;
                return quantity;
            } else {
                int removed = target.quantity;
                items.remove(target);
                return removed;
            }
        }
        return 0;
    }

    /**
     * 指定したアイテムIDを持っているか確認します。
     */
    public boolean hasItem(int itemId) {
        for (Item item : items) {
            // アイテムIDの一致をチェック
            // ※Itemクラスのコンストラクタで data が必須になっている前提
            if (item.data != null && item.data.id == itemId) {
                return true;
            }
        }
        return false;
    }

    /**
     * 指定したアイテムIDの所持数を返します。
     */
    public int getItemCount(int itemId) {
        for (Item item : items) {
            if (item.data != null && item.data.id == itemId) {
                return item.quantity;
            }
        }
        return 0;
    }

    public Item getItem(int itemId) {
        for (Item item : items) {
            if (item.data != null && item.data.id == itemId) {
                return item;
            }
        }
        return null;
    }
}
