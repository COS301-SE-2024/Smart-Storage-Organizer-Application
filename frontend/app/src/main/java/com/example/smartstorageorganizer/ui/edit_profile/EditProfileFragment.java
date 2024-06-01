package com.example.smartstorageorganizer.ui.edit_profile;

import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.databinding.FragmentEditProfileBinding;
import com.example.smartstorageorganizer.ui.notifications.NotificationsViewModel;


public class EditProfileFragment extends Fragment {

    private FragmentEditProfileBinding binding;
    public static EditProfileFragment newInstance() {
        return new EditProfileFragment();
    }

    private EditProfileViewModel mViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        EditProfileViewModel editProfileViewModel =
                new ViewModelProvider(this).get(EditProfileViewModel.class);

        binding = FragmentEditProfileBinding.inflate(inflater, container, false);
//        final TextView textView = binding.textEditProfile;
//        editProfileViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(EditProfileViewModel.class);
        // TODO: Use the ViewModel

    }

}