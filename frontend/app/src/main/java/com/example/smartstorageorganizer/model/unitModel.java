package com.example.smartstorageorganizer.model;

public class unitModel {
    String unitName;
    String id;
    int capacity;
    int currentCapacity;
    boolean isExpanded;

    public unitModel(String unitName, String id, int capacity, int currentCapacity) {
        this.unitName = unitName;
        this.id = id;
        this.capacity = capacity;
        this.currentCapacity = currentCapacity;
        this.isExpanded = false;
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
    public String getCapacityAsString(){
        return "Capacity: "+Integer.toString(this.capacity);
    }

    public String getCapacityUsedAsString(){
        return "Capacity Used: "+Integer.toString(this.currentCapacity);
    }


    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}