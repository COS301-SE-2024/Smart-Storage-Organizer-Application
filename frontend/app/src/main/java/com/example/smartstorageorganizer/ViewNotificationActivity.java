package com.example.smartstorageorganizer;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.smartstorageorganizer.databinding.ActivityViewNotificationBinding;

public class ViewNotificationActivity extends AppCompatActivity {

    private TextView titleTextView, messageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notification);

        // Initialize views
        titleTextView = findViewById(R.id.textView_title);
        messageTextView = findViewById(R.id.textView_message);

        // Get data from the intent
        Intent intent = getIntent();
        if (intent != null) {
            String title = intent.getStringExtra("notification_title");
            String message = intent.getStringExtra("notification_message");

            // Display the data
            titleTextView.setText(title);
            messageTextView.setText(message);
        }
    }
}
