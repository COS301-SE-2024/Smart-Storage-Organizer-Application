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
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.databinding.FragmentNotificationsBinding;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private TextView notificationTextView;
    private NotificationReceiver notificationReceiver;

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        notificationTextView = root.findViewById(R.id.notification_text);

        // Register the receiver to listen for notifications
        notificationReceiver = new NotificationReceiver();
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
