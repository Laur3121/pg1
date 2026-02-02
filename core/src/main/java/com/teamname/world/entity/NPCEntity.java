package com.teamname.world.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Color;
import com.teamname.world.AdventureRPG;

public class NPCEntity extends Entity {

    private String name;
    private String dialogue;
    private AdventureRPG game;
    private String flagToSet; // インタラクト時にセットするフラグキー
    private int flagValue; // セットする値

    public NPCEntity(float x, float y, String name, String dialogue, AdventureRPG game) {
        this(x, y, name, dialogue, game, null, 0);
    }

    public NPCEntity(float x, float y, String name, String dialogue, AdventureRPG game, String flagToSet,
            int flagValue) {
        super(x, y, 32, 32);
        this.name = name;
        this.dialogue = dialogue;
        this.game = game;
        this.flagToSet = flagToSet;
        this.flagValue = flagValue;

        // Placeholder texture (Green box for NPC)
        Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.GREEN);
        pixmap.fill();
        this.texture = new Texture(pixmap);
        pixmap.dispose();
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
