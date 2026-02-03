package com.teamname.world.combat.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * ターン順を管理するクラス
 * 戦闘参加者の素早さに基づいて行動順を決定する
 */
public class TurnOrder {

    private Queue<ICombatant> turnQueue;
    private List<ICombatant> allCombatants;

    public TurnOrder() {
        this.turnQueue = new LinkedList<>();
        this.allCombatants = new ArrayList<>();
    }

    /**
     * ターン順を初期化
     * 素早さの高い順にソートして行動順を決定
     * 
     * @param combatants 戦闘参加者のリスト
     */
    public void initialize(List<ICombatant> combatants) {
        allCombatants.clear();
        allCombatants.addAll(combatants);

        // 素早さの高い順にソート
        List<ICombatant> sorted = new ArrayList<>(combatants);
        sorted.sort(Comparator.comparingInt(ICombatant::getQuick).reversed());

        turnQueue.clear();
        turnQueue.addAll(sorted);

        // System.out.println("=== turn ===");
        for (ICombatant c : sorted) {
            // System.out.println(c.getName() + " (quick: " + c.getQuick() + ")");
        }
    }

    /**
     * 次の行動者を取得
     * キューが空になったら再度全員をキューに追加（新しいラウンド）
     * 
     * @return 次に行動する戦闘参加者
     */
    public ICombatant getNext() {
        // キューが空なら再初期化
        if (turnQueue.isEmpty()) {
            // 生存者のみでターン順を再構築
            List<ICombatant> alive = new ArrayList<>();
            for (ICombatant c : allCombatants) {
                if (c.isAlive()) {
                    alive.add(c);
                }
            }

            if (alive.isEmpty()) {
                return null;
            }

            alive.sort(Comparator.comparingInt(ICombatant::getQuick).reversed());
            turnQueue.addAll(alive);
        }

        return turnQueue.poll();
    }

    /**
     * ターン順をリセット
     */
    public void reset() {
        turnQueue.clear();
        if (!allCombatants.isEmpty()) {
            initialize(allCombatants);
        }
    }

    /**
     * 戦闘参加者をターン順から除外(戦闘不能時など)
     * 
     * @param combatant 除外する戦闘参加者
     */
    public void remove(ICombatant combatant) {
        turnQueue.remove(combatant);
        allCombatants.remove(combatant);
    }
}
