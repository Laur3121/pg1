package com.teamname.world.system.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.teamname.world.AdventureRPG;
import com.teamname.world.system.FontSystem;
import com.teamname.world.system.quest.QuestData;
import com.teamname.world.system.quest.QuestManager;
import com.teamname.world.system.quest.QuestProgress;
import java.util.Map;

public class QuestLogUI {

    private Stage stage;
    private Skin skin;
    private Window window;
    private Table contentTable;
    private boolean isVisible = false;
    private AdventureRPG game;

    public QuestLogUI(AdventureRPG game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        skin = new Skin();
        createBasicSkin();
        createWindow();
    }

    private void createBasicSkin() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        skin.add("white", texture);

        // 日本語フォント
        BitmapFont font = FontSystem.createJapaneseFont(24);
        skin.add("default", font);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);

        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.titleFont = font;
        windowStyle.titleFontColor = Color.YELLOW;
        windowStyle.background = skin.newDrawable("white", 0.0f, 0.0f, 0.0f, 0.9f);
        skin.add("default", windowStyle);
    }

    private void createWindow() {
        window = new Window("クエストログ", skin);
        window.setSize(600, 400);
        window.setPosition((Gdx.graphics.getWidth() - 600) / 2, (Gdx.graphics.getHeight() - 400) / 2);

        contentTable = new Table();
        contentTable.top().left();

        contentTable.top().left();
        
        window.add(contentTable).expand().fill().top().left().pad(20);

        window.setVisible(false);
        stage.addActor(window);
    }

    public void show() {
        updateQuestList();
        window.setVisible(true);
        isVisible = true;
        Gdx.input.setInputProcessor(stage);
    }

    public void hide() {
        window.setVisible(false);
        isVisible = false;
        Gdx.input.setInputProcessor(null);
    }
    
    public void toggle() {
        if (isVisible) hide();
        else show();
    }

    private void updateQuestList() {
        contentTable.clearChildren();
        
        QuestManager qm = game.getGameState().questManager;
        if (qm == null) return;
        
        Map<Integer, QuestProgress> activeQuests = qm.getActiveQuests();
        
        if (activeQuests.isEmpty()) {
             Label emptyLabel = new Label("現在進行中のクエストはありません。", skin);
             contentTable.add(emptyLabel).pad(10);
             return;
        }

        for (QuestProgress progress : activeQuests.values()) {
            if (progress.isCompleted) continue; // 完了済みを表示するかはオプション

            QuestData data = qm.getQuestData(progress.questId);
            if (data == null) continue;

            Label titleLabel = new Label("◆ " + data.title, skin);
            titleLabel.setColor(Color.ORANGE);
            contentTable.add(titleLabel).left().padTop(10).row();

            Label descLabel = new Label(data.description, skin);
            descLabel.setWrap(true);
            contentTable.add(descLabel).width(500).left().padLeft(20).padBottom(5).row();
            
            // Current Step
            if (data.steps != null && progress.currentStepIndex < data.steps.size()) {
                 String stepDesc = data.steps.get(progress.currentStepIndex).description;
                 Label stepLabel = new Label("進行状況: " + stepDesc, skin);
                 stepLabel.setColor(Color.CYAN);
                 contentTable.add(stepLabel).width(500).left().padLeft(20).row();
            }
        }
    }

    public void updateAndRender(float delta) {
        if (isVisible) {
            stage.act(delta);
            stage.draw();
            
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
                hide();
            }
        }
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        window.setPosition((width - 600) / 2, (height - 400) / 2);
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    public boolean isVisible() {
        return isVisible;
    }
}
