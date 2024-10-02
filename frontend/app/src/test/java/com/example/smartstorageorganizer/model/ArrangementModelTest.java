package com.example.smartstorageorganizer.model;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

public class ArrangementModelTest {

    private ArrangementModel arrangementModel;
    private List<BinItemModel> items;
    private List<BinItemModel> unfittedItems;

    @Before
    public void setUp() {
        // Prepare mock BinItemModel objects
        BinItemModel item1 = new BinItemModel("item1", "#ffffff");
        BinItemModel item2 = new BinItemModel("item2", "#000000");

        // Initialize lists
        items = new ArrayList<>();
        items.add(item1);

        unfittedItems = new ArrayList<>();
        unfittedItems.add(item2);

        // Initialize the ArrangementModel with sample data
        arrangementModel = new ArrangementModel("http://example.com/image.png", items, unfittedItems);
    }

    @Test
    public void testConstructor() {
        // Test if the constructor sets the values correctly
        assertEquals("http://example.com/image.png", arrangementModel.getImageUrl());
        assertEquals(1, arrangementModel.getItems().size()); // Check if the size is correct
        assertEquals(1, arrangementModel.getUnfittedItems().size()); // Check if the size is correct

        // Verify the item in the list
        assertEquals("item1", arrangementModel.getItems().get(0).getName());
        assertEquals("item2", arrangementModel.getUnfittedItems().get(0).getName());
    }

    @Test
    public void testGettersAndSetters() {
        // Test the getter and setter for imageUrl
        arrangementModel.setImageUrl("http://newimage.com/new.png");
        assertEquals("http://newimage.com/new.png", arrangementModel.getImageUrl());

        // Test the getter and setter for items
        BinItemModel newItem = new BinItemModel("item3", "#ff0000");
        List<BinItemModel> newItemsList = new ArrayList<>();
        newItemsList.add(newItem);
        arrangementModel.setItems(newItemsList);
        assertEquals(1, arrangementModel.getItems().size());
        assertEquals("item3", arrangementModel.getItems().get(0).getName());

        // Test the getter and setter for unfittedItems
        BinItemModel newUnfittedItem = new BinItemModel("item4", "fff333");
        List<BinItemModel> newUnfittedItemsList = new ArrayList<>();
        newUnfittedItemsList.add(newUnfittedItem);
        arrangementModel.setUnfittedItems(newUnfittedItemsList);
        assertEquals(1, arrangementModel.getUnfittedItems().size());
        assertEquals("item4", arrangementModel.getUnfittedItems().get(0).getName());
    }
}
