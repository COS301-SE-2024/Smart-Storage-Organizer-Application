package com.example.smartstorageorganizer;

public class Item3D {
    private int width;
    private int height;
    private int depth;
    private int weight;

    public Item3D(int width, int height, int depth, int weight) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.weight = weight;
    }

    // Getters for the dimensions and weight
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getDepth() { return depth; }
    public int getWeight() { return weight; }
}
