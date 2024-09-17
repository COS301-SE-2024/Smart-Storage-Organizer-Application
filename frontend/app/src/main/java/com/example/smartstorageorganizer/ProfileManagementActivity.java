package com.example.smartstorageorganizer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.cognito.result.AWSCognitoAuthSignOutResult;
import com.amplifyframework.core.Amplify;
import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.concurrent.CompletableFuture;

public class ProfileManagementActivity extends BaseActivity {

    private static final String TAG = "ProfileManagementActivity";
    private TextView email;
    private TextView username;
    private ConstraintLayout content;
    private LottieAnimationView loadingScreen;
    private ImageView profileBackButton;
    private ShapeableImageView profilePicture;

    private String currentEmail;
    private String currentName;
    private String currentSurname;
    private String currentProfileImage;
    MyAmplifyApp app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_management);

        app = (MyAmplifyApp) getApplicationContext();

        TextView organizationName = findViewById(R.id.organizationName);
        organizationName.setText(app.getOrganizationName());

        initializeViews();
        setupWindowInsets();
        fetchUserDetails();

        setupButtons();
    }

    private void initializeViews() {
        email = findViewById(R.id.email);
        username = findViewById(R.id.username);
        content = findViewById(R.id.content);
        loadingScreen = findViewById(R.id.loadingScreen);
        profileBackButton = findViewById(R.id.ProfileBackButton);
        profilePicture = findViewById(R.id.profilePicture);
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void fetchUserDetails() {
        getDetails().thenAccept(success -> {
            Log.i(TAG, "User details fetched successfully");
        });
    }

    private CompletableFuture<Boolean> getDetails() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Amplify.Auth.fetchUserAttributes(
                attributes -> {
                    for (AuthUserAttribute attribute : attributes) {
                        switch (attribute.getKey().getKeyString()) {
                            case "email":
                                currentEmail = attribute.getValue();
                                break;
                            case "name":
                                currentName = attribute.getValue();
                                break;
                            case "family_name":
                                currentSurname = attribute.getValue();
                                break;
                            case "picture":
                                currentProfileImage = attribute.getValue();
                                break;
                            default:
                                break;
                        }
                    }
                    Log.i(TAG, "User attributes fetched successfully");
                    runOnUiThread(() -> {
                        displayUserInfo();
                    });
                    future.complete(true);
                },
                error -> {
                    Log.e(TAG, "Failed to fetch user attributes", error);
                    future.complete(false);
                }
        );
        return future;
    }

    private void displayUserInfo() {
        Glide.with(this)
                .load(currentProfileImage)
                .placeholder(R.drawable.no_profile_image)
                .error(R.drawable.no_profile_image)
                .into(profilePicture);

        email.setText(currentEmail);
        String fullName = currentName + " " + currentSurname;
        username.setText(fullName);

        loadingScreen.setVisibility(View.GONE);
        loadingScreen.pauseAnimation();
        content.setVisibility(View.VISIBLE);
    }

    private void setupButtons() {
        AppCompatButton editProfileButton = findViewById(R.id.editProfileButton);
        ConstraintLayout logoutButton = findViewById(R.id.logoutButton);

        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileManagementActivity.this, EditProfileActivity.class);
            startActivity(intent);
            finish();
        });

        logoutButton.setOnClickListener(v -> signOut());

        profileBackButton.setOnClickListener(v -> finish());
    }

    private void signOut() {
        Amplify.Auth.signOut(
                signOutResult -> {
                    if (signOutResult instanceof AWSCognitoAuthSignOutResult) {
                        handleSuccessfulSignOut();
                    } else if (signOutResult instanceof AWSCognitoAuthSignOutResult.FailedSignOut) {
                        handleFailedSignOut(((AWSCognitoAuthSignOutResult.FailedSignOut) signOutResult).getException());
                    }
                }
        );
    }

    private void handleSuccessfulSignOut() {
        Log.i(TAG, "Signed out successfully");
        moveToLoginActivity();
    }

    private void handleFailedSignOut(Exception exception) {
        Log.e(TAG, "Sign out failed", exception);
        moveToLoginActivity();
    }

    private void moveToLoginActivity() {
        runOnUiThread(() -> {
            Intent intent = new Intent(ProfileManagementActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
