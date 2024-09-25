package com.example.smartstorageorganizer.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.core.Amplify;
import com.example.smartstorageorganizer.NotificationModel;
import com.example.smartstorageorganizer.NotificationsActivity;
import com.example.smartstorageorganizer.NotificationsAdapter;
import com.example.smartstorageorganizer.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NotificationsFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationsAdapter adapter;
    private List<NotificationModel> notificationList;
    private Button sendNotificationButton; // Declare the button

    private static final String ONESIGNAL_APP_ID = "152f0f5c-d21d-4e43-89b1-5e02acc42abe";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view_messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize the notification list and adapter
        notificationList = new ArrayList<>();
        adapter = new NotificationsAdapter(requireContext(), notificationList);
        recyclerView.setAdapter(adapter);

        // Initialize the send notification button
        sendNotificationButton = view.findViewById(R.id.send_notification_button);
        sendNotificationButton.setOnClickListener(v -> {
            // Start NotificationActivity
            Intent intent = new Intent(requireContext(), NotificationsActivity.class);
            startActivity(intent);
        });

        // Check if the user is signed in before showing notifications
        checkSessionState();

        // Load notifications (from OneSignal)
        loadNotifications(); // The method will fetch from OneSignal

        // Receiving bundle data (if any)
        Bundle arguments = getArguments();
        if (arguments != null) {
            String data = arguments.getString("data_key");
            if (data != null && !data.isEmpty()) {
                // Handle the incoming data
                notificationList.add(new NotificationModel("New Notification", data, "Now"));
                adapter.notifyDataSetChanged(); // Refresh the RecyclerView to show new data
            }
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Check session state when resuming the fragment
        checkSessionState();
    }

    private void checkSessionState() {
        Amplify.Auth.fetchAuthSession(
                result -> {
                    if (!result.isSignedIn()) {
                        // Redirect to login if the user is not signed in
                        Toast.makeText(requireContext(), "Please log in to view notifications", Toast.LENGTH_SHORT).show();
                        // Navigate to login fragment or activity
                    }
                },
                error -> {
                    Log.e("AuthSession", "Failed to check auth session: " + error);
                    Toast.makeText(requireContext(), "Unable to verify session. Please try again.", Toast.LENGTH_LONG).show();
                }
        );
    }

    // Load notifications from OneSignal API
    private void loadNotifications() {
        String API_URL = "https://onesignal.com/api/v1/notifications?app_id=152f0f5c-d21d-4e43-89b1-5e02acc42abe&limit=10";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Basic ZGFlMzY3MjktOGIyOC00ZDI4LTg1MzQtMWE5NjY2ZDJkOGZh")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // Handle failure (optional: show a message to the user)
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
//                    Log.d("API Response", response.body().string());

                    String jsonData = response.body().string();
                    Log.d("API Response", jsonData);

                    try {
                        JSONObject jsonObject = new JSONObject(jsonData);
                        JSONArray notifications = jsonObject.getJSONArray("notifications");

                        // Clear the existing list to avoid duplicates
                        notificationList.clear();

                        // Parse the notification details
                        for (int i = 0; i < notifications.length(); i++) {
                            JSONObject notification = notifications.getJSONObject(i);
                            String title = notification.optString("headings", "No Title");
                            String message = notification.optString("contents", "No Message");
                            String date = notification.optString("delivery_time_of_day", "No Date");

                            // Add to notification list
                            String currentDate = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault()).format(new Date());

                            notificationList.add(new NotificationModel(title, message, currentDate));
                        }

                        // Notify the adapter (make sure this is done on the UI thread)
                        requireActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}

