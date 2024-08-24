package com.example.smartstorageorganizer.ui.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstorageorganizer.MyFirebaseMessagingService;
import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.adapters.NotificationAdapter;
import com.example.smartstorageorganizer.model.AppNotification;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<AppNotification> notifications;
    private NotificationsViewModel notificationsViewModel;
    private BroadcastReceiver notificationReceiver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        notificationsViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);

        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        recyclerView = root.findViewById(R.id.recycler_view_notifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

//        Button buttonTransactions = root.findViewById(R.id.button_transactions);
//        Button buttonMessages = root.findViewById(R.id.button_messages);
//        Button buttonOffers = root.findViewById(R.id.button_offers);
        TextView textSlideshow = root.findViewById(R.id.text_slideshow);
//
//        buttonTransactions.setOnClickListener(v -> fetchNotifications("transactions"));
//        buttonMessages.setOnClickListener(v -> fetchNotifications("messages"));
//        buttonOffers.setOnClickListener(v -> fetchNotifications("offers"));

        notifications = new ArrayList<>();
        adapter = new NotificationAdapter(notifications);
        recyclerView.setAdapter(adapter);

        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textSlideshow::setText);
//        fetchNotifications();
//        processNotification("This is a test notification.");
//
//        processNotification("New notification after the test.");
//        simulateFirebaseMessage();
        setupBroadcastReceiver();
        return root;
    }

//    private void simulateFirebaseMessage() {
//        MyFirebaseMessagingService messagingService = new MyFirebaseMessagingService();
//        messagingService.simulateMessageReceived("Test Title", "This is a test message.");
//    }

    private void setupBroadcastReceiver() {
        notificationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (MyFirebaseMessagingService.ACTION_NEW_NOTIFICATION.equals(intent.getAction())) {
                    String message = intent.getStringExtra("message");
                    processNotification(message);
                }
            }
        };
        IntentFilter filter = new IntentFilter(MyFirebaseMessagingService.ACTION_NEW_NOTIFICATION);
        getContext().registerReceiver(notificationReceiver, filter);
    }

    public void processNotification(String message) {
        // Simulate adding a new notification
        AppNotification newNotification = new AppNotification(message, new Date(), false);
        notifications.add(0, newNotification); // Add at the top of the list

        // Update the RecyclerView
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (notificationReceiver != null) {
            getContext().unregisterReceiver(notificationReceiver);
        }
    }

//    private void fetchNotifications() {
//        // Get a reference to the Firebase database node specified by 'type'
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("messages");
//
//        // Add a listener to read data from the database
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                // Clear the existing notifications list to avoid duplicates
//                if (notifications != null) {
//                    notifications.clear();
//                } else {
//                    notifications = new ArrayList<>();
//                }
//
//                // Loop through the children of the database snapshot
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    // Attempt to deserialize each child into an AppNotification object
//                    AppNotification notification = snapshot.getValue(AppNotification.class);
//
//                    // Check if deserialization was successful
//                    if (notification != null) {
//                        notifications.add(notification);
//                    }
//                }
//
//                // Sort notifications by date (most recent first)
//                Collections.sort(notifications, Comparator.comparing(AppNotification::getDate).reversed());
//
//                // Update the RecyclerView on the main thread
//                if (getActivity() != null) {
//                    getActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Log the error or notify the user, as appropriate
//                Log.e("fetchNotifications", "DatabaseError: " + databaseError.getMessage());
//            }
//        });
//    }

}
