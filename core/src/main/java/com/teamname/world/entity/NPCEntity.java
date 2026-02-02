package com.teamname.world.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.teamname.world.AdventureRPG;

public class NPCEntity extends Entity implements com.teamname.world.system.event.IInteractable {

    private String name;
    private String dialogue;
    private AdventureRPG game;
    private String flagToSet; // インタラクト時にセットするフラグキー
    private int flagValue; // セットする値
    
    // Event ID to trigger
    private String eventId;

    private TextureRegion textureRegion; // To support sprite sheet splitting

    public NPCEntity(float x, float y, String name, String dialogue, AdventureRPG game) {
        this(x, y, name, dialogue, game, null, 0);
    }
    
    // Existing constructors... keeping them for compatibility but ideally we move to eventId
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
        // ... (existing constructor logic)
        super(x, y, 32, 32);
        this.name = name;
        this.dialogue = dialogue;
        this.game = game;
        this.flagToSet = flagToSet;
        this.flagValue = flagValue;
        
        // Use flagToSet as eventId if it starts with "DIALOG_" or "EVENT_" logic could be added here
        // For now, let's allow setting eventId explicitly via a new constructor or just use interact logic logic
        
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
    
    // New constructor for Event ID
    public NPCEntity(float x, float y, String name, String eventId, AdventureRPG game, boolean isEvent) {
         this(x, y, name, eventId, game, isEvent, null, 0, 0);
    }
    
    public NPCEntity(float x, float y, String name, String eventId, AdventureRPG game, boolean isEvent, String texturePath, int frameX, int frameY) {
         super(x, y, 32, 32);
         this.name = name;
         this.eventId = eventId;
         this.game = game;
         this.dialogue = ""; // unused if eventId is set
         
         if (texturePath != null) {
            this.texture = new Texture(com.badlogic.gdx.Gdx.files.internal(texturePath));
            // Split into 32x32 frames
            TextureRegion[][] tmp = TextureRegion.split(this.texture, 32, 32);
            if (tmp.length > frameY && tmp[0].length > frameX) {
                this.textureRegion = tmp[frameY][frameX];
            } else {
                this.textureRegion = new TextureRegion(this.texture);
            }
        } else {
          // Placeholder texture default
            com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(32, 32,
                    com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
            pixmap.setColor(com.badlogic.gdx.graphics.Color.CYAN);
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
        bounds.setPosition(x, y);
    }

    public void interact() {
        onInteract(null);
    }
    
    @Override
    public void onInteract(com.teamname.world.entity.PlayerEntity player) {
         if (this.eventId != null && game.getGameState().eventManager != null) {
             game.getGameState().eventManager.startEvent(this.eventId);
         } else {
             // Fallback to old behavior
            if (game.getUIManager() != null) {
                game.getUIManager().showDialog(name, dialogue);
            }
            if (flagToSet != null && game.getGameState() != null) {
                game.getGameState().setFlag(flagToSet, flagValue);
                System.out.println("Flag Set: " + flagToSet + " = " + flagValue);
            }
         }
    }

    public String getName() {
        return name;
    }
}
