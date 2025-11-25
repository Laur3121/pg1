package com.teamname.world;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input; // テスト用のキー入力に必要
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.teamname.world.combat.VisualCombatScreen;

public class AdventureRPG extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;
    private VisualCombatScreen combatScreen;    // 戦闘画面
    // フラグ変数 (0: マップ/通常, 1: 戦闘)
    public int battleflag = 1;

    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("libgdx.png");
        combatScreen = new VisualCombatScreen();  // 戦闘画面の初期化
    }

    @Override
    public void render() {
        // フラグによる分岐処理
        if (battleflag == 0) {
            ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
            batch.begin();
            batch.draw(image, 140, 210);
            batch.end();

        }else if (battleflag == 1) {
            combatScreen.render(Gdx.graphics.getDeltaTime());
        }
    }

    @Override
    public void dispose() {
        // プログラム1のリソース破棄
        batch.dispose();
        image.dispose();

        // プログラム2のリソース破棄
        if (combatScreen != null) {
            combatScreen.dispose();
        }
    }
}
