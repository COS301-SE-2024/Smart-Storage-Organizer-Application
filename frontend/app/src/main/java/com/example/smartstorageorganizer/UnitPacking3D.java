package com.example.smartstorageorganizer;

import android.util.Log;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UnitPacking3D {
    private Unit plane;

    public UnitPacking3D(double width, double height, double depth) {
        this.plane = new Unit(width, height, depth);
    }

    public void packItems(List<Item3D> items) {
        // Sort items by weight (heaviest first)

        // check in which category does the item belong -- electronics, furniture, clothes, etc.
        // check if the category has units available -- do we have any unit in electronics
        // if yes, then check if the item fits in any of the units using size and weight
        // if yes, then add the item to the unit and update the remaining capacity.
        // if the item doesn't fit in any of the units, then we look for any existing unit that the item can fight in.
        // if there is a unit that the item can fit in, then add the item to the unit and update the remaining capacity
        // if there is no unit that the item can fit in, then create a new unit (should be under the electronic category) and add the item to the unit and update the remaining capacity


        Collections.sort(items, new Comparator<Item3D>() {

            @Override
            public int compare(Item3D i1, Item3D i2) {
                return Double.compare(i2.getWeight(), i1.getWeight());
            }
        });

        // Try to place each item in the box
        for (Item3D item : items) {
            if (!plane.addItem(item)) {
                // Handle the case where the item doesn't fit
            }
        }
    }

    public Unit getBox() {
        Log.i("getBox:"," Remaining capacity: " + plane.getItems());
        return plane;

    }
}
