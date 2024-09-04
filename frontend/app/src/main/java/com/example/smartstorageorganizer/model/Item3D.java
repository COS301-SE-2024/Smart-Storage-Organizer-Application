package com.example.smartstorageorganizer.model;

public class Item3D {
    private int height, width, depth, weight;

    public Item3D(int height, int width, int depth, int weight) {
        this.height = height;
        this.width = width;
        this.depth = depth;
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getDepth() {
        return depth;
    }

    public int getWeight() {
        return weight;
    }
}

