package com.example.smartstorageorganizer.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.smartstorageorganizer.ui.users.AllFragment;
import com.example.smartstorageorganizer.ui.users.RequestFragment;

import java.util.HashMap;
import java.util.Map;

public class ViewUsersAdapter extends FragmentStateAdapter {

    private final Map<Integer, Fragment> fragmentMap = new HashMap<>();

    public ViewUsersAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new RequestFragment();
                break;
            case 1:
                fragment = new AllFragment();
                break;
            default:
                fragment = new RequestFragment();
        }
        fragmentMap.put(position, fragment);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public void refreshFragment(int position) {
        Fragment fragment = fragmentMap.get(position);
        if (fragment != null) {
            fragmentMap.remove(position);
            notifyItemChanged(position);
        }
    }
}
