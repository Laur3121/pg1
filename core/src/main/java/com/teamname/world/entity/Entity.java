package com.teamname.world.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public abstract class Entity {
    protected float x, y;
    protected float width, height;
    protected Texture texture;
    protected Rectangle bounds;

    public Entity(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.bounds = new Rectangle(x, y, width, height);
    }

    public abstract void update(float delta);

    public void render(SpriteBatch batch) {
        if (texture != null) {
            batch.draw(texture, x, y, width, height);
        }
    }

    public Rectangle getBounds() {
        // Update bounds position before returning
        bounds.setPosition(x, y);
        return bounds;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        this.bounds.setPosition(x, y);
    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}
