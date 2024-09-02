package com.example.smartstorageorganizer;

import android.app.Activity;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

    TextInputEditText name, surname, email, phone, address;
    LinearLayout content;
    LottieAnimationView loadingScreen;
    CountryCodePicker cpp;
    Uri ImageUri;
    List<String> imagesEncodedList;
    ArrayList<Uri> ChooseImageList;

    ImageView profileImage;

    String currentEmail = "";
    String currentName = "";
    String currentSurname = "";
    String currentPhone = "";
    String currentAddress = "";
    String currentProfileUrl = "";
    String customAttribute = "";
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initializeUI();
        fetchUserDetails();

        profileImage.setOnClickListener(v -> openGallery());

        findViewById(R.id.save_button).setOnClickListener(v -> {
            upDateDetails().thenAccept(updateDetails -> {
                showLoadingScreen(true);
                if (file == null || !file.exists()) {
                    navigateToProfileManagement();
                }
            });
        });
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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.editProfileBackButton).setOnClickListener(v -> finish());
    }

    private CompletableFuture<Boolean> fetchUserDetails() {
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
                                customAttribute = attribute.getValue();
                                break;
                        }
                    }
                    populateUIWithDetails();
                    future.complete(true);
                },
                error -> {
                    Log.e("AuthDemo", "Failed to fetch user attributes.", error);
                    future.complete(false);
                }
        );
        return future;
    }

    private void populateUIWithDetails() {
        runOnUiThread(() -> {
            email.setText(currentEmail);
            name.setText(currentName);
            surname.setText(currentSurname);
            phone.setText(currentPhone.substring(currentPhone.length() - 9));
            address.setText(currentAddress);

            String country = currentPhone.substring(1, currentPhone.length() - 9);
            cpp.setCountryForPhoneCode(Integer.parseInt(country));

            if (!currentProfileUrl.isEmpty()) {
                Glide.with(this).load(currentProfileUrl)
                        .placeholder(R.drawable.no_profile_image)
                        .error(R.drawable.no_profile_image)
                        .into(profileImage);
            } else {
                profileImage.setImageResource(R.drawable.no_profile_image);
            }
            showLoadingScreen(false);
        });
    }

    private void showLoadingScreen(boolean show) {
        runOnUiThread(() -> {
            if (show) {
                loadingScreen.setVisibility(View.VISIBLE);
                loadingScreen.playAnimation();
                content.setVisibility(View.GONE);
            } else {
                loadingScreen.setVisibility(View.GONE);
                content.setVisibility(View.VISIBLE);
            }
        });
    }

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    handleGalleryResult(result.getData());
                }
            }
    );

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        galleryLauncher.launch(Intent.createChooser(galleryIntent, "Select Picture"));
    }

    private void handleGalleryResult(Intent data) {
        ImageUri = data.getData();
        if (ImageUri != null) {
            profileImage.setImageURI(ImageUri);
            saveImageToFile();
        }
    }

    private void saveImageToFile() {
        BitmapDrawable drawable = (BitmapDrawable) profileImage.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        file = new File(getCacheDir(), "image.jpeg");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public CompletableFuture<Boolean> upDateDetails() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        String Name = Objects.requireNonNull(name.getText()).toString().trim();
        String Surname = Objects.requireNonNull(surname.getText()).toString().trim();
        String Address = Objects.requireNonNull(address.getText()).toString().trim();

        if (!Name.equals(currentName)) {
            Amplify.Auth.updateUserAttribute(
                    new AuthUserAttribute(AuthUserAttributeKey.name(), Name),
                    result -> Log.i("AuthDemo", "Updated name"),
                    error -> Log.e("AuthDemo", "Update failed", error)
            );
            future.complete(true);
        }
        if (!Surname.equals(currentSurname)) {
            Amplify.Auth.updateUserAttribute(
                    new AuthUserAttribute(AuthUserAttributeKey.familyName(), Surname),
                    result -> Log.i("AuthDemo", "Updated surname"),
                    error -> Log.e("AuthDemo", "Update failed", error)
            );
            future.complete(true);
        }
        if (!Address.equals(currentAddress)) {
            Amplify.Auth.updateUserAttribute(
                    new AuthUserAttribute(AuthUserAttributeKey.address(), Address),
                    result -> Log.i("AuthDemo", "Updated address"),
                    error -> Log.e("AuthDemo", "Update failed", error)
            );
            future.complete(true);
        }
        if (file != null && file.exists()) {
            UploadProfilePicture(file);
        }

        return future;
    }

    public CompletableFuture<Boolean> UploadProfilePicture(File ProfilePicture) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        StorageUploadFileOptions options = StorageUploadFileOptions.builder()
                .contentType("image/png") // Adjust based on file type
                .build();
        long Time = System.nanoTime();
        String key = String.valueOf(Time);
        String Path = "Public/ProfilePictures/" + key + ".png";
        Amplify.Storage.uploadFile(
                StoragePath.fromString(Path),
                ProfilePicture,
                options,
                result -> {
                    Log.i("MyAmplifyApp", "Successfully uploaded: " + Path);
                    String url = GetObjectUrl(key);
                    updateUserProfilePicture(url);
                    future.complete(true);
                },
                storageFailure -> {
                    Log.e("MyAmplifyApp", "Upload failed", storageFailure);
                    future.complete(false);
                }
        );

        return future;
    }

    public String GetObjectUrl(String key) {
        return "https://frontend-storage-5dbd9817acab2-dev.s3.amazonaws.com/public/ProfilePictures/" + key + ".png";
    }

    private void updateUserProfilePicture(String imageUrl) {
        Amplify.Auth.updateUserAttribute(
                new AuthUserAttribute(AuthUserAttributeKey.picture(), imageUrl),
                result -> {
                    Log.i("AuthDemo", "Updated Profile Picture");
                    navigateToProfileManagement();
                },
                error -> Log.e("AuthDemo", "Update failed", error)
        );
    }

    private void navigateToProfileManagement() {
        Intent intent = new Intent(EditProfileActivity.this, ProfileManagementActivity.class);
        startActivity(intent);
        finish();
    }
}
