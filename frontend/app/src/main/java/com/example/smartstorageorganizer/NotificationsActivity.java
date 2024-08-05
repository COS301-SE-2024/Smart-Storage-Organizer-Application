package com.example.smartstorageorganizer;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class NotificationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        TextView transactions = findViewById(R.id.transactions);
        TextView messages = findViewById(R.id.messages);
        TextView offers = findViewById(R.id.offers);

        transactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NotificationsActivity.this, "Transactions clicked", Toast.LENGTH_SHORT).show();
                // Handle transactions click
            }
        });

        messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NotificationsActivity.this, "Messages clicked", Toast.LENGTH_SHORT).show();
                // Handle messages click
            }
        });

        offers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NotificationsActivity.this, "Offers for you clicked", Toast.LENGTH_SHORT).show();
                // Handle offers click
            }
        });
    }
}
