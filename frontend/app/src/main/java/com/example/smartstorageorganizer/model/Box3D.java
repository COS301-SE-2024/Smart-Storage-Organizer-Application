package com.example.smartstorageorganizer.model;

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

    public boolean addItem(ItemModel item) {
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

    public int getRemainingSpace() {
        int usedSpace = usedHeight * usedWidth * usedDepth;
        int totalSpace = height * width * depth;
        return totalSpace - usedSpace;
    }

    public int getRemainingWeight() {
        return maxWeight - currentWeight;
    }
}
