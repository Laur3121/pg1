package com.teamname.world.system;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;

public class ShopTest {

    private Shop shop;
    private Inventory inventory;
    private GameState gameState;

    @Before
    public void setUp() {
        shop = new Shop();
        inventory = new Inventory();
        gameState = new GameState();
        // Default GameState: 100 Gold
        gameState.gold = 100;
    }

    private ItemData createItemData(int id, String name, int price) {
        ItemData data = new ItemData();
        data.id = id;
        data.name = name;
        data.value = price;
        data.type = "ITEM";
        return data;
    }

    @Test
    public void testBuyItemSuccess() {
        ItemData potion = createItemData(1, "Potion", 10);
        shop.addShopItem(potion);

        boolean result = shop.buyItem(gameState, inventory, 0);

        assertTrue(result);
        assertEquals(90, gameState.gold);
        assertEquals(1, inventory.getItemCount(1));
    }

    @Test
    public void testBuyItemNotEnoughGold() {
        ItemData expensiveItem = createItemData(2, "SuperSword", 200);
        shop.addShopItem(expensiveItem);

        boolean result = shop.buyItem(gameState, inventory, 0);

        assertFalse(result);
        assertEquals(100, gameState.gold); // Gold should not change
        assertEquals(0, inventory.getItemCount(2));
    }

    @Test
    public void testSellItem() {
        ItemData potion = createItemData(1, "Potion", 10);
        inventory.addItem(potion, 1);

        // Sell price is usually half of value (implemented in Shop.java as value / 2)
        // 10 / 2 = 5

        shop.sellItem(gameState, inventory, inventory.getItems().get(0));

        assertEquals(105, gameState.gold);
        assertEquals(0, inventory.getItemCount(1));
    }
}
