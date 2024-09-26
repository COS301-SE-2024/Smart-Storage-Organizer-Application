package com.example.smartstorageorganizer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.core.Amplify;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.Utils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import yuku.ambilwarna.AmbilWarnaDialog;

public class AddColorCodeActivity extends BaseActivity  {
    public View mColorPreview;

    public int mDefaultColor;
    public TextInputEditText titleEditText;
    public TextInputEditText descriptionEditText;
    public String currentEmail;
    MyAmplifyApp app;
    private long startTime;
    LottieAnimationView loadingScreen;
    LinearLayout mainLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_color_code);

        app = (MyAmplifyApp) getApplicationContext();

        mainLayout = findViewById(R.id.mainLayout);
        loadingScreen = findViewById(R.id.loadingScreen);
        Button mPickColorButton = findViewById(R.id.pick_color_button);
        Button addColorCodeButton = findViewById(R.id.add_colorcode_button);
        mColorPreview = findViewById(R.id.preview_selected_color);
        titleEditText = findViewById(R.id.colorCodeName);
        descriptionEditText = findViewById(R.id.colorCodeDescription);

        mDefaultColor = 0;

        mPickColorButton.setOnClickListener(v -> openColorPickerDialogue());
        addColorCodeButton.setOnClickListener(v -> {
            String color = convertIntToHexColor(mDefaultColor);
            String titleInput = titleEditText.getText().toString().trim();
            String descriptionInput = descriptionEditText.getText().toString().trim();

            if (validateForm(titleInput, descriptionInput)) {
                mainLayout.setVisibility(View.GONE);
                loadingScreen.setVisibility(View.VISIBLE);
                addNewColorCode(color, titleInput, descriptionInput, app.getOrganizationID());
            }
        });
    }

    public boolean validateForm(String title, String description) {
        if (TextUtils.isEmpty(title)) {
            this.titleEditText.setError("Title is required.");
            this.titleEditText.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(description)) {
            this.descriptionEditText.setError("Description is required.");
            this.descriptionEditText.requestFocus();
            return false;
        }
        return true;
    }

    public void openColorPickerDialogue() {
        final AmbilWarnaDialog colorPickerDialogue = new AmbilWarnaDialog(this, mDefaultColor,
                new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        // leave this function body as
                        // blank, as the dialog
                        // automatically closes when
                        // clicked on cancel button
                    }

                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        mDefaultColor = color;
                        mColorPreview.setBackgroundColor(mDefaultColor);
                    }
                });
        colorPickerDialogue.show();
    }

    String convertIntToHexColor(int color) {
        return String.format("#%06X", (0xFFFFFF & color));
    }

    public void addNewColorCode(String colorCode, String title, String description, String organizationId) {
        Utils.addColourGroup(colorCode, title, description, app.getEmail(), app.getOrganizationID(), this, new OperationCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (Boolean.TRUE.equals(result)) {
                    showToast("Color Code added successfully");
                    navigateToHome();
                }
            }

            @Override
            public void onFailure(String error) {
                showToast("Failed to add category: " + error);
            }
        });
    }

    public void showToast(String message) {
        mainLayout.setVisibility(View.VISIBLE);
        loadingScreen.setVisibility(View.GONE);
        Toast.makeText(AddColorCodeActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    public void navigateToHome() {
        Intent intent = new Intent(AddColorCodeActivity.this, HomeActivity.class);
        logUserFlow("HomeFragment");
        startActivity(intent);
        finish();
    }

    private void logActivityView(String activityName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = app.getEmail();

        Map<String, Object> activityView = new HashMap<>();
        activityView.put("user_id", userId);
        activityView.put("activity_name", activityName);
        activityView.put("view_time", new Timestamp(new Date()));

        db.collection("activity_views")
                .add(activityView)
                .addOnSuccessListener(documentReference -> Log.d("Firestore", "Activity view logged."))
                .addOnFailureListener(e -> Log.w("Firestore", "Error logging activity view", e));
    }

    private void logSessionDuration(String activityName, long sessionDuration) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = app.getEmail();

        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("user_id", userId);
        sessionData.put("activity_name", activityName);
        sessionData.put("session_duration", sessionDuration); // Duration in milliseconds

        db.collection("activity_sessions")
                .add(sessionData)
                .addOnSuccessListener(documentReference -> Log.d("Firestore", "Session duration logged."))
                .addOnFailureListener(e -> Log.w("Firestore", "Error logging session duration", e));
    }
    public void logUserFlow(String toActivity) {
        long sessionDuration = System.currentTimeMillis() - startTime;
        logSessionDuration("AddColorCodeActivity", (sessionDuration));
        long transitionTime = System.currentTimeMillis();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = app.getEmail();

        Map<String, Object> userFlowData = new HashMap<>();
        userFlowData.put("user_id", userId);
        userFlowData.put("previous_activity", "AddColorCodeActivity");
        userFlowData.put("next_activity", toActivity);
        userFlowData.put("transition_time", new Timestamp(new Date(transitionTime)));

        db.collection("user_flow")
                .add(userFlowData)
                .addOnSuccessListener(documentReference -> Log.d("Firestore", "User flow logged."))
                .addOnFailureListener(e -> Log.w("Firestore", "Error logging user flow", e));
    }

//    public void logUser

    @Override
    public void onStart() {
        super.onStart();
        logActivityView("AddColorCodeActivity");
    }

    @Override
    public void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
