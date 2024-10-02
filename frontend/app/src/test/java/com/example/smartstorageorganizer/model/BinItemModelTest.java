package com.example.smartstorageorganizer.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BinItemModelTest {

    private BinItemModel binItem;

    @Before
    public void setUp() {
        binItem = new BinItemModel("ItemName", "Red");
    }

    @Test
    public void testGetName() {
        assertEquals("ItemName", binItem.getName());
    }

    @Test
    public void testSetName() {
        binItem.setName("NewItemName");
        assertEquals("NewItemName", binItem.getName());
    }

    @Test
    public void testGetColor() {
        assertEquals("Red", binItem.getColor());
    }

    @Test
    public void testSetColor() {
        binItem.setColor("Blue");
        assertEquals("Blue", binItem.getColor());
    }
}
