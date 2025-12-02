package com.teamname.world;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.teamname.world.combat.CombatScreen;

public class AdventureRPG extends Game {

    SpriteBatch batch;

    // 共有したいもの
    public CombatScreen combatScreen;
    public int battleflag = 0;

    @Override
    public void create() {
        batch = new SpriteBatch();
        combatScreen = new CombatScreen();

        // 最初はタイトル画面から
        setScreen(new TitleScreen(this));
    }

    @Override
    public void render() {
        // 現在の Screen に処理を投げる
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (batch != null) batch.dispose();
        if (combatScreen != null) combatScreen.dispose();
    }

    public SpriteBatch getBatch() {
        return batch;
    }
}
