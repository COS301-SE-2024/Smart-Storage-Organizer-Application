package com.example.smartstorageorganizer;

import static com.example.smartstorageorganizer.utils.Utils.fetchCategorySuggestions;

import androidx.appcompat.app.AppCompatActivity;
import com.example.smartstorageorganizer.model.Box3D;
import com.example.smartstorageorganizer.model.Item3D;
import com.example.smartstorageorganizer.model.CategoryModel;
import com.example.smartstorageorganizer.model.ItemModel;
import com.example.smartstorageorganizer.utils.OperationCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackingManager extends AppCompatActivity {
    private final Map<String, List<Box3D>> categoryBoxes;
    private final int boxHeight;
    private final int boxWidth;
    private final int boxDepth;
    private final int boxMaxWeight;

    public PackingManager(int boxHeight, int boxWidth, int boxDepth, int boxMaxWeight) {
        this.categoryBoxes = new HashMap<>();
        this.boxHeight = boxHeight;
        this.boxWidth = boxWidth;
        this.boxDepth = boxDepth;
        this.boxMaxWeight = boxMaxWeight;
    }

    public void packItems(List<ItemModel> items) {
        // Fetch the categories for each item
        for (ItemModel item : items) {
            fetchCategorySuggestions(item.getItemName(), item.getDescription(), item.getEmail(), item.getOrganizationId(), this, new OperationCallback<List<CategoryModel>>() {
                @Override
                public void onSuccess(List<CategoryModel> categories) {
                    String category = categories.get(0).getCategoryName(); // Assuming the first category is the most relevant
                    List<Box3D> boxes = categoryBoxes.getOrDefault(category, new ArrayList<>());

                    // Sort items by volume (largest first) to optimize space
                    items.sort((i1, i2) -> Integer.compare(i2.getVolume(), i1.getVolume()));

                    // Try to place the item in a box for this category
                    boolean itemPlaced = false;
                    assert boxes != null;
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
