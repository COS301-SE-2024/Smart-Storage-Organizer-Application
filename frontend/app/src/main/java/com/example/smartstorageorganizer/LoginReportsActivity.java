package com.example.smartstorageorganizer;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
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
import com.example.smartstorageorganizer.model.ItemModel;
import com.example.smartstorageorganizer.model.LoginReportsModel;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.Utils;

import java.util.List;
import java.util.Objects;

public class LoginReportsActivity extends AppCompatActivity {
    private MyAmplifyApp app;
    private TableLayout usersListTable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_reports);

        app = (MyAmplifyApp) getApplicationContext();
        usersListTable = findViewById(R.id.usersListTable);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        getLoginReports();
    }

    private void populateTable(List<LoginReportsModel> loginReports) {
        // Clear all rows from the table first
        usersListTable.removeAllViews();

        // Add the header row first
        TableRow headerRow = new TableRow(this);
        headerRow.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        // Create TextViews for the header row
        TextView headerNameTextView = new TextView(this);
        headerNameTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 4));
        headerNameTextView.setText("Name");
        headerNameTextView.setTextColor(Color.WHITE);
        headerNameTextView.setPadding(10, 10, 10, 10);
        headerNameTextView.setTextSize(14);
        headerNameTextView.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView headerCategoryTextView = new TextView(this);
        headerCategoryTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2));
        headerCategoryTextView.setText("Time In");
        headerCategoryTextView.setTextColor(Color.WHITE);
        headerCategoryTextView.setPadding(10, 10, 10, 10);
        headerCategoryTextView.setTextSize(14);
        headerCategoryTextView.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView headerDateTextView = new TextView(this);
        headerDateTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 4));
        headerDateTextView.setText("Time Out");
        headerDateTextView.setTextColor(Color.WHITE);
        headerDateTextView.setPadding(10, 10, 10, 10);
        headerDateTextView.setTextSize(14);
        headerDateTextView.setGravity(Gravity.CENTER_HORIZONTAL);

        // Add TextViews to the header row
        headerRow.addView(headerNameTextView);
        headerRow.addView(headerCategoryTextView);
        headerRow.addView(headerDateTextView);

        // Add the header row to the table
        usersListTable.addView(headerRow);

        // Add the item rows
        for (LoginReportsModel item : loginReports) {
            TableRow row = new TableRow(this);

            TextView nameTextView = new TextView(this);
            nameTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 4));
            nameTextView.setText(item.getName()+" "+item.getSurname());
            nameTextView.setGravity(Gravity.CENTER);
            nameTextView.setPadding(10, 10, 10, 10);
            nameTextView.setTextSize(12);

            TextView categoryTextView = new TextView(this);
            categoryTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 4));
            categoryTextView.setText(item.getTimeIn());
            categoryTextView.setGravity(Gravity.CENTER);
            categoryTextView.setPadding(10, 10, 10, 10);
            categoryTextView.setTextSize(12);

            TextView dateTextView = new TextView(this);
            dateTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 4));
            dateTextView.setText(item.getTimeOut());
            dateTextView.setGravity(Gravity.CENTER);
            dateTextView.setPadding(10, 10, 10, 10);
            dateTextView.setTextSize(12);

            // Add TextViews to the item row
            row.addView(nameTextView);
            row.addView(categoryTextView);
            row.addView(dateTextView);

            // Add the item row to the table
            usersListTable.addView(row);
        }
    }

    public void getLoginReports() {
        Utils.getLoginReports(app.getOrganizationID(), app.getEmail(), this, new OperationCallback<List<LoginReportsModel>>() {
            @Override
            public void onSuccess(List<LoginReportsModel> result) {
                populateTable(result);
                Toast.makeText(LoginReportsActivity.this, "Stats fetched successfully!!!"+result.get(0).getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(LoginReportsActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}