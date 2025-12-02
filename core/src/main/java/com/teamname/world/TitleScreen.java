package com.teamname.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TitleScreen implements Screen {

    private final AdventureRPG game;
    private SpriteBatch batch;
    private BitmapFont font;

    public TitleScreen(AdventureRPG game) {
        this.game = game;
    }

    @Override
    public void show() {
        // Game が持っている SpriteBatch を使い回す
        batch = game.getBatch();
        font = new BitmapFont(); // とりあえずデフォルト
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        font.draw(batch, "Adventure RPG", 100, 300);
        font.draw(batch, "Press ENTER to start", 100, 250);
        batch.end();

        // ENTER でゲーム本編へ
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(new GameScreen(game));
        }
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (font != null) font.dispose();
        // batch は AdventureRPG 側でまとめて dispose する
    }
}
