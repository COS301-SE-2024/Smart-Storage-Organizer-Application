package com.example.smartstorageorganizer.model;

public class unitModel {
    final String unitName;
    final String id;
    int capacity;
    int currentCapacity;
    public unitModel(String unitName, String id, int capacity, int currentCapacity) {
        this.unitName = unitName;
        this.id = id;
        this.capacity = capacity;
        this.currentCapacity = currentCapacity;
    }
    public String getUnitName() {
        return unitName;
    }
    public String getId() {
        return id;
    }
    public int getCapacity() {
        return capacity;
    }
    public int getFreeCapacity(){
        return capacity-currentCapacity;
    }
}