package com.teamname.world.system;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class InventoryTest {

    private Inventory inventory;

    @Before
    public void setUp() {
        inventory = new Inventory();
    }

    private ItemData createItemData(int id, String name) {
        ItemData data = new ItemData();
        data.id = id;
        data.name = name;
        data.type = "ITEM";
        return data;
    }

    @Test
    public void testAddItem() {
        ItemData potion = createItemData(1, "Potion");
        inventory.addItem(potion, 3);

        assertEquals(1, inventory.getItems().size());
        assertTrue(inventory.hasItem(1));
        assertEquals(3, inventory.getItemCount(1));
    }

    @Test
    public void testRemoveItemPartial() {
        ItemData potion = createItemData(1, "Potion");
        inventory.addItem(potion, 5);

        int removed = inventory.removeItem(potion, 2);

        assertEquals(2, removed);
        assertEquals(3, inventory.getItemCount(1));
        assertTrue(inventory.hasItem(1));
    }

    @Test
    public void testRemoveItemAll() {
        ItemData potion = createItemData(1, "Potion");
        inventory.addItem(potion, 2);

        int removed = inventory.removeItem(potion, 2);

        assertEquals(2, removed);
        assertEquals(0, inventory.getItemCount(1));
        assertFalse(inventory.hasItem(1)); // Should be removed from list?
        // Logic check: removeItem removes the object from list if quantity becomes 0?
        // Let's check implementation behavior expectation.
        // My implementation:
        // if (target.quantity > quantity) -> decrease
        // else -> items.remove(target)
        // So yes, it should be removed.
    }

    @Test
    public void testRemoveItemMoreThanHeld() {
        ItemData potion = createItemData(1, "Potion");
        inventory.addItem(potion, 2);

        int removed = inventory.removeItem(potion, 5);

        assertEquals(2, removed); // Only removed 2
        assertEquals(0, inventory.getItemCount(1));
        assertFalse(inventory.hasItem(1));
    }

    @Test
    public void testGetItemCount() {
        ItemData potion = createItemData(1, "Potion");
        inventory.addItem(potion, 10);

        assertEquals(10, inventory.getItemCount(1));
        assertEquals(0, inventory.getItemCount(999));
    }
}
