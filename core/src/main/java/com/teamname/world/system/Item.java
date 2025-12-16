package com.teamname.world.system;

/**
 * プレイヤーが実際に所持するアイテムを表すクラス。
 * どのアイテムデータ（設計図）を、何個持っているかを管理します。
 */
public class Item {

    // アイテムの設計図（名前、説明、種類など）
    public ItemData data;

    // 所持している個数
    public int quantity;

    /**
     * 新しいアイテムを作成します。
     * @param data このアイテムの元となる ItemData (DataLoaderが読み込んだもの)
     * @param quantity 所持する個数
     */
    public Item(ItemData data, int quantity) {
        if (data == null) {
            throw new IllegalArgumentException("Item data cannot be null");
        }
        this.data = data;
        this.quantity = quantity;
    }

    // デバッグ用に、アイテム名と個数を返すメソッド
    @Override
    public String toString() {
        return data.name + " (" + quantity + "個)";
    }
}
