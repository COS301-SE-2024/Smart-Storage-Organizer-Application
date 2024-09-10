package com.example.smartstorageorganizer;

import android.animation.LayoutTransition;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smartstorageorganizer.model.CategoryModel;
import com.example.smartstorageorganizer.model.CategoryReportModel;
import com.example.smartstorageorganizer.model.ItemModel;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.Utils;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LowStockActivity extends AppCompatActivity {
    private EditText customNumberEditText;
    private Button confirmButton;
    private int selectedNumber;
    private ImageView arrow;
    private TableLayout itemsListTable;
    private static final int PAGE_SIZE = 1000;
    private int currentPage = 1;
    List<ItemModel> originalItemList; // Store the original unfiltered list
    private MyAmplifyApp app;
    private List<CategoryReportModel> categoryReportModelList;
    private List<CategoryModel> categoryModelList;
    private ArrayList<String> parentCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_low_stock);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        categoryReportModelList = new ArrayList<>();
        parentCategories = new ArrayList<>();
        categoryModelList = new ArrayList<>();

        customNumberEditText = findViewById(R.id.custom_number_editText);
        confirmButton = findViewById(R.id.confirm_button);

        app = (MyAmplifyApp) getApplicationContext();
        originalItemList = new ArrayList<>();

        arrow = findViewById(R.id.arrow);
        itemsListTable = findViewById(R.id.itemsListTable);

        RelativeLayout parentLayout = findViewById(R.id.parentLayout);
        LayoutTransition layoutTransition = new LayoutTransition();
        parentLayout.setLayoutTransition(layoutTransition);

        findViewById(R.id.cardViewItemsReports).setOnClickListener(v -> {
            if (itemsListTable.getVisibility() == View.VISIBLE) {
                itemsListTable.setVisibility(View.GONE); // Collapse
                rotateArrow(arrow, 180, 0);
            } else {
                itemsListTable.setVisibility(View.VISIBLE); // Expand
                rotateArrow(arrow, 0, 180);
            }
        });

        confirmButton.setOnClickListener(v -> {
            String customNumberText = customNumberEditText.getText().toString();

            if (!customNumberText.isEmpty()) {
                try {
                    selectedNumber = Integer.parseInt(customNumberText);
                    populateTable(originalItemList);
                    Toast.makeText(LowStockActivity.this, "Custom number: " + selectedNumber, Toast.LENGTH_SHORT).show();
                } catch (NumberFormatException e) {
                    Toast.makeText(LowStockActivity.this, "Invalid number entered", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LowStockActivity.this, "Spinner number: " + selectedNumber, Toast.LENGTH_SHORT).show();
            }
        });
        fetchItems();
    }

    private void rotateArrow(ImageView arrow, float fromDegree, float toDegree) {
        RotateAnimation rotate = new RotateAnimation(fromDegree, toDegree,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.startAnimation(rotate);
    }

    private void fetchItems() {
        Utils.fetchAllItems(PAGE_SIZE, currentPage, app.getOrganizationID(),this, new OperationCallback<List<ItemModel>>() {
            @Override
            public void onSuccess(List<ItemModel> result) {
                originalItemList.clear();
                originalItemList.addAll(result);
                fetchAllCategories();
//                populateTable(originalItemList);
//                Toast.makeText(InventorySummaryActivity.this, "Items fetched successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(LowStockActivity.this, "Failed to fetch items: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void fetchAllCategories() {
        Utils.fetchAllCategories(app.getOrganizationID(), this, new OperationCallback<List<CategoryModel>>() {
            @Override
            public void onSuccess(List<CategoryModel> result) {
                categoryModelList.addAll(result);
                for (ItemModel item: originalItemList){
                    String parentCategoryName = getCategoryName(item.getParentCategoryId());
                    String subCategoryName = getCategoryName(item.getSubCategoryId());
                    item.setParentCategoryName(parentCategoryName);
                    item.setSubcategoryName(subCategoryName);
                }
                populateTable(originalItemList);
//                String selectedRange = dateFilterSpinner.getSelectedItem().toString();
//                filterItemsByDate(selectedRange);
                Toast.makeText(LowStockActivity.this, "Stats fetched successfully!!!"+result.get(0).getCategoryName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(LowStockActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getCategoryName(String categoryId) {
        for(CategoryModel category: categoryModelList){
            if(Objects.equals(category.getCategoryID(), categoryId)){
                return category.getCategoryName();
            }
        }
        return "";
    }

    private void populateTable(List<ItemModel> items) {
        // Clear all rows from the table first
        itemsListTable.removeAllViews();

        // Add the header row first
        TableRow headerRow = new TableRow(this);
        headerRow.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        // Create TextViews for the header row
        TextView headerNameTextView = new TextView(this);
        headerNameTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 4));
        headerNameTextView.setText("Item Name");
        headerNameTextView.setTextColor(Color.WHITE);
        headerNameTextView.setPadding(10, 10, 10, 10);
        headerNameTextView.setTextSize(14);
        headerNameTextView.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView headerQuantityTextView = new TextView(this);
        headerQuantityTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2));
        headerQuantityTextView.setText("Current Stock");
        headerQuantityTextView.setTextColor(Color.WHITE);
        headerQuantityTextView.setPadding(10, 10, 10, 10);
        headerQuantityTextView.setTextSize(14);
        headerQuantityTextView.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView headerThresholdTextView = new TextView(this);
        headerThresholdTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2));
        headerThresholdTextView.setText("Threshold");
        headerThresholdTextView.setTextColor(Color.WHITE);
        headerThresholdTextView.setPadding(10, 10, 10, 10);
        headerThresholdTextView.setTextSize(14);
        headerThresholdTextView.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView headerShortfallTextView = new TextView(this);
        headerShortfallTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2));
        headerShortfallTextView.setText("Shortfall");
        headerShortfallTextView.setTextColor(Color.WHITE);
        headerShortfallTextView.setPadding(10, 10, 10, 10);
        headerShortfallTextView.setTextSize(14);
        headerShortfallTextView.setGravity(Gravity.CENTER_HORIZONTAL);

        // Add TextViews to the header row
        headerRow.addView(headerNameTextView);
        headerRow.addView(headerQuantityTextView);
        headerRow.addView(headerThresholdTextView);
        headerRow.addView(headerShortfallTextView);

        // Add the header row to the table
        itemsListTable.addView(headerRow);

        // Add the item rows
        for (ItemModel item : items) {
            if(Integer.parseInt(item.getQuantity()) < Integer.parseInt(customNumberEditText.getText().toString())){
                TableRow row = new TableRow(this);

                TextView nameTextView = new TextView(this);
                nameTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 4));
                nameTextView.setText(item.getItemName());
                nameTextView.setGravity(Gravity.CENTER);
                nameTextView.setPadding(10, 10, 10, 10);
                nameTextView.setTextSize(12);

                TextView quantityTextView = new TextView(this);
                quantityTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2));
                quantityTextView.setText(item.getQuantity());
                quantityTextView.setGravity(Gravity.CENTER);
                quantityTextView.setPadding(10, 10, 10, 10);
                quantityTextView.setTextSize(12);

                TextView thresholdTextView = new TextView(this);
                thresholdTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2));
                thresholdTextView.setText(customNumberEditText.getText().toString());
                thresholdTextView.setGravity(Gravity.CENTER);
                thresholdTextView.setPadding(10, 10, 10, 10);
                thresholdTextView.setTextSize(12);

                TextView shortFallTextView = new TextView(this);
                shortFallTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2));
                int shortFall = (Integer.parseInt(customNumberEditText.getText().toString())-Integer.parseInt(item.getQuantity()));
                shortFallTextView.setText(String.valueOf(shortFall));
                shortFallTextView.setGravity(Gravity.CENTER);
                shortFallTextView.setPadding(10, 10, 10, 10);
                shortFallTextView.setTextSize(12);

                // Add TextViews to the item row
                row.addView(nameTextView);
                row.addView(quantityTextView);
                row.addView(thresholdTextView);
                row.addView(shortFallTextView);

                // Add the item row to the table
                itemsListTable.addView(row);
            }
        }
    }
}