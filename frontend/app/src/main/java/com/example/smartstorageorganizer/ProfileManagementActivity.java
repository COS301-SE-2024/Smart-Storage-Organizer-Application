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
import com.amplifyframework.storage.StoragePath;
import com.amplifyframework.storage.options.StorageUploadFileOptions;
import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public class ProfileManagementActivity extends AppCompatActivity {

    private TextView email, username;
    ConstraintLayout content;
    LottieAnimationView loadingScreen;
    private String currentEmail, currentName, currentSurname, currentProfileImage;
    ImageView profileBackButton;
    ShapeableImageView profilePicture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_management);

        email = findViewById(R.id.email);
        username = findViewById(R.id.username);
        content = findViewById(R.id.content);
        loadingScreen = findViewById(R.id.loadingScreen);
        profileBackButton = findViewById(R.id.ProfileBackButton);
        profilePicture = findViewById(R.id.profilePicture);


        getDetails().thenAccept(getDetails->{
            Log.i("AuthDemo", "User is signed in");
            Log.i("AuthEmailFragment", currentEmail);
        });

        AppCompatButton editProfileButton = findViewById(R.id.editProfileButton);
        ConstraintLayout logoutButton = findViewById(R.id.logoutButton);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileManagementActivity.this, EditProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignOut();
            }
        });

        profileBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private CompletableFuture<Boolean> getDetails() {
        CompletableFuture<Boolean> future=new CompletableFuture<>();
       // UploadProfilePicture("app\\src\\main\\java\\com\\example\\com.example.smartstorageorganizer\\left.jpg");
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
                        }
                    }
                    Log.i("progress","User attributes fetched successfully");
                    Log.i("progressEmail",currentEmail);
                    runOnUiThread(() -> {
                        Glide.with(this).load(currentProfileImage).placeholder(R.drawable.no_profile_image).error(R.drawable.no_profile_image).into(profilePicture);
                        email.setText(currentEmail);
                        String fullName = currentName+" "+currentSurname;
                        username.setText(fullName);
                        loadingScreen.setVisibility(View.GONE);
                        loadingScreen.pauseAnimation();
                        content.setVisibility(View.VISIBLE);
                    });
                    future.complete(true);
                },
                error ->{ Log.e("AuthDemo", "Failed to fetch user attributes.", error);
                    future.complete(false);
                }

        );
        return future;
    }

    public void SignOut()
    {

        Amplify.Auth.signOut(signOutResult -> {
            if (signOutResult instanceof AWSCognitoAuthSignOutResult.CompleteSignOut) {
                // Sign Out completed fully and without errors.
                Log.i("AuthQuickStart", "Signed out successfully");
                //move to a different page
                runOnUiThread(() -> {
                    Intent intent = new Intent(ProfileManagementActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                });
            } else if (signOutResult instanceof AWSCognitoAuthSignOutResult.PartialSignOut) {
                // Sign Out completed with some errors. User is signed out of the device.
                AWSCognitoAuthSignOutResult.PartialSignOut partialSignOutResult =
                        (AWSCognitoAuthSignOutResult.PartialSignOut) signOutResult;
                //move to the different page
                runOnUiThread(() -> {
                    Intent intent = new Intent(ProfileManagementActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                });

            } else if (signOutResult instanceof AWSCognitoAuthSignOutResult.FailedSignOut) {
                AWSCognitoAuthSignOutResult.FailedSignOut failedSignOutResult =
                        (AWSCognitoAuthSignOutResult.FailedSignOut) signOutResult;
                // Sign Out failed with an exception, leaving the user signed in.
                Log.e("AuthQuickStart", "Sign out Failed", failedSignOutResult.getException());
                //dont move to different page
                runOnUiThread(() -> {
//                    requireActivity().Toast.makeText(this, "Sign Out Failed.", Toast.LENGTH_LONG).show();

                });
            }
        });

    }




}