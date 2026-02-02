package com.teamname.world.system.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.teamname.world.system.FontSystem;

public class DialogUI {

    private Stage stage;
    private Skin skin;
    private Window window;
    private Label textLabel;
    private Label nameLabel;
    private boolean isVisible = false;

    public DialogUI() {
        stage = new Stage(new ScreenViewport());
        skin = new Skin();
        createBasicSkin();
        createWindow();
    }

    private void createBasicSkin() {
        // MenuTabと同じような簡易スキン作成
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        skin.add("white", texture);

        // フォント生成
        BitmapFont font = FontSystem.createJapaneseFont(24);
        skin.add("default", font);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);

        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.titleFont = font;
        windowStyle.titleFontColor = Color.GREEN; // 名前は緑色で
        windowStyle.background = skin.newDrawable("white", 0.0f, 0.0f, 0.0f, 0.8f); // 背景は少し濃い黒
        skin.add("default", windowStyle);
    }

    private void createWindow() {
        window = new Window("", skin);
        // 画面下部に配置
        float w = Gdx.graphics.getWidth() - 40;
        window.setSize(w, 150);
        window.setPosition(20, 20);

        textLabel = new Label("", skin);
        textLabel.setWrap(true); // 折り返し有効

        // 名前表示はWindowのタイトルを使用するか、別途ラベルを追加するか
        // ここではWindowのタイトルを使用する

        window.add(textLabel).width(w - 40).expand().top().left().pad(10);

        // 初期状態は非表示
        window.setVisible(false);
        stage.addActor(window);
    }

    /**
     * 会話テキストを表示します。
     * 
     * @param name 話者名
     * @param text 表示するメッセージ
     */
    public void showDialog(String name, String text) {
        window.getTitleLabel().setText(name);
        textLabel.setText(text);
        window.setVisible(true);
        isVisible = true;
        Gdx.input.setInputProcessor(stage); // クリックで進むなどの処理があれば
    }

    // 古いメソッド互換用
    public void showDialog(String text) {
        showDialog("System", text);
    }

    public void hideDialog() {
        window.setVisible(false);
        isVisible = false;
        Gdx.input.setInputProcessor(null);
    }

    public void updateAndRender(float delta) {
        if (isVisible) {
            stage.act(delta);
            stage.draw();

            // クリックで閉じる（簡易実装）
            if (Gdx.input.justTouched()) {
                hideDialog();
            }
        }
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        float w = width - 40;
        window.setSize(w, 150);
        window.setPosition(20, 20);

        // Labelの幅制限を更新（セルを取得して更新）
        // clearして作り直すのが手っ取り早い
        window.clearChildren();
        window.add(textLabel).width(w - 40).expand().top().left().pad(10);
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    public boolean isVisible() {
        return isVisible;
    }
}
