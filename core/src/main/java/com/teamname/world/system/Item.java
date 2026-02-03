package com.teamname.world.system;

/**
 * プレイヤーが実際に所持するアイテムを表すクラス。
 */
public class Item {

    public ItemData data;
    public int quantity;

    // ▼▼▼ これを追加してください！ ▼▼▼
    /**
     * JSON読み込み用（セーブデータのロードに必須）
     */
    public Item() {
    }
    // ▲▲▲ 追加ここまで ▲▲▲

    /**
     * 新しいアイテムを作成します。
     * @param data このアイテムの元となる ItemData
     * @param quantity 所持する個数
     */
    public Item(ItemData data, int quantity) {
        if (data == null) {
            throw new IllegalArgumentException("Item data cannot be null");
        }
        this.data = data;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return data.name + " (" + quantity + "個)";
    }
}
