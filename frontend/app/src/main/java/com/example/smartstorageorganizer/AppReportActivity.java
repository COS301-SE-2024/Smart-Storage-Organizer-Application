package com.example.smartstorageorganizer;

import android.animation.LayoutTransition;
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
    Spinner activitySpinner;
    PieChart pieChart;


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

        activitySpinner = findViewById(R.id.activitySpinner);
        pieChart = findViewById(R.id.userFlowPieChart);

        // Define the list of activities
        List<String> activities = new ArrayList<>();
        activities.add("HomeFragment");
        activities.add("SearchActivity");
        activities.add("ReportsActivity");
        activities.add("ViewItemActivity");
        activities.add("ViewUnitItemsActivity");
        activities.add("UncategorizedItemsActivity");
        activities.add("LoginActivity");
        activities.add("ItemDetailsActivity");
        activities.add("LandingActivity");
        activities.add("AddOrganizationActivity");
        activities.add("GetStartedActivity");
        activities.add("AddCategoryActivity");
        activities.add("UnitActivity");
        activities.add("AddColorCodeActivity");
        activities.add("AddItemActivity");

        arrow = findViewById(R.id.arrow);
        usersListTable = findViewById(R.id.usersListTable);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        appReportModelList = new ArrayList<>();
        appReportAdapter = new AppReportAdapter(appReportModelList);
        recyclerView.setAdapter(appReportAdapter);

        RelativeLayout parentLayout = findViewById(R.id.parentLayout);
        LayoutTransition layoutTransition = new LayoutTransition();
        parentLayout.setLayoutTransition(layoutTransition);

        findViewById(R.id.cardViewAppReports).setOnClickListener(v -> {
            if (usersListTable.getVisibility() == View.VISIBLE) {
                usersListTable.setVisibility(View.GONE);
                rotateArrow(arrow, 180, 0);
            } else {
                usersListTable.setVisibility(View.VISIBLE);
                rotateArrow(arrow, 0, 180);
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, activities);
        activitySpinner.setAdapter(adapter);

        // Add listener to Spinner selection
        activitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedActivity = activities.get(position);
                // Update the pie chart with the selected activity
                updateUserFlowRealTime(selectedActivity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle case when nothing is selected, if necessary
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

    private void addRowToTable(String email, String lastActiveTime) {
        TableRow tableRow = new TableRow(this);
        tableRow.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        tableRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView emailTextView = new TextView(this);
        emailTextView.setText(email);
        emailTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 6));
        emailTextView.setPadding(16, 16, 16, 16);
        emailTextView.setTextSize(16);
        emailTextView.setTextColor(Color.BLACK);

        TextView lastActiveTextView = new TextView(this);
        lastActiveTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 6));
        lastActiveTextView.setText(lastActiveTime);
        lastActiveTextView.setPadding(16, 16, 16, 16);
        lastActiveTextView.setTextSize(16);
        lastActiveTextView.setTextColor(Color.GRAY);

        tableRow.addView(emailTextView);
        tableRow.addView(lastActiveTextView);

        usersListTable.addView(tableRow);
    }

    private void rotateArrow(ImageView arrow, float fromDegree, float toDegree) {
        RotateAnimation rotate = new RotateAnimation(fromDegree, toDegree,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.startAnimation(rotate);
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

                            activeUsersSet.add(userId);

                            totalViews++;

                            if (activityViewsMap.containsKey(activityName)) {
                                activityViewsMap.put(activityName, activityViewsMap.get(activityName) + 1);
                            } else {
                                activityViewsMap.put(activityName, 1);
                            }
                        }

                        int viewsPerActiveUser = (activeUsersSet.size() > 0) ? totalViews / activeUsersSet.size() : 0;

                        updateAppReportViews(activityViewsMap, activeUsersSet.size(), viewsPerActiveUser);
                    }
                });
    }

    private void updateAppReportViews(HashMap<String, Integer> activityViewsMap, int activeUsersCount, int viewsPerActiveUser) {
        appReportModelList.clear();

        for (Map.Entry<String, Integer> entry : activityViewsMap.entrySet()) {
            String activityName = entry.getKey();
            String views = String.valueOf(entry.getValue());

            AppReportModel reportModel = new AppReportModel(
                    activityName,
                    views,
                    String.valueOf(activeUsersCount),
                    String.valueOf(viewsPerActiveUser),
                    "N/A",
                    views
            );

            appReportModelList.add(reportModel);
        }

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

                        HashMap<String, Long> totalDurationMap = new HashMap<>();
                        HashMap<String, Integer> sessionCountMap = new HashMap<>();

                        for (QueryDocumentSnapshot doc : value) {
                            String activityName = doc.getString("activity_name");
                            Long sessionDuration = doc.getLong("session_duration");

                            totalDurationMap.put(activityName, totalDurationMap.getOrDefault(activityName, 0L) + sessionDuration);

                            sessionCountMap.put(activityName, sessionCountMap.getOrDefault(activityName, 0) + 1);
                        }

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

                long averageDuration = totalDuration / sessionCount;
                String averageDurationFormatted = formatDuration(averageDuration);

                reportModel.setAverageEngagementTimePerActiveUser(averageDurationFormatted);
            }
        }

        appReportAdapter.notifyDataSetChanged();
    }

    private String formatDuration(long durationMillis) {
        long minutes = (durationMillis / 1000) / 60;
        long seconds = (durationMillis / 1000) % 60;
        return String.format("%dm %02ds", minutes, seconds);
    }

//    private void listenForUserFlowRealTime() {
//        listenerRegistration = db.collection("user_flow")
//                .whereEqualTo("previous_activity", "HomeFragment")
//                .addSnapshotListener((value, error) -> {
//                    if (error != null) {
//                        Log.d("Firestore", "Error getting documents: ", error);
//                        return;
//                    }
//
//                    if (value != null && !value.isEmpty()) {
//                        HashMap<String, Integer> userFlowMap = new HashMap<>();
//
//                        for (QueryDocumentSnapshot document : value) {
//                            String nextActivity = document.getString("next_activity");
//
//                            userFlowMap.put(nextActivity, userFlowMap.getOrDefault(nextActivity, 0) + 1);
//                        }
//                        displayUserFlowChart(userFlowMap, value.size());
//                    }
//                });
//    }

    private void updateUserFlowRealTime(String activityName) {
        // Unregister the old Firestore listener if needed
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }

        listenerRegistration = db.collection("user_flow")
                .whereEqualTo("previous_activity", activityName)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.d("Firestore", "Error getting documents: ", error);
                        return;
                    }

                    if (value != null && !value.isEmpty()) {
                        HashMap<String, Integer> userFlowMap = new HashMap<>();

                        for (QueryDocumentSnapshot document : value) {
                            String nextActivity = document.getString("next_activity");
                            userFlowMap.put(nextActivity, userFlowMap.getOrDefault(nextActivity, 0) + 1);
                        }
                        displayUserFlowChart(userFlowMap, value.size(), activityName);
                    }
                });
    }



    private void displayUserFlowChart(HashMap<String, Integer> userFlowMap, int totalFlows, String activityName) {
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : userFlowMap.entrySet()) {
            pieEntries.add(new PieEntry(((float) entry.getValue() / totalFlows) * 100, entry.getKey()));
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "User Flow from " + activityName);
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setValueTextSize(12f);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.setDrawSliceText(false);
        pieChart.setDescription(null);

        pieChart.setCenterText(activityName);
        pieChart.setCenterTextSize(18f);
        pieChart.setCenterTextColor(Color.BLACK);
        pieChart.setDrawCenterText(true);

        pieChart.invalidate(); // Refresh the chart
    }


    @Override
    protected void onResume() {
        super.onResume();
        listenForActiveUsersRealTime();
        listenForActivityViewsRealTime();
        listenForActivitySessionsRealTime();
//        listenForUserFlowRealTime();
    }

}