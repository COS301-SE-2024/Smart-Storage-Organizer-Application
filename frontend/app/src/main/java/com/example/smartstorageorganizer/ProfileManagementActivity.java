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
//        String url = "https://imgs.search.brave.com/JcvHKXMXIjkAGi-NLMSt6_9p-uazLRn5V2HGCFVk4ws/rs:fit:860:0:0/g:ce/aHR0cHM6Ly9jZG4x/LnVuaXRlZGluZm9j/dXMuY29tL3VwbG9h/ZHMvMTQvMjAyMi8w/My9HZXR0eUltYWdl/cy0xMjM5MjIxMDM2/LTItMTAyNHg2ODMu/anBn";
//        String awsUrl = "https://smart-storage-f0629f0176059-staging.s3.eu-north-1.amazonaws.com/public/ProfilePictures/622013117918730.jpeg?x-id=GetObject&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=ASIA6ODVAP7O5ZR4SOSM%2F20240608%2Feu-north-1%2Fs3%2Faws4_request&X-Amz-Date=20240608T141540Z&X-Amz-Expires=604800&X-Amz-Security-Token=IQoJb3JpZ2luX2VjEH4aCmV1LW5vcnRoLTEiRjBEAiB%2FfiiQsnR7L84F%2F%2F9%2B6ZabvImLyAOuuwUmotYk0SvJAwIgSUlahHdDQ4mJ2nCuKxvZkaDhUodUJOyMqAICU31vEAcqygQIFxAAGgw5OTIzODI4NDQ4OTMiDJQePNcXlk0BW0YBZCqnBCPSNy6i4C020OIDjcW119iK9an6x7lQH%2Fj8636Y%2F1GNwOI7d%2BVoRkTn87qQn871YrvKryMfJDyv52u4mwy3swtleQy4eo0qOSksuyTxFxPMehLVhalcKPF1uvBgfgfQltraI4MRihgC1LwRl05xKZ4cd0Uo%2FAKQqOsyy2IIsn5xMdaQfhqFs6fLfLXvcW369aLu2%2FXyUvwbPRqUl%2BK1tF5KzDkaLptfhM7fk8xYWM2peMhMRTJ27f%2FH4gPMTMijhWCEVgC0UbaGvLvrK8kzYzJZrnFFheCsH%2FxprdGKl1RShBrfXNolnkqqq8U%2FguDHXt3DI61S0ZvlyMn3xcf3jrh%2BMl4nOShnAEno%2B%2BPCD3Kx4X8tW8xUvoYd9MBvz7vq5UjplPZLGeFTawSbj%2BEu2iS%2B1BLPdJYjEoHE6eN9EjEQx2%2BceHkOGkjdKnilSTq0SMnkfYG2%2BkuzEw7v6TfKi%2FbxMaL%2F0wQQ10UtjhaT%2FMbZ07SWwgAQNEOEpR%2Fyv9tYUjLODbGtRW5RfGMrvtYue4n7DIaET8MM3T%2Bpu%2FqspsRxSxIDlflgaKR%2FpNG3mmd888jsBnGGc0aUh61YoacnlDSejRlSZNOM3e5MeN5VkJodJnLM1Ox0zIRFrzbxIkaxItpHwWJBQS11o9oGeVFwtmwi3MyKqKZmnFvx4wsTwawBkX9sZBc3ErtAwO8FhCNalU%2FqF4%2FQghywsexDXoeuj0vcG0fPDeMMMN3NkbMGOoYCEtVKL7HzobCvtkHWwrYtFZ87C6NtVkl3aKOw%2FqkTN3YCwGcQJmob3D68FSeiHgVORAAWPf9eXOShK9gmJqz%2FIjZseL%2FyRGUK%2B1qst9ks5%2FLMooLrHlZgRZRLnE9SBaaM3xXxwO5%2FxCzf1Isrl80IJULgdohWn1uNxYD440PHcC%2Fqt%2BadzRPxWd%2FSTeiJATSRYPWwypzTqFZ7q7tg6efbwGM0aLrn7lkiGlyQebx4o4qQNj1knhYqh3UoBj2Yv6ePXtLKW4Fkoea5dGsXe66946RJE0xAWtQFiF3fzSWynfryKZ4jLkhRMQikrq7PSVVBoY7yw9VU2ERcCyKcK0%2FjrE5rVvAlmA%3D%3D&X-Amz-SignedHeaders=host&X-Amz-Signature=9de3e4835fcae4ed1b02968bfabae2e2905387787c5dae9cb1faa37365b7d702";


//        Glide.with(this).load(awsUrl).placeholder(R.drawable.profile).error(R.drawable.profile).into(profilePicture);



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
                        email.setText(currentEmail);
                        String fullName = currentName+" "+currentSurname;
                        username.setText(fullName);
                        Glide.with(this).load(currentProfileImage).placeholder(R.drawable.no_profile_image).error(R.drawable.no_profile_image).into(profilePicture);
                        loadingScreen.setVisibility(View.GONE);
                        loadingScreen.pauseAnimation();
                        content.setVisibility(View.VISIBLE);
                    });
                    future.complete(true);
                },
                error -> Log.e("AuthDemo", "Failed to fetch user attributes.", error)

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

    public void UploadProfilePicture(String ProfilePicturePath)
    {
        File ProfilePicture= new File(ProfilePicturePath);
        Amplify.Storage.uploadFile(
                StoragePath.fromString("public/ProfilePictures"),
                ProfilePicture,
                StorageUploadFileOptions.defaultInstance(),
                progress ->{ Log.i("MyAmplifyApp", "Fraction completed: " + progress.getFractionCompleted());},
                result ->{ Log.i("MyAmplifyApp", "Successfully uploaded: " + result.getPath());},
                storageFailure ->{ Log.e("MyAmplifyApp", "Upload failed", storageFailure);}
        );
    }
}