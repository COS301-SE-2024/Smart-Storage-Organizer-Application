package com.example.smartstorageorganizer.model;

import java.util.List;

public class ArrangementModel {
    String imageUrl;
    List<BinItemModel> items;

    public ArrangementModel(String imageUrl, List<BinItemModel> items) {
        this.imageUrl = imageUrl;
        this.items = items;
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
}

