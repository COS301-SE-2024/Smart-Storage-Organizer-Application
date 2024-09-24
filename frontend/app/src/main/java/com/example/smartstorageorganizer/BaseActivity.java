package com.example.smartstorageorganizer;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    public boolean hasInternetAccess() {
        try {
            HttpURLConnection urlConnection = (HttpURLConnection)
                    (new URL("https://www.google.com").openConnection());
            urlConnection.setRequestProperty("User-Agent", "ConnectionTest");
            urlConnection.setRequestProperty("Connection", "close");
            urlConnection.setConnectTimeout(1500); // Timeout if no internet
            urlConnection.connect();
            return (urlConnection.getResponseCode() == 200);
        } catch (IOException e) {
//            Log.e(TAG, "Error checking internet connection", e);
            return false;
        }
    }


    private AlertDialog noInternetDialog;

    public void showNoInternetDialog() {
        if (noInternetDialog == null || !noInternetDialog.isShowing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Connect to a network")
                    .setMessage("To use Smart Storage Organizer, turn on mobile data or connect to Wi-Fi.")
                    .setCancelable(false)
                    .setPositiveButton("OK", (dialog, which) -> {
                        finish();
                    });
            noInternetDialog = builder.create();
            noInternetDialog.show();
        }
    }


    public void dismissNoInternetDialog() {
        if (noInternetDialog != null && noInternetDialog.isShowing()) {
            noInternetDialog.dismiss();
        }
    }

    private BroadcastReceiver networkChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isNetworkConnected()) {
                dismissNoInternetDialog();  // If connected to the internet, dismiss the popup
            } else {
                showNoInternetDialog();  // If not connected to the internet, show the popup
            }
        }
    };

    public void checkInternetAccessInBackground() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            boolean hasInternet = hasInternetAccess();  // Run this on a background thread
            runOnUiThread(() -> {
                // Update the UI based on the result
                if (hasInternet) {
//                    Log.i(TAG, "Internet connection available");
                    dismissNoInternetDialog();
                } else {
                    showNoInternetDialog();
                }
            });
        });
    }

    @Override
    public void onStart() {
        super.onStart();
//        logActivityView("LoginActivity");

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, filter);

        checkInternetAccessInBackground();  // Call this to run the check off the main thread

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

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(networkChangeReceiver);
    }
}

