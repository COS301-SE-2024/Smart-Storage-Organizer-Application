package com.example.smartstorageorganizer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.core.Amplify;

public class NotificationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        // Check if the user is signed in before showing notifications
        checkSessionState();

        // Receiving intent data (if any)
        String data = getIntent().getStringExtra("data_key");  // "data_key" should match the key used in the sending activity

        // Display the data in a TextView
        TextView textView = findViewById(R.id.textView_message);
        if (data != null && !data.isEmpty()) {
            textView.setText(data);
        } else {
            textView.setText("No new notifications");
        }



    }
    @Override
    protected void onResume() {
        super.onResume();
        // Check session state when resuming the activity
        checkSessionState();
    }

    private void checkSessionState() {
        Amplify.Auth.fetchAuthSession(
                result -> {
                    if (!result.isSignedIn()) {
                        // Redirect to login if the user is not signed in
                        Intent intent = new Intent(NotificationsActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish(); // Ensure the NotificationsActivity is finished so the user can't go back to it
                    }
                },
                error->
                {
                    Log.e("AuthSession", "Failed to check auth session: " + error);
                    Toast.makeText(NotificationsActivity.this, "Unable to verify session. Please try again.", Toast.LENGTH_LONG).show();

                }
        );
    }
}
