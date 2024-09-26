package com.example.smartstorageorganizer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.activity.EdgeToEdge;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.amplifyframework.core.Amplify;
import com.google.firebase.FirebaseApp; // Import Firebase
import com.google.firebase.messaging.FirebaseMessaging; // Import Firebase Messaging

import java.util.concurrent.CompletableFuture;
import com.example.smartstorageorganizer.adapters.SlidePagerAdapter;
import android.Manifest;

public class MainActivity extends AppCompatActivity {
    private static final String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.VIBRATE,
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.FOREGROUND_SERVICE_DATA_SYNC
    };

    private static final int REQUEST_CODE = 100;

    private static final String PREFS_NAME = "onboarding_prefs";
    private static final String KEY_ONBOARDING_COMPLETE = "onboarding_complete";

    private ViewPager2 viewPager;
    private LinearLayout dotsLayout;
    private int numPages = 6;
    private ImageView[] dots;
    private TextView nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setupWindowInsets();
        requestPermissions();

        // Check if onboarding is already completed
        if (isOnboardingCompleted()) {
            // If completed, go directly to the HomeActivity
            Intent intent = new Intent(MainActivity.this, LandingActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
//
        viewPager = findViewById(R.id.viewPager);
        dotsLayout = findViewById(R.id.dots_layout);
        nextButton = findViewById(R.id.next);
//
        SlidePagerAdapter pagerAdapter = new SlidePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        Log.d("MainActivity", "Firebase initialized in MainActivity.");

        // Optionally retrieve the FCM token for debugging
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w("MainActivity", "Fetching FCM registration token failed", task.getException());
                return;
            }
            // Get new FCM registration token
            String token = task.getResult();
            Log.i("MainActivity", "FCM Token: " + token);
        });

//        setupWindowInsets();
//        navigateAfterDelay();
//    }
        addDotsIndicator(0);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                addDotsIndicator(position);

                // Change "Next" button text on the last page
                if (position == numPages - 1) {
                    nextButton.setText("Get Started");
                } else {
                    nextButton.setText("Next");
                }
            }
        });

        findViewById(R.id.skip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGetStartedClicked();
            }
        });

        // Set the listener for the "Next" button
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = viewPager.getCurrentItem();
                if (currentItem < numPages - 1) {
                    // Move to the next page
                    viewPager.setCurrentItem(currentItem + 1);
                } else {
                    // If on the last page, proceed to GetStartedActivity
                    onGetStartedClicked();
                }
            }
        });
    }

    private boolean isOnboardingCompleted() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_ONBOARDING_COMPLETE, false);
    }

    private void setOnboardingCompleted() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_ONBOARDING_COMPLETE, true);
        editor.apply();
    }

    private void addDotsIndicator(int position) {
        dots = new ImageView[numPages];
        dotsLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            dots[i].setVisibility(View.VISIBLE);
            dots[i].setImageResource(R.drawable.dot_inactive);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            dots[i].setLayoutParams(params);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[position].setImageResource(R.drawable.dot_active);
        }
    }

    public void onGetStartedClicked() {
        // Set the onboarding completed flag
        setOnboardingCompleted();

        // Navigate to the HomeActivity
        Intent intent = new Intent(MainActivity.this, GetStartedActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToHome() {
        Intent intent = new Intent(MainActivity.this, GetStartedActivity.class);
        startActivity(intent);
        finish();
    }

    private void resetOnboarding() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_ONBOARDING_COMPLETE, false);
        editor.apply();
    }

    private void requestPermissions() {
        // Check each permission
        boolean allPermissionsGranted = true;
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }

        // If all permissions are granted, proceed with your functionality
        if (allPermissionsGranted) {
            // All permissions granted, proceed with your app logic
//            Toast.makeText(this, "All permissions granted", Toast.LENGTH_SHORT).show();
        } else {
            // Request the permissions
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
//                Toast.makeText(this, "All permissions granted", Toast.LENGTH_SHORT).show();
                // Proceed with your app logic
            } else {
//                Toast.makeText(this, "Some permissions were denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

