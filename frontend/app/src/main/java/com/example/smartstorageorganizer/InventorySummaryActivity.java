package com.example.smartstorageorganizer;

import android.animation.LayoutTransition;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
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

import com.example.smartstorageorganizer.adapters.ItemAdapter;
import com.example.smartstorageorganizer.model.ItemModel;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.Utils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class InventorySummaryActivity extends AppCompatActivity {
    private PieChart parentCategoriesPieChart;
    private PieChart subCategoriesPieChart;
    private BarChart colorCodingBarGraph;
    private ImageView arrow;
    private TableLayout itemsListTable;
    private static final int PAGE_SIZE = 1000;
    private int currentPage = 1;
    Spinner dateFilterSpinner;
    List<ItemModel> originalItemList; // Store the original unfiltered list
    private MyAmplifyApp app;
    private TextView quantityOfItems, uniqueItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inventory_summary);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        uniqueItems = findViewById(R.id.uniqueItems);
        quantityOfItems = findViewById(R.id.quantityOfItems);
        dateFilterSpinner = findViewById(R.id.dateFilterSpinner);

        app = (MyAmplifyApp) getApplicationContext();
        originalItemList = new ArrayList<>();

        parentCategoryPieChart();
        subcategoryPieChart();
        colorCodingBarGraph();

        arrow = findViewById(R.id.arrow);
        itemsListTable = findViewById(R.id.itemsListTable);

        // Get the parent layout (RelativeLayout or CardView) and set LayoutTransition
        RelativeLayout parentLayout = findViewById(R.id.parentLayout); // Replace with the correct ID
        LayoutTransition layoutTransition = new LayoutTransition();
        parentLayout.setLayoutTransition(layoutTransition);

//        List<ItemModel> items = fetchItems();

//        populateTable(items);
        fetchItems();

        dateFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedRange = parent.getItemAtPosition(position).toString();
                filterItemsByDate(selectedRange);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Set click listener for expanding/collapsing the GridLayout
        findViewById(R.id.cardViewItemsReports).setOnClickListener(v -> {
            if (itemsListTable.getVisibility() == View.VISIBLE) {
                itemsListTable.setVisibility(View.GONE); // Collapse
                rotateArrow(arrow, 180, 0);
            } else {
                itemsListTable.setVisibility(View.VISIBLE); // Expand
                rotateArrow(arrow, 0, 180);
            }
        });
    }

    private void parentCategoryPieChart(){
        parentCategoriesPieChart = findViewById(R.id.pieChart);

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(30f, "Electronics"));
        entries.add(new PieEntry(20f, "All Automotive"));
        entries.add(new PieEntry(50f, "Health"));

        PieDataSet dataSet = new PieDataSet(entries, "Parent Categories");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.WHITE);
        parentCategoriesPieChart.setData(data);

        parentCategoriesPieChart.setEntryLabelColor(Color.BLACK); // Change the label color to black
//        pieChart.setEntryLabelTextSize(14f);

        parentCategoriesPieChart.invalidate();

        parentCategoriesPieChart.getDescription().setEnabled(false);
    }

    private void subcategoryPieChart(){
        subCategoriesPieChart = findViewById(R.id.pieChartSubcategory);
        Spinner categorySpinner = findViewById(R.id.categorySpinner);

        // Set an item selected listener on the spinner
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = parent.getItemAtPosition(position).toString();
                updatePieChart(selectedCategory); // Update pie chart based on the selected category
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing if nothing is selected
            }
        });

        // Display the pie chart for the default selection (e.g., "Electronics")
        updatePieChart("Electronics");
    }

    private void updatePieChart(String category) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        switch (category) {
            case "Electronics":
                entries.add(new PieEntry(40f, "Laptops"));
                entries.add(new PieEntry(30f, "Cellphones"));
                entries.add(new PieEntry(20f, "Desktops"));
                entries.add(new PieEntry(10f, "Tablets"));
                break;

            case "All Automotive":
                entries.add(new PieEntry(50f, "Cars"));
                entries.add(new PieEntry(30f, "Motorcycles"));
                entries.add(new PieEntry(20f, "Trucks"));
                break;

            case "Health":
                entries.add(new PieEntry(40f, "Medicine"));
                entries.add(new PieEntry(30f, "Fitness Equipment"));
                entries.add(new PieEntry(30f, "Supplements"));
                break;
        }

        PieDataSet dataSet = new PieDataSet(entries, category + " Subcategories");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData data = new PieData(dataSet);
        data.setValueTextSize(14f);
        data.setValueTextColor(Color.WHITE);

        subCategoriesPieChart.setData(data);

        subCategoriesPieChart.setEntryLabelColor(Color.BLACK);

        subCategoriesPieChart.invalidate();
        subCategoriesPieChart.getDescription().setEnabled(false);

    }

    private void colorCodingBarGraph(){
        colorCodingBarGraph = findViewById(R.id.barChart);

        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(1f, 30f));
        entries.add(new BarEntry(2f, 20f));
        entries.add(new BarEntry(3f, 50f));

        BarDataSet dataSet = new BarDataSet(entries, "Color Codes");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        BarData data = new BarData(dataSet);
        colorCodingBarGraph.setData(data);

// Create a list of labels for the X-axis
        final String[] labels = new String[]{"Category 1", "Category 2", "Category 3"};

// Set the labels to the X-axis
        XAxis xAxis = colorCodingBarGraph.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f); // Set the granularity to 1 to show each label
        xAxis.setGranularityEnabled(true);

        colorCodingBarGraph.invalidate(); // Refresh the chart
        colorCodingBarGraph.getDescription().setEnabled(false);
    }

    private void rotateArrow(ImageView arrow, float fromDegree, float toDegree) {
        RotateAnimation rotate = new RotateAnimation(fromDegree, toDegree,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.startAnimation(rotate);
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

        TextView headerCategoryTextView = new TextView(this);
        headerCategoryTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2));
        headerCategoryTextView.setText("Category");
        headerCategoryTextView.setTextColor(Color.WHITE);
        headerCategoryTextView.setPadding(10, 10, 10, 10);
        headerCategoryTextView.setTextSize(14);
        headerCategoryTextView.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView headerDateTextView = new TextView(this);
        headerDateTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 4));
        headerDateTextView.setText("Date Created");
        headerDateTextView.setTextColor(Color.WHITE);
        headerDateTextView.setPadding(10, 10, 10, 10);
        headerDateTextView.setTextSize(14);
        headerDateTextView.setGravity(Gravity.CENTER_HORIZONTAL);

        // Add TextViews to the header row
        headerRow.addView(headerNameTextView);
        headerRow.addView(headerCategoryTextView);
        headerRow.addView(headerDateTextView);

        // Add the header row to the table
        itemsListTable.addView(headerRow);

        // Add the item rows
        for (ItemModel item : items) {
            TableRow row = new TableRow(this);

            TextView nameTextView = new TextView(this);
            nameTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 4));
            nameTextView.setText(item.getItemName());
            nameTextView.setGravity(Gravity.CENTER);
            nameTextView.setPadding(10, 10, 10, 10);
            nameTextView.setTextSize(12);

            TextView categoryTextView = new TextView(this);
            categoryTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2));
            categoryTextView.setText(item.getParentCategoryId());
            categoryTextView.setGravity(Gravity.CENTER);
            categoryTextView.setPadding(10, 10, 10, 10);
            categoryTextView.setTextSize(12);

            TextView dateTextView = new TextView(this);
            dateTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 4));
            dateTextView.setText(item.getCreatedAt());
            dateTextView.setGravity(Gravity.CENTER);
            dateTextView.setPadding(10, 10, 10, 10);
            dateTextView.setTextSize(12);

            // Add TextViews to the item row
            row.addView(nameTextView);
            row.addView(categoryTextView);
            row.addView(dateTextView);

            // Add the item row to the table
            itemsListTable.addView(row);
        }
    }


    private void fetchItems() {
        Utils.fetchAllItems(PAGE_SIZE, currentPage, app.getOrganizationID(),this, new OperationCallback<List<ItemModel>>() {
            @Override
            public void onSuccess(List<ItemModel> result) {
                originalItemList.clear();
                originalItemList.addAll(result);
                String selectedRange = dateFilterSpinner.getSelectedItem().toString();
                filterItemsByDate(selectedRange);
//                populateTable(originalItemList);
                Toast.makeText(InventorySummaryActivity.this, "Items fetched successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(InventorySummaryActivity.this, "Failed to fetch items: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterItemsByDate(String selectedRange) {
        List<ItemModel> filteredItems = new ArrayList<>();
        Date now = new Date();

        for (ItemModel item : originalItemList) {
            try {
                // Convert the createdAt string to a Date object
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date itemDate = sdf.parse(item.getCreatedAt().split("\\.")[0]); // Ignore the fractional seconds

                // Calculate date range based on selected filter
                switch (selectedRange) {
                    case "Last 30 Days":
                        if (isWithinDays(itemDate, now, 30)) filteredItems.add(item);
                        break;
                    case "Last 14 Days":
                        if (isWithinDays(itemDate, now, 14)) filteredItems.add(item);
                        break;
                    case "Last 7 Days":
                        if (isWithinDays(itemDate, now, 7)) filteredItems.add(item);
                        break;
                    case "All Time":
                        filteredItems.add(item);
                        break;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // Update the table with the filtered list
        populateTable(filteredItems);
        uniqueItems.setText(String.valueOf(filteredItems.size()));
        int quantitySize = 0;
        for (ItemModel item: filteredItems){
            quantitySize += Integer.parseInt(item.getQuantity());
        }
        quantityOfItems.setText(String.valueOf(quantitySize));
    }

    private boolean isWithinDays(Date itemDate, Date currentDate, int days) {
        long diffInMillies = currentDate.getTime() - itemDate.getTime();
        long daysDiff = TimeUnit.MILLISECONDS.toDays(diffInMillies);
        return daysDiff <= days;
    }

}