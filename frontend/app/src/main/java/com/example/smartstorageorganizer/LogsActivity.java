package com.example.smartstorageorganizer;

import android.animation.LayoutTransition;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstorageorganizer.adapters.LogEntryAdapter;
import com.example.smartstorageorganizer.model.LogEntry;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class LogsActivity extends AppCompatActivity {
    private AppCompatButton btnAdd, btnModify, btnDelete;
    private Button userRoleBtn;
    private RelativeLayout btnOpenBottomSheet;
    private Spinner dateRangeSpinner;
    private TextView expandCollapseText;
    private LinearLayout expandableSection, activitiesList;
    private boolean isExpanded = false;
    private ImageView arrow;
    private RecyclerView recyclerView;
    private LogEntryAdapter logEntryAdapter;
    private List<LogEntry> logEntryList;
    private boolean isLoading = false;
    private int visibleThreshold = 1; // Trigger load more when 1 item is visible at the bottom
    private int lastVisibleItem, totalItemCount;
    private int currentPage = 1;
    private int itemsPerPage = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_logs);

        btnOpenBottomSheet = findViewById(R.id.buttonFilter);
        activitiesList = findViewById(R.id.activitiesList);
        arrow = findViewById(R.id.arrow);

//        expandCollapseText = findViewById(R.id.expandCollapseText);
//        expandableSection = findViewById(R.id.expandableSection);

        RelativeLayout parentLayout = findViewById(R.id.parentLayout);
        LayoutTransition layoutTransition = new LayoutTransition();
        parentLayout.setLayoutTransition(layoutTransition);

        findViewById(R.id.cardViewItemsReports).setOnClickListener(v -> {
            if (activitiesList.getVisibility() == View.VISIBLE) {
                activitiesList.setVisibility(View.GONE); // Collapse
                rotateArrow(arrow, 180, 0);
            } else {
                activitiesList.setVisibility(View.VISIBLE); // Expand
                rotateArrow(arrow, 0, 180);
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the log entry list
        logEntryList = new ArrayList<>();
        loadInitialData();
//        logEntryList.add(new LogEntry("John Doe", "Added", "Laptop", "Role: Admin | 2 hours ago", null, null));
//        logEntryList.add(new LogEntry("Jane Smith", "Modified", "Smartphone", "Role: Manager | 5 hours ago", "iPhone X", "iPhone 12"));
//        logEntryList.add(new LogEntry("Sam Wilson", "Deleted", "Tablet", "Role: User | 1 day ago", null, null));

        // Add more log entries as needed

        // Set the adapter
        logEntryAdapter = new LogEntryAdapter(logEntryList);
        recyclerView.setAdapter(logEntryAdapter);

        btnOpenBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog();
            }
        });
    }

    private void showBottomSheetDialog() {
        // Create Bottom Sheet Dialog
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(LogsActivity.this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_filters, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        // Initialize buttons and spinner
        btnAdd = bottomSheetView.findViewById(R.id.btnAdd);
        btnModify = bottomSheetView.findViewById(R.id.btnModify);
        btnDelete = bottomSheetView.findViewById(R.id.btnDelete);
        dateRangeSpinner = bottomSheetView.findViewById(R.id.dateRangeSpinner);

        // Action button click listeners to change the background color
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetActionButtonBackgrounds();
                btnAdd.setBackgroundResource(R.drawable.btn_blue);
            }
        });

        btnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetActionButtonBackgrounds();
                btnModify.setBackgroundResource(R.drawable.btn_blue);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetActionButtonBackgrounds();
                btnDelete.setBackgroundResource(R.drawable.btn_blue);
            }
        });

        // Show the Bottom Sheet Dialog
        bottomSheetDialog.show();
    }

    // Helper method to reset all buttons to gray background
    private void resetActionButtonBackgrounds() {
        btnAdd.setBackgroundResource(R.drawable.btn_gray);
        btnModify.setBackgroundResource(R.drawable.btn_gray);
        btnDelete.setBackgroundResource(R.drawable.btn_gray);
    }

    private void rotateArrow(ImageView arrow, float fromDegree, float toDegree) {
        RotateAnimation rotate = new RotateAnimation(fromDegree, toDegree,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.startAnimation(rotate);
    }

    // Load the initial set of data (first 5 items)
    private void loadInitialData() {
        logEntryList.add(new LogEntry("John Doe", "Added", "Laptop", "Role: Admin | 2 hours ago", null, null));
        logEntryList.add(new LogEntry("Jane Smith", "Modified", "Smartphone", "Role: Manager | 5 hours ago", "iPhone X", "iPhone 12"));
        logEntryList.add(new LogEntry("Sam Wilson", "Deleted", "Tablet", "Role: User | 1 day ago", null, null));
        logEntryList.add(new LogEntry("Emily Stone", "Added", "Printer", "Role: Admin | 3 days ago", null, null));
        logEntryList.add(new LogEntry("Robert Brown", "Modified", "Monitor", "Role: Manager | 5 days ago", "Samsung 24\"", "Samsung 27\""));

//        logEntryAdapter.addData(logEntryList.subList(0, Math.min(itemsPerPage, logEntryList.size())));
    }

    // Load more data for pagination
    private void loadMoreData(int page) {
        List<LogEntry> moreEntries = new ArrayList<>();
        int start = (page - 1) * itemsPerPage;
        int end = Math.min(start + itemsPerPage, logEntryList.size());

        // Simulate adding more items
        if (start < logEntryList.size()) {
            moreEntries.addAll(logEntryList.subList(start, end));
            logEntryAdapter.addData(moreEntries);
        }

        isLoading = false;
    }
}