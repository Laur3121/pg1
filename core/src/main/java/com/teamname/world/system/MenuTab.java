package com.teamname.world.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.teamname.world.AdventureRPG;

public class MenuTab {
    private AdventureRPG game;
    private Stage stage;
    private Skin skin;
    private boolean isVisible = false;

    public MenuTab(AdventureRPG game) {
        this.game = game;
        createUI();
    }

    private void createUI() {
        stage = new Stage(new ScreenViewport());
        skin = new Skin();
        createBasicSkin();
    }

    private void createBasicSkin() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        skin.add("white", texture);

        BitmapFont font = FontSystem.createJapaneseFont(24);
        skin.add("default", font);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.down = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.over = skin.newDrawable("white", Color.GRAY);
        textButtonStyle.fontColor = Color.WHITE;
        skin.add("default", textButtonStyle);

        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.titleFont = font;
        windowStyle.titleFontColor = Color.WHITE;
        windowStyle.background = skin.newDrawable("white", 0.1f, 0.1f, 0.1f, 0.9f);
        skin.add("default", windowStyle);
    }

    public void toggle() {
        isVisible = !isVisible;
        if (isVisible) {
            rebuildMenu();
            Gdx.input.setInputProcessor(stage);
        } else {
            Gdx.input.setInputProcessor(null);
        }
    }

    private void rebuildMenu() {
        stage.clear();
        Window window = new Window("Main Menu (ESC)", skin);

        // 画面サイズの約40%幅、50%高さ（最小サイズ保証つき）
        float w = Math.max(300, Gdx.graphics.getWidth() * 0.4f);
        float h = Math.max(400, Gdx.graphics.getHeight() * 0.5f);
        window.setSize(w, h);
        window.setPosition((Gdx.graphics.getWidth() - w) / 2f, (Gdx.graphics.getHeight() - h) / 2f);

        // 閉じるボタン
        TextButton closeBtn = new TextButton("X", skin);
        closeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggle();
            }
        });
        // タイトルバー右
        window.getTitleTable().add(closeBtn).size(40, 40).padRight(10);

        // ▼ 追加: ステータス概要（リーダー）
        Table statusTable = new Table();
        com.teamname.world.system.GameState state = game.getGameState();
        com.teamname.world.system.Character leader = state.getLeader();

        if (leader != null) {
            Label nameLabel = new Label(leader.name, skin);
            Label hpLabel = new Label("HP: " + leader.currentHp + " / " + leader.maxHp, skin);
            Label mpLabel = new Label("MP: " + leader.currentMp + " / " + leader.maxMp, skin);
            Label goldLabel = new Label("Gold: " + state.gold + " 円", skin);

            statusTable.add(nameLabel).pad(5);
            statusTable.add(hpLabel).pad(5);
            statusTable.add(mpLabel).pad(5);
            statusTable.add(goldLabel).pad(5);
        } else {
            statusTable.add(new Label("No Party", skin));
        }

        // ウィンドウの上部に追加
        window.add(statusTable).center().row();

        Table table = new Table();

        // 1. Bag (B)
        TextButton bagBtn = new TextButton("バッグ (B)", skin);
        bagBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggle(); // メニュー閉じる
                game.getUIManager().showInventory();
            }
        });
        table.add(bagBtn).fillX().pad(10).row();

        // 2. Status (C)
        TextButton statusBtn = new TextButton("ステータス (C)", skin);
        statusBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggle();
                game.getUIManager().showStatus();
            }
        });
        table.add(statusBtn).fillX().pad(10).row();

        // 3. Save (S)
        TextButton saveBtn = new TextButton("セーブ (S)", skin);
        saveBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggle();
                game.getUIManager().showSave();
            }
        });
        table.add(saveBtn).fillX().pad(10).row();

        // 4. Debug (D)
        TextButton debugBtn = new TextButton("デバッグ (D)", skin);
        debugBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggle();
                game.getUIManager().showDebug();
            }
        });
        table.add(debugBtn).fillX().pad(10).row();

        window.add(table).expand().top().padTop(20);
        stage.addActor(window);
    }

    public void updateAndRender(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            toggle();
        }
        if (isVisible) {
            stage.act(delta);
            stage.draw();
        }
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        if (isVisible) {
            rebuildMenu();
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
