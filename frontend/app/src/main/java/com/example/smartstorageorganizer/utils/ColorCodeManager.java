package com.example.smartstorageorganizer.utils;

import android.app.Activity;

public class ColorCodeManager {
    public void addNewColorCode(String colorCode, String title, String description, String currentEmail, Activity activity, OperationCallback<Boolean> callback) {
        Utils.addColourGroup(colorCode, title, description, currentEmail, activity, callback);
    }
}
