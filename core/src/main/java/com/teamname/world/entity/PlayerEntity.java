package com.teamname.world.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.MathUtils;

public class PlayerEntity extends Entity {

    private float speed = 200f;
    private float mapWidth, mapHeight;

    private Animation<TextureRegion> idleAnimation; // Add other animations later
    private float stateTime = 0f;

    public PlayerEntity(float x, float y, float mapWidth, float mapHeight) {
        super(x, y, 32, 32); // 32x32 size
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;

        // Load animation frames (hardcoded path based on request for now, or pass in)
        // assets/evil Mage Pack/warriar/idle/1.png to 4.png
        Array<TextureRegion> frames = new Array<>();
        for (int i = 1; i <= 4; i++) {
            Texture texture = new Texture(Gdx.files.internal("evil Mage Pack/warriar/idle/" + i + ".png"));
            frames.add(new TextureRegion(texture));
        }
        idleAnimation = new Animation<>(0.2f, frames, Animation.PlayMode.LOOP);
    }

    public void setTexture(Texture texture) {
        if (this.texture != null)
            this.texture.dispose();
        this.texture = texture;
    }

    @Override
    public void update(float delta) {
        stateTime += delta;
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

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = idleAnimation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, x, y, width, height);
    }
}
