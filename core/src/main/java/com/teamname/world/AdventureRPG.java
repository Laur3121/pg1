package com.teamname.world;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

// import com.teamname.world.DataLoader; // 既にインポートされているか、同じパッケージにあるはず
// import com.teamname.world.Inventory; //
// import com.teamname.world.ItemData;  //

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class AdventureRPG extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;

    /**
     * ゲーム全体のデータを管理するローダーです。
     */
    private DataLoader dataLoader;


    /**
     * プレイヤーのもちものぶくろです。
     */
    private Inventory inventory;

    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("libgdx.png"); //最初の画像の読み込み。画像ファイルはassetへ

        // 1. DataLoaderをインスタンス化
        dataLoader = new DataLoader();

        // 2. すべてのデータをロード！
        try {
            dataLoader.loadAllData();
            System.out.println("DataLoaderのテスト: 読み込み成功！");

            // --- ▼ここから追加されたコードです（createメソッド内）▼ ---

            // 3. Inventoryをインスタンス化
            inventory = new Inventory();

            // 4. 読み込んだデータを使って、アイテムをインベントリに追加するテスト
            if (dataLoader.allItems != null && dataLoader.allItems.size() >= 3) {
                // dataLoaderが読み込んだ「やくそう」(ID:1)の設計図を取得
                ItemData herbData = dataLoader.allItems.get(0);
                // 「やくそう」を5個追加
                inventory.addItem(herbData, 5);

                // 「ひのきのぼう」(ID:3)の設計図を取得
                ItemData stickData = dataLoader.allItems.get(2);
                // 「ひのきのぼう」を1個追加
                inventory.addItem(stickData, 1);

                // 5. インベントリの現在の中身をコンソールに出力して確認
                inventory.printInventory();

            } else {
                System.err.println("インベントリテスト: スキップ (DataLoaderがアイテムを読み込めなかったため)");
            }

            // --- ▲ここまでが追加されたコードです（createメソッド内）▲ ---

        } catch (Exception e) {
            System.err.println("DataLoaderのテスト: 読み込み失敗...");
            e.printStackTrace(); // エラーの詳細を出力
        }

    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        batch.draw(image, 140, 210);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
    }
}
