package com.example.smartstorageorganizer;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class AppReportActivity extends BaseActivity {
    FirebaseFirestore db;
    FirebaseAuth auth;
    MyAmplifyApp app;

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

        // Update user's last active time when the app opens
        updateActiveUser();
    }

    private void updateActiveUser() {
        String userId = "ezemakau@gmail.com";
        Map<String, Object> activeUserData = new HashMap<>();
        activeUserData.put("last_active_time", Timestamp.now());

        db.collection("active_users").document(userId)
                .set(activeUserData)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "User activity updated"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error updating user", e));
    }

    // Call this method to listen for real-time updates
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
                            activeUsersTextView.setText("Active Users: " + activeUsersCount);
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateActiveUser();  // Update user activity
        listenForActiveUsersRealTime();  // Listen for real-time active users
    }

}