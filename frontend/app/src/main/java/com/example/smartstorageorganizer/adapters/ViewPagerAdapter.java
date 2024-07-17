package com.example.smartstorageorganizer.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.smartstorageorganizer.ui.requests.ApprovedFragment;
import com.example.smartstorageorganizer.ui.requests.PendingFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new PendingFragment();
            case 1:
                return new ApprovedFragment();
            default:
                return new PendingFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

