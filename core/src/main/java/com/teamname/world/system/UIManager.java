package com.teamname.world.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.teamname.world.AdventureRPG;
import com.teamname.world.system.ui.DialogUI;
import com.teamname.world.system.ui.ShopUI;
import com.teamname.world.system.ui.ShopUI;

/**
 * ゲーム内のUI全体を管理するクラス。
 * メニュー、会話、戦闘UIへの遷移などを担当します。
 */
public class UIManager {

    private AdventureRPG game;
    private MenuTab menuTab;
    private DialogUI dialogUI;
    private ShopUI shopUI;

    // 追加UI
    private com.teamname.world.system.ui.InventoryUI inventoryUI;
    private com.teamname.world.system.ui.StatusUI statusUI;
    private com.teamname.world.system.ui.SaveUI saveUI;
    private com.teamname.world.system.ui.DebugUI debugUI;

    public UIManager(AdventureRPG game) {
        this.game = game;
        this.menuTab = new MenuTab(game); // Hub
        this.dialogUI = new DialogUI();
        this.shopUI = new ShopUI(game);

        this.inventoryUI = new com.teamname.world.system.ui.InventoryUI(game);
        this.statusUI = new com.teamname.world.system.ui.StatusUI(game);
        this.saveUI = new com.teamname.world.system.ui.SaveUI(game);
        this.debugUI = new com.teamname.world.system.ui.DebugUI(game);
    }

    // --- 各画面の表示メソッド (MenuTabから呼ばれる) ---
    public void showInventory() {
        if (!inventoryUI.isVisible())
            inventoryUI.show();
    }

    public void showStatus() {
        if (!statusUI.isVisible())
            statusUI.show();
    }

    public void showSave() {
        if (!saveUI.isVisible())
            saveUI.show();
    }

    public void showDebug() {
        if (!debugUI.isVisible())
            debugUI.show();
    }

    public void showMenu() {
        if (!menuTab.isVisible())
            menuTab.toggle();
    }

    public void showShop() {
        if (!shopUI.isVisible())
            shopUI.show();
    }

    // --- 会話関連 ---
    public void showDialog(String text) {
        dialogUI.showDialog(text);
    }

    public void showDialog(String name, String text) {
        dialogUI.showDialog(name, text);
    }
    
    public void showDialogWithOptions(String name, String text, java.util.List<com.teamname.world.system.event.DialogOption> options, com.teamname.world.system.event.EventManager eventManager) {
        dialogUI.showDialogWithOptions(name, text, options, eventManager);
    }

    public DialogUI getDialogUI() {
        return dialogUI;
    }

    // --- 戦闘関連 ---
    // --- 戦闘関連 ---
    public void showBattleUI(int enemyId) {
        // 敵生成
        java.util.List<com.teamname.world.combat.ICombatant> enemies = new java.util.ArrayList<>();
        // とりあえずIDに関わらず固定の敵セット
        enemies.add(new com.teamname.world.combat.Monster("Evil Mage", 80, 18, 8, 14, 50, 100));
        enemies.add(new com.teamname.world.combat.Monster("Archer", 60, 16, 5, 16, 40, 80));

        showBattleUI(enemies);
    }

    public void showBattleUI(java.util.List<com.teamname.world.combat.ICombatant> enemies) {
        // パーティ取得
        java.util.List<com.teamname.world.combat.ICombatant> party = new java.util.ArrayList<>();
        if (game.getGameState() != null && game.getGameState().partyMembers != null) {
            party.addAll(game.getGameState().partyMembers);
        }

        System.out.println("Battle Started with " + enemies.size() + " enemies.");

        // 戦闘画面初期化
        if (game.combatScreen != null) {
            game.combatScreen.startBattle(party, enemies, game.getGameState());
        }

        game.battleflag = 1;
        // GameScreen内でbattleflagを見て描画切り替えを行うため、setScreenはしない
    }

    public void addBattleLog(String message) {
        System.out.println("[BATTLE LOG] " + message);
    }

    // --- 更新・描画 ---
    public void updateAndRender(float delta) {
        // キー入力監視
        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            inventoryUI.toggle();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            if (statusUI.isVisible())
                statusUI.hide();
            else
                statusUI.show();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            if (saveUI.isVisible())
                saveUI.hide();
            else
                saveUI.show();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            if (debugUI.isVisible())
                debugUI.hide();
            else
                debugUI.show();
        }

        // 各UIの描画 (順序注意: 後に描くほうが手前)
        if (menuTab != null)
            menuTab.updateAndRender(delta);
        if (inventoryUI != null)
            inventoryUI.updateAndRender(delta);
        if (statusUI != null)
            statusUI.updateAndRender(delta);
        if (saveUI != null)
            saveUI.updateAndRender(delta);
        if (debugUI != null)
            debugUI.updateAndRender(delta);
        if (shopUI != null)
            shopUI.updateAndRender(delta);
        if (dialogUI != null)
            dialogUI.updateAndRender(delta);
    }

    public void resize(int width, int height) {
        if (menuTab != null)
            menuTab.resize(width, height);
        if (inventoryUI != null)
            inventoryUI.resize(width, height);
        if (statusUI != null)
            statusUI.resize(width, height);
        if (saveUI != null)
            saveUI.resize(width, height);
        if (debugUI != null)
            debugUI.resize(width, height);
        if (shopUI != null)
            shopUI.resize(width, height);
        if (dialogUI != null)
            dialogUI.resize(width, height);
    }

    public void dispose() {
        if (menuTab != null)
            menuTab.dispose();
        if (inventoryUI != null)
            inventoryUI.dispose();
        if (statusUI != null)
            statusUI.dispose();
        if (saveUI != null)
            saveUI.dispose();
        if (debugUI != null)
            debugUI.dispose();
        if (shopUI != null)
            shopUI.dispose();
        if (dialogUI != null)
            dialogUI.dispose();
    }

    public boolean isAnyUIOpen() {
        return (menuTab != null && menuTab.isVisible()) ||
                (inventoryUI != null && inventoryUI.isVisible()) ||
                (statusUI != null && statusUI.isVisible()) ||
                (saveUI != null && saveUI.isVisible()) ||
                (debugUI != null && debugUI.isVisible()) ||
                (shopUI != null && shopUI.isVisible()) ||
                (dialogUI != null && dialogUI.isVisible());
    }
}
