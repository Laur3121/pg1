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
import com.teamname.world.system.SaveManager;

public class SaveUI {
    private AdventureRPG game;
    private Stage stage;
    private Skin skin;
    private boolean isVisible = false;

    public SaveUI(AdventureRPG game) {
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
        textButtonStyle.down = skin.newDrawable("white", Color.BLUE);
        textButtonStyle.over = skin.newDrawable("white", Color.GRAY);
        textButtonStyle.fontColor = Color.WHITE;
        skin.add("default", textButtonStyle);

        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.titleFont = font;
        windowStyle.titleFontColor = Color.WHITE;
        windowStyle.background = skin.newDrawable("white", 0.1f, 0.1f, 0.1f, 0.9f);
        skin.add("default", windowStyle);
    }

    private void rebuildWindow() {
        stage.clear();
        Window window = new Window("Save System", skin);

        float w = Math.max(400, Gdx.graphics.getWidth() * 0.5f);
        float h = Math.max(250, Gdx.graphics.getHeight() * 0.4f);
        window.setSize(w, h);
        window.setPosition((Gdx.graphics.getWidth() - w) / 2f, (Gdx.graphics.getHeight() - h) / 2f);

        Label msg = new Label("現在の進行状況をセーブしますか？", skin);
        msg.setWrap(true);
        window.add(msg).width(w * 0.8f).pad(20).row();

        Table btnTable = new Table();

        TextButton yesBtn = new TextButton("はい (Yes)", skin);
        yesBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SaveManager.saveGame(game);
                System.out.println("セーブしました。");
                hide(); // セーブしたら閉じる
            }
        });

        TextButton noBtn = new TextButton("いいえ (No)", skin);
        noBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });

        btnTable.add(yesBtn).size(w * 0.35f, 50).pad(10);
        btnTable.add(noBtn).size(w * 0.35f, 50).pad(10);

        window.add(btnTable).row();
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
