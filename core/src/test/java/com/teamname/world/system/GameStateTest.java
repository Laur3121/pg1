package com.teamname.world.system;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class GameStateTest {

    private GameState gameState;
    private DataLoader mockDataLoader;

    @Before
    public void setUp() {
        gameState = new GameState();
        // Setup mock data loader
        mockDataLoader = new DataLoader() {
            @Override
            public ItemData getItemDataById(int id) {
                if (id == 1) { // Sword
                    ItemData d = new ItemData();
                    d.id = 1;
                    d.name = "Sword";
                    d.type = "WEAPON";
                    d.power = 10;
                    return d;
                }
                if (id == 2) { // Shield
                    ItemData d = new ItemData();
                    d.id = 2;
                    d.name = "Shield";
                    d.type = "ARMOR";
                    d.defense = 5;
                    return d;
                }
                return null;
            }
        };
    }

    @Test
    public void testPartyManagement() {
        Character hero = new Character("Hero", 100, 30, 10, 5);
        gameState.addMember(hero);

        assertEquals(1, gameState.partyMembers.size());
        assertEquals("Hero", gameState.getLeader().name);
    }

    @Test
    public void testCharacterStats() {
        Character hero = new Character("Hero", 50, 20, 10, 5);

        // Initial stats
        assertEquals(50, hero.maxHp);
        assertEquals(10, hero.str);
        assertEquals(5, hero.def);
    }

    @Test
    public void testEquipWeaponCalculation() {
        Character hero = new Character("Hero", 50, 20, 10, 5);
        // 直接IDを設定（InventoryUIの実装に合わせる）
        hero.equippedWeaponId = 1;

        // Calculate Attack
        int attack = hero.getAttack(mockDataLoader);
        assertEquals(20, attack); // 10 (Base) + 10 (Sword)
    }

    @Test
    public void testEquipArmorCalculation() {
        Character hero = new Character("Hero", 50, 20, 10, 5);
        hero.equippedArmorId = 2;

        // Calculate Defense
        int defense = hero.getDefense(mockDataLoader);
        assertEquals(10, defense); // 5 (Base) + 5 (Shield)
    }

    @Test
    public void testEquipBothCalculation() {
        Character hero = new Character("Hero", 50, 20, 10, 5);
        hero.equippedWeaponId = 1;
        hero.equippedArmorId = 2;

        assertEquals(20, hero.getAttack(mockDataLoader));
        assertEquals(10, hero.getDefense(mockDataLoader));
    }
}
