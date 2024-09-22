package com.example.smartstorageorganizer;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NotificationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        // Receiving intent data (if any)
        String data = getIntent().getStringExtra("data_key");  // "data_key" should match the key used in the sending activity

        // Display the data in a TextView
        TextView textView = findViewById(R.id.textView_message);
        if (data != null) {
            textView.setText(data);
        }


    }
}
