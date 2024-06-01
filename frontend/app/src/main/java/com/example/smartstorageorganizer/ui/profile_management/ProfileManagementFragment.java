package com.example.smartstorageorganizer.ui.profile_management;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.smartstorageorganizer.EditProfileActivity;
import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.databinding.FragmentProfileManagementBinding;

import java.util.Objects;

public class ProfileManagementFragment extends Fragment {

    private FragmentProfileManagementBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileManagementViewModel profileManagementViewModelViewModel =
                new ViewModelProvider(this).get(ProfileManagementViewModel.class);

        binding = FragmentProfileManagementBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        AppCompatButton editProfileButton = root.findViewById(R.id.editProfileButton);
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

//        editProfileButton.setOnClickListener(v -> {
//            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_home);
//            navController.navigate(R.id.action_profileFragment_to_editProfileFragment);
//        });

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