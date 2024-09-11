package com.example.smartstorageorganizer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;

public class LogsActivity extends AppCompatActivity {
    private AppCompatButton btnAdd, btnModify, btnDelete;
    private Button userRoleBtn;
    private RelativeLayout btnOpenBottomSheet;
    private Spinner dateRangeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_logs);

        btnOpenBottomSheet = findViewById(R.id.buttonFilter);

        // Open Bottom Sheet when button is clicked
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
}