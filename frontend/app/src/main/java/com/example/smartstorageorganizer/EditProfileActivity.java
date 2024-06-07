package com.example.smartstorageorganizer;

import static android.app.PendingIntent.getActivity;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
    int PICK_IMAGE_MULTIPLE = 1;
    private static final int GALLERY_CODE = 1;
    Uri ImageUri;
    List<String> imagesEncodedList;
    ArrayList<Uri> ChooseImageList;
    ImageView profileImage;

    String currentEmail = "";
    String currentName = "";
    String currentSurname = "";
    String currentPhone = "";
    String currentAddress = "";
    String customAttribute = "";
    boolean flag = false;

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

        editProfileBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });


        findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upDateDetails().thenAccept(updateDetails -> {
                    Toast.makeText(EditProfileActivity.this, "Details Updated", Toast.LENGTH_SHORT).show();
                    Log.i("EditProfileActivity", "Back button clicked");
                    Intent intent = new Intent(EditProfileActivity.this, ProfileManagementActivity.class);
                    startActivity(intent);
                    finish();
                });
//                getDetails().thenAccept(getDetails -> {
//                    Log.i("AuthDemo", "User is signed in");
//                    Log.i("AuthEmail", currentEmail);
//                    name = findViewById(R.id.name);
//                    surname = findViewById(R.id.surname);
//                    email = findViewById(R.id.email);
//                    address = findViewById(R.id.address);
//                    phone = findViewById(R.id.phone);
//                    cpp = findViewById(R.id.ccp);
                    //  cpp.setCountryForPhoneCode(1); // This will set the country code to USA (+1)

                    // cpp.setCountryForPhoneCode(new Integer(currentPhone.substring(0, currentPhone.length()-9)));

//                    email.setText(currentEmail);
//                    name.setText(currentName);
//                    surname.setText(currentSurname);
//                    phone.setText(currentPhone.substring(currentPhone.length() - 9, currentPhone.length()));
//                    Log.i("phoneSet", currentPhone.substring(currentPhone.length() - 9, currentPhone.length()));
//                    address.setText(currentAddress);
//                    String country = currentPhone.substring(0, currentPhone.length() - 9);
//                    Integer countryiNT = new Integer(country);
//
//                    cpp.setCountryForPhoneCode(Integer.parseInt(country));
//                       phone.setText(currentPhone);
//                });

//                finish();
            }
        });
//
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

                        address.setText(currentAddress);
                        String country=currentPhone.substring(1, currentPhone.length()-9);
                        cpp.setCountryForPhoneCode(Integer.parseInt(country));
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


    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        //startActivityForResult(galleryIntent, GalleryPick);
        startActivityForResult(Intent.createChooser(galleryIntent,"Select Picture"), PICK_IMAGE_MULTIPLE);
    }

    public CompletableFuture<Boolean>  upDateDetails(){
        Log.i("We are here","We are here");
        CompletableFuture<Boolean> future=new CompletableFuture<>();
        String Name = Objects.requireNonNull(name.getText()).toString().trim();
        String Surname = Objects.requireNonNull(surname.getText()).toString().trim();
        String Address = Objects.requireNonNull(address.getText()).toString().trim();
        String Phone = Objects.requireNonNull(phone.getText()).toString().trim();
        if(!Name.equals(name)){
            Amplify.Auth.updateUserAttribute(
                    new AuthUserAttribute(AuthUserAttributeKey.name(), Name),
                    result -> Log.i("AuthDemo", "Updated name"),
                    error -> Log.e("AuthDemo", "Update failed", error)
                    );
            future.complete(true);
        }
        if(!Surname.equals(surname)){
            Amplify.Auth.updateUserAttribute(
                    new AuthUserAttribute(AuthUserAttributeKey.familyName(), Surname),
                    result -> Log.i("AuthDemo", "Updated surname"),
                    error -> Log.e("AuthDemo", "Update failed", error)
            );
            future.complete(true);
        }
        if(!Address.equals(address)){
            Amplify.Auth.updateUserAttribute(
                    new AuthUserAttribute(AuthUserAttributeKey.address(), Address),
                    result -> Log.i("AuthDemo", "Updated address"),
                    error -> Log.e("AuthDemo", "Update failed", error)
            );
            future.complete(true);
        }
        if(!Phone.equals(phone)){
            Amplify.Auth.updateUserAttribute(
                    new AuthUserAttribute(AuthUserAttributeKey.phoneNumber(), Phone),
                    result -> Log.i("AuthDemo", "Updated phone"),
                    error -> Log.e("AuthDemo", "Update failed", error)

            );
            future.complete(true);
        }
        return future;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK  && data != null) {

            imagesEncodedList = new ArrayList<>();

            if (data.getData() != null) {
                ImageUri = data.getData();
                profileImage.setImageURI(ImageUri);
                BitmapDrawable drawable = (BitmapDrawable) profileImage.getDrawable();
                Bitmap bitmap = drawable.getBitmap();

                // Create a file to save the image
                File file = new File(getCacheDir(), "image.png");
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                UploadProfilePicture(file);
//

            }

            else if (data.getClipData() != null) {
                ImageUri = data.getClipData().getItemAt(0).getUri();
                //UploadProfilePicture(nn);
            }

        }
    }
    public void UploadProfilePicture(File ProfilePicture)
    {
        //File ProfilePicture= new File(ProfilePicturePath);
        Amplify.Storage.uploadFile(
                StoragePath.fromString("ProfilePictures"),
                ProfilePicture,
                StorageUploadFileOptions.defaultInstance(),
                progress ->{ Log.i("MyAmplifyApp", "Fraction completed: " + progress.getFractionCompleted());},
                result ->{ Log.i("MyAmplifyApp", "Successfully uploaded: " + result.getPath());},
                storageFailure ->{ Log.e("MyAmplifyApp", "Upload failed", storageFailure);}
        );
    }
    public String getRealPathFromURI(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return null;
    }
    public static String getPath(Context context, Uri uri ) {
        String result = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver( ).query( uri, proj, null, null, null );
        if(cursor != null){
            if ( cursor.moveToFirst( ) ) {
                int column_index = cursor.getColumnIndexOrThrow( proj[0] );
                result = cursor.getString( column_index );
            }
            cursor.close( );
        }
        if(result == null) {
            result = "Not found";
        }
        return result;
    }
    private CompletableFuture<Boolean> isSignedIn(){
        CompletableFuture<Boolean> future=new CompletableFuture<>();

        Amplify.Auth.fetchAuthSession(

                result->{
                    if(result.isSignedIn()){
                        Log.i("AmplifyQuickstart", "User is signed in");
                        future.complete(true);
                    }
                    else {
                        Log.i("AmplifyQuickstart", "User is not signed in");
                        future.complete(false);
                    }},
                error -> {
                    Log.e("AmplifyQuickstart", error.toString());
                    future.completeExceptionally(error);
                }

        );
        return future;
    }

}