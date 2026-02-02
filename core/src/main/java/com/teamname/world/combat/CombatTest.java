package com.teamname.world.combat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 戦闘システムのテストクラス
 * コンソールで戦闘の流れを確認できる
 */
public class CombatTest {

    /**
     * 簡易的な戦闘テストを実行
     */
    public static void runSimpleCombatTest() {
        // System.out.println("\n##########################################");
        // System.out.println("# TEST START");
        // System.out.println("##########################################\n");

        // 戦闘マネージャーを作成
        CombatManager combatManager = new CombatManager();

        // 味方パーティを作成
        List<ICombatant> party = new ArrayList<>();
        party.add(new TestCharacter("brave man", 100, 20, 10, 15));
        party.add(new TestCharacter("fighter", 120, 25, 15, 10));
        party.add(new TestCharacter("magician", 70, 30, 5, 12));

        // 敵グループを作成
        List<ICombatant> enemies = new ArrayList<>();
        enemies.add(new TestCharacter("slime", 50, 10, 5, 8));
        enemies.add(new TestCharacter("gobline", 60, 15, 8, 14));

        // System.out.println("=== party ===");
        for (ICombatant member : party) {
            // System.out.println(member);
        }

        // System.out.println("\n=== enermy ===");
        for (ICombatant enemy : enemies) {
            // System.out.println(enemy);
        }
        // System.out.println();

        // 戦闘開始
        combatManager.startBattle(party, enemies, null);

        // 自動戦闘シミュレーション
        int turnCount = 0;
        int maxTurns = 20; // 無限ループ防止

        while (combatManager.isBattleActive() && turnCount < maxTurns) {
            turnCount++;
            ICombatant actor = combatManager.getCurrentActor();

            if (actor == null) {
                break;
            }

            // 味方か敵かを判定
            boolean isPartyMember = party.contains(actor);

            // ターゲットを選択
            List<ICombatant> possibleTargets = isPartyMember ? combatManager.getEnemies() : combatManager.getParty();

            // 生存しているターゲットをフィルタリング
            List<ICombatant> aliveTargets = new ArrayList<>();
            for (ICombatant target : possibleTargets) {
                if (target.isAlive()) {
                    aliveTargets.add(target);
                }
            }

            if (!aliveTargets.isEmpty()) {
                // 最初の生存者を攻撃
                ICombatant target = aliveTargets.get(0);
                combatManager.applyAction(actor, CombatAction.ATTACK, Arrays.asList(target));
            }

            // ターン間の区切りを見やすく
            try {
                Thread.sleep(500); // 0.5秒待機
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 結果表示
        // System.out.println("\n##########################################");
        // System.out.println("# result: " + combatManager.getBattleState());
        // System.out.println("# total turn: " + turnCount);
        // System.out.println("##########################################\n");

        // System.out.println("=== result ===");
        // System.out.println("\nyou:");
        for (ICombatant member : party) {
            // System.out.println(member + (member.isAlive() ? " [live]" : " [dead]"));
        }

        // System.out.println("\nenemy:");
        for (ICombatant enemy : enemies) {
            // System.out.println(enemy + (enemy.isAlive() ? " [live]" : " [dead]"));
        }
    }

    /**
     * メインメソッド - 単体テスト用
     */
    public static void main(String[] args) {
        runSimpleCombatTest();
    }
}
