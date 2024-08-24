package com.example.smartstorageorganizer;

import android.app.Notification;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstorageorganizer.adapters.NotificationAdapter;
import com.example.smartstorageorganizer.model.AppNotification;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

//public class NotificationsActivity extends AppCompatActivity {
//
//    private TextView messageTextView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_notifications);
//
//        messageTextView = findViewById(R.id.textView_message);
//
//        Intent intent = getIntent();
//        String message = intent.getStringExtra("message");
//
//        if (message != null) {
//            displayMessage(message);
//        } else {
//            Toast.makeText(this, "No message received", Toast.LENGTH_SHORT).show();
//        }
//
//        // Existing buttons handling logic...
//    }
//
//    public void displayMessage(String message) {
//        messageTextView.setText(message);
//    }
//}

public class NotificationsActivity extends AppCompatActivity{
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<AppNotification> notifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        recyclerView = findViewById(R.id.recycler_view_messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        Button buttonTransactions = findViewById(R.id.button_transactions);
//        Button buttonMessages = findViewById(R.id.button_messages);
//        Button buttonOffers = findViewById(R.id.button_offers);

        notifications = new ArrayList<>();
        adapter = new NotificationAdapter(notifications);
        recyclerView.setAdapter(adapter);

//        buttonTransactions.setOnClickListener(v -> fetchNotifications("transactions"));
//        buttonMessages.setOnClickListener(v -> fetchNotifications("messages"));
//        buttonOffers.setOnClickListener(v -> fetchNotifications("offers"));

        // Fetch all notifications on startup
        fetchNotifications("all");
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
//                Collections.sort(notifications( Comparator.comparing(Notification::getDate).reversed());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }


}
