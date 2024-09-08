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
    private GridLayout gridLayoutItems;
    private ImageView arrow;
    private CardView inventorySummary, expiryReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reports);

        arrow = findViewById(R.id.arrow);
        gridLayoutItems = findViewById(R.id.gridLayoutItems);
        inventorySummary = findViewById(R.id.inventorySummary);
        expiryReport = findViewById(R.id.expiryReport);

        // Get the parent layout (RelativeLayout or CardView) and set LayoutTransition
        RelativeLayout parentLayout = findViewById(R.id.parentLayout); // Replace with the correct ID
        LayoutTransition layoutTransition = new LayoutTransition();
        parentLayout.setLayoutTransition(layoutTransition);

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
    }

    private void rotateArrow(ImageView arrow, float fromDegree, float toDegree) {
        RotateAnimation rotate = new RotateAnimation(fromDegree, toDegree,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.startAnimation(rotate);
    }
}