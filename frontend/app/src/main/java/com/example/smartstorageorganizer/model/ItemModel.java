package com.example.smartstorageorganizer.model;

public class ItemModel {
    private String item_id;
    private String item_name;
    private String description;
    private String colourcoding;
    private String barcode;
    private String qrcode;
    private String quanity;
    private String location;
    private String email;

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColourcoding() {
        return colourcoding;
    }

    public void setColourcoding(String colourcoding) {
        this.colourcoding = colourcoding;
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

    public String getQuanity() {
        return quanity;
    }

    public void setQuanity(String quanity) {
        this.quanity = quanity;
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

    public ItemModel() {

    }

    public ItemModel(String item_id, String item_name, String description, String colourcoding, String barcode, String qrcode, String quanity, String location, String email) {
        this.item_id = item_id;
        this.item_name = item_name;
        this.description = description;
        this.colourcoding = colourcoding;
        this.barcode = barcode;
        this.qrcode = qrcode;
        this.quanity = quanity;
        this.location = location;
        this.email = email;
    }
}
