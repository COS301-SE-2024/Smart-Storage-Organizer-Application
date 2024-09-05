package com.example.smartstorageorganizer;

public class Item3D {
    private double width;
    private double height;
    private double depth;
    private double weight;

    public Item3D(double width, double height, double depth, double weight) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.weight = weight;
    }

    // Getters for the dimensions and weight
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public double getDepth() { return depth; }
    public double getWeight() { return weight; }
}
