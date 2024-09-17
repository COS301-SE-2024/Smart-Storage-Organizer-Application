package com.example.smartstorageorganizer.ui.settings;

import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.smartstorageorganizer.MyGLRenderer;
import com.example.smartstorageorganizer.MyGLSurfaceView;
import com.example.smartstorageorganizer.ProfileManagementActivity;
import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.ReportsActivity;
import com.example.smartstorageorganizer.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;
    private GLSurfaceView glSurfaceView;
    private MyGLRenderer renderer;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize GLSurfaceView
//        glSurfaceView = new MyGLSurfaceView(getActivity());
//

        glSurfaceView = root.findViewById(R.id.glSurfaceView);
        renderer = new MyGLRenderer();
        glSurfaceView.setEGLContextClientVersion(2); // Use OpenGL ES 2.0
        glSurfaceView.setRenderer(renderer);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        // Add GLSurfaceView to the layout
//        container.addView(glSurfaceView);

        Button profile = root.findViewById(R.id.profile);
        Button color = root.findViewById(R.id.color);
        Button renderButton = root.findViewById(R.id.render_button);

        profile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProfileManagementActivity.class);
            startActivity(intent);
        });

        color.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ReportsActivity.class);
            startActivity(intent);
        });

        renderButton.setOnClickListener(v -> {
            // Trigger rendering of the 3D model
            glSurfaceView.requestRender();
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}