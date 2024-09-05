package com.example.smartstorageorganizer;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Unit {
    private double width;
    private double height;
    private double depth;
    private double remainingCapacity;
    private List<Item3D> items;

    public Unit(double width, double height, double depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.remainingCapacity = width * height * depth;
        this.items = new ArrayList<>();
    }

    public boolean addItem(Item3D item) {
        // Check if the item fits within the remaining space and orientation constraints
        if (fitsInBox(item)) {
            items.add(item);
            updateRemainingCapacity(item);
            return true;
        }
        return false;
    }

    private boolean fitsInBox(Item3D item) {
        // Logic to check if the item fits in the remaining space
        return item.getWidth() <= this.width &&
                item.getHeight() <= this.height &&
                item.getDepth() <= this.depth;
    }

    private void updateRemainingCapacity(Item3D item) {
        this.remainingCapacity -= item.getWidth() * item.getHeight() * item.getDepth();
        Log.i("updateRemainingCapacity:"," Remaining capacity: " + this.remainingCapacity);


    }

    public List<Item3D> getItems() {
        return items;
    }
}
