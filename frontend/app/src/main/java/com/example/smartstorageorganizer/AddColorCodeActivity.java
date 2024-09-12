package com.example.smartstorageorganizer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.core.Amplify;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.Utils;
import com.google.android.material.textfield.TextInputEditText;

import java.util.concurrent.CompletableFuture;

import yuku.ambilwarna.AmbilWarnaDialog;

public class AddColorCodeActivity extends BaseActivity  {
    public View mColorPreview;

    public int mDefaultColor;
    public TextInputEditText titleEditText;
    public TextInputEditText descriptionEditText;
    public String currentEmail;
    MyAmplifyApp app;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_color_code);

        app = (MyAmplifyApp) getApplicationContext();

        TextView gfgTextView = findViewById(R.id.gfg_heading);
        Button mPickColorButton = findViewById(R.id.pick_color_button);
        Button addColorCodeButton = findViewById(R.id.add_colorcode_button);
        mColorPreview = findViewById(R.id.preview_selected_color);
        titleEditText = findViewById(R.id.colorCodeName);
        descriptionEditText = findViewById(R.id.colorCodeDescription);

        getDetails();

        mDefaultColor = 0;

        mPickColorButton.setOnClickListener(v -> openColorPickerDialogue());
        addColorCodeButton.setOnClickListener(v -> {
            gfgTextView.setTextColor(mDefaultColor);
            String color = convertIntToHexColor(mDefaultColor);
            String titleInput = titleEditText.getText().toString().trim();
            String descriptionInput = descriptionEditText.getText().toString().trim();

            if (validateForm(titleInput, descriptionInput)) {
                addNewColorCode(color, titleInput, descriptionInput, app.getOrganizationID());
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public CompletableFuture<Boolean> getDetails() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        Amplify.Auth.fetchUserAttributes(
                attributes -> {
                    for (AuthUserAttribute attribute : attributes) {
                        switch (attribute.getKey().getKeyString()) {
                            case "email":
                                currentEmail = attribute.getValue();
                                break;
                            default:
                        }
                    }
                    Log.i("progress", "User attributes fetched successfully");
                    future.complete(true);
                },
                error -> {
                    Log.e("AuthDemo", "Failed to fetch user attributes.", error);
                    future.complete(false);
                }
        );
        return future;
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
        Utils.addColourGroup(colorCode, title, description, currentEmail, organizationId, this, new OperationCallback<Boolean>() {
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
        Toast.makeText(AddColorCodeActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    public void navigateToHome() {
        Intent intent = new Intent(AddColorCodeActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
