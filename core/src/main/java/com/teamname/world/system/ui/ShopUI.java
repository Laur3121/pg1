package com.teamname.world.system.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.teamname.world.AdventureRPG;
import com.teamname.world.system.FontSystem;
import com.teamname.world.system.ItemData;
import com.teamname.world.system.Shop;

public class ShopUI {
    private AdventureRPG game;
    private Stage stage;
    private Skin skin;
    private Window window;
    private Shop shop;
    private boolean isVisible = false;

    public ShopUI(AdventureRPG game) {
        this.game = game;
        this.shop = new Shop();
        // テスト用商品を追加: ロードされた全アイテムを売る店として初期化
        if (game.getDataLoader().allItems != null) {
            for (ItemData item : game.getDataLoader().allItems) {
                shop.addShopItem(item);
            }
        }
        createUI();
    }

    private void createUI() {
        stage = new Stage(new ScreenViewport());
        skin = new Skin();

        // 簡易スタイル作成
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1,
                com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        skin.add("white", texture);

        // 日本語フォントを使用
        skin.add("default", FontSystem.createJapaneseFont(24));

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default");
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = skin.getFont("default");
        textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.down = skin.newDrawable("white", Color.BLUE);
        textButtonStyle.over = skin.newDrawable("white", Color.GRAY);
        textButtonStyle.fontColor = Color.WHITE;
        skin.add("default", textButtonStyle);

        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.titleFont = skin.getFont("default");
        windowStyle.titleFontColor = Color.WHITE;
        windowStyle.background = skin.newDrawable("white", 0, 0, 0, 0.9f);
        skin.add("default", windowStyle);

        rebuildWindow();
    }

    private void rebuildWindow() {
        stage.clear();

        window = new Window("Shop (Sキーで開閉)", skin); // タイトル変更
        window.setSize(600, 500);
        window.setPosition((Gdx.graphics.getWidth() - 600) / 2f, (Gdx.graphics.getHeight() - 500) / 2f);

        // 閉じるボタン
        TextButton closeBtn = new TextButton("Close", skin);
        closeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });
        window.getTitleTable().add(closeBtn).size(60, 60).padRight(10);

        // 所持金表示
        Label goldLabel = new Label("所持金: " + game.getGameState().gold + " 円", skin);
        window.add(goldLabel).pad(10).row();

        // 商品リスト
        Table itemTable = new Table();
        itemTable.top();

        for (int i = 0; i < shop.getShopItems().size(); i++) {
            final int index = i;
            ItemData item = shop.getShopItems().get(i);

            Label nameLabel = new Label(item.name, skin);
            Label priceLabel = new Label(item.value + " 円", skin);
            TextButton buyBtn = new TextButton("購入", skin);

            buyBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    boolean success = shop.buyItem(game.getGameState(), game.getInventory(), index);
                    if (success) {
                        System.out.println("購入成功: " + item.name);
                        // SE再生（仮）- ファイルがあれば鳴る
                        if (game.getAudioManager() != null) {
                            game.getAudioManager().playSe("buy.wav");
                        }
                        rebuildWindow(); // 更新（所持金など）
                    } else {
                        System.out.println("購入失敗: お金が足りません");
                    }
                }
            });

            itemTable.add(nameLabel).width(200).left().pad(5);
            itemTable.add(priceLabel).width(100).right().pad(5);
            itemTable.add(buyBtn).width(80).pad(5);
            itemTable.row();
        }

        window.add(itemTable).expand().top().fillX();

        stage.addActor(window);
    }

    public void show() {
        isVisible = true;
        rebuildWindow();
        Gdx.input.setInputProcessor(stage);
    }

    public void hide() {
        isVisible = false;
        Gdx.input.setInputProcessor(null);
    }

    public void updateAndRender(float delta) {
        if (isVisible) {
            stage.act(delta);
            stage.draw();
        }
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
