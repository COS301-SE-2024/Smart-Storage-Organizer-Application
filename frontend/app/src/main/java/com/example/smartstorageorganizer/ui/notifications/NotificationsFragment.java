package com.example.smartstorageorganizer.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.List;

public class NotificationsFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<AppNotification> notifications;
    private NotificationsViewModel notificationsViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        notificationsViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);

        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        recyclerView = root.findViewById(R.id.recycler_view_notifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Button buttonTransactions = root.findViewById(R.id.button_transactions);
        Button buttonMessages = root.findViewById(R.id.button_messages);
        Button buttonOffers = root.findViewById(R.id.button_offers);
        TextView textSlideshow = root.findViewById(R.id.text_slideshow);

        buttonTransactions.setOnClickListener(v -> fetchNotifications("transactions"));
        buttonMessages.setOnClickListener(v -> fetchNotifications("messages"));
        buttonOffers.setOnClickListener(v -> fetchNotifications("offers"));

        notifications = new ArrayList<AppNotification>();
        adapter = new NotificationAdapter(notifications);
        recyclerView.setAdapter(adapter);

        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textSlideshow::setText);
        return root;
    }

    private void fetchNotifications(String type) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(type);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notifications.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    AppNotification notification = snapshot.getValue(AppNotification.class);
                    if (notification != null) {
                        notifications.add(notification);
                    }
                }
                // Sort notifications by date (most recent first)
                Collections.sort(notifications, Comparator.comparing(AppNotification::getDate).reversed());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }
}
