package com.example.smartstorageorganizer.ui.profile_management;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartstorageorganizer.databinding.FragmentProfileManagementBinding;

public class ProfileManagementFragment extends Fragment {

    private FragmentProfileManagementBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileManagementViewModel galleryViewModel =
                new ViewModelProvider(this).get(ProfileManagementViewModel.class);

        binding = FragmentProfileManagementBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.textGallery;
//        galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}