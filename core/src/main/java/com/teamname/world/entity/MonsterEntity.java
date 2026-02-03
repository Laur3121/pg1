package com.teamname.world.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.teamname.world.combat.core.ICombatant;
import com.teamname.world.combat.Monster;

import java.util.ArrayList;
import java.util.List;

public class MonsterEntity extends Entity {

    private float moveTimer = 0;
    private float moveDuration = 1.0f;
    private float moveDx = 0;
    private float moveDy = 0;
    private float speed = 50f;
    private float mapWidth, mapHeight;

    private Animation<TextureRegion> walkAnimation;
    private float stateTime = 0f;

    private List<ICombatant> enemiesForBattle;

    public MonsterEntity(float x, float y, float mapWidth, float mapHeight, String texturePath, int frameCols,
            int frameRows) {
        this(x, y, mapWidth, mapHeight, texturePath, frameCols, frameRows, -1);
    }

    public MonsterEntity(float x, float y, float mapWidth, float mapHeight, String texturePath, int frameCols,
            int frameRows, int maxFrames) {
        super(x, y, 64, 64);
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;

        // Load texture from path and create animation
        this.texture = new Texture(com.badlogic.gdx.Gdx.files.internal(texturePath));

        // Use frameCols and frameRows to split
        TextureRegion[][] tmp = TextureRegion.split(this.texture, this.texture.getWidth() / frameCols,
                this.texture.getHeight() / frameRows);

        // Flatten or select frames
        // Assumes row 0 for now as previously
        TextureRegion[] frames;
        if (maxFrames > 0 && maxFrames <= tmp[0].length) {
            frames = new TextureRegion[maxFrames];
            System.arraycopy(tmp[0], 0, frames, 0, maxFrames);
        } else {
            frames = tmp[0];
        }

        walkAnimation = new Animation<>(0.1f, frames);
        walkAnimation.setPlayMode(Animation.PlayMode.LOOP);

        // Default enemies
        enemiesForBattle = new ArrayList<>();
        enemiesForBattle.add(new Monster("Slime", 30, 10, 2, 5, 10, 5));
    }

    // Overloaded constructor for backward compatibility (defaults to 6 cols, 1 row
    // for regular monsters)
    public MonsterEntity(float x, float y, float mapWidth, float mapHeight, String texturePath) {
        this(x, y, mapWidth, mapHeight, texturePath, 6, 1);
    }

    // Previous overload for Boss with just cols (assumes 1 row) - DEPRECATED but
    // keeping for safety if needed,
    // but better to remove or update usage. I will update usage in GameScreen
    // instead.
    public MonsterEntity(float x, float y, float mapWidth, float mapHeight, String texturePath, int frameCols) {
        this(x, y, mapWidth, mapHeight, texturePath, frameCols, 1);
    }

    private String textureKey = "1"; // デフォルト

    public void setEnemies(List<ICombatant> enemies) {
        this.enemiesForBattle = enemies;
    }

    // テクスチャキーを設定（GameScreenから呼ばれる）
    public void setTextureKey(String key) {
        this.textureKey = key;
        // キーに応じて、デフォルトの敵グループを再設定する
        enemiesForBattle = new ArrayList<>();
        // ※詳細なパラメータ調整は将来的にJSON等で行うべき
        enemiesForBattle.add(new Monster("Monster " + key, 50, 10, 5, 10, 20, 10, key));
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public List<ICombatant> getEnemies() {
        return enemiesForBattle;
    }

    @Override
    public void update(float delta) {
        stateTime += delta;
        // Simple Random Walk AI
        moveTimer -= delta;
        if (moveTimer <= 0) {
            moveTimer = MathUtils.random(0.5f, 2.0f);
            moveDx = MathUtils.random(-1f, 1f) * speed;
            moveDy = MathUtils.random(-1f, 1f) * speed;

            // Stop sometimes
            if (MathUtils.randomBoolean(0.3f)) {
                moveDx = 0;
                moveDy = 0;
            }
        }

        x += moveDx * delta;
        y += moveDy * delta;

        // Clamp
        x = MathUtils.clamp(x, 0, mapWidth - width);
        y = MathUtils.clamp(y, 0, mapHeight - height);

        bounds.setPosition(x, y);
    }

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = walkAnimation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, x, y, width, height);
    }
}
