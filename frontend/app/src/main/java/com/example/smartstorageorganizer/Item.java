package com.example.smartstorageorganizer;

public class Item {
    private int id;
    private String name;
    private String description;
    private String location;
    private String colorCode;

    // Constructor
    public Item(int id, String name, String description, String location, String colorCode) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.colorCode = colorCode;
    }

    // Getter methods
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getColorCode() {
        return colorCode;
    }

    // Setter methods (if needed)
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }
}
