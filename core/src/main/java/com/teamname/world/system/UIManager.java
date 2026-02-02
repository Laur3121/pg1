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
    private ShopUI shopUI; // 追加

    public UIManager(AdventureRPG game) {
        this.game = game;
        this.menuTab = new MenuTab(game); // 既存のMenuTabを利用
        this.dialogUI = new DialogUI();
        this.shopUI = new ShopUI(game); // 追加
    }

    // --- メニュー関連 ---
    public void showMenu() {
        if (!menuTab.isVisible() && !shopUI.isVisible()) {
            menuTab.toggle();
        }
    }

    public void hideMenu() {
        if (menuTab.isVisible()) {
            menuTab.toggle();
        }
    }

    public MenuTab getMenuTab() {
        return menuTab;
    }

    // --- ショップ関連 ---
    public void showShop() {
        if (!shopUI.isVisible()) {
            // メニューが出てたら閉じる
            if (menuTab.isVisible())
                menuTab.toggle();
            shopUI.show();
        }
    }

    // --- 会話関連 ---
    public void showDialog(String text) {
        dialogUI.showDialog(text);
    }

    public DialogUI getDialogUI() {
        return dialogUI;
    }

    // --- 戦闘関連 ---
    /**
     * 戦闘画面へ遷移します。
     * 
     * @param enemyId 敵のIDなどを渡す想定
     */
    public void showBattleUI(int enemyId) {
        // TODO: 敵IDに応じたセットアップをここで行う
        // 例: game.combatScreen.setupEncounter(enemyId);
        System.out.println("Battle Started with Enemy ID: " + enemyId);
        game.battleflag = 1;
        game.setScreen(game.combatScreen);
    }

    /**
     * 戦闘ログなどを表示する（仮）
     */
    public void addBattleLog(String message) {
        // 必要ならBattleScreenへ転送、またはオーバーレイ表示
        System.out.println("[BATTLE LOG] " + message);
    }

    // --- 更新・描画 ---
    public void updateAndRender(float delta) {
        // キー入力でショップテスト（Sキー）
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            if (shopUI.isVisible())
                shopUI.hide();
            else
                showShop();
        }

        // メニューの描画
        if (menuTab != null) {
            menuTab.updateAndRender(delta);
        }
        // 会話ウィンドウの描画（メニューより上に表示）
        if (dialogUI != null) {
            dialogUI.updateAndRender(delta);
        }
        // ショップ画面
        if (shopUI != null) {
            shopUI.updateAndRender(delta);
        }
    }

    public void resize(int width, int height) {
        if (menuTab != null)
            menuTab.resize(width, height);
        if (dialogUI != null)
            dialogUI.resize(width, height);
        if (shopUI != null)
            shopUI.resize(width, height);
    }

    public void dispose() {
        if (menuTab != null)
            menuTab.dispose();
        if (dialogUI != null)
            dialogUI.dispose();
    }
}
