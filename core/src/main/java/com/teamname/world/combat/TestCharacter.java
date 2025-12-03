package com.teamname.world.combat;

/**
 * テスト用のキャラクタークラス
 * ICombatantインターフェースの簡易実装
 */
public class TestCharacter implements ICombatant {

    private String name;
    private int currentHP;
    private int maxHP;
    private int attackPower;
    private int defense;
    private int speed;

    /**
     * コンストラクタ
     * @param name 名前
     * @param maxHP 最大HP
     * @param attackPower 攻撃力
     * @param defense 防御力
     * @param speed 素早さ
     */
    public TestCharacter(String name, int maxHP, int attackPower, int defense, int speed) {
        this.name = name;
        this.maxHP = maxHP;
        this.currentHP = maxHP;
        this.attackPower = attackPower;
        this.defense = defense;
        this.speed = speed;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getCurrentHP() {
        return currentHP;
    }

    @Override
    public int getMaxHP() {
        return maxHP;
    }

    @Override
    public boolean isAlive() {
        return currentHP > 0;
    }

    @Override
    public void takeDamage(int damage) {
        currentHP -= damage;
        if (currentHP < 0) {
            currentHP = 0;
        }
        //System.out.println(name + " HP: " + currentHP + "/" + maxHP);
    }

    @Override
    public void heal(int amount) {
        currentHP += amount;
        if (currentHP > maxHP) {
            currentHP = maxHP;
        }
        //System.out.println(name + " HP: " + currentHP + "/" + maxHP);
    }

    @Override
    public int getSpeed() {
        return speed;
    }

    @Override
    public int getAttackPower() {
        return attackPower;
    }

    @Override
    public int getDefense() {
        return defense;
    }

    @Override
    public String toString() {
        return name + " [HP:" + currentHP + "/" + maxHP + " ATK:" + attackPower + " DEF:" + defense + " SPD:" + speed + "]";
    }
}
