package com.example.smartstorageorganizer.ui.notifications;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.smartstorageorganizer.MyAmplifyApp;
import com.example.smartstorageorganizer.NotificationsActivity;
import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.ViewNotificationActivity;
import com.example.smartstorageorganizer.NotificationsAdapter;
import com.example.smartstorageorganizer.NotificationModel;


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
    private MyAmplifyApp app;

    private static final String ONESIGNAL_APP_ID = "152f0f5c-d21d-4e43-89b1-5e02acc42abe";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        app = (MyAmplifyApp) requireActivity().getApplicationContext();

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view_messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize the notification list and adapter
        notificationList = new ArrayList<>();
//        adapter = new NotificationsAdapter(requireContext(), notificationList);


        // Set up the adapter with the click listener
        adapter = new NotificationsAdapter(requireContext(), notificationList, notification -> {
            // Handle the click event here
            Intent intent = new Intent(requireContext(), ViewNotificationActivity.class);
            intent.putExtra("notification_title", notification.getTitle());
            intent.putExtra("notification_message", notification.getMessage());
            intent.putExtra("notification_date", notification.getDate());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);



        // Check if the user is signed in before showing notifications
        checkSessionState();

        // Initialize the send notification button
//        sendNotificationButton.setOnClickListener(v -> {
//            Toast.makeText(requireContext(), "Button Clicked!", Toast.LENGTH_SHORT).show();
//            // Start NotificationActivity
//            Intent intent = new Intent(requireContext(), NotificationsActivity.class);
//            startActivity(intent);
//        });

        // Load notifications (from OneSignal)
        loadNotifications(); // The method will fetch from OneSignal

        // Receiving bundle data (if any)
        Bundle arguments = getArguments();
        if (arguments != null) {
            String data = arguments.getString("data_key");
            if (data != null && !data.isEmpty()) {
                // Handle the incoming data
                // Add a new notification with 'isRead' set to false (unread)
                notificationList.add(new NotificationModel("New Notification", data, "Now", false, "", ""));
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
        loadNotifications();
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
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Failed to load notifications", Toast.LENGTH_SHORT).show();
                });
            }


            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Get the response body as a string
                    String jsonData = response.body().string();
                    Log.d("API Response", jsonData);

                    try {
                        // Convert response string to JSON object
                        JSONObject jsonObject = new JSONObject(jsonData);
                        JSONArray notifications = jsonObject.getJSONArray("notifications");

//                        JSONArray filters = jsonObject.getJSONArray("filters");
//
                        requireActivity().runOnUiThread(() -> {
                            Log.i("Notification Request", "notifications: "+notifications.toString());
                            Log.i("Notification Request ", "jsonObject: "+jsonObject.toString());
                        });

                        // Clear the existing list to avoid duplicates
                        notificationList.clear();

                        // Parse the notification details
                        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Notifications", Context.MODE_PRIVATE);

                        for (int i = 0; i < notifications.length(); i++) {
                            JSONObject notification = notifications.getJSONObject(i);

                            JSONArray filters = notification.getJSONArray("filters");

                            requireActivity().runOnUiThread(() -> {
                                Log.i("Notification Request", "Filters: "+filters.toString());
//                                Log.i("Notification Request ", "jsonObject: "+jsonObject.toString());
                            });

                            JSONObject filterObjectOne = filters.getJSONObject(0);
                            JSONObject filterObjectTwo = filters.getJSONObject(1);

                            String userRole = filterObjectOne.getString("value");
                            String organizationId = filterObjectTwo.getString("value");




                            String title = cleanNotificationString(notification.optString("headings", "No Title"));
                            String message = cleanNotificationString(notification.optString("contents", "No Message"));
//                            String date = notification.optString("delivery_time_of_day", "No Date");

                            long queuedAt = notification.optLong("queued_at", 0);   // For the Org Manager
                            long sendAfter = notification.optLong("send_after", 0); // For the Org Manager
                            long completedAt = notification.optLong("completed_at", 0);
                            // Log the entire notification to debug
                            Log.d("Notification JSON", notification.toString());

                            String date = "No Date";
                            Boolean isRead;

                            if (completedAt != 0) {
                                Date deliveryDate = new Date(completedAt * 1000L); // Convert seconds to milliseconds
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
                                date = dateFormat.format(deliveryDate);
                                String key = app.getEmail() + "_" + dateFormat.format(deliveryDate);
                                isRead = sharedPreferences.getBoolean(key, false);
                            } else {
                                isRead = false;
                            }

                            // Fetch the read status for the specific user


                            // Add notification with read status
                            notificationList.add(new NotificationModel(title, message, date, isRead, userRole, organizationId));
                    }

                        // Notify the adapter (make sure this is done on the UI thread)
                        requireActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());

                    } catch (JSONException e) {
                        requireActivity().runOnUiThread(() -> {
                            Log.i("Notification Request", "notifications: "+e.toString());
                        });
                        e.printStackTrace();
                    }
                } else {
                    // Handle non-successful responses
                    requireActivity().runOnUiThread(() -> {
                        Log.i("Notification Request", "notifications: "+response.message());
                        Toast.makeText(requireContext(), "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                    });
                }
            }

            // Helper function to clean the notification string by removing "en" and braces
            private String cleanNotificationString(String input) {
                // Replace any occurrence of '{"en":' or '}'
                return input.replace("{\"en\":", "").replace("}", "").replace("\"", "");
            }

        });



    }
}