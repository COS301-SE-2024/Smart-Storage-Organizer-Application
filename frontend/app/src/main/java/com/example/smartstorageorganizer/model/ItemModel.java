package com.example.smartstorageorganizer.model;

public class ItemModel {
    private String itemId;
    private String itemName;
    private String description;
    private String colourCoding;
    private String barcode;
    private String qrcode;
    private String quantity;
    private String location;
    private String email;
    private String itemImage;
    private String createdAt;

    public ItemModel() {

    }

    public ItemModel(String itemId, String itemName, String description, String colourCoding, String barcode, String qrcode, String quantity, String location, String email, String itemImage, String createdAt) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.description = description;
        this.colourCoding = colourCoding;
        this.barcode = barcode;
        this.qrcode = qrcode;
        this.quantity = quantity;
        this.location = location;
        this.email = email;
        this.itemImage = itemImage;
        this.createdAt = createdAt;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColourCoding() {
        return colourCoding;
    }

    public void setColourCoding(String colourCoding) {
        this.colourCoding = colourCoding;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
