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
    private Table optionsTable; // For buttons
    private boolean isVisible = false;
    
    // Callback for when dialog finishes or option selected
    private com.teamname.world.system.event.EventManager eventManager;

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
        
        // Button Style
        com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle textButtonStyle = new com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.up = skin.newDrawable("white", 0.2f, 0.2f, 0.2f, 1f);
        textButtonStyle.down = skin.newDrawable("white", 0.4f, 0.4f, 0.4f, 1f);
        textButtonStyle.over = skin.newDrawable("white", 0.3f, 0.3f, 0.3f, 1f);
        textButtonStyle.fontColor = Color.WHITE;
        skin.add("default", textButtonStyle);
    }

    private void createWindow() {
        window = new Window("", skin);
        // 画面下部に配置
        float w = Gdx.graphics.getWidth() - 40;
        window.setSize(w, 200); // Choiceが入るかもしれんので少し広く
        window.setPosition(20, 20);

        textLabel = new Label("", skin);
        textLabel.setWrap(true); // 折り返し有効
        
        optionsTable = new Table();

        window.add(textLabel).width(w - 40).expand().top().left().pad(10);
        window.row();
        window.add(optionsTable).expandX().fillX().pad(10);

        // 初期状態は非表示
        window.setVisible(false);
        stage.addActor(window);
    }

    /**
     * 通常の会話（選択肢なし）
     */
    public void showDialog(String name, String text) {
        showDialogWithOptions(name, text, null, null);
    }

    // 古いメソッド互換用
    public void showDialog(String text) {
        showDialog("System", text);
    }
    
    /**
     * 選択肢付き会話
     */
    public void showDialogWithOptions(String name, String text, java.util.List<com.teamname.world.system.event.DialogOption> options, com.teamname.world.system.event.EventManager eventManager) {
        System.out.println("DialogUI showing: " + text); // Debug
        window.getTitleLabel().setText(name);
        textLabel.setText(text);
        
        this.eventManager = eventManager;
        optionsTable.clearChildren();
        
        if (options != null && !options.isEmpty()) {
            for (int i = 0; i < options.size(); i++) {
                final int index = i;
                com.teamname.world.system.event.DialogOption opt = options.get(i);
                com.badlogic.gdx.scenes.scene2d.ui.TextButton btn = new com.badlogic.gdx.scenes.scene2d.ui.TextButton(opt.text, skin);
                
                btn.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
                    @Override
                    public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                        if (DialogUI.this.eventManager != null) {
                            DialogUI.this.eventManager.onDialogOptionSelected(index);
                        } else {
                            hideDialog();
                        }
                    }
                });
                
                optionsTable.add(btn).fillX().pad(5).row();
            }
        }
        
        window.setVisible(true);
        isVisible = true;
        Gdx.input.setInputProcessor(stage);
    }
    
    // 外部から明示的に閉じる場合
    public void hide() {
        hideDialog();
    }

    public void hideDialog() {
        window.setVisible(false);
        isVisible = false;
        Gdx.input.setInputProcessor(null);
        
        if (eventManager != null) {
            eventManager.onDialogFinished();
            eventManager = null; // Clear callback
        }
    }

    public void updateAndRender(float delta) {
        if (isVisible) {
            stage.act(delta);
            stage.draw();

            // 選択肢がない場合のみ、クリックで閉じる（または次へ）
            // optionsTableに子要素がある＝選択肢がある
            if (!optionsTable.hasChildren() && Gdx.input.justTouched()) {
                // ウィンドウ内クリック判定などは省略、画面どこでもクリックで進む
                hideDialog();
            }
        }
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        float w = width - 40;
        window.setSize(w, 200);
        window.setPosition(20, 20);

        // Labelの幅制限を更新
        window.clearChildren();
        window.add(textLabel).width(w - 40).expand().top().left().pad(10);
        window.row();
        window.add(optionsTable).expandX().fillX().pad(10);
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    public boolean isVisible() {
        return isVisible;
    }
}
