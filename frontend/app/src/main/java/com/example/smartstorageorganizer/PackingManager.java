package com.example.smartstorageorganizer;

import static com.example.smartstorageorganizer.utils.Utils.fetchCategorySuggestions;

import androidx.appcompat.app.AppCompatActivity;
import com.example.smartstorageorganizer.model.Box3D;
import com.example.smartstorageorganizer.model.Item3D;
import com.example.smartstorageorganizer.model.CategoryModel;
import com.example.smartstorageorganizer.utils.OperationCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackingManager extends AppCompatActivity {
    private Map<String, List<Box3D>> categoryBoxes;
    private int boxHeight;
    private int boxWidth;
    private int boxDepth;
    private int boxMaxWeight;

    public PackingManager(int boxHeight, int boxWidth, int boxDepth, int boxMaxWeight) {
        this.categoryBoxes = new HashMap<>();
        this.boxHeight = boxHeight;
        this.boxWidth = boxWidth;
        this.boxDepth = boxDepth;
        this.boxMaxWeight = boxMaxWeight;
    }

    public void packItems(List<Item3D> items) {
        // Fetch the categories for each item
        for (Item3D item : items) {
            fetchCategorySuggestions(item.getName(), item.getDescription(), item.getEmail(), item.getOrganizationId(), this, new OperationCallback<List<CategoryModel>>() {
                @Override
                public void onSuccess(List<CategoryModel> categories) {
                    String category = categories.get(0).getCategoryName(); // Assuming the first category is the most relevant
                    List<Box3D> boxes = categoryBoxes.getOrDefault(category, new ArrayList<>());

                    // Sort items by volume (largest first) to optimize space
                    Collections.sort(items, new Comparator<Item3D>() {
                        @Override
                        public int compare(Item3D i1, Item3D i2) {
                            return Integer.compare(i2.getVolume(), i1.getVolume());
                        }
                    });

                    // Try to place the item in a box for this category
                    boolean itemPlaced = false;
                    for (Box3D box : boxes) {
                        if (box.addItem(item)) {
                            itemPlaced = true;
                            break;
                        }
                    }

                    // If item doesn't fit in any existing box, create a new box and add the item there
                    if (!itemPlaced) {
                        Box3D newBox = new Box3D(boxHeight, boxWidth, boxDepth, boxMaxWeight);
                        newBox.addItem(item);
                        boxes.add(newBox);
                        categoryBoxes.put(category, boxes);
                    }
                }

                @Override
                public void onFailure(String errorMessage) {
                    // Handle failure to fetch category suggestions
                }
            });
        }
    }

    public Map<String, List<Box3D>> getCategoryBoxes() {
        return categoryBoxes;
    }
}
