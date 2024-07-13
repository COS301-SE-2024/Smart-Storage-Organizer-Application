package com.example.smartstorageorganizer.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.smartstorageorganizer.ui.users.AllFragment;
import com.example.smartstorageorganizer.ui.users.RequestFragment;

public class ViewUsersAdapter extends FragmentStateAdapter {
    public ViewUsersAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new RequestFragment();
            case 1:
                return new AllFragment();
            default:
                return new RequestFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

