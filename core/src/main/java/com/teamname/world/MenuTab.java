package com.teamname.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

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

        try {
            TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("skin/uiskin.atlas"));
            skin.addRegions(atlas);

            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("skin/font.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 24;
            parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "あいうえおかきくけこさしすせそたちつてとなにぬねのはひふへほまみむめもやゆよらりるれろわをん"
                + "がぎぐげござじずぜぞだぢづでどばびぶべぼぱぴぷぺぽッャュョ一二三四五六七八九十百千万薬草毒消し檜の棒、。！？";
            BitmapFont font = generator.generateFont(parameter);
            generator.dispose();
            skin.add("default-font", font);
            skin.load(Gdx.files.internal("skin/uiskin.json"));
        } catch (Exception e) {
            e.printStackTrace();
            // エラー時はデフォルトスキンで続行
            skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        }
    }

    public void toggle() {
        isVisible = !isVisible;
        if (isVisible) {
            rebuildMenu();
            // 入力を受け付けるプロセッサを切り替える（多重登録に注意）
            // ※本格的な実装では InputMultiplexer を使いますが、まずはシンプルに切り替えます
            Gdx.input.setInputProcessor(stage);
        } else {
            Gdx.input.setInputProcessor(null);
        }
    }

    private void rebuildMenu() {
        stage.clear();
        Window window = new Window("Menu", skin);
        window.setSize(400, 300);
        window.setPosition((Gdx.graphics.getWidth() - 400) / 2f, (Gdx.graphics.getHeight() - 300) / 2f);

        Table contentTable = new Table();
        contentTable.top();

        Inventory inventory = game.getInventory();
        if (inventory != null) {
            for (Item item : inventory.getItems()) {
                Label nameLabel = new Label(item.data.name, skin);
                Label qtyLabel = new Label("x " + item.quantity, skin);
                contentTable.add(nameLabel).left().pad(5);
                contentTable.add(qtyLabel).right().pad(5);
                contentTable.row();
            }
        }
        window.add(contentTable).expand().top();
        stage.addActor(window);
    }

    public void updateAndRender(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            toggle();
        }
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
