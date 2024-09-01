package com.example.smartstorageorganizer;

import android.util.Log;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BoxPacking3D {
    private Box box;

    public BoxPacking3D(int width, int height, int depth) {
        this.box = new Box(width, height, depth);
    }

    public void packItems(List<Item3D> items) {
        // Sort items by weight (heaviest first)
        Collections.sort(items, new Comparator<Item3D>() {
            @Override
            public int compare(Item3D i1, Item3D i2) {
                return Integer.compare(i2.getWeight(), i1.getWeight());
            }
        });

        // Try to place each item in the box
        for (Item3D item : items) {
            if (!box.addItem(item)) {
                // Handle the case where the item doesn't fit
            }
        }
    }

    public Box getBox() {
        Log.i("getBox:"," Remaining capacity: " + box.getItems());
        return box;

    }
}
