package com.example.smartstorageorganizer;

import android.animation.LayoutTransition;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstorageorganizer.adapters.AppReportAdapter;
import com.example.smartstorageorganizer.model.AppReportModel;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class AppReportActivity extends BaseActivity {
    FirebaseFirestore db;
    FirebaseAuth auth;
    MyAmplifyApp app;
    TableLayout usersListTable;
    private ImageView arrow;
    private RecyclerView recyclerView;
    private AppReportAdapter appReportAdapter;
    private List<AppReportModel> appReportModelList;
    private ListenerRegistration listenerRegistration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_app_report);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        app = (MyAmplifyApp) getApplicationContext();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        arrow = findViewById(R.id.arrow);
        usersListTable = findViewById(R.id.usersListTable);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the log entry list and the adapter
        appReportModelList = new ArrayList<>();
        appReportAdapter = new AppReportAdapter(appReportModelList);
        recyclerView.setAdapter(appReportAdapter);

//        loadInitialData();

        RelativeLayout parentLayout = findViewById(R.id.parentLayout);
        LayoutTransition layoutTransition = new LayoutTransition();
        parentLayout.setLayoutTransition(layoutTransition);

        findViewById(R.id.cardViewAppReports).setOnClickListener(v -> {
            if (usersListTable.getVisibility() == View.VISIBLE) {
                usersListTable.setVisibility(View.GONE); // Collapse
                rotateArrow(arrow, 180, 0);
            } else {
                usersListTable.setVisibility(View.VISIBLE); // Expand
                rotateArrow(arrow, 0, 180);
            }
        });
    }

    private void listenForActiveUsersRealTime() {
        Timestamp thirtyMinutesAgo = new Timestamp(new Date(System.currentTimeMillis() - (30 * 60 * 1000)));

        db.collection("active_users")
                .whereGreaterThan("last_active_time", thirtyMinutesAgo)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot value, FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("Firestore", "Listen failed.", e);
                            return;
                        }

                        if (value != null) {
                            int activeUsersCount = value.size();
                            Log.d("Firestore", "Real-time active users count: " + activeUsersCount);

                            // Update your UI with the count
                            TextView activeUsersTextView = findViewById(R.id.activeUsersTextView);
                            activeUsersTextView.setText(String.valueOf(activeUsersCount));
                            TableLayout usersListTable = findViewById(R.id.usersListTable);
                            usersListTable.removeAllViews();
                            addRowHeader();
                            /* // Loop through all the active users and display their emails */
                            for (QueryDocumentSnapshot doc : value) {
                                String email = doc.getId(); // Assuming document ID is the email
                                Timestamp lastActiveTimestamp = doc.getTimestamp("last_active_time");

                                String lastActiveTime = formatTimestamp(lastActiveTimestamp);

                                addRowToTable(email, lastActiveTime);
                            }
                        }
                    }
                });
    }

    private void addRowHeader(){
        TableRow headerRow = new TableRow(this);
        headerRow.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        // Create TextViews for the header row
        TextView headerNameTextView = new TextView(this);
        headerNameTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 6));
        headerNameTextView.setText("Username");
        headerNameTextView.setTextColor(Color.WHITE);
        headerNameTextView.setPadding(10, 10, 10, 10);
        headerNameTextView.setTextSize(14);
        headerNameTextView.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView headerQuantityTextView = new TextView(this);
        headerQuantityTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 6));
        headerQuantityTextView.setText("Time");
        headerQuantityTextView.setTextColor(Color.WHITE);
        headerQuantityTextView.setPadding(10, 10, 10, 10);
        headerQuantityTextView.setTextSize(14);
        headerQuantityTextView.setGravity(Gravity.CENTER_HORIZONTAL);

        headerRow.addView(headerNameTextView);
        headerRow.addView(headerQuantityTextView);

        usersListTable.addView(headerRow);
    }

    private String formatTimestamp(Timestamp timestamp) {
        if (timestamp != null) {
            Date date = timestamp.toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.format(date);
        }
        return "N/A";
    }

    // Method to add a row to the TableLayout with the user's email and last active time
    private void addRowToTable(String email, String lastActiveTime) {
        // Create a new TableRow
        TableRow tableRow = new TableRow(this);
        tableRow.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        tableRow.setGravity(Gravity.CENTER_VERTICAL);

        // Create a TextView for the email
        TextView emailTextView = new TextView(this);
        emailTextView.setText(email);
        emailTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 6));
        emailTextView.setPadding(16, 16, 16, 16);
        emailTextView.setTextSize(16);
        emailTextView.setTextColor(Color.BLACK);

        // Create a TextView for the last active time
        TextView lastActiveTextView = new TextView(this);
        lastActiveTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 6));
        lastActiveTextView.setText(lastActiveTime);
        lastActiveTextView.setPadding(16, 16, 16, 16);
        lastActiveTextView.setTextSize(16);
        lastActiveTextView.setTextColor(Color.GRAY);

        // Add TextViews to the TableRow
        tableRow.addView(emailTextView);
        tableRow.addView(lastActiveTextView);

        // Add TableRow to TableLayout
        usersListTable.addView(tableRow);
    }

    private void rotateArrow(ImageView arrow, float fromDegree, float toDegree) {
        RotateAnimation rotate = new RotateAnimation(fromDegree, toDegree,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.startAnimation(rotate);
    }

    private void loadInitialData() {
        appReportModelList.add(new AppReportModel("HomeActivity", "665", "12", "55.42", "23m 16s", "799"));
        appReportModelList.add(new AppReportModel("LandingActivity", "498", "13", "38.31", "8m 02s", "562"));
        appReportModelList.add(new AppReportModel("LoginActivity", "429", "12", "35.75", "8m 53s", "798"));

        // Initially load the first page
        appReportAdapter.notifyDataSetChanged();
    }

    private void listenForActivityViewsRealTime() {
        db.collection("activity_views")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot value, FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("Firestore", "Listen failed.", e);
                            return;
                        }

                        HashMap<String, Integer> activityViewsMap = new HashMap<>();
                        HashSet<String> activeUsersSet = new HashSet<>();
                        int totalViews = 0;

                        for (QueryDocumentSnapshot doc : value) {
                            String activityName = doc.getString("activity_name");
                            String userId = doc.getString("user_id");

                            // Track unique active users
                            activeUsersSet.add(userId);

                            // Track the total views
                            totalViews++;

                            // Increment view count per activity
                            if (activityViewsMap.containsKey(activityName)) {
                                activityViewsMap.put(activityName, activityViewsMap.get(activityName) + 1);
                            } else {
                                activityViewsMap.put(activityName, 1);
                            }
                        }

                        // Calculate views per active user
                        int viewsPerActiveUser = (activeUsersSet.size() > 0) ? totalViews / activeUsersSet.size() : 0;

                        // Update the app report views with the new data
                        updateAppReportViews(activityViewsMap, activeUsersSet.size(), viewsPerActiveUser);
                    }
                });
    }

    private void updateAppReportViews(HashMap<String, Integer> activityViewsMap, int activeUsersCount, int viewsPerActiveUser) {
        appReportModelList.clear(); // Clear the list before adding updated data

        for (Map.Entry<String, Integer> entry : activityViewsMap.entrySet()) {
            String activityName = entry.getKey();
            String views = String.valueOf(entry.getValue());

            // Create a new AppReportModel with the activity views and active users count
            AppReportModel reportModel = new AppReportModel(
                    activityName,
                    views,
                    String.valueOf(activeUsersCount),
                    String.valueOf(viewsPerActiveUser),  // Views per active user
                    "N/A",  // Placeholder for average engagement time
                    views   // Event count (assuming it's the same as views)
            );

            appReportModelList.add(reportModel);
        }

        // Notify the adapter of the data change
        appReportAdapter.notifyDataSetChanged();
    }


    private void listenForActivitySessionsRealTime() {
        db.collection("activity_sessions")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot value, FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("Firestore", "Listen failed.", e);
                            return;
                        }

                        // Map to hold total session durations and count per activity
                        HashMap<String, Long> totalDurationMap = new HashMap<>();
                        HashMap<String, Integer> sessionCountMap = new HashMap<>();

                        for (QueryDocumentSnapshot doc : value) {
                            String activityName = doc.getString("activity_name");
                            Long sessionDuration = doc.getLong("session_duration");

                            // Increment total duration for the activity
                            totalDurationMap.put(activityName, totalDurationMap.getOrDefault(activityName, 0L) + sessionDuration);

                            // Increment session count for the activity
                            sessionCountMap.put(activityName, sessionCountMap.getOrDefault(activityName, 0) + 1);
                        }

                        // Now update the appReportModelList based on totalDurationMap and sessionCountMap
                        updateAppReportSessions(totalDurationMap, sessionCountMap);
                    }
                });
    }

    private void updateAppReportSessions(HashMap<String, Long> totalDurationMap, HashMap<String, Integer> sessionCountMap) {
        for (AppReportModel reportModel : appReportModelList) {
            String activityName = reportModel.getPageTitle();

            if (totalDurationMap.containsKey(activityName)) {
                long totalDuration = totalDurationMap.get(activityName);
                int sessionCount = sessionCountMap.get(activityName);

                // Calculate the average session duration
                long averageDuration = totalDuration / sessionCount;
                String averageDurationFormatted = formatDuration(averageDuration);

                // Update the report model
                reportModel.setAverageEngagementTimePerActiveUser(averageDurationFormatted);
            }
        }

        // Notify the adapter of the data change
        appReportAdapter.notifyDataSetChanged();
    }

    // Helper method to format the duration in milliseconds to a readable time (minutes:seconds)
    private String formatDuration(long durationMillis) {
        long minutes = (durationMillis / 1000) / 60;
        long seconds = (durationMillis / 1000) % 60;
        return String.format("%dm %02ds", minutes, seconds);
    }

    private void listenForUserFlowRealTime() {
        listenerRegistration = db.collection("user_flow")
                .whereEqualTo("previous_activity", "HomeFragment")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.d("Firestore", "Error getting documents: ", error);
                        return;
                    }

                    if (value != null && !value.isEmpty()) {
                        HashMap<String, Integer> userFlowMap = new HashMap<>();

                        for (QueryDocumentSnapshot document : value) {
                            String nextActivity = document.getString("next_activity");

                            // Count how many times each next activity occurs
                            userFlowMap.put(nextActivity, userFlowMap.getOrDefault(nextActivity, 0) + 1);
                        }

                        // Once data is collected, display it in a chart
                        displayUserFlowChart(userFlowMap, value.size());
                    }
                });
    }


    private void displayUserFlowChart(HashMap<String, Integer> userFlowMap, int size) {
        PieChart pieChart = findViewById(R.id.userFlowPieChart);

        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : userFlowMap.entrySet()) {
            pieEntries.add(new PieEntry(((float) entry.getValue() /size)*100, entry.getKey()));
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "User Flow from HomeFragment");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setValueTextSize(12f);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.setDrawSliceText(false);
        pieChart.setDescription(null);

        // Set the text in the center of the PieChart
        pieChart.setCenterText("HomeFragment");
        pieChart.setCenterTextSize(18f);  // Set text size
        pieChart.setCenterTextColor(Color.BLACK); // Set text color
        pieChart.setDrawCenterText(true); // Enable center text

//        pieChart.getDescription().setEnabled(false);

        pieChart.invalidate(); // refresh chart
    }




    @Override
    protected void onResume() {
        super.onResume();
        listenForActiveUsersRealTime();
        listenForActivityViewsRealTime();
        listenForActivitySessionsRealTime();
        listenForUserFlowRealTime();
    }

}