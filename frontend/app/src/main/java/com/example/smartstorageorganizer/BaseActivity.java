package com.example.smartstorageorganizer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    private static final long INACTIVITY_TIMEOUT = 60000; // 5 minutes in milliseconds (300,000)
    private Handler inactivityHandler;
    private Runnable inactivityCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the handler and runnable for inactivity
        inactivityHandler = new Handler(Looper.getMainLooper());
        inactivityCallback = new Runnable() {
            @Override
            public void run() {
                // Show popup after 5 minutes of inactivity
                showInactivityPopup();
            }
        };

        // Start the inactivity timer when the activity is created
        resetInactivityTimer();
    }

    @Override
    public void onUserInteraction() {
        // Called whenever the user interacts with the app (touch, key press, etc.)
        resetInactivityTimer();
    }

    private void resetInactivityTimer() {
        // Remove any previous callbacks and start the timer again
        inactivityHandler.removeCallbacks(inactivityCallback);
        inactivityHandler.postDelayed(inactivityCallback, INACTIVITY_TIMEOUT); // Set the delay for 5 minutes
    }

    private void showInactivityPopup() {
        // Create and show a dialog asking if the user is still there
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you still there?")
                .setMessage("It seems you've been inactive for a while. Are you still using the app?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // If user is still there, reset the inactivity timer
                        resetInactivityTimer();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle what happens when the user is not there (e.g., exit the app or log out)
                        finish(); // Optionally, you can close the app or perform another action
                    }
                })
                .setCancelable(false) // Make sure the user can't dismiss the dialog without choosing
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove the callback to avoid memory leaks when the activity is destroyed
        inactivityHandler.removeCallbacks(inactivityCallback);
    }

    @Override
    protected void onPause() {
        super.onPause();
        inactivityHandler.removeCallbacks(inactivityCallback); // Pause the timer
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetInactivityTimer(); // Restart the timer when the app comes back to the foreground
    }
}

