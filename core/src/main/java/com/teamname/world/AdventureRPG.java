package com.teamname.world;

import com.badlogic.gdx.Game;
import com.teamname.world.combat.CombatScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class AdventureRPG extends Game {

    @Override
    public void create() {
        // Launch combat screen on startup
        System.out.println("\n========================================");
        System.out.println("   AdventureRPG Starting");
        System.out.println("   Launching Combat Screen...");
        System.out.println("========================================\n");

        // Set combat screen as the active screen
        setScreen(new CombatScreen());
    }

    @Override
    public void dispose() {
        super.dispose();
        if (getScreen() != null) {
            getScreen().dispose();
        }
    }
}
