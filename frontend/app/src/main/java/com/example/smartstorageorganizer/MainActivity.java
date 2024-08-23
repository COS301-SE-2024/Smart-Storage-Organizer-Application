package com.example.smartstorageorganizer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.smartstorageorganizer.adapters.SlidePagerAdapter;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "onboarding_prefs";
    private static final String KEY_ONBOARDING_COMPLETE = "onboarding_complete";

    private ViewPager2 viewPager;
    private LinearLayout dotsLayout;
    private int numPages = 6;
    private ImageView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setupWindowInsets();

        // Check if onboarding is already completed
//        if (isOnboardingCompleted()) {
//            // If completed, go directly to the HomeActivity
//            navigateToHome();
//            return;
//        }

        setContentView(R.layout.activity_main);
//
        viewPager = findViewById(R.id.viewPager);
        dotsLayout = findViewById(R.id.dots_layout);
//
        SlidePagerAdapter pagerAdapter = new SlidePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        addDotsIndicator(0);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                addDotsIndicator(position);
            }
        });

        findViewById(R.id.skip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGetStartedClicked();
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
        navigateToHome();
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
//
//
//    private void setupWindowInsets() {
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//    }
}

