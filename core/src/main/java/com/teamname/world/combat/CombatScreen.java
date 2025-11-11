package com.teamname.world.combat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Combat screen for visual battle display
 * Displays battle information in the game window
 */
public class CombatScreen implements Screen {

    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;

    private CombatManager combatManager;
    private List<ICombatant> party;
    private List<ICombatant> enemies;

    private float stateTime;
    private float turnDelay;
    private static final float TURN_DURATION = 2.0f; // 2 seconds per turn

    private List<String> combatLog;
    private static final int MAX_LOG_LINES = 10;

    public CombatScreen() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(1.5f);

        shapeRenderer = new ShapeRenderer();

        combatLog = new ArrayList<>();
        stateTime = 0;
        turnDelay = 0;

        initializeBattle();
    }

    /**
     * Initialize battle with test characters
     */
    private void initializeBattle() {
        combatManager = new CombatManager();

        // Create party
        party = new ArrayList<>();
        party.add(new TestCharacter("Hero", 100, 20, 10, 15));
        party.add(new TestCharacter("Warrior", 120, 25, 15, 10));
        party.add(new TestCharacter("Mage", 70, 30, 5, 12));

        // Create enemies
        enemies = new ArrayList<>();
        enemies.add(new TestCharacter("Slime", 50, 10, 5, 8));
        enemies.add(new TestCharacter("Goblin", 60, 15, 8, 14));

        addLog("=== Battle Start! ===");
        addLog("Party: " + party.size() + " members");
        addLog("Enemies: " + enemies.size() + " enemies");

        combatManager.startBattle(party, enemies);
    }

    /**
     * Add message to combat log
     */
    private void addLog(String message) {
        combatLog.add(message);
        if (combatLog.size() > MAX_LOG_LINES) {
            combatLog.remove(0);
        }
    }

    @Override
    public void show() {
        // Called when screen becomes active
    }

    @Override
    public void render(float delta) {
        stateTime += delta;
        turnDelay += delta;

        // Clear screen
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Auto-progress turns
        if (combatManager.isBattleActive() && turnDelay >= TURN_DURATION) {
            turnDelay = 0;
            executeTurn();
        }

        // Draw battle UI
        drawBattleUI();
    }

    /**
     * Execute one turn automatically
     */
    private void executeTurn() {
        ICombatant actor = combatManager.getCurrentActor();

        if (actor == null) {
            return;
        }

        addLog("--- " + actor.getName() + "'s Turn ---");

        // Determine if actor is party member or enemy
        boolean isPartyMember = party.contains(actor);

        // Select target
        List<ICombatant> possibleTargets = isPartyMember ?
            combatManager.getEnemies() : combatManager.getParty();

        // Filter alive targets
        List<ICombatant> aliveTargets = new ArrayList<>();
        for (ICombatant target : possibleTargets) {
            if (target.isAlive()) {
                aliveTargets.add(target);
            }
        }

        if (!aliveTargets.isEmpty()) {
            ICombatant target = aliveTargets.get(0);
            int hpBefore = target.getCurrentHP();

            combatManager.applyAction(actor, CombatAction.ATTACK, Arrays.asList(target));

            int damage = hpBefore - target.getCurrentHP();
            addLog(actor.getName() + " -> " + target.getName() + " (" + damage + " dmg)");

            if (!target.isAlive()) {
                addLog(target.getName() + " defeated!");
            }
        }

        // Check battle end
        if (!combatManager.isBattleActive()) {
            addLog("=== " + combatManager.getBattleState() + " ===");
        }
    }

    /**
     * Draw battle UI elements
     */
    private void drawBattleUI() {
        batch.begin();

        // Title
        font.getData().setScale(2.0f);
        font.draw(batch, "COMBAT SYSTEM", 20, Gdx.graphics.getHeight() - 20);
        font.getData().setScale(1.5f);

        // Draw party status (left side)
        float partyX = 20;
        float partyY = Gdx.graphics.getHeight() - 80;

        font.setColor(Color.GREEN);
        font.draw(batch, "=== PARTY ===", partyX, partyY);
        partyY -= 30;

        for (ICombatant member : party) {
            if (member.isAlive()) {
                font.setColor(Color.WHITE);
            } else {
                font.setColor(Color.GRAY);
            }

            String status = member.getName() + ": " + member.getCurrentHP() + "/" + member.getMaxHP();
            font.draw(batch, status, partyX, partyY);

            // Draw HP bar
            drawHPBar(partyX + 150, partyY - 5, 100, 10, member);

            partyY -= 25;
        }

        // Draw enemy status (right side)
        float enemyX = Gdx.graphics.getWidth() - 300;
        float enemyY = Gdx.graphics.getHeight() - 80;

        font.setColor(Color.RED);
        font.draw(batch, "=== ENEMIES ===", enemyX, enemyY);
        enemyY -= 30;

        for (ICombatant enemy : enemies) {
            if (enemy.isAlive()) {
                font.setColor(Color.WHITE);
            } else {
                font.setColor(Color.GRAY);
            }

            String status = enemy.getName() + ": " + enemy.getCurrentHP() + "/" + enemy.getMaxHP();
            font.draw(batch, status, enemyX, enemyY);

            // Draw HP bar
            drawHPBar(enemyX + 150, enemyY - 5, 100, 10, enemy);

            enemyY -= 25;
        }

        // Draw combat log (bottom)
        font.setColor(Color.YELLOW);
        float logY = 250;
        font.draw(batch, "=== Combat Log ===", 20, logY);
        logY -= 25;

        font.setColor(Color.WHITE);
        font.getData().setScale(1.0f);
        for (int i = combatLog.size() - 1; i >= 0; i--) {
            font.draw(batch, combatLog.get(i), 20, logY);
            logY -= 20;
        }
        font.getData().setScale(1.5f);

        // Battle state
        if (!combatManager.isBattleActive()) {
            font.getData().setScale(3.0f);
            font.setColor(Color.GOLD);
            String result = combatManager.getBattleState().toString();
            font.draw(batch, result, Gdx.graphics.getWidth() / 2 - 100, Gdx.graphics.getHeight() / 2);
            font.getData().setScale(1.5f);
        }

        batch.end();
    }

    /**
     * Draw HP bar for a combatant
     */
    private void drawHPBar(float x, float y, float width, float height, ICombatant combatant) {
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Background (black)
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(x, y, width, height);

        // HP bar
        float hpPercent = (float) combatant.getCurrentHP() / combatant.getMaxHP();

        if (hpPercent > 0.5f) {
            shapeRenderer.setColor(Color.GREEN);
        } else if (hpPercent > 0.25f) {
            shapeRenderer.setColor(Color.YELLOW);
        } else {
            shapeRenderer.setColor(Color.RED);
        }

        shapeRenderer.rect(x, y, width * hpPercent, height);

        // Border (white)
        shapeRenderer.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(x, y, width, height);
        shapeRenderer.end();

        batch.begin();
    }

    @Override
    public void resize(int width, int height) {
        // Handle window resize
    }

    @Override
    public void pause() {
        // Handle pause
    }

    @Override
    public void resume() {
        // Handle resume
    }

    @Override
    public void hide() {
        // Called when screen is no longer active
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        shapeRenderer.dispose();
    }
}
