package com.example.smartstorageorganizer.model;

import java.util.List;
import java.util.Collections;
import java.util.Comparator;

public class Box3D {
    private int height, width, depth, maxWeight;
    private int usedHeight, usedWidth, usedDepth, currentWeight;

    public Box3D(int height, int width, int depth, int maxWeight) {
        this.height = height;
        this.width = width;
        this.depth = depth;
        this.maxWeight = maxWeight;
        this.usedHeight = 0;
        this.usedWidth = 0;
        this.usedDepth = 0;
        this.currentWeight = 0;
    }

    public boolean addItem(Item3D item) {
        if (canFit(item) && canHoldWeight(item)) {
            usedHeight += item.getHeight();
            usedWidth += item.getWidth();
            usedDepth += item.getDepth();
            currentWeight += item.getWeight();
            return true;
        }
        return false;
    }

    private boolean canFit(Item3D item) {
        return (usedHeight + item.getHeight() <= height) &&
                (usedWidth + item.getWidth() <= width) &&
                (usedDepth + item.getDepth() <= depth);
    }

    private boolean canHoldWeight(Item3D item) {
        return currentWeight + item.getWeight() <= maxWeight;
    }

    public void packItems(List<Item3D> items) {
        Collections.sort(items, new Comparator<Item3D>() {
            @Override
            public int compare(Item3D i1, Item3D i2) {
                return Integer.compare(i2.getWeight(), i1.getWeight());
            }
        });

        for (Item3D item : items) {
            if (!addItem(item)) {
                // Handle the case where the item doesn't fit
                // For example, you could skip this item or log a message
            }
        }
    }

    public int getRemainingSpace() {
        int usedSpace = usedHeight * usedWidth * usedDepth;
        int totalSpace = height * width * depth;
        return totalSpace - usedSpace;
    }

    public int getRemainingWeight() {
        return maxWeight - currentWeight;
    }
}
