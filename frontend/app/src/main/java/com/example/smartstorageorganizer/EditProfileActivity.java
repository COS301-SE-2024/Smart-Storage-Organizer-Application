package com.example.smartstorageorganizer;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class EditProfileActivity extends BaseActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_CROPPER = 102;
    private String imageFilePath;

    TextInputEditText name, surname, email, phone, address;
    LinearLayout content;
    LottieAnimationView loadingScreen;
    CountryCodePicker cpp;
    int PICK_IMAGE_MULTIPLE = 1;
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
    File file;
    boolean isNewProfilePicture = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        getDetails().thenAccept(getDetails -> {
            Log.i("AuthDemo", "User is signed in");
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

        profileImage.setOnClickListener(v -> showImagePickerDialog());

        findViewById(R.id.save_button).setOnClickListener(v -> {
            loadingScreen.setVisibility(View.VISIBLE);
            loadingScreen.playAnimation();
            content.setVisibility(View.GONE);
            upDateDetails().thenAccept(updateDetails -> {
                loadingScreen.setVisibility(View.VISIBLE);
                loadingScreen.playAnimation();
                content.setVisibility(View.GONE);

                Toast.makeText(EditProfileActivity.this, "Details Updated", Toast.LENGTH_SHORT).show();
                Log.i("EditProfileActivity", "Back button clicked");

                Intent intent = new Intent(EditProfileActivity.this, ProfileManagementActivity.class);
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                startActivity(intent);
                finish();
            });
        });


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
                        }
                    }
                    Log.i("progress","User attributes fetched successfully");
                    Log.i("progressEmail",currentPhone);
                    runOnUiThread(() -> {
                        email.setText(currentEmail);
                        name.setText(currentName);
                        surname.setText(currentSurname);
                        phone.setText(currentPhone.substring(currentPhone.length()-9,currentPhone.length()));
                        Glide.with(this).load(currentProfileUrl).placeholder(R.drawable.no_profile_image).error(R.drawable.no_profile_image).into(profileImage);

                        address.setText(currentAddress);
                        String country=currentPhone.substring(1, currentPhone.length()-9);
                        cpp.setCountryForPhoneCode(Integer.parseInt(country));
                        loadingScreen.setVisibility(View.GONE);
                        content.setVisibility(View.VISIBLE);
                    });
                    future.complete(true);
                },
                error -> Log.e("AuthDemo", "Failed to fetch user attributes.", error)

        );
        return future;
    }

    public CompletableFuture<Boolean> upDateDetails() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        String Name = Objects.requireNonNull(name.getText()).toString().trim();
        String Surname = Objects.requireNonNull(surname.getText()).toString().trim();
        String Address = Objects.requireNonNull(address.getText()).toString().trim();
        String Phone = Objects.requireNonNull(phone.getText()).toString().trim();

        // Compare with current values, not input fields
        if (!Name.equals(currentName)) {
            Amplify.Auth.updateUserAttribute(
                    new AuthUserAttribute(AuthUserAttributeKey.name(), Name),
                    result -> Log.i("AuthDemo", "Updated name"),
                    error -> Log.e("AuthDemo", "Update failed", error)
            );
        }
        if (!Surname.equals(currentSurname)) {
            Amplify.Auth.updateUserAttribute(
                    new AuthUserAttribute(AuthUserAttributeKey.familyName(), Surname),
                    result -> Log.i("AuthDemo", "Updated surname"),
                    error -> Log.e("AuthDemo", "Update failed", error)
            );
        }
        if (!Address.equals(currentAddress)) {
            Amplify.Auth.updateUserAttribute(
                    new AuthUserAttribute(AuthUserAttributeKey.address(), Address),
                    result -> Log.i("AuthDemo", "Updated address"),
                    error -> Log.e("AuthDemo", "Update failed", error)
            );
        }

        if (isNewProfilePicture) {
            UploadProfilePicture(file).thenRun(() -> future.complete(true));
        } else {
            future.complete(true);
        }

        return future;
    }
    private void saveBitmapToGallery(Bitmap bitmap) {
        String savedImageURL = MediaStore.Images.Media.insertImage(
                getContentResolver(),
                bitmap,
                "MyImage",
                "Image of something"
        );
        Uri savedImageURI = Uri.parse(savedImageURL);

        Toast.makeText(EditProfileActivity.this, "Image saved to gallery!\n" + savedImageURI.toString(), Toast.LENGTH_LONG).show();
    }
    public CompletableFuture<Boolean> UploadProfilePicture(File ProfilePicture)
    {
        CompletableFuture<Boolean> future=new CompletableFuture<>();
        StorageUploadFileOptions options = StorageUploadFileOptions.builder()
                .contentType("image/png")
                .build();
        long Time = System.nanoTime();
        String key= String.valueOf(Time);
        String Path="public/ProfileImage/"+key+".png";
        Amplify.Storage.uploadFile(
                StoragePath.fromString(Path),
                ProfilePicture,
                options,
                result ->{ Log.i("MyAmplifyApp", "Successfully uploaded: " + GetObjectUrl(key)); future.complete(true);},
                storageFailure -> {Log.e("MyAmplifyApp", "Upload failed", storageFailure); future.complete(false);}
        );

        return future;
    }
    public String GetObjectUrl(String key)
    {
        Log.e("AuthDemo", "GetObjectUrl inside");
        String url = "https://frontend-storage-5dbd9817acab2-dev.s3.amazonaws.com/public/ProfileImage/"+key+".png";
        Amplify.Auth.updateUserAttribute(
                new AuthUserAttribute(AuthUserAttributeKey.picture(), url),
                resultProfile -> Log.i("AuthDemo", "Updated Profile Picture"),
                error -> Log.e("AuthDemo", "Update failed", error)
        );
        Toast.makeText(EditProfileActivity.this, "Details Updated", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(EditProfileActivity.this, ProfileManagementActivity.class);
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
        startActivity(intent);
        finish();

        return "https://frontend-storage-5dbd9817acab2-dev.s3.amazonaws.com/public/ProfileImage/"+key+".png";
    }
    private void showImagePickerDialog() {
        String[] options = {"Take Photo", "Choose from Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Action");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                takePhoto();
            } else if (which == 1) {
                OpenGallery();
            }
        });
        builder.show();
    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), PICK_IMAGE_MULTIPLE);
    }

    private void takePhoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this, "com.example.smartstorageorganizer.provider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        imageFilePath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE_MULTIPLE && data != null) {
                imagesEncodedList = new ArrayList<>();

                if (data.getData() != null) {
                    Uri selectedImageUri = data.getData();
                    if (selectedImageUri != null) {
                        startCropperActivity(selectedImageUri);
                    }
                } else if (data.getClipData() != null) {
                    Uri selectedImageUri = data.getClipData().getItemAt(0).getUri();
                    if (selectedImageUri != null) {
                        startCropperActivity(selectedImageUri);
                    }
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                File file = new File(imageFilePath);
                if (file.exists()) {
                    Uri capturedImageUri = Uri.fromFile(file);
                    startCropperActivity(capturedImageUri);
                }
            } else if (requestCode == REQUEST_CROPPER && data != null) {
                String result = data.getStringExtra("RESULT");
                Uri resultUri = null;
                if (result != null) {
                    resultUri = Uri.parse(result);
                    profileImage.setImageURI(resultUri);
                    isNewProfilePicture = true;
                    saveBitmapToFile(getBitmapFromUri(resultUri));
                }
            }
        }
    }

    private void startCropperActivity(Uri imageUri) {
        Intent intent = new Intent(EditProfileActivity.this, CropperActivity.class);
        intent.putExtra("DATA", imageUri.toString());
        startActivityForResult(intent, REQUEST_CROPPER);
    }

    private Bitmap getBitmapFromUri(Uri uri) {
        try {
            return MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveBitmapToFile(Bitmap bitmap) {
        // Step 1: Resize the Bitmap to a smaller resolution (e.g., 400x400)
        Bitmap resizedBitmap = resizeBitmap(bitmap, 400, 400);

        // Step 2: Create a file in the cache directory to store the compressed image
        file = new File(getCacheDir(), "compressed_image.jpeg");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            // Step 3: Compress the image to JPEG format with 75% quality to reduce file size
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 25, fos);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to resize the bitmap
    private Bitmap resizeBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

}