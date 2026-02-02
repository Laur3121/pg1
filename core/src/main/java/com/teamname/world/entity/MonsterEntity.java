package com.teamname.world.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.teamname.world.combat.ICombatant;
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

    private List<ICombatant> enemiesForBattle;

    public MonsterEntity(float x, float y, float mapWidth, float mapHeight) {
        super(x, y, 32, 32);
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;

        // Placeholder texture (Red box)
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(32, 32,
                com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(com.badlogic.gdx.graphics.Color.RED);
        pixmap.fill();
        this.texture = new Texture(pixmap);
        pixmap.dispose();

        // Default enemies
        enemiesForBattle = new ArrayList<>();
        enemiesForBattle.add(new Monster("Slime", 30, 10, 2, 5, 10, 5));
    }

    public void setEnemies(List<ICombatant> enemies) {
        this.enemiesForBattle = enemies;
    }

    public List<ICombatant> getEnemies() {
        return enemiesForBattle;
    }

    @Override
    public void update(float delta) {
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
}
