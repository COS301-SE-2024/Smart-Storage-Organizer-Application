package com.example.smartstorageorganizer;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ExpiryActivity extends AppCompatActivity {

    String customStartDate;
    String customEndDate;
    private Spinner spinnerDateRange;
    private ArrayList<String> dateRangeOptions;
    private ArrayAdapter<String> adapter;
    private String customRangeText = "";
    private MyAmplifyApp app;
    List<ItemModel> originalItemList;
    private ImageView arrow;
    private TableLayout itemsListTable, expiredItemsListTable;

    private static final int PAGE_SIZE = 1000;
    private int currentPage = 1;
    private List<CategoryModel> categoryModelList;
    private ArrayList<String> parentCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_expiry);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        parentCategories = new ArrayList<>();
        categoryModelList = new ArrayList<>();

        app = (MyAmplifyApp) getApplicationContext();
        originalItemList = new ArrayList<>();

        arrow = findViewById(R.id.arrow);
        itemsListTable = findViewById(R.id.itemsListTable);
        expiredItemsListTable = findViewById(R.id.expiredItemsListTable);

        RelativeLayout parentLayout = findViewById(R.id.parentLayout);
        RelativeLayout parentLayoutExpired = findViewById(R.id.parentLayoutExpired);

        // Initialize the spinner
        spinnerDateRange = findViewById(R.id.spinner_date_range);

        // Initialize date range options
        dateRangeOptions = new ArrayList<>();
        dateRangeOptions.add("Next 7 Days");
        dateRangeOptions.add("Next 30 Days");
        dateRangeOptions.add("Custom Range");

        // Create an ArrayAdapter using the string array and a default spinner layout
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dateRangeOptions);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinnerDateRange.setAdapter(adapter);

        // Set a listener for the spinner
        spinnerDateRange.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = parent.getItemAtPosition(position).toString();

                if (selectedOption.equals("Custom Range")) {
                    showCustomDateRangePicker();
                } else if (selectedOption.equals("Next 7 Days") || selectedOption.equals("Next 30 Days")){
                    filterItemsByDate(selectedOption);
//                    Toast.makeText(ExpiryActivity.this, "Selected: " + selectedOption, Toast.LENGTH_SHORT).show();
//                    barGraph();
                    // Handle predefined date ranges
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        LayoutTransition layoutTransition = new LayoutTransition();
        parentLayout.setLayoutTransition(layoutTransition);
        LayoutTransition ExpiredLayoutTransition = new LayoutTransition();
        parentLayoutExpired.setLayoutTransition(ExpiredLayoutTransition);

        fetchItems();
//        fetchCategoryStats();

        findViewById(R.id.cardViewItemsReports).setOnClickListener(v -> {
            if (itemsListTable.getVisibility() == View.VISIBLE) {
                itemsListTable.setVisibility(View.GONE); // Collapse
                rotateArrow(arrow, 180, 0);
            } else {
                itemsListTable.setVisibility(View.VISIBLE); // Expand
                rotateArrow(arrow, 0, 180);
            }
        });
        findViewById(R.id.cardViewExpiredItems).setOnClickListener(v -> {
            if (expiredItemsListTable.getVisibility() == View.VISIBLE) {
                expiredItemsListTable.setVisibility(View.GONE); // Collapse
                rotateArrow(arrow, 180, 0);
            } else {
                expiredItemsListTable.setVisibility(View.VISIBLE); // Expand
                rotateArrow(arrow, 0, 180);
            }
        });
    }

    // Show the custom date range picker dialog
    private void showCustomDateRangePicker() {
        // Get the current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Start date picker dialog
        DatePickerDialog startDatePicker = new DatePickerDialog(this, (view, startYear, startMonth, startDayOfMonth) -> {
            customStartDate = startDayOfMonth + "/" + (startMonth + 1) + "/" + startYear;

            // End date picker dialog
            DatePickerDialog endDatePicker = new DatePickerDialog(ExpiryActivity.this, (view1, endYear, endMonth, endDayOfMonth) -> {
                customEndDate = endDayOfMonth + "/" + (endMonth + 1) + "/" + endYear;

                // Create a custom date range text
                customRangeText = "From " + customStartDate + " to " + customEndDate;

                // Check if the custom range option already exists
                if (!dateRangeOptions.contains(customRangeText)) {
                    // Add the custom range to the spinner
                    dateRangeOptions.add(customRangeText);
                    adapter.notifyDataSetChanged();
                }

                // Set the spinner to the custom range
                int customRangeIndex = dateRangeOptions.indexOf(customRangeText);
                spinnerDateRange.setSelection(customRangeIndex);
                filterItemsByDate("Custom Range");
                Toast.makeText(ExpiryActivity.this, "Selected Custom Range: " + customRangeText, Toast.LENGTH_SHORT).show();

            }, year, month, day);
            endDatePicker.show();

        }, year, month, day);
        startDatePicker.show();
    }

    private void drawBarGraph(int expiredCount, int nearExpiryCount) {
        BarChart barChart = findViewById(R.id.barChart);

        // Prepare data for the Bar Chart
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, expiredCount));  // X value = 0 for "Expired"
        entries.add(new BarEntry(1f, nearExpiryCount));  // X value = 1 for "Near Expiry"

        BarDataSet dataSet = new BarDataSet(entries, "Items Status");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS); // Use default material colors

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.9f);  // Set the bar width

        // Set the labels for the X axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"Expired", "Near Expiry"}));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);

        // Set data to the bar chart
        barChart.setData(data);
        barChart.setFitBars(true);  // Make the bars fit nicely in the graph
        barChart.invalidate();  // Refresh the chart
    }


    private void fetchItems() {
        Utils.fetchAllItems(PAGE_SIZE, currentPage, app.getOrganizationID(),this, new OperationCallback<List<ItemModel>>() {
            @Override
            public void onSuccess(List<ItemModel> result) {
                originalItemList.clear();
                originalItemList.addAll(result);
                fetchAllCategories();
                populateTable(originalItemList, "");
//                Toast.makeText(InventorySummaryActivity.this, "Items fetched successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(ExpiryActivity.this, "Failed to fetch items: " + error, Toast.LENGTH_SHORT).show();
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
                String selectedRange = spinnerDateRange.getSelectedItem().toString();
                filterItemsByDate(selectedRange);
                expiredItems();
                countExpiredAndNearExpiryItems();
//                Toast.makeText(ExpiryActivity.this, "Stats fetched successfully!!!"+result.get(0).getCategoryName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(ExpiryActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterItemsByDate(String selectedRange) {
        List<ItemModel> filteredItems = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date now = new Date();

        for (ItemModel item : originalItemList) {
            String expiryDateString = item.getExpiryDate();
            if (!expiryDateString.equalsIgnoreCase("no expiry date")) {
                try {
                    // Parse the expiry date
                    Date expiryDate = sdf.parse(expiryDateString);
                    if (expiryDate != null) {
                        // Check the selected range and filter accordingly
                        switch (selectedRange) {
                            case "Next 7 Days":
                                if (isWithinDays(expiryDate, now, 7)) filteredItems.add(item);
                                break;
                            case "Next 30 Days":
                                if (isWithinDays(expiryDate, now, 30)) filteredItems.add(item);
                                break;
                            case "Custom Range":
                                // Handle custom date range logic here
                                // Assuming you have startDate and endDate defined for custom range
                                if (isWithinCustomRange(expiryDate)){
                                    Log.e("View Response Results Body Array", "adding "+item.getExpiryDate());
                                    filteredItems.add(item);
                                }
                                break;
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d("populateTable", "Number of items: " + filteredItems.size());

        // Update the table with filtered items
        populateTable(filteredItems, "");
    }

    private void expiredItems() {
        List<ItemModel> filteredItems = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date now = new Date();

        for (ItemModel item : originalItemList) {
            String expiryDateString = item.getExpiryDate();
            if (!expiryDateString.equalsIgnoreCase("no expiry date")) {
                try {
                    // Parse the expiry date
                    Date expiryDate = sdf.parse(expiryDateString);
                    if (expiryDate != null && expiryDate.before(now)) {
                        // If the expiry date is before the current date, add to the filtered list
                        filteredItems.add(item);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        // Update the table with filtered items (expired items only)
        populateTable(filteredItems, "expired");
    }


    private boolean isWithinDays(Date expiryDate, Date currentDate, int days) {
        long diffInMillies = expiryDate.getTime() - currentDate.getTime();
        long daysDiff = TimeUnit.MILLISECONDS.toDays(diffInMillies);
        return daysDiff >= 0 && daysDiff <= days;
    }

    private boolean isWithinCustomRange(Date expiryDate) {
        // Assuming you have variables startDate and endDate for the custom range
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date startDate = sdf.parse(customStartDate); // Parse start date from custom range
            Date endDate = sdf.parse(customEndDate); // Parse end date from custom range
            boolean flag = expiryDate != null && !expiryDate.before(startDate) && !expiryDate.after(endDate);
            Log.e("View Response Results Body Array", startDate+" - "+endDate + ": expiryDate: "+expiryDate + " "+flag);
            return expiryDate != null && !expiryDate.before(startDate) && !expiryDate.after(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }


    private void populateTable(List<ItemModel> items, String type) {
        // Clear all rows from the table first
        if(Objects.equals(type, "")){
            itemsListTable.removeAllViews();
        }
        else {
            expiredItemsListTable.removeAllViews();
        }

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
        headerDateTextView.setText("Expiry Date");
        headerDateTextView.setTextColor(Color.WHITE);
        headerDateTextView.setPadding(10, 10, 10, 10);
        headerDateTextView.setTextSize(14);
        headerDateTextView.setGravity(Gravity.CENTER_HORIZONTAL);

        // Add TextViews to the header row
        headerRow.addView(headerNameTextView);
        headerRow.addView(headerCategoryTextView);
        headerRow.addView(headerDateTextView);

        if(Objects.equals(type, "")){
            itemsListTable.addView(headerRow);
        }
        else {
            expiredItemsListTable.addView(headerRow);
        }
        // Add the header row to the table

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
            dateTextView.setText(item.getExpiryDate());
            dateTextView.setGravity(Gravity.CENTER);
            dateTextView.setPadding(10, 10, 10, 10);
            dateTextView.setTextSize(12);

            // Add TextViews to the item row
            row.addView(nameTextView);
            row.addView(categoryTextView);
            row.addView(dateTextView);

            if(Objects.equals(type, "")){
                itemsListTable.addView(row);
            }
            else {
                expiredItemsListTable.addView(row);
            }
            // Add the item row to the table
        }
    }

    private String getCategoryName(String categoryId) {
        for(CategoryModel category: categoryModelList){
            if(Objects.equals(category.getCategoryID(), categoryId)){
                return category.getCategoryName();
            }
        }
        return "";
    }

    private void rotateArrow(ImageView arrow, float fromDegree, float toDegree) {
        RotateAnimation rotate = new RotateAnimation(fromDegree, toDegree,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.startAnimation(rotate);
    }

    private void countExpiredAndNearExpiryItems() {
        int expiredCount = 0;
        int nearExpiryCount = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date now = new Date();

        for (ItemModel item : originalItemList) {
            String expiryDateString = item.getExpiryDate();
            if (!expiryDateString.equalsIgnoreCase("no expiry date")) {
                try {
                    // Parse the expiry date
                    Date expiryDate = sdf.parse(expiryDateString);
                    if (expiryDate != null) {
                        long diffInMillies = expiryDate.getTime() - now.getTime();
                        long daysDiff = TimeUnit.MILLISECONDS.toDays(diffInMillies);

                        // Count expired items
                        if (expiryDate.before(now)) {
                            expiredCount++;
                        }
                        // Count near expiry items (within the next 7 days)
                        else if (daysDiff <= 7) {
                            nearExpiryCount++;
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        // Draw the bar graph with expiredCount and nearExpiryCount
        drawBarGraph(expiredCount, nearExpiryCount);
    }

}