package com.example.smartstorageorganizer.ui.notifications;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstorageorganizer.MyFirebaseMessagingService;
import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.adapters.NotificationAdapter;
import com.example.smartstorageorganizer.databinding.FragmentNotificationsBinding;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private TextView notificationTextView;
    private BroadcastReceiver notificationReceiver;
    private RecyclerView recyclerView;
    private NotificationAdapter notificationsAdapter;
    private List<String> notificationList = new ArrayList<>();
    private MyFirebaseMessagingService messagingService;

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        notificationTextView = root.findViewById(R.id.notification_text);

        recyclerView = root.findViewById(R.id.recycler_view_notifications);

        // Set up RecyclerView with LayoutManager and Adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        notificationsAdapter = new NotificationAdapter(notificationList);
        recyclerView.setAdapter(notificationsAdapter);

        messagingService = new MyFirebaseMessagingService();
        messagingService.initOneSignal();

        notificationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra("message");
                if (message != null) {
                    notificationList.add(message);
                    notificationsAdapter.notifyDataSetChanged();
                }
            }
        };
        requireContext().registerReceiver(notificationReceiver, new IntentFilter("com.example.smartstorageorganizer.NOTIFICATION"), Context.RECEIVER_NOT_EXPORTED);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Unregister the receiver when the view is destroyed
        requireContext().unregisterReceiver(notificationReceiver);
    }

    // BroadcastReceiver to update the UI when a notification is received
    private class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            notificationTextView.setText(message);  // Update the TextView with the received message
        }
    }
}
