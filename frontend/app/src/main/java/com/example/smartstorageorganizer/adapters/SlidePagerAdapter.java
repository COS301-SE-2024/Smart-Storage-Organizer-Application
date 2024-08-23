package com.example.smartstorageorganizer.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.ui.SlideFragment;

public class SlidePagerAdapter extends FragmentStateAdapter {

    private static final int NUM_PAGES = 6;

    public SlidePagerAdapter(@NonNull FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        boolean isLastSlide = (position == NUM_PAGES - 1);
        switch (position) {
            case 0:
                return SlideFragment.newInstance("Welcome to Your Smart Storage Solution", "Say goodbye to clutter and lost items! Our app helps you organize your belongings with ease and find them in seconds. Let’s get started.", R.drawable.welcome, isLastSlide);
            case 1:
                return SlideFragment.newInstance("Effortless Organization", "Easily label and categorize your items using QR codes and color coding. Manage everything in one place, hassle-free.", R.drawable.organize, isLastSlide);
            case 2:
                return SlideFragment.newInstance("Find Anything, Anytime", "Use text or voice search to locate your items quickly. Our AI-powered search understands your needs and finds exactly what you’re looking for.", R.drawable.search, isLastSlide);
            case 3:
                return SlideFragment.newInstance("Secure & Reliable", "Your data is safe with us. We ensure robust security measures and store everything securely, even when you’re offline.", R.drawable.secure, isLastSlide);
            case 4:
                return SlideFragment.newInstance("Stay Informed", "Receive real-time updates and alerts about your items. Never miss a thing with our push notifications.", R.drawable.notification, isLastSlide);
            case 5:
                return SlideFragment.newInstance("Ready to Organize?", "Start adding your items now and enjoy a clutter-free life. Tap below to begin organizing!", R.drawable.ready, isLastSlide);
            default:
                return SlideFragment.newInstance("Welcome", "This is your smart storage solution!", R.drawable.welcome, isLastSlide);
        }
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}

