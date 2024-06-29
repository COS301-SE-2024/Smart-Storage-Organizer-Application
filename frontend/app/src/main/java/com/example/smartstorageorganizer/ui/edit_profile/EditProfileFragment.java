package com.example.smartstorageorganizer.ui.edit_profile;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.smartstorageorganizer.databinding.FragmentEditProfileBinding;

public class EditProfileFragment extends Fragment {

    public static EditProfileFragment newInstance() {
        return new EditProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        com.example.smartstorageorganizer.databinding.FragmentEditProfileBinding binding = FragmentEditProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}