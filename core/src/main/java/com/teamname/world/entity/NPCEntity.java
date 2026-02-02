package com.teamname.world.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.teamname.world.AdventureRPG;

public class NPCEntity extends Entity {

    private String name;
    private String dialogue;
    private AdventureRPG game;
    private String flagToSet; // インタラクト時にセットするフラグキー
    private int flagValue; // セットする値

    private TextureRegion textureRegion; // To support sprite sheet splitting

    public NPCEntity(float x, float y, String name, String dialogue, AdventureRPG game) {
        this(x, y, name, dialogue, game, null, 0);
    }

    public NPCEntity(float x, float y, String name, String dialogue, AdventureRPG game, String flagToSet,
            int flagValue) {
        this(x, y, name, dialogue, game, flagToSet, flagValue, null);
    }

    public NPCEntity(float x, float y, String name, String dialogue, AdventureRPG game, String flagToSet,
            int flagValue, String texturePath) {
        this(x, y, name, dialogue, game, flagToSet, flagValue, texturePath, 0, 0);
    }

    public NPCEntity(float x, float y, String name, String dialogue, AdventureRPG game, String flagToSet,
            int flagValue, String texturePath, int frameX, int frameY) {
        super(x, y, 32, 32);
        this.name = name;
        this.dialogue = dialogue;
        this.game = game;
        this.flagToSet = flagToSet;
        this.flagValue = flagValue;

        if (texturePath != null) {
            this.texture = new Texture(com.badlogic.gdx.Gdx.files.internal(texturePath));
            // Split into 32x32 frames
            TextureRegion[][] tmp = TextureRegion.split(this.texture, 32, 32);
            if (tmp.length > frameY && tmp[0].length > frameX) {
                this.textureRegion = tmp[frameY][frameX];
            } else {
                // Fallback to full or safe default
                this.textureRegion = new TextureRegion(this.texture);
            }
        } else {
            // Placeholder texture (Green box for NPC)
            com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(32, 32,
                    com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
            pixmap.setColor(com.badlogic.gdx.graphics.Color.GREEN);
            pixmap.fill();
            this.texture = new Texture(pixmap);
            this.textureRegion = new TextureRegion(this.texture);
            pixmap.dispose();
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (textureRegion != null) {
            batch.draw(textureRegion, x, y, width, height);
        }
    }

    @Override
    public void update(float delta) {
        // NPC usually stays still or slight movement
        // Update bounds just in case position changes
        bounds.setPosition(x, y);
    }

    public void interact() {
        if (game.getUIManager() != null) {
            game.getUIManager().showDialog(name, dialogue);
        }
        if (flagToSet != null && game.getGameState() != null) {
            game.getGameState().setFlag(flagToSet, flagValue);
            System.out.println("Flag Set: " + flagToSet + " = " + flagValue);
        }
    }

    public String getName() {
        return name;
    }
}
