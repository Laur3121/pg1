package com.teamname.world.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

public class PlayerEntity extends Entity {

    private float speed = 200f;
    private float mapWidth, mapHeight;

    public PlayerEntity(float x, float y, float mapWidth, float mapHeight) {
        super(x, y, 32, 32); // 32x32 size
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;

        // Placeholder texture (white box) if asset not available
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(32, 32,
                com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(com.badlogic.gdx.graphics.Color.CYAN);
        pixmap.fill();
        this.texture = new Texture(pixmap);
        pixmap.dispose();
    }

    public void setTexture(Texture texture) {
        if (this.texture != null)
            this.texture.dispose();
        this.texture = texture;
    }

    @Override
    public void update(float delta) {
        float dx = 0;
        float dy = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            dx -= speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            dx += speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
            dy -= speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.UP))
            dy += speed * delta;

        x += dx;
        y += dy;

        // Clamp to map bounds
        x = MathUtils.clamp(x, 0, mapWidth - width);
        y = MathUtils.clamp(y, 0, mapHeight - height);

        bounds.setPosition(x, y);
    }
}
