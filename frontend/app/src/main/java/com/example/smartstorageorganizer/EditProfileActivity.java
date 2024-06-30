package com.example.smartstorageorganizer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.StoragePath;
import com.amplifyframework.storage.options.StorageUploadFileOptions;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.hbb20.CountryCodePicker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class EditProfileActivity extends AppCompatActivity {

    // UI Elements
    private TextInputEditText name;
    private TextInputEditText surname;
    private TextInputEditText email;
    private TextInputEditText phone;
    private TextInputEditText address;
    private LinearLayout content;
    private LottieAnimationView loadingScreen;
    private CountryCodePicker cpp;
    private ImageView profileImage;

    // Constants
    private static final int PICK_IMAGE_MULTIPLE = 1;
    private static final int GALLERY_CODE = 1;
    private static final String AUTH_DEMO = "AuthDemo";

    // User Details
    private String currentEmail = "";
    private String currentName = "";
    private String currentSurname = "";
    private String currentPhone = "";
    private String currentAddress = "";
    private String currentProfileUrl = "";

    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        initializeUI();

        getDetails().thenAccept(getDetails -> {
            Log.i(AUTH_DEMO, "User is signed in");
            Log.i("AuthEmail", currentEmail);
            Log.i("AuthSurname", currentSurname);
        });

        configureInsets();
        configureButtons();
    }

    private void initializeUI() {
        profileImage = findViewById(R.id.profileImage);
        content = findViewById(R.id.content);
        loadingScreen = findViewById(R.id.loadingScreen);
        name = findViewById(R.id.name);
        surname = findViewById(R.id.surname);
        email = findViewById(R.id.email);
        address = findViewById(R.id.address);
        phone = findViewById(R.id.phone);
        cpp = findViewById(R.id.ccp);
    }

    private void configureInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void configureButtons() {
        findViewById(R.id.editProfileBackButton).setOnClickListener(v -> finish());
        profileImage.setOnClickListener(v -> openGallery());
        findViewById(R.id.save_button).setOnClickListener(v -> updateDetails().thenAccept(updateDetails -> {
            loadingScreen.setVisibility(View.VISIBLE);
            loadingScreen.playAnimation();
            content.setVisibility(View.GONE);

            showUpdateSuccessMessage();
            navigateToProfileManagement();
        }));
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
                            case "phone_number":
                                currentPhone = attribute.getValue();
                                break;
                            case "address":
                                currentAddress = attribute.getValue();
                                break;
                            case "picture":
                                currentProfileUrl = attribute.getValue();
                                break;
                            case "custom:myCustomAttribute":
                                break;
                            default:
                                // Handle unknown attribute key
                        }
                    }
                    Log.i("progress", "User attributes fetched successfully");
                    Log.i("progressEmail", currentPhone);
                    runOnUiThread(this::updateUIWithDetails);
                    future.complete(true);
                },
                error -> {
                    Log.e(AUTH_DEMO, "Failed to fetch user attributes.", error);
                    future.complete(false);
                }
        );
        return future;
    }

    private void updateUIWithDetails() {
        email.setText(currentEmail);
        name.setText(currentName);
        surname.setText(currentSurname);
        phone.setText(currentPhone.substring(currentPhone.length() - 9));
        Glide.with(this).load(currentProfileUrl).placeholder(R.drawable.no_profile_image).error(R.drawable.no_profile_image).into(profileImage);
        address.setText(currentAddress);
        String country = currentPhone.substring(1, currentPhone.length() - 9);
        cpp.setCountryForPhoneCode(Integer.parseInt(country));
        loadingScreen.setVisibility(View.GONE);
        content.setVisibility(View.VISIBLE);
    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), PICK_IMAGE_MULTIPLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK && data != null) {
            handleGalleryResult(data);
        }
    }

    private void handleGalleryResult(Intent data) {
        // Image Handling
        Uri imageUri;
        if (data.getData() != null) {
            imageUri = data.getData();
            processImageUri(imageUri);
        } else if (data.getClipData() != null) {
            imageUri = data.getClipData().getItemAt(0).getUri();
            processImageUri(imageUri);
        }
    }

    private void processImageUri(Uri uri) {
        profileImage.setImageURI(uri);
        BitmapDrawable drawable = (BitmapDrawable) profileImage.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        file = new File(getCacheDir(), "image.jpeg");
        saveBitmapToFile(bitmap, file);
    }

    private void saveBitmapToFile(Bitmap bitmap, File file) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private CompletableFuture<Boolean> updateDetails() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        String nameText = Objects.requireNonNull(name.getText()).toString().trim();
        String surnameText = Objects.requireNonNull(surname.getText()).toString().trim();
        String addressText = Objects.requireNonNull(address.getText()).toString().trim();

        if (!nameText.equals(currentName)) {
            updateUserAttribute(AuthUserAttributeKey.name(), nameText);
            future.complete(true);
        }
        if (!surnameText.equals(currentSurname)) {
            updateUserAttribute(AuthUserAttributeKey.familyName(), surnameText);
            future.complete(true);
        }
        if (!addressText.equals(currentAddress)) {
            updateUserAttribute(AuthUserAttributeKey.address(), addressText);
            future.complete(true);
        }
        uploadProfilePicture(file);
        return future;
    }

    private void updateUserAttribute(AuthUserAttributeKey key, String value) {
        Amplify.Auth.updateUserAttribute(
                new AuthUserAttribute(key, value),
                result -> Log.i(AUTH_DEMO, "Updated " + key.getKeyString()),
                error -> Log.e(AUTH_DEMO, "Update failed on " + key.getKeyString(), error)
        );
    }

    private CompletableFuture<Boolean> uploadProfilePicture(File profilePicture) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        StorageUploadFileOptions options = StorageUploadFileOptions.builder()
                .contentType("image/png") // Adjust based on file type
                .build();
        long time = System.nanoTime();
        String key = String.valueOf(time);
        String path = "public/ProfilePictures/" + key + ".png";
        Amplify.Storage.uploadFile(
                StoragePath.fromString(path),
                profilePicture,
                options,
                result -> {
                    Log.i("MyAmplifyApp", "Successfully uploaded: " + getObjectUrl(key));
                    future.complete(true);
                },
                storageFailure -> {
                    Log.e("MyAmplifyApp", "Upload failed", storageFailure);
                    future.complete(false);
                }
        );
        return future;
    }

    private String getObjectUrl(String key) {
        String url = "https://smart-storage-f0629f0176059-staging.s3.eu-north-1.amazonaws.com/public/ProfilePictures/" + key + ".png";
        updateUserAttribute(AuthUserAttributeKey.picture(), url);
        showUpdateSuccessMessage();
        navigateToProfileManagement();
        return url;
    }

    private void showUpdateSuccessMessage() {
        Toast.makeText(EditProfileActivity.this, "Details Updated", Toast.LENGTH_SHORT).show();
    }

    private void navigateToProfileManagement() {
        Intent intent = new Intent(EditProfileActivity.this, ProfileManagementActivity.class);
        startActivity(intent);
        finish();
    }
}
