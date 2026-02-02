package com.teamname.world;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.teamname.world.combat.VisualCombatScreen;
import com.teamname.world.system.DataLoader;
import com.teamname.world.system.GameInitializer; // 既存のファイルをインポート
import com.teamname.world.system.Inventory;
import com.teamname.world.system.UIManager;
import com.teamname.world.system.AudioManager;
import com.teamname.world.system.GameState;

import com.teamname.world.combat.DataLoaderProvider;

public class AdventureRPG extends Game implements DataLoaderProvider {

    private SpriteBatch batch;

    // ゲーム全体の主要データ
    private DataLoader dataLoader;
    private Inventory inventory;
    private GameState gameState;

    // UIマネージャー
    private UIManager uiManager;

    // 戦闘画面（VisualCombatScreen を使用）
    public VisualCombatScreen combatScreen;

    // フラグ管理（0: 通常, 1: 戦闘）
    public int battleflag = 0;

    @Override
    public void create() {
        batch = new SpriteBatch();
        GameInitializer.initialize(this);
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (batch != null)
            batch.dispose();
        if (uiManager != null)
            uiManager.dispose();
        if (combatScreen != null)
            combatScreen.dispose();
    }

    // --- ▼ 追加したセッター（GameInitializerから使うために必要） ---

    public void setDataLoader(DataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void setUIManager(UIManager uiManager) {
        this.uiManager = uiManager;
    }

    // --- ゲッター（既存のまま） ---

    public SpriteBatch getBatch() {
        return batch;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public GameState getGameState() {
        return gameState;
    }

    public DataLoader getDataLoader() {
        return dataLoader;
    }

    public UIManager getUIManager() {
        return uiManager;
    }

    // --- AudioManager ---
    private AudioManager audioManager;

    public void setAudioManager(AudioManager audioManager) {
        this.audioManager = audioManager;
    }

    public AudioManager getAudioManager() {
        return audioManager;
    }
}

