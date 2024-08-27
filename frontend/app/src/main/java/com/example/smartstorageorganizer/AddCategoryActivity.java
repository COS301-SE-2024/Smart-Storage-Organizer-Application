package com.example.smartstorageorganizer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.StoragePath;
import com.amplifyframework.storage.options.StorageUploadFileOptions;
import com.bumptech.glide.Glide;
import com.example.smartstorageorganizer.model.CategoryModel;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.Utils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AddCategoryActivity extends AppCompatActivity {
    public static final int GALLERY_CODE = 1;
    public static final String EMAIL_KEY = "email";
    public static final String IMAGE_TYPE = "image/*";
    public static final String IMAGE_CACHE_NAME = "image.jpeg";
    public static final String IMAGE_CONTENT_TYPE = "image/png";
    public static final String IMAGE_PATH_FORMAT = "public/Category/%s.png";
    public static final String STORAGE_URL_FORMAT = "https://frontend-storage-5dbd9817acab2-dev.s3.amazonaws.com/public/Category/%s.png";

    public File imageFile;
    public RadioGroup radioGroup;
    public RadioButton selectedRadioButton;
    public TextInputLayout subcategoryInput;
    public TextInputLayout parentCategoryInput;
    public ImageView parentCategoryImage;
    public ConstraintLayout uploadButton;
    public TextView spinnerHeaderText;
    public String currentSelectedParent;
    public LinearLayout addCategoryLayout;
    public ConstraintLayout addButton;
    public TextInputEditText parentCategoryEditText;
    public TextInputEditText subCategoryEditText;
    public LottieAnimationView loadingScreen;
    public List<CategoryModel> categoryModelList = new ArrayList<>();
    public Spinner categorySpinner;
    public String currentEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_category);
        initViews();
        setupWindowInsets();
        getDetails();
        fetchParentCategories();
        setupUploadButton();
        setupAddButton();
        setupRadioGroup();
        setupSpinnerListener();
    }

    public void initViews() {
        categorySpinner = findViewById(R.id.mySpinner);
        radioGroup = findViewById(R.id.radioGroup);
        subcategoryInput = findViewById(R.id.subcategoryInput);
        parentCategoryInput = findViewById(R.id.parentcategoryInput);
        parentCategoryEditText = findViewById(R.id.parentcategory);
        spinnerHeaderText = findViewById(R.id.spinnerHeaderText);
        addButton = findViewById(R.id.addButton);
        addCategoryLayout = findViewById(R.id.addCategoryLayout);
        subCategoryEditText = findViewById(R.id.subcategory);
        parentCategoryImage = findViewById(R.id.parentCategoryImage);
        uploadButton = findViewById(R.id.uploadButton);
        loadingScreen = findViewById(R.id.loadingScreen);
    }

    public void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void navigateToHome() {
        Intent intent = new Intent(AddCategoryActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    public CompletableFuture<Boolean> getDetails() {
        CompletableFuture<Boolean> future=new CompletableFuture<>();

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
//                    Log.i("progress","User attributes fetched successfully");
                    future.complete(true);
                },
                error -> {
//                    Log.e("AuthDemo", "Failed to fetch user attributes.", error);  future.complete(false);
                }

        );
        return future;
    }

    public void fetchParentCategories() {
        String email = getIntent().getStringExtra(EMAIL_KEY);
        Utils.fetchParentCategories(0, email,"", this, new OperationCallback<List<CategoryModel>>() {
            @Override
            public void onSuccess(List<CategoryModel> result) {
                categoryModelList = result;
                setupSpinnerAdapter();
            }

            @Override
            public void onFailure(String error) {
                showToast("Failed to fetch categories: " + error);
            }
        });
    }

    public void setupSpinnerAdapter() {
        List<String> parentCategories = new ArrayList<>();
        for (CategoryModel category : categoryModelList) {
            parentCategories.add(category.getCategoryName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, parentCategories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }

    public void setupUploadButton() {
        uploadButton.setOnClickListener(v -> openGallery());
    }

    public void setupAddButton() {
        addButton.setOnClickListener(v -> handleAddButtonClick());
    }

    public void handleAddButtonClick() {
        loadingScreen.setVisibility(View.VISIBLE);
        addCategoryLayout.setVisibility(View.GONE);
        addButton.setVisibility(View.GONE);
        if (isParentCategorySelected()) {
            handleParentCategory();
        } else if (isSubCategorySelected() && validateSubCategoryForm()) {
            CategoryModel parent = findCategoryByName(currentSelectedParent);
            if (parent != null) {
                addNewCategory(Integer.parseInt(parent.getCategoryID()), subCategoryEditText.getText().toString(), "");
            }
        }
    }

    public boolean isParentCategorySelected() {
        return selectedRadioButton.getText().toString().equals("Parent Category");
    }

    public boolean isSubCategorySelected() {
        return selectedRadioButton.getText().toString().equals("Sub Category");
    }

    public void handleParentCategory() {
        String parentCategoryText = parentCategoryEditText.getText().toString().trim();
        if (validateParentForm(parentCategoryText)) {
            uploadParentCategoryImage(imageFile);
        }
    }

    public void setupRadioGroup() {
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> handleRadioGroupSelection(checkedId));
    }

    public void handleRadioGroupSelection(int checkedId) {
        selectedRadioButton = findViewById(checkedId);
        if (isSubCategorySelected()) {
            showSubCategoryFields();
        } else if (isParentCategorySelected()) {
            showParentCategoryFields();
        }
    }

    public void showSubCategoryFields() {
        categorySpinner.setVisibility(View.VISIBLE);
        spinnerHeaderText.setVisibility(View.VISIBLE);
        subcategoryInput.setVisibility(View.VISIBLE);
        parentCategoryInput.setVisibility(View.GONE);
        parentCategoryImage.setVisibility(View.GONE);
        uploadButton.setVisibility(View.GONE);
    }

    public void showParentCategoryFields() {
        categorySpinner.setVisibility(View.GONE);
        spinnerHeaderText.setVisibility(View.GONE);
        subcategoryInput.setVisibility(View.GONE);
        parentCategoryInput.setVisibility(View.VISIBLE);
        parentCategoryImage.setVisibility(View.VISIBLE);
        uploadButton.setVisibility(View.VISIBLE);
    }

    public void setupSpinnerListener() {
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private boolean isFirstTime = true;

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (isFirstTime) {
                    isFirstTime = false;
                    return;
                }
                currentSelectedParent = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //Handle when nothing is not selected.
            }
        });
    }

    public void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType(IMAGE_TYPE);
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), GALLERY_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK && data != null) {
            handleImageSelection(data);
        }
    }

    public void handleImageSelection(Intent data) {
        if (data.getData() != null) {
            handleSingleImage(data.getData());
        } else if (data.getClipData() != null) {
            handleSingleImage(data.getClipData().getItemAt(0).getUri());
        }
    }

    public void handleSingleImage(Uri uri) {
        parentCategoryImage.setImageURI(uri);
        saveImageToFile(((BitmapDrawable) parentCategoryImage.getDrawable()).getBitmap());
    }

    public void saveImageToFile(Bitmap bitmap) {
        imageFile = new File(getCacheDir(), IMAGE_CACHE_NAME);
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public CategoryModel findCategoryByName(String categoryName) {
        for (CategoryModel category : categoryModelList) {
            if (category.getCategoryName().equalsIgnoreCase(categoryName)) {
                return category;
            }
        }
        return null;
    }

    boolean validateParentForm(String parentCategoryText) {
        if (TextUtils.isEmpty(parentCategoryText)) {
            parentCategoryEditText.setError("Parent Category is required.");
            parentCategoryEditText.requestFocus();
            return false;
        }
        return true;
    }

    public boolean validateSubCategoryForm() {
        String subCategoryText = subCategoryEditText.getText().toString().trim();
        if (TextUtils.isEmpty(subCategoryText)) {
            subCategoryEditText.setError("Sub Category is required.");
            subCategoryEditText.requestFocus();
            return false;
        }
        return true;
    }

    public void uploadParentCategoryImage(File parentCategoryImage) {
        StorageUploadFileOptions options = StorageUploadFileOptions.builder()
                .contentType(IMAGE_CONTENT_TYPE)
                .build();
        String key = String.valueOf(System.nanoTime());
        String path = String.format(IMAGE_PATH_FORMAT, key);

        Amplify.Storage.uploadFile(
                StoragePath.fromString(path),
                parentCategoryImage,
                options,
                result -> handleImageUploadSuccess(key),
                storageFailure -> Log.e("MyAmplifyApp", "Upload failed", storageFailure)
        );
    }

    public void handleImageUploadSuccess(String key) {
        String url = String.format(STORAGE_URL_FORMAT, key);
        addNewCategory(0, parentCategoryEditText.getText().toString(), url);
    }

    public void addNewCategory(int parentCategory, String categoryName, String url) {
        Utils.addCategory(parentCategory, categoryName, currentEmail, url, this, new OperationCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (Boolean.TRUE.equals(result)) {
                    showToast("Category added successfully");
                    navigateToHome();
                }
            }

            @Override
            public void onFailure(String error) {
                showToast("Failed to add category: " + error);
                loadingScreen.setVisibility(View.GONE);
                addCategoryLayout.setVisibility(View.VISIBLE);
                addButton.setVisibility(View.VISIBLE);
            }
        });
    }

    public void showToast(String message) {
        Toast.makeText(AddCategoryActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
