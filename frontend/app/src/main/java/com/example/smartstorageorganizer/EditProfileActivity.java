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

    TextInputEditText name;
    TextInputEditText surname;
    TextInputEditText email;
    TextInputEditText phone;
    TextInputEditText address;
    LinearLayout content;
    LottieAnimationView loadingScreen;
    CountryCodePicker cpp;
    static final int PICK_IMAGE_MULTIPLE = 1;
    private static final int GALLERY_CODE = 1;
    Uri imageUri;
    List<String> imagesEncodedList;
    ImageView profileImage;

    String currentEmail = "";
    String currentName = "";
    String currentSurname = "";
    String currentPhone = "";
    String currentAddress = "";
    String currentProfileUrl = "";
    String customAttribute = "";
    static final String AUTH_DEMO = "AuthDemo";
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        getDetails().thenAccept(getDetails -> {
            Log.i(AUTH_DEMO, "User is signed in");
            Log.i("AuthEmail", currentEmail);
            Log.i("AuthSurname", currentSurname);
        });

        ImageView editProfileBackButton = findViewById(R.id.editProfileBackButton);
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

        editProfileBackButton.setOnClickListener(v -> finish());

        profileImage.setOnClickListener(v -> openGallery());


        findViewById(R.id.save_button).setOnClickListener(v -> upDateDetails().thenAccept(updateDetails -> {
            loadingScreen.setVisibility(View.VISIBLE);
            loadingScreen.playAnimation();
            content.setVisibility(View.GONE);
            if(!file.exists()){
                Toast.makeText(EditProfileActivity.this, "Details Updated", Toast.LENGTH_SHORT).show();
                Log.i("EditProfileActivity", "Back button clicked");
                Intent intent = new Intent(EditProfileActivity.this, ProfileManagementActivity.class);
                startActivity(intent);
                finish();
            }
        }));
    }
    private CompletableFuture<Boolean> getDetails() {
        CompletableFuture<Boolean> future=new CompletableFuture<>();

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
                            default:
                        }
                    }
                    Log.i("progress","User attributes fetched successfully");
                    Log.i("progressEmail",currentPhone);
                    runOnUiThread(() -> {
                        email.setText(currentEmail);
                        name.setText(currentName);
                        surname.setText(currentSurname);
                        phone.setText(currentPhone.substring(currentPhone.length()-9));
                        Glide.with(this).load(currentProfileUrl).placeholder(R.drawable.no_profile_image).error(R.drawable.no_profile_image).into(profileImage);

                        address.setText(currentAddress);
                        String country=currentPhone.substring(1, currentPhone.length()-9);
                        cpp.setCountryForPhoneCode(Integer.parseInt(country));
                        loadingScreen.setVisibility(View.GONE);
                        content.setVisibility(View.VISIBLE);
                    });
                    future.complete(true);
                },
                error -> Log.e(AUTH_DEMO, "Failed to fetch user attributes.", error)

        );
        return future;
    }


    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(galleryIntent,"Select Picture"), PICK_IMAGE_MULTIPLE);
    }

    public CompletableFuture<Boolean>  upDateDetails(){
        Log.i("We are here","We are here");
        CompletableFuture<Boolean> future=new CompletableFuture<>();
        String nameText = Objects.requireNonNull(name.getText()).toString().trim();
        String surnameText = Objects.requireNonNull(surname.getText()).toString().trim();
        String addressText = Objects.requireNonNull(address.getText()).toString().trim();

        if(!nameText.equals(currentName)){
            Amplify.Auth.updateUserAttribute(
                    new AuthUserAttribute(AuthUserAttributeKey.name(), nameText),
                    result -> Log.i(AUTH_DEMO, "Updated name"),
                    error -> Log.e(AUTH_DEMO, "Update failed on name", error)
            );
            future.complete(true);
        }
        if(!surnameText.equals(currentSurname)){
            Amplify.Auth.updateUserAttribute(
                    new AuthUserAttribute(AuthUserAttributeKey.familyName(), surnameText),
                    result -> Log.i(AUTH_DEMO, "Updated surname"),
                    error -> Log.e(AUTH_DEMO, "Update failed on surname", error)
            );
            future.complete(true);
        }
        if(!addressText.equals(currentAddress)){
            Amplify.Auth.updateUserAttribute(
                    new AuthUserAttribute(AuthUserAttributeKey.address(), addressText),
                    result -> Log.i(AUTH_DEMO, "Updated address"),
                    error -> Log.e(AUTH_DEMO, "Update failed on address", error)
            );
            future.complete(true);
        }
        uploadProfilePicture(file);
        return future;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK  && data != null) {

            imagesEncodedList = new ArrayList<>();

            if (data.getData() != null) {
                imageUri = data.getData();
                profileImage.setImageURI(imageUri);
                BitmapDrawable drawable = (BitmapDrawable) profileImage.getDrawable();
                Bitmap bitmap = drawable.getBitmap();

                // Create a file to save the image
                file = new File(getCacheDir(), "image.jpeg");
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            else if (data.getClipData() != null) {
                imageUri = data.getClipData().getItemAt(0).getUri();
                profileImage.setImageURI(imageUri);
                BitmapDrawable drawable = (BitmapDrawable) profileImage.getDrawable();
                Bitmap bitmap = drawable.getBitmap();

                // Create a file to save the image
                file = new File(getCacheDir(), "image.jpeg");
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
    public CompletableFuture<Boolean> uploadProfilePicture(File profilePicture)
    {
        CompletableFuture<Boolean> future=new CompletableFuture<>();
        StorageUploadFileOptions options = StorageUploadFileOptions.builder()
                .contentType("image/png") // Adjust based on file type
                .build();
        long time = System.nanoTime();
        String key= String.valueOf(time);
        String path="public/ProfilePictures/"+key+".png";
        Amplify.Storage.uploadFile(
                StoragePath.fromString(path),
                profilePicture,
                options,
                result ->{ Log.i("MyAmplifyApp", "Successfully uploaded: " + getObjectUrl(key)); future.complete(true);},
                storageFailure -> {Log.e("MyAmplifyApp", "Upload failed", storageFailure); future.complete(false);}
        );

        return future;
    }
    public String getObjectUrl(String key)
    {
        String url = "https://smart-storage-f0629f0176059-staging.s3.eu-north-1.amazonaws.com/public/ProfilePictures/"+key+".png";
        Amplify.Auth.updateUserAttribute(
                new AuthUserAttribute(AuthUserAttributeKey.picture(), url),
                resultProfile -> Log.i(AUTH_DEMO, "Updated Profile Picture"),
                error -> Log.e(AUTH_DEMO, "Update failed", error)
        );
        Toast.makeText(EditProfileActivity.this, "Details Updated", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(EditProfileActivity.this, ProfileManagementActivity.class);
        startActivity(intent);
        finish();

        return "https://smart-storage-f0629f0176059-staging.s3.eu-north-1.amazonaws.com/public/ProfilePictures/"+key+".png";
    }


}