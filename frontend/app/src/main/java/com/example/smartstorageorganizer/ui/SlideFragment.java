package com.example.smartstorageorganizer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smartstorageorganizer.GetStartedActivity;
import com.example.smartstorageorganizer.LandingActivity;
import com.example.smartstorageorganizer.R;

import java.util.Objects;

public class SlideFragment extends Fragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_TEXT = "text";
    private static final String ARG_IMAGE = "image";
    private static final String ARG_IS_LAST = "is_last";

    public static SlideFragment newInstance(String title, String text, int image, boolean isLastSlide) {
        SlideFragment fragment = new SlideFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_TEXT, text);
        args.putInt(ARG_IMAGE, image);
        args.putBoolean(ARG_IS_LAST, isLastSlide);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.slide_layout, container, false);

        TextView title = view.findViewById(R.id.slide_title);
        TextView text = view.findViewById(R.id.slide_text);
        ImageView image = view.findViewById(R.id.slide_image);
        Button getStartedButton = view.findViewById(R.id.get_started_button);

        if (getArguments() != null) {
            title.setText(getArguments().getString(ARG_TITLE));
            text.setText(getArguments().getString(ARG_TEXT));
            image.setImageResource(getArguments().getInt(ARG_IMAGE));

            if (getArguments().getBoolean(ARG_IS_LAST)) {
                getStartedButton.setVisibility(View.VISIBLE);
                getStartedButton.setOnClickListener(v -> {
                    // Navigate to the main part of the app
                    // For example:
                     startActivity(new Intent(getActivity(), GetStartedActivity.class));
                     requireActivity().finish();
                });
            }
        }

        return view;
    }
}
