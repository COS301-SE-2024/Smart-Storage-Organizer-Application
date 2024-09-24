package com.example.smartstorageorganizer;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class NotificationsActivity extends AppCompatActivity {
    EditText ed1,ed2,ed3;
    Button b1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        ed1= findViewById(R.id.editText);
        ed2= findViewById(R.id.editText2);
        ed3= findViewById(R.id.editText3);
        b1= findViewById(R.id.button);

        b1.setOnClickListener(v -> {
            String tittle=ed1.getText().toString().trim();
            String subject=ed2.getText().toString().trim();
            String body=ed3.getText().toString().trim();

            NotificationManager notif=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notify=new Notification.Builder
                    (getApplicationContext()).setContentTitle(tittle).setContentText(body).
                    setContentTitle(subject).setSmallIcon(R.drawable.ic_notification).build();

            notify.flags |= Notification.FLAG_AUTO_CANCEL;
            notif.notify(0, notify);
        });

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
//                    @Override
//                    public void onComplete(@NonNull Task<String> task) {
//                        if (!task.isSuccessful()) {
//                            return;
//                        }
//
//                        // Get the FCM registration token
//                        String token = task.getResult();
//
//                        // Display the token in a Toast
//                        Toast.makeText(getBaseContext(), token, Toast.LENGTH_LONG).show();
//
//                        // Copy the token to the clipboard
//                        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//                        ClipData clipData = ClipData.newPlainText("text", token);
//                        clipboardManager.setPrimaryClip(clipData);
//                    }
//                });
//            }
//        });


//        Button transactions = findViewById(R.id.button_transactions);
//        Button messages = findViewById(R.id.button_messages);
//        Button offers = findViewById(R.id.button_offers);
//
//        transactions.setOnClickListener(v -> {
//            Toast.makeText(NotificationsActivity.this, "Transactions clicked", Toast.LENGTH_SHORT).show();
//            // Handle transactions click
//        });
//
//        messages.setOnClickListener(v -> {
//            Toast.makeText(NotificationsActivity.this, "Messages clicked", Toast.LENGTH_SHORT).show();
//            // Handle messages click
//        });
//
//        offers.setOnClickListener(v -> {
//            Toast.makeText(NotificationsActivity.this, "Offers for you clicked", Toast.LENGTH_SHORT).show();
//            // Handle offers click
//        });
    }
}
