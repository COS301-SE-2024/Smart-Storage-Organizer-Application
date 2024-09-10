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
import com.example.smartstorageorganizer.model.SubcategoryReportModel;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.Utils;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LowStockActivity extends AppCompatActivity {
    private PieChart subCategoriesPieChart;
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
    private ArrayAdapter<String> adapter;

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
                    subcategoryPieChart();
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
                subcategoryPieChart();
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

    private void subcategoryPieChart(){
        subCategoriesPieChart = findViewById(R.id.pieChartSubcategory);
//        categoriesBarGraph = findViewById(R.id.groupedBarChart);
        Spinner categorySpinner = findViewById(R.id.categorySpinner);

        for(CategoryModel category: categoryModelList) {
            if(Objects.equals(category.getParentCategoryId(), "0")){
                parentCategories.add(category.getCategoryName());
            }
//            parentCategories.add(categoryReport.getParentCategory());
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, parentCategories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categorySpinner.setAdapter(adapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = parent.getItemAtPosition(position).toString();
                // Update pie and bar chart based on the selected category
                updatePieChart(selectedCategory);
//                categoriesBarGraph(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing if nothing is selected
            }
        });
    }

    private String getParentCategoryId(String parentCategory) {
        for(CategoryModel categoryModel : categoryModelList){
            if(Objects.equals(categoryModel.getCategoryName(), parentCategory)) {
                return categoryModel.getCategoryID();
            }
        }
        return null;
    }

    private List<String> getSubcategories(String parentCategoryId) {
        List<String> subcategories = new ArrayList<>();
        for(CategoryModel categoryModel : categoryModelList){
            if(Objects.equals(categoryModel.getParentCategoryId(), parentCategoryId)) {
                subcategories.add(categoryModel.getCategoryName());
            }
        }
        return subcategories;
    }

    private double getSumOfItemsBelowThreshold(String category){
        double total = 0;
        for (ItemModel item: originalItemList){
            if(Objects.equals(item.getSubcategoryName(), category) && (Integer.parseInt(item.getQuantity()) < Integer.parseInt(customNumberEditText.getText().toString()))){
                total += 1;
            }
        }
        return total;
    }

    private double getTotal(List<String> subcategories) {
        double total = 0;
        for (String subcategory: subcategories){
            total += getSumOfItemsBelowThreshold(subcategory);
        }
        return total;
    }

    private void updatePieChart(String category) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        String parentCategoryId = getParentCategoryId(category);
        List<String> subcategories = getSubcategories(parentCategoryId);
        double total = getTotal(subcategories);

        for(String subcategory: subcategories) {
            double sum = getSumOfItemsBelowThreshold(subcategory);
            if ( sum > 0){
                entries.add(new PieEntry((float) ((sum / total) * 100), subcategory));
            }
        }

        PieDataSet dataSet = new PieDataSet(entries, "("+category + " Subcategories)");

        // Define a custom color array
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.rgb(192, 0, 0));     // Dark Red
        colors.add(Color.rgb(255, 87, 34));   // Orange
        colors.add(Color.rgb(34, 139, 34));   // Forest Green
        colors.add(Color.rgb(0, 0, 255));     // Blue
        colors.add(Color.rgb(255, 193, 7));   // Amber
        colors.add(Color.rgb(75, 0, 130));    // Indigo
        colors.add(Color.rgb(238, 130, 238)); // Violet
        colors.add(Color.rgb(128, 0, 128));   // Purple
        colors.add(Color.rgb(0, 128, 128));   // Teal
        colors.add(Color.rgb(255, 165, 0));   // Orange
        colors.add(Color.rgb(0, 255, 127));   // Spring Green
        colors.add(Color.rgb(173, 216, 230)); // Light Blue
        colors.add(Color.rgb(255, 69, 0));    // Red-Orange
        colors.add(Color.rgb(154, 205, 50));  // Yellow-Green
        colors.add(Color.rgb(220, 20, 60));   // Crimson
        colors.add(Color.rgb(64, 224, 208));  // Turquoise
        colors.add(Color.rgb(255, 105, 180)); // Hot Pink
        colors.add(Color.rgb(0, 191, 255));   // Deep Sky Blue
        colors.add(Color.rgb(139, 69, 19));   // Saddle Brown
        colors.add(Color.rgb(70, 130, 180));  // Steel Blue

        // Add more colors if needed

        Legend legend = subCategoriesPieChart.getLegend();
        legend.setWordWrapEnabled(true); // Enable word wrapping
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER); // Align the legend to the center
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM); // Align legend at the bottom
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL); // Set orientation to horizontal


        dataSet.setColors(colors); // Set custom colors

        PieData data = new PieData(dataSet);
        data.setValueTextSize(14f);
        data.setValueTextColor(Color.WHITE);

        subCategoriesPieChart.setData(data);
        subCategoriesPieChart.setEntryLabelColor(Color.BLACK);
        subCategoriesPieChart.setDrawSliceText(false);
        subCategoriesPieChart.invalidate();
        subCategoriesPieChart.getDescription().setEnabled(false);

        // Set the text in the center of the PieChart
        subCategoriesPieChart.setCenterText("Total: " + total + " Items");
        subCategoriesPieChart.setCenterTextSize(18f);  // Set text size
        subCategoriesPieChart.setCenterTextColor(Color.BLACK); // Set text color
        subCategoriesPieChart.setDrawCenterText(true); // Enable center text
    }
}