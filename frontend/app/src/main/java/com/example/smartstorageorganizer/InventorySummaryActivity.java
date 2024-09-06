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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smartstorageorganizer.model.ItemModel;
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

import java.util.ArrayList;
import java.util.List;

public class InventorySummaryActivity extends AppCompatActivity {
    private PieChart parentCategoriesPieChart;
    private PieChart subCategoriesPieChart;
    private BarChart colorCodingBarGraph;
    private ImageView arrow;
    private TableLayout itemsListTable;

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

        parentCategoryPieChart();
        subcategoryPieChart();
        colorCodingBarGraph();

        arrow = findViewById(R.id.arrow);
        itemsListTable = findViewById(R.id.itemsListTable);

        // Get the parent layout (RelativeLayout or CardView) and set LayoutTransition
        RelativeLayout parentLayout = findViewById(R.id.parentLayout); // Replace with the correct ID
        LayoutTransition layoutTransition = new LayoutTransition();
        parentLayout.setLayoutTransition(layoutTransition);

        List<ItemModel> items = fetchItems();

        populateTable(items);

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

    private List<ItemModel> fetchItems() {
        ItemModel item1 = new ItemModel();
        item1.setItemName("Item One Name");
        item1.setCreatedAt("2024/09/06");
        item1.setParentCategoryId("Electronics");
        ItemModel item2 = new ItemModel();
        item2.setItemName("Item Two Name");
        item2.setCreatedAt("2024/09/06");
        item2.setParentCategoryId("Electronics");
        ItemModel item3 = new ItemModel();
        item3.setItemName("Item Three Name");
        item3.setCreatedAt("2024/09/06");
        item3.setParentCategoryId("Electronics");
        List<ItemModel> list = new ArrayList<>();
        list.add(item1);
        list.add(item2);
        list.add(item3);
        return list;
        // Fetch or generate your list of ItemModel here
        // This is just a placeholder method
    }

    private void populateTable(List<ItemModel> items) {
        // Clear any existing rows except the header
//        itemsListTable.removeAllViews();
//        TableRow headerRow = (TableRow) LayoutInflater.from(this).inflate(R.layout.table_row_header, itemsListTable, false);
//        itemsListTable.addView(headerRow);

        // Populate table rows
        for (ItemModel item : items) {
            TableRow row = new TableRow(this);

            TextView nameTextView = new TextView(this);
            nameTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 4));
            nameTextView.setText(item.getItemName());
            nameTextView.setGravity(Gravity.CENTER);
            nameTextView.setPadding(10, 10, 10, 10);
            nameTextView.setTextSize(12);
//            nameTextView.setGravity(View.TEXT_ALIGNMENT_CENTER);

            TextView categoryTextView = new TextView(this);
            categoryTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2));
            categoryTextView.setText(item.getParentCategoryId()); // Assuming this field is used for category
            categoryTextView.setGravity(Gravity.CENTER);
            categoryTextView.setPadding(10, 10, 10, 10);
            categoryTextView.setTextSize(12);
//            categoryTextView.setGravity(View.TEXT_ALIGNMENT_CENTER);

            TextView dateTextView = new TextView(this);
            dateTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 4));
            dateTextView.setText(item.getCreatedAt());
            dateTextView.setGravity(Gravity.CENTER);
            dateTextView.setPadding(10, 10, 10, 10);
            dateTextView.setTextSize(12);
//            dateTextView.setGravity(View.TEXT_ALIGNMENT_CENTER);

            row.addView(nameTextView);
            row.addView(categoryTextView);
            row.addView(dateTextView);

            itemsListTable.addView(row);
        }
    }
}