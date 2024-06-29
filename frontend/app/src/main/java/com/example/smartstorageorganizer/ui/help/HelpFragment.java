package com.example.smartstorageorganizer.ui.help;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.smartstorageorganizer.HelpActivity;
import com.example.smartstorageorganizer.ManualActivity;
import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.databinding.FragmentHelpBinding;
import com.example.smartstorageorganizer.databinding.FragmentSettingsBinding;

public class HelpFragment extends Fragment {

    public static HelpFragment newInstance() {
        return new HelpFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        com.example.smartstorageorganizer.databinding.FragmentHelpBinding binding = FragmentHelpBinding.inflate(inflater, container, false);

        View root = binding.getRoot();

        Button help = root.findViewById(R.id.help);
        Button manual = root.findViewById(R.id.manual);

        help.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), HelpActivity.class);
            startActivity(intent);
        });

        manual.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), ManualActivity.class);
            startActivity(intent);
        });

        return binding.getRoot();
    }
}