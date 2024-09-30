package com.example.smartstorageorganizer.model;

import java.util.List;

public class ArrangementModel {
    String imageUrl;
    List<BinItemModel> items;
    List<BinItemModel> unfittedItems;

    public ArrangementModel(String imageUrl, List<BinItemModel> items, List<BinItemModel> unfittedItems) {
        this.imageUrl = imageUrl;
        this.items = items;
        this.unfittedItems =unfittedItems;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<BinItemModel> getItems() {
        return items;
    }

    public void setItems(List<BinItemModel> items) {
        this.items = items;
    }

    public List<BinItemModel> getUnfittedItems() {
        return unfittedItems;
    }

    public void setUnfittedItems(List<BinItemModel> unfittedItems) {
        this.unfittedItems = unfittedItems;
    }
}

