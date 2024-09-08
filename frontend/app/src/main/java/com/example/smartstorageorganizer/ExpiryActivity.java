package com.example.smartstorageorganizer;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Calendar;

public class ExpiryActivity extends AppCompatActivity {

    private Spinner spinnerDateRange;
    private ArrayList<String> dateRangeOptions;
    private ArrayAdapter<String> adapter;
    private String customRangeText = "";

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
                } else {
                    Toast.makeText(ExpiryActivity.this, "Selected: " + selectedOption, Toast.LENGTH_SHORT).show();
                    // Handle predefined date ranges
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
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
            String startDate = startDayOfMonth + "/" + (startMonth + 1) + "/" + startYear;

            // End date picker dialog
            DatePickerDialog endDatePicker = new DatePickerDialog(ExpiryActivity.this, (view1, endYear, endMonth, endDayOfMonth) -> {
                String endDate = endDayOfMonth + "/" + (endMonth + 1) + "/" + endYear;

                // Create a custom date range text
                customRangeText = "From " + startDate + " to " + endDate;

                // Check if the custom range option already exists
                if (!dateRangeOptions.contains(customRangeText)) {
                    // Add the custom range to the spinner
                    dateRangeOptions.add(customRangeText);
                    adapter.notifyDataSetChanged();
                }

                // Set the spinner to the custom range
                int customRangeIndex = dateRangeOptions.indexOf(customRangeText);
                spinnerDateRange.setSelection(customRangeIndex);

                Toast.makeText(ExpiryActivity.this, "Selected Custom Range: " + customRangeText, Toast.LENGTH_SHORT).show();

            }, year, month, day);
            endDatePicker.show();

        }, year, month, day);
        startDatePicker.show();
    }
}