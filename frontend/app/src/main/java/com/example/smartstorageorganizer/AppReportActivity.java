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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

        loadInitialData();

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

    @Override
    protected void onResume() {
        super.onResume();
        listenForActiveUsersRealTime();
    }

}