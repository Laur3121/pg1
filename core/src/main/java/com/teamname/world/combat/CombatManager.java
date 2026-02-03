package com.teamname.world.combat;

import java.util.ArrayList;
import java.util.List;
import com.teamname.world.system.GameState;
import com.teamname.world.system.Character;

/**
 * 戦闘全体を管理するメインクラス
 * 戦闘の開始、ターン進行、アクション実行を制御する
 */
public class CombatManager {

    // 味方パーティ
    private List<ICombatant> party;

    // 敵グループ
    private List<ICombatant> enemies;

    // ゲーム状態 (報酬付与用)
    private GameState gameState;

    // 全ての戦闘参加者
    private List<ICombatant> allCombatants;

    // ターン順管理
    private TurnOrder turnOrder;

    // 現在の行動者
    private ICombatant currentActor;

    // 戦闘状態
    private BattleState battleState;

    /**
     * 戦闘状態の列挙型
     */
    public enum BattleState {
        NOT_STARTED, // 未開始
        IN_PROGRESS, // 進行中
        VICTORY, // 勝利
        DEFEAT, // 敗北
        FLED // 逃走成功
    }

    /**
     * コンストラクタ
     */
    public CombatManager() {
        this.party = new ArrayList<>();
        this.enemies = new ArrayList<>();
        this.allCombatants = new ArrayList<>();
        this.turnOrder = new TurnOrder();
        this.battleState = BattleState.NOT_STARTED;
    }

    /**
     * 戦闘を開始する
     * 
     * @param party   味方パーティのリスト
     * @param enemies 敵グループのリスト
     */
    public void startBattle(List<ICombatant> party, List<ICombatant> enemies, GameState gameState) {
        this.gameState = gameState;
        // 参加者リストをクリア
        this.party.clear();
        this.enemies.clear();
        this.allCombatants.clear();

        // 味方と敵を設定
        this.party.addAll(party);
        this.enemies.addAll(enemies);

        // 全参加者リストに追加
        this.allCombatants.addAll(party);
        this.allCombatants.addAll(enemies);

        // ターン順を初期化
        turnOrder.initialize(allCombatants);

        // 戦闘状態を進行中に設定
        this.battleState = BattleState.IN_PROGRESS;

        // 最初のターンを開始
        nextTurn();

        // System.out.println("===== START! =====");
        // System.out.println("you: " + party.size() + "people");
        // System.out.println("enemy: " + enemies.size() + "people");
    }

    /**
     * 次のターンへ進む
     * ターン順に基づいて次の行動者を決定する
     */
    public void nextTurn() {
        // 戦闘終了判定
        if (checkBattleEnd()) {
            return;
        }

        // Reset multiplier
        damageMultiplier = 1.0f;

        // 次の行動者を取得
        currentActor = turnOrder.getNext();

        // 行動者が戦闘不能の場合はスキップ
        while (currentActor != null && !currentActor.isAlive()) {
            turnOrder.remove(currentActor);
            currentActor = turnOrder.getNext();
        }

        if (currentActor != null) {
            // System.out.println("\n--- " + currentActor.getName() + "no ta-nn ---");
        }
    }

    /**
     * アクションを実行する
     * 
     * @param actor   行動者
     * @param action  実行するアクション
     * @param targets 対象のリスト
     */
    public void applyAction(ICombatant actor, CombatAction action, List<ICombatant> targets) {
        if (battleState != BattleState.IN_PROGRESS) {
            // System.out.println("tatakaityuujanaiyo");
            return;
        }

        if (actor != currentActor) {
            // System.out.println("This calactor dont turn");
            return;
        }

        if (!actor.isAlive()) {
            // System.out.println(actor.getName() + " dont action");
            return;
        }

        // System.out.println(actor.getName() + " action : " + action);

        // アクションタイプに応じた処理
        switch (action) {
            case ATTACK:
                executeAttack(actor, targets);
                break;
            case DEFEND:
                executeDefend(actor);
                break;
            case SKILL:
                executeSkill(actor, targets);
                break;
            case ITEM:
                executeItem(actor, targets);
                break;
            case FLEE:
                executeFlee();
                break;
        }

        // 戦闘不能者をターン順から除外
        removeDefeatedCombatants();

        // 戦闘終了判定
        if (!checkBattleEnd()) {
            // 次のターンへ
            nextTurn();
        }
    }

    private float damageMultiplier = 1.0f;

    public void setDamageMultiplier(float multiplier) {
        this.damageMultiplier = multiplier;
    }

    /**
     * 通常攻撃を実行
     * 
     * @param actor   攻撃者
     * @param targets 対象リスト
     */
    private void executeAttack(ICombatant actor, List<ICombatant> targets) {
        if (targets == null || targets.isEmpty()) {
            // System.out.println("you dont select target");
            return;
        }

        ICombatant target = targets.get(0);

        // ダメージ計算(簡易版) + Multiplier
        int baseDamage = Math.max(1, actor.getPower() - target.getBlock());
        int damage = (int) (baseDamage * damageMultiplier);

        target.takeDamage(damage);
        // System.out.println(target.getName() + ":" + damage + " dameges");
        if (damageMultiplier > 1.0f) {
            System.out.println("[LUCKY TIME ACTIVE] Damage Multiplied! Def: " + baseDamage + " -> " + damage);
        }

        if (!target.isAlive()) {
            // System.out.println(target.getName() + " is dead");
        }
    }

    /**
     * 防御を実行
     * 
     * @param actor 防御者
     */
    private void executeDefend(ICombatant actor) {
        // System.out.println(actor.getName() + " is blocked");
        // TODO: 防御状態の実装
    }

    /**
     * スキルを実行
     * 
     * @param actor   使用者
     * @param targets 対象リスト
     */
    private void executeSkill(ICombatant actor, List<ICombatant> targets) {
        // System.out.println("skill system dont implement");
        // TODO: スキルシステムの実装
    }

    /**
     * アイテムを使用
     * 
     * @param actor   使用者
     * @param targets 対象リスト
     */
    private void executeItem(ICombatant actor, List<ICombatant> targets) {
        // System.out.println("item dont implement");
        // TODO: アイテムシステムの実装
    }

    /**
     * 逃走を試みる
     */
    private void executeFlee() {
        // TODO: 逃走判定の実装(成功率など)
        battleState = BattleState.FLED;
        // System.out.println("success to escape!");
    }

    /**
     * 戦闘不能者をターン順から除外
     */
    private void removeDefeatedCombatants() {
        for (ICombatant combatant : allCombatants) {
            if (!combatant.isAlive()) {
                turnOrder.remove(combatant);
            }
        }
    }

    /**
     * 戦闘終了判定
     * 
     * @return 戦闘が終了していればtrue
     */
    private boolean checkBattleEnd() {
        // 味方が全滅
        boolean partyDefeated = party.stream().noneMatch(ICombatant::isAlive);
        if (partyDefeated) {
            battleState = BattleState.DEFEAT;
            // System.out.println("\n===== lose... =====");
            return true;
        }

        // 敵が全滅
        boolean enemiesDefeated = enemies.stream().noneMatch(ICombatant::isAlive);
        if (enemiesDefeated) {
            battleState = BattleState.VICTORY;
            processVictory();
            // System.out.println("\n===== win! =====");
            return true;
        }

        return false;
    }

    // ===== ゲッター =====

    public List<ICombatant> getParty() {
        return new ArrayList<>(party);
    }

    public List<ICombatant> getEnemies() {
        return new ArrayList<>(enemies);
    }

    public List<ICombatant> getAllCombatants() {
        return new ArrayList<>(allCombatants);
    }

    public ICombatant getCurrentActor() {
        return currentActor;
    }

    public BattleState getBattleState() {
        return battleState;
    }

    public boolean isBattleActive() {
        return battleState == BattleState.IN_PROGRESS;
    }

    /**
     * 戦闘を強制終了する（デバッグ用）
     * 
     * @param state 終了状態（VICTORY または DEFEAT）
     */
    public void forceFinish(BattleState state) {
        this.battleState = state;
        if (state == BattleState.VICTORY) {
            processVictory();
        }
    }

    private void processVictory() {
        if (this.gameState == null)
            return;

        int totalExp = 0;
        int totalGold = 0;

        for (ICombatant enemy : enemies) {
            if (enemy instanceof Monster) {
                Monster m = (Monster) enemy;
                totalExp += m.getExp();
                totalGold += m.getGold();
            }
        }

        // ゴールド加算
        this.gameState.gold += totalGold;
        System.out.println("Victory! Gained " + totalGold + "G, " + totalExp + " EXP.");

        // EXP加算
        for (ICombatant member : party) {
            if (member instanceof Character) {
                Character c = (Character) member;
                c.exp += totalExp;
                // レベルアップ処理などはCharacterクラスに任せるか、ここで実装する
                // 簡易実装: レベルアップ閾値チェックなどは割愛
                if (c.exp >= c.level * 100) {
                    c.level++;
                    c.exp -= (c.level - 1) * 100;
                    c.maxHp += 10;
                    c.currentHp = c.maxHp;
                    c.str += 2;
                    System.out.println(c.name + " Level Up! -> " + c.level);
                }
            }
        }
    }
}
