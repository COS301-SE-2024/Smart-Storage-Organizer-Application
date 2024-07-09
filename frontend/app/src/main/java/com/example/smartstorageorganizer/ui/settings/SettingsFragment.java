package com.example.smartstorageorganizer.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartstorageorganizer.AddColorCodeActivity;
import com.example.smartstorageorganizer.CodeScannerActivity;
import com.example.smartstorageorganizer.HelpActivity;
import com.example.smartstorageorganizer.ItemDetailsActivity;
import com.example.smartstorageorganizer.ManualActivity;
import com.example.smartstorageorganizer.ProfileManagementActivity;
import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.ViewColorCodesActivity;
import com.example.smartstorageorganizer.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button profile = root.findViewById(R.id.profile);
        Button color = root.findViewById(R.id.color);

        profile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProfileManagementActivity.class);
            startActivity(intent);
        });

        color.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ItemDetailsActivity.class);
            startActivity(intent);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}