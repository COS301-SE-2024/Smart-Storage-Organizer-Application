package com.example.smartstorageorganizer.ui.notifications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.core.Amplify;
import com.example.smartstorageorganizer.NotificationModel;
import com.example.smartstorageorganizer.NotificationsAdapter;
import com.example.smartstorageorganizer.R;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationsAdapter adapter;
    private List<NotificationModel> notificationList;

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

        // Check if the user is signed in before showing notifications
        checkSessionState();

        // Load notifications (from OneSignal or any other source)
        loadNotifications();

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
                        // Note: Use Fragment-based navigation instead of Intent
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
