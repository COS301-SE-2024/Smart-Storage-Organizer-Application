package com.example.smartstorageorganizer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.core.Amplify;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NotificationsAdapter adapter;
    private List<NotificationModel> notificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recycler_view_messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the notification list and adapter
        notificationList = new ArrayList<>();
        adapter = new NotificationsAdapter(this, notificationList);
        recyclerView.setAdapter(adapter);

        // Check if the user is signed in before showing notifications
        checkSessionState();

        // Load notifications (from OneSignal or any other source)
        loadNotifications();

        // Receiving intent data (if any)
        String data = getIntent().getStringExtra("data_key");  // "data_key" should match the key used in the sending activity

        if (data != null && !data.isEmpty()) {
            // Optionally handle this incoming data
            notificationList.add(new NotificationModel("New Notification", data, "Now"));
            adapter.notifyDataSetChanged(); // Refresh the RecyclerView to show new data
        } else {
            // You can still show a placeholder message or load more data
//             textView.setText("No new notifications");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check session state when resuming the activity
        checkSessionState();
    }

    private void checkSessionState() {
        Amplify.Auth.fetchAuthSession(
                result -> {
                    if (!result.isSignedIn()) {
                        // Redirect to login if the user is not signed in
                        Intent intent = new Intent(NotificationsActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish(); // Ensure the NotificationsActivity is finished so the user can't go back to it
                    }
                },
                error -> {
                    Log.e("AuthSession", "Failed to check auth session: " + error);
                    Toast.makeText(NotificationsActivity.this, "Unable to verify session. Please try again.", Toast.LENGTH_LONG).show();
                }
        );
    }

    // Load notifications from OneSignal or any other data source
    private void loadNotifications() {
        // Add notifications to the list (You can fetch this from OneSignal or any data source)
        notificationList.add(new NotificationModel("Title 1", "Message 1", "Date 1"));
        notificationList.add(new NotificationModel("Title 2", "Message 2", "Date 2"));
        notificationList.add(new NotificationModel("Title 3", "Message 3", "Date 3"));

        // Notify the adapter that the data has changed
        adapter.notifyDataSetChanged();
    }
}
