package com.teamname.world.system.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
import com.teamname.world.system.Item;
import com.teamname.world.system.Character; // 追加

public class InventoryUI {
    private AdventureRPG game;
    private Stage stage;
    private Skin skin;
    private boolean isVisible = false;

    public InventoryUI(AdventureRPG game) {
        this.game = game;
        createUI();
    }

    private void createUI() {
        stage = new Stage(new ScreenViewport());
        skin = new Skin();
        createBasicSkin();
    }

    private void createBasicSkin() {
        // 白テクスチャ
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        skin.add("white", texture);

        // フォント
        BitmapFont font = FontSystem.createJapaneseFont(24);
        skin.add("default", font);

        // ラベルスタイル
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);

        // ボタンスタイル
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.down = skin.newDrawable("white", Color.BLUE);
        textButtonStyle.over = skin.newDrawable("white", Color.GRAY);
        textButtonStyle.fontColor = Color.WHITE;
        skin.add("default", textButtonStyle);

        // ウィンドウスタイル
        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.titleFont = font;
        windowStyle.titleFontColor = Color.WHITE;
        windowStyle.background = skin.newDrawable("white", 0.1f, 0.1f, 0.1f, 0.9f);
        skin.add("default", windowStyle);
    }

    private void rebuildWindow() {
        stage.clear();
        Window window = new Window("Inventory (B)", skin);

        // 画面サイズの80%幅、70%高さ
        float w = Math.max(500, Gdx.graphics.getWidth() * 0.8f);
        float h = Math.max(400, Gdx.graphics.getHeight() * 0.7f);
        window.setSize(w, h);
        window.setPosition((Gdx.graphics.getWidth() - w) / 2f, (Gdx.graphics.getHeight() - h) / 2f);

        // 閉じるボタン
        TextButton closeBtn = new TextButton("X", skin);
        closeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });
        window.getTitleTable().add(closeBtn).size(40, 40).padRight(10);

        // アイテムリスト
        Table contentTable = new Table();
        contentTable.top();

        if (game.getInventory() != null && !game.getInventory().getItems().isEmpty()) {
            for (final Item item : new java.util.ArrayList<>(game.getInventory().getItems())) {
                // 装備中マーク (誰か一人でも装備していたらEをつける)
                String displayName = item.data.name;
                boolean isEquipped = false;
                for (Character member : game.getGameState().partyMembers) {
                    if (member.equippedWeaponId == item.data.id || member.equippedArmorId == item.data.id) {
                        isEquipped = true;
                        break;
                    }
                }

                if (isEquipped) {
                    displayName = "(E) " + displayName;
                }

                TextButton itemButton = new TextButton(displayName, skin);
                itemButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        useItem(item);
                    }
                });

                Label qtyLabel = new Label("x " + item.quantity, skin);

                contentTable.add(itemButton).left().fillX().expandX().pad(5);
                contentTable.add(qtyLabel).right().pad(5);
                contentTable.row();
            }
        } else {
            contentTable.add(new Label("No Items", skin));
        }

        window.add(contentTable).expand().top().fillX();
        stage.addActor(window);
    }

    private void useItem(Item item) {
        String type = item.data.type;
        // とりあえずリーダー（先頭キャラ）に対して使用する
        Character target = game.getGameState().getLeader();
        if (target == null)
            return;

        if ("POTION".equals(type)) {
            System.out.println(item.data.name + " を " + target.name + " に使った！");
            int healAmount = item.data.power > 0 ? item.data.power : 10;

            target.currentHp += healAmount;
            if (target.currentHp > target.maxHp)
                target.currentHp = target.maxHp;

            item.quantity--;
            if (item.quantity <= 0) {
                game.getInventory().getItems().remove(item);
            }
            rebuildWindow();

        } else if ("WEAPON".equals(type)) {
            System.out.println(item.data.name + " を " + target.name + " に装備した！");
            target.equippedWeaponId = item.data.id;
            rebuildWindow();
        } else if ("ARMOR".equals(type)) {
            System.out.println(item.data.name + " を " + target.name + " に装備した！");
            target.equippedArmorId = item.data.id;
            rebuildWindow();
        } else {
            System.out.println("これは使えません。");
        }
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

    public void toggle() {
        if (isVisible)
            hide();
        else
            show();
    }

    public void updateAndRender(float delta) {
        if (isVisible) {
            stage.act(delta);
            stage.draw();
        }
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        if (isVisible) {
            rebuildWindow();
        }
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    public boolean isVisible() {
        return isVisible;
    }
}
