package com.example.smartstorageorganizer;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ReportsActivity extends BaseActivity  {
    private CardView cardViewItemsReports, cardViewUnit, cardViewCategory, cardViewColorCode;
    private GridLayout gridLayoutItems, gridLayoutApp;
    private ImageView arrow, appArrow;
    private CardView inventorySummary, expiryReport, stockReport, loginReport, logsReport;
    private MyAmplifyApp app;
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reports);

        app = (MyAmplifyApp) getApplicationContext();


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
                logUserFlow("InventorySummaryActivity");
                startActivity(intent);
            }
        });
        expiryReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridLayoutItems.setVisibility(View.VISIBLE);
                rotateArrow(arrow, 0, 180);
                Intent intent = new Intent(ReportsActivity.this, ExpiryActivity.class);
                logUserFlow("ExpiryActivity");
                startActivity(intent);
            }
        });
        stockReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridLayoutItems.setVisibility(View.VISIBLE);
                rotateArrow(arrow, 0, 180);
                Intent intent = new Intent(ReportsActivity.this, LowStockActivity.class);
                logUserFlow("LowStockActivity");
                startActivity(intent);
            }
        });

        loginReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridLayoutApp.setVisibility(View.VISIBLE);
                rotateArrow(arrow, 0, 180);
                Intent intent = new Intent(ReportsActivity.this, LoginReportsActivity.class);
                logUserFlow("LoginReportsActivity");
                startActivity(intent);
            }
        });

        logsReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridLayoutApp.setVisibility(View.VISIBLE);
                rotateArrow(arrow, 0, 180);
                Intent intent = new Intent(ReportsActivity.this, LogsActivity.class);
                logUserFlow("LogsActivity");
                startActivity(intent);
            }
        });

        findViewById(R.id.unitsReport).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridLayoutApp.setVisibility(View.VISIBLE);
                rotateArrow(arrow, 0, 180);
                Intent intent = new Intent(ReportsActivity.this, AppReportActivity.class);
                logUserFlow("AppReportActivity");
                startActivity(intent);
            }
        });
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    private void rotateArrow(ImageView arrow, float fromDegree, float toDegree) {
        RotateAnimation rotate = new RotateAnimation(fromDegree, toDegree,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.startAnimation(rotate);
    }

    private void logActivityView(String activityName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = app.getEmail();

        Map<String, Object> activityView = new HashMap<>();
        activityView.put("user_id", userId);
        activityView.put("activity_name", activityName);
        activityView.put("view_time", new Timestamp(new Date()));

        db.collection("activity_views")
                .add(activityView)
                .addOnSuccessListener(documentReference -> Log.d("Firestore", "Activity view logged."))
                .addOnFailureListener(e -> Log.w("Firestore", "Error logging activity view", e));
    }

    private void logSessionDuration(String activityName, long sessionDuration) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = app.getEmail();

        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("user_id", userId);
        sessionData.put("activity_name", activityName);
        sessionData.put("session_duration", sessionDuration); // Duration in milliseconds

        db.collection("activity_sessions")
                .add(sessionData)
                .addOnSuccessListener(documentReference -> Log.d("Firestore", "Session duration logged."))
                .addOnFailureListener(e -> Log.w("Firestore", "Error logging session duration", e));
    }
    public void logUserFlow(String toActivity) {
        long sessionDuration = System.currentTimeMillis() - startTime;
        logSessionDuration("ReportsActivity", (sessionDuration));
        long transitionTime = System.currentTimeMillis();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = app.getEmail();

        Map<String, Object> userFlowData = new HashMap<>();
        userFlowData.put("user_id", userId);
        userFlowData.put("previous_activity", "ReportsActivity");
        userFlowData.put("next_activity", toActivity);
        userFlowData.put("transition_time", new Timestamp(new Date(transitionTime)));

        db.collection("user_flow")
                .add(userFlowData)
                .addOnSuccessListener(documentReference -> Log.d("Firestore", "User flow logged."))
                .addOnFailureListener(e -> Log.w("Firestore", "Error logging user flow", e));
    }

//    public void logUser

    @Override
    public void onStart() {
        super.onStart();
        logActivityView("ReportsActivity");
    }

    @Override
    public void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}