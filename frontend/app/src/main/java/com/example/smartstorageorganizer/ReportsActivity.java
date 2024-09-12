package com.example.smartstorageorganizer;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.smartstorageorganizer.adapters.ItemAdapter;
import com.example.smartstorageorganizer.adapters.ReportAdapter;
import com.example.smartstorageorganizer.model.ItemModel;
import com.example.smartstorageorganizer.model.ReportModel;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReportsActivity extends AppCompatActivity {
    private CardView cardViewItemsReports, cardViewUnit, cardViewCategory, cardViewColorCode;
    private GridLayout gridLayoutItems, gridLayoutApp;
    private ImageView arrow, appArrow;
    private CardView inventorySummary, expiryReport, stockReport, loginReport, logsReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reports);

        arrow = findViewById(R.id.arrow);
        appArrow = findViewById(R.id.appReportarrow);
        gridLayoutItems = findViewById(R.id.gridLayoutItems);
        gridLayoutApp = findViewById(R.id.gridLayoutLoginReport);
        inventorySummary = findViewById(R.id.inventorySummary);
        expiryReport = findViewById(R.id.expiryReport);
        stockReport = findViewById(R.id.stockReport);
        loginReport = findViewById(R.id.loginReport);
        logsReport = findViewById(R.id.userActivityLogs);

        // Get the parent layout (RelativeLayout or CardView) and set LayoutTransition
        RelativeLayout parentLayout = findViewById(R.id.parentLayout); // Replace with the correct ID
        LayoutTransition layoutTransition = new LayoutTransition();
        parentLayout.setLayoutTransition(layoutTransition);

        RelativeLayout parentLayoutApp = findViewById(R.id.parentLayoutApp); // Replace with the correct ID
        LayoutTransition layoutTransitionApp = new LayoutTransition();
        parentLayoutApp.setLayoutTransition(layoutTransitionApp);

        // Set click listener for expanding/collapsing the GridLayout
        findViewById(R.id.cardViewItemsReports).setOnClickListener(v -> {
            if (gridLayoutItems.getVisibility() == View.VISIBLE) {
                gridLayoutItems.setVisibility(View.GONE); // Collapse
                rotateArrow(arrow, 180, 0);
            } else {
                gridLayoutItems.setVisibility(View.VISIBLE);
                rotateArrow(arrow, 0, 180);
            }
        });

        findViewById(R.id.cardViewAppReports).setOnClickListener(v -> {
            if (gridLayoutApp.getVisibility() == View.VISIBLE) {
                gridLayoutApp.setVisibility(View.GONE); // Collapse
                rotateArrow(appArrow, 180, 0);
            } else {
                gridLayoutApp.setVisibility(View.VISIBLE);
                rotateArrow(appArrow, 0, 180);
            }
        });

        inventorySummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridLayoutItems.setVisibility(View.VISIBLE);
                rotateArrow(arrow, 0, 180);
                Intent intent = new Intent(ReportsActivity.this, InventorySummaryActivity.class);
                startActivity(intent);
            }
        });
        expiryReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridLayoutItems.setVisibility(View.VISIBLE);
                rotateArrow(arrow, 0, 180);
                Intent intent = new Intent(ReportsActivity.this, ExpiryActivity.class);
                startActivity(intent);
            }
        });
        stockReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridLayoutItems.setVisibility(View.VISIBLE);
                rotateArrow(arrow, 0, 180);
                Intent intent = new Intent(ReportsActivity.this, LowStockActivity.class);
                startActivity(intent);
            }
        });

        loginReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridLayoutApp.setVisibility(View.VISIBLE);
                rotateArrow(arrow, 0, 180);
                Intent intent = new Intent(ReportsActivity.this, LoginReportsActivity.class);
                startActivity(intent);
            }
        });

        logsReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridLayoutApp.setVisibility(View.VISIBLE);
                rotateArrow(arrow, 0, 180);
                Intent intent = new Intent(ReportsActivity.this, LogsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void rotateArrow(ImageView arrow, float fromDegree, float toDegree) {
        RotateAnimation rotate = new RotateAnimation(fromDegree, toDegree,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.startAnimation(rotate);
    }
}