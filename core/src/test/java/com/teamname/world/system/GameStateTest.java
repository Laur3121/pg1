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
    public void testInitialStats() {
        assertEquals(50, gameState.maxHp);
        assertEquals(10, gameState.str);
        assertEquals(5, gameState.def);
    }

    @Test
    public void testEquipWeapon() {
        gameState.equip(1, "WEAPON");
        assertEquals(1, gameState.equippedWeaponId);

        // Calculate Attack
        int attack = gameState.getAttack(mockDataLoader);
        assertEquals(20, attack); // 10 (Base) + 10 (Sword)
    }

    @Test
    public void testEquipArmor() {
        gameState.equip(2, "ARMOR");
        assertEquals(2, gameState.equippedArmorId);

        // Calculate Defense
        int defense = gameState.getDefense(mockDataLoader);
        assertEquals(10, defense); // 5 (Base) + 5 (Shield)
    }

    @Test
    public void testEquipBoth() {
        gameState.equip(1, "WEAPON");
        gameState.equip(2, "ARMOR");

        assertEquals(20, gameState.getAttack(mockDataLoader));
        assertEquals(10, gameState.getDefense(mockDataLoader));
    }

    @Test
    public void testEquipInvalidType() {
        gameState.equip(1, "POTION");
        assertEquals(0, gameState.equippedWeaponId);
        assertEquals(0, gameState.equippedArmorId);
    }
}
