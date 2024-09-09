package com.example.smartstorageorganizer;

import android.animation.LayoutTransition;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import com.example.smartstorageorganizer.model.CategoryModel;
import com.example.smartstorageorganizer.model.CategoryReportModel;
import com.example.smartstorageorganizer.model.ItemModel;
import com.example.smartstorageorganizer.model.SubcategoryReportModel;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.Utils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private HorizontalBarChart categoriesBarGraph;
    private HorizontalBarChart parentCategoriesgroupedBarChart;
    private ImageView arrow;
    private TableLayout itemsListTable;
    private static final int PAGE_SIZE = 1000;
    private int currentPage = 1;
    Spinner dateFilterSpinner;
    List<ItemModel> originalItemList; // Store the original unfiltered list
    private MyAmplifyApp app;
    private TextView quantityOfItems, uniqueItems;
    private List<CategoryReportModel> categoryReportModelList;
    private List<CategoryModel> categoryModelList;
    private ArrayList<String> parentCategories;
    private ArrayAdapter<String> adapter;

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

        categoryReportModelList = new ArrayList<>();
        parentCategories = new ArrayList<>();
        categoryModelList = new ArrayList<>();

        uniqueItems = findViewById(R.id.uniqueItems);
        quantityOfItems = findViewById(R.id.quantityOfItems);
        dateFilterSpinner = findViewById(R.id.dateFilterSpinner);
        parentCategoriesgroupedBarChart = findViewById(R.id.parentCategoriesgroupedBarChart);

        app = (MyAmplifyApp) getApplicationContext();
        originalItemList = new ArrayList<>();

        arrow = findViewById(R.id.arrow);
        itemsListTable = findViewById(R.id.itemsListTable);

        RelativeLayout parentLayout = findViewById(R.id.parentLayout);
        LayoutTransition layoutTransition = new LayoutTransition();
        parentLayout.setLayoutTransition(layoutTransition);

        fetchItems();
        fetchCategoryStats();
//        fetchAllCategories();

        Button generatePdfButton = findViewById(R.id.btnGeneratePdf);
        generatePdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the method to generate PDF when button is clicked
                createPdf();
            }
        });

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

    private void parentCategoryPieChart() {
        parentCategoriesPieChart = findViewById(R.id.pieChart);

        ArrayList<PieEntry> entries = new ArrayList<>();
        for (CategoryReportModel categoryReport : categoryReportModelList) {
            if (categoryReport.getTotalNumberOfItems() != 0) {
                entries.add(new PieEntry((float) categoryReport.getPercentage(38.00), categoryReport.getParentCategory()));
            }
        }

        PieDataSet dataSet = new PieDataSet(entries, "Parent Categories");
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

        Legend legend = parentCategoriesPieChart.getLegend();
        legend.setWordWrapEnabled(true); // Enable word wrapping
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER); // Align the legend to the center
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM); // Align legend at the bottom
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL); // Set orientation to horizontal

        dataSet.setColors(colors); // Set custom colors

        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.WHITE);
        parentCategoriesPieChart.setData(data);

        parentCategoriesPieChart.setDrawSliceText(false); // Hide the labels on the slices
        parentCategoriesPieChart.invalidate();

        parentCategoriesPieChart.getDescription().setEnabled(false);
    }


    private void subcategoryPieChart(){
        subCategoriesPieChart = findViewById(R.id.pieChartSubcategory);
        categoriesBarGraph = findViewById(R.id.groupedBarChart);
        Spinner categorySpinner = findViewById(R.id.categorySpinner);

        for(CategoryReportModel categoryReport: categoryReportModelList) {
            parentCategories.add(categoryReport.getParentCategory());
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
                categoriesBarGraph(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing if nothing is selected
            }
        });
    }

    private CategoryReportModel getSubcategories(String parentName) {
        CategoryReportModel subcategories = new CategoryReportModel();
        for(CategoryReportModel categoryReport : categoryReportModelList){
            if(Objects.equals(categoryReport.getParentCategory(), parentName)) {
                return categoryReport;
            }
        }
        return subcategories;
    }

    private void updatePieChart(String category) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        CategoryReportModel categoryReportModel = getSubcategories(category);

        for(SubcategoryReportModel subcategoryReport: categoryReportModel.getSubCategories()) {
            if(subcategoryReport.getTotalNumberOfItems() != 0){
                entries.add(new PieEntry((float) subcategoryReport.getPercentage(categoryReportModel.getTotalNumberOfItems()), subcategoryReport.getSubcategory()));
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
        subCategoriesPieChart.setCenterText("Total: " + categoryReportModel.getTotalNumberOfItems() + " Items");
        subCategoriesPieChart.setCenterTextSize(18f);  // Set text size
        subCategoriesPieChart.setCenterTextColor(Color.BLACK); // Set text color
        subCategoriesPieChart.setDrawCenterText(true); // Enable center text
    }


    private void categoriesBarGraph(String category) {
        List<BarEntry> entriesGroup1 = new ArrayList<>();
        CategoryReportModel categoryReportModel = getSubcategories(category);
        int i = 0;
        for (SubcategoryReportModel subcategoryReport : categoryReportModel.getSubCategories()) {
            if (subcategoryReport.getTotalNumberOfItems() != 0) {
                entriesGroup1.add(new BarEntry(i, (float) subcategoryReport.getTotalNumberOfItems()));
                i++;
            }
        }

        List<BarEntry> entriesGroup2 = new ArrayList<>();
        int j = 0;
        for (SubcategoryReportModel subcategoryReport : categoryReportModel.getSubCategories()) {
            if (subcategoryReport.getTotalNumberOfItems() != 0) {
                entriesGroup2.add(new BarEntry(j, (float) subcategoryReport.getTotalQuantity()));
                j++;
            }
        }

        BarDataSet set1 = new BarDataSet(entriesGroup1, "Unique Items");
        set1.setColor(Color.BLUE);

        BarDataSet set2 = new BarDataSet(entriesGroup2, "Total Quantity");
        set2.setColor(Color.RED);

        BarData data = new BarData(set1, set2);

        float groupSpace = 0.1f;
        float barSpace = 0.05f;
        float barWidth = 0.4f;
        data.setBarWidth(barWidth);

        categoriesBarGraph.setData(data);

        XAxis xAxis = categoriesBarGraph.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);

        List<SubcategoryReportModel> subcategoryList = categoryReportModel.getSubCategories();
        String[] subcategoryNames = new String[i];

        int k = 0;
        for (int n = 0; n < subcategoryList.size(); n++) {
            if(subcategoryList.get(n).getTotalNumberOfItems() != 0){
                subcategoryNames[k] = subcategoryList.get(n).getSubcategory();
                k += 1;
            }
        }

        xAxis.setValueFormatter(new IndexAxisValueFormatter(subcategoryNames));
        YAxis leftAxis = categoriesBarGraph.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(false);

        categoriesBarGraph.getAxisRight().setEnabled(false);
        categoriesBarGraph.groupBars(0f, groupSpace, barSpace);
        categoriesBarGraph.getXAxis().setAxisMinimum(0f);
        categoriesBarGraph.getXAxis().setAxisMaximum(categoriesBarGraph.getBarData().getGroupWidth(groupSpace, barSpace) * i);

        categoriesBarGraph.setScaleEnabled(true);
        categoriesBarGraph.getDescription().setEnabled(false);
        categoriesBarGraph.invalidate();
    }

    private void parentCategoriesBarGraph() {
        List<BarEntry> entriesGroup1 = new ArrayList<>();
        int i = 0;
        for (CategoryReportModel categoryReport : categoryReportModelList) {
            if (categoryReport.getTotalNumberOfItems() != 0) {
                entriesGroup1.add(new BarEntry(i, (float) categoryReport.getTotalNumberOfItems()));
                i++;
            }
        }

        List<BarEntry> entriesGroup2 = new ArrayList<>();
        int j = 0;
        for (CategoryReportModel categoryReport : categoryReportModelList) {
            if (categoryReport.getTotalNumberOfItems() != 0) {
                entriesGroup2.add(new BarEntry(j, (float) categoryReport.getTotalQuantity()));
                j++;
            }
        }

        BarDataSet set1 = new BarDataSet(entriesGroup1, "Unique Items");
        set1.setColor(Color.BLUE);

        BarDataSet set2 = new BarDataSet(entriesGroup2, "Total Quantity");
        set2.setColor(Color.RED);

        BarData data = new BarData(set1, set2);

        float groupSpace = 0.1f;
        float barSpace = 0.05f;
        float barWidth = 0.4f;
        data.setBarWidth(barWidth);

        parentCategoriesgroupedBarChart.setData(data);

        XAxis xAxis = parentCategoriesgroupedBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);

//        List<SubcategoryReportModel> subcategoryList = categoryReportModel.getSubCategories();
        String[] subcategoryNames = new String[i];

        int k = 0;
        for (int n = 0; n < categoryReportModelList.size(); n++) {
            if(categoryReportModelList.get(n).getTotalNumberOfItems() != 0){
                subcategoryNames[k] = categoryReportModelList.get(n).getParentCategory();
                k += 1;
            }
        }

        xAxis.setValueFormatter(new IndexAxisValueFormatter(subcategoryNames));
        YAxis leftAxis = parentCategoriesgroupedBarChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(false);

        parentCategoriesgroupedBarChart.getAxisRight().setEnabled(false);
        parentCategoriesgroupedBarChart.groupBars(0f, groupSpace, barSpace);
        parentCategoriesgroupedBarChart.getXAxis().setAxisMinimum(0f);
        parentCategoriesgroupedBarChart.getXAxis().setAxisMaximum(parentCategoriesgroupedBarChart.getBarData().getGroupWidth(groupSpace, barSpace) * i);

        parentCategoriesgroupedBarChart.setScaleEnabled(true);
        parentCategoriesgroupedBarChart.getDescription().setEnabled(false);
        parentCategoriesgroupedBarChart.invalidate();
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
            if(Objects.equals(item.getParentCategoryName(), "")){
                categoryTextView.setText("Uncategorized");
            }
            else {
                categoryTextView.setText(item.getParentCategoryName()+" - "+item.getSubcategoryName());
            }
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
                fetchAllCategories();
//                populateTable(originalItemList);
//                Toast.makeText(InventorySummaryActivity.this, "Items fetched successfully", Toast.LENGTH_SHORT).show();
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
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date itemDate = sdf.parse(item.getCreatedAt().split("\\.")[0]);

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

    private void createPdf() {
        ScrollView scrollView = findViewById(R.id.mainScrollview);
        LinearLayout contentLayout = (LinearLayout) scrollView.getChildAt(0);

        PdfDocument document = new PdfDocument();

        int pageHeight = 842;
        int pageWidth = 595;

        contentLayout.measure(
                View.MeasureSpec.makeMeasureSpec(scrollView.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        int contentHeight = contentLayout.getMeasuredHeight();
        int contentWidth = contentLayout.getMeasuredWidth();

        float scaleFactor = (float) pageWidth / contentWidth;

        int scaledContentHeight = (int) (contentHeight * scaleFactor);

        int totalPages = (int) Math.ceil((float) scaledContentHeight / pageHeight);

        for (int i = 0; i < totalPages; i++) {
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, i + 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);

            Canvas canvas = page.getCanvas();

            int translateY = -i * pageHeight;

            canvas.scale(scaleFactor, scaleFactor);

            canvas.translate(0, translateY / scaleFactor);

            contentLayout.draw(canvas);

            document.finishPage(page);
        }

        File pdfFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "inventory_summary.pdf");

        try {
            document.writeTo(new FileOutputStream(pdfFile));
            Toast.makeText(this, "PDF saved to: " + pdfFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        document.close();
    }

    public void fetchCategoryStats() {
        Utils.fetchCategoryStats(app.getOrganizationID(), this, new OperationCallback<List<CategoryReportModel>>() {
            @Override
            public void onSuccess(List<CategoryReportModel> result) {
                categoryReportModelList.addAll(result);
                parentCategoryPieChart();
                parentCategoriesBarGraph();
                subcategoryPieChart();
//                Toast.makeText(InventorySummaryActivity.this, "Stats fetched successfully!!!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(InventorySummaryActivity.this, error, Toast.LENGTH_SHORT).show();
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
                String selectedRange = dateFilterSpinner.getSelectedItem().toString();
                filterItemsByDate(selectedRange);
                Toast.makeText(InventorySummaryActivity.this, "Stats fetched successfully!!!"+result.get(0).getCategoryName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(InventorySummaryActivity.this, error, Toast.LENGTH_SHORT).show();
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

}