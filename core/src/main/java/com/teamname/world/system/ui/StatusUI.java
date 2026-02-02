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
import com.teamname.world.system.GameState;

public class StatusUI {
    private AdventureRPG game;
    private Stage stage;
    private Skin skin;
    private boolean isVisible = false;

    public StatusUI(AdventureRPG game) {
        this.game = game;
        createUI();
    }

    private void createUI() {
        stage = new Stage(new ScreenViewport());
        skin = new Skin();
        createBasicSkin();
    }

    private void createBasicSkin() {
        // 共通のスキン作成ロジック（本来は共通クラス化すべきですが、今回はコピーします）
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
        Window window = new Window("Status (C)", skin);
        window.setSize(400, 400);
        window.setPosition((Gdx.graphics.getWidth() - 400) / 2f, (Gdx.graphics.getHeight() - 400) / 2f);

        TextButton closeBtn = new TextButton("X", skin);
        closeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });
        window.getTitleTable().add(closeBtn).size(40, 40).padRight(10);

        Table statusTable = new Table();
        GameState state = game.getGameState();

        statusTable.add(new Label("Level:", skin)).left();
        statusTable.add(new Label(String.valueOf(state.level), skin)).right().row();

        statusTable.add(new Label("HP:", skin)).left();
        statusTable.add(new Label(state.currentHp + " / " + state.maxHp, skin)).right().row();

        statusTable.add(new Label("MP:", skin)).left();
        statusTable.add(new Label(state.currentMp + " / " + state.maxMp, skin)).right().row();

        statusTable.add(new Label("STR:", skin)).left();
        statusTable.add(new Label(String.valueOf(state.str), skin)).right().row();

        statusTable.add(new Label("DEF:", skin)).left();
        statusTable.add(new Label(String.valueOf(state.def), skin)).right().row();

        statusTable.add(new Label("Gold:", skin)).left();
        statusTable.add(new Label(state.gold + " 円", skin)).right().row();

        // 総合攻撃力・防御力
        statusTable.add(new Label("----------", skin)).colspan(2).row();
        statusTable.add(new Label("Total ATK:", skin)).left();
        statusTable.add(new Label(String.valueOf(state.getAttack(game.getDataLoader())), skin)).right().row();

        statusTable.add(new Label("Total DEF:", skin)).left();
        statusTable.add(new Label(String.valueOf(state.getDefense(game.getDataLoader())), skin)).right().row();

        window.add(statusTable).expand().top().pad(20);
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

    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    public boolean isVisible() {
        return isVisible;
    }
}
