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
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.StoragePath;
import com.amplifyframework.storage.options.StorageUploadFileOptions;
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
import java.util.Objects;

public class AddCategoryActivity extends AppCompatActivity {
    int pickImageMultiple = 1;
    private static final int GALLERY_CODE = 1;
    Uri imageUri;
    File file;
    List<String> imagesEncodedList;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private TextInputLayout subcategoryInput;
    private TextInputLayout parentCategoryInput;
    private ImageView parentCategoryImage;
    ConstraintLayout uploadButton;
    TextView spinnerHeaderText;
    private String currentSelectedParent;
    private LinearLayout addCategoryLayout;
    private ConstraintLayout addButton;
    private TextInputEditText parentCategoryEditText;
    private TextInputEditText subCategory;
    private LottieAnimationView loadingScreen;
    private List<CategoryModel> categoryModelList = new ArrayList<>();
    private Spinner mySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            mySpinner = findViewById(R.id.mySpinner);
            radioGroup = findViewById(R.id.radioGroup);
            subcategoryInput = findViewById(R.id.subcategoryInput);
            parentCategoryInput = findViewById(R.id.parentcategoryInput);
            parentCategoryEditText = findViewById(R.id.parentcategory);
            spinnerHeaderText = findViewById(R.id.spinnerHeaderText);
            addButton = findViewById(R.id.addButton);
            addCategoryLayout = findViewById(R.id.addCategoryLayout);
            subCategory = findViewById(R.id.subcategory);
            parentCategoryImage = findViewById(R.id.parentCategoryImage);
            uploadButton = findViewById(R.id.uploadButton);
            loadingScreen = findViewById(R.id.loadingScreen);

            String email = getIntent().getStringExtra("email");

            Utils.fetchParentCategories(0, email, this, new OperationCallback<List<CategoryModel>>() {
                @Override
                public void onSuccess(List<CategoryModel> result) {
                    categoryModelList = result;
                    List<String> parentCategories = null;
                    parentCategories = new ArrayList<>();
                    for (CategoryModel category : categoryModelList) {
                        parentCategories.add(category.getCategoryName());
                    }

                    ArrayAdapter<String> adapter = null;
                    adapter = new ArrayAdapter<>(AddCategoryActivity.this, android.R.layout.simple_spinner_item, parentCategories);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mySpinner.setAdapter(adapter);
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(AddCategoryActivity.this, "Failed to fetch categories: " + error, Toast.LENGTH_SHORT).show();
                }
            });



            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            uploadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openGallery();
                }
            });

            addButton.setOnClickListener(v1 -> {
                loadingScreen.setVisibility(View.VISIBLE);
                addCategoryLayout.setVisibility(View.GONE);
                if(radioButton.getText().toString().equals("Parent Category")){
                    String parentCategoryText = Objects.requireNonNull(parentCategoryEditText.getText()).toString().trim();
                    if(validateParentForm(parentCategoryText)) {
                        uploadParentCategoryImage(file);
                    }
                }
                else if(radioButton.getText().toString().equals("Sub Category") && validateSubCategoryForm()) {
                            CategoryModel parent = findCategoryByName( currentSelectedParent);
                    assert parent != null;
                    Log.i("Spinner", parent.getCategoryID() + " : " + parent.getCategoryName());
                            addNewCategory(Integer.parseInt(parent.getCategoryID()), Objects.requireNonNull(subCategory.getText()).toString(), "ezemakau@gmail.com", "");
                        }

            });
            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                radioButton = findViewById(checkedId);
                if(radioButton.getText().toString().equals("Sub Category")){
                    mySpinner.setVisibility(View.VISIBLE);
                    spinnerHeaderText.setVisibility(View.VISIBLE);
                    subcategoryInput.setVisibility(View.VISIBLE);
                    parentCategoryInput.setVisibility(View.GONE);
                    parentCategoryImage.setVisibility(View.GONE);
                    uploadButton.setVisibility(View.GONE);
                }
                else if(radioButton.getText().toString().equals("Parent Category")){
                    mySpinner.setVisibility(View.GONE);
                    spinnerHeaderText.setVisibility(View.GONE);
                    subcategoryInput.setVisibility(View.GONE);
                    parentCategoryInput.setVisibility(View.VISIBLE);
                    parentCategoryImage.setVisibility(View.VISIBLE);
                    uploadButton.setVisibility(View.VISIBLE);

                }
            });
            mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    // Get selected item
                    currentSelectedParent = parentView.getItemAtPosition(position).toString();

                    // Do something with the selected item
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // Do something here if nothing is selected
                }
            });

            return insets;
        });
    }

    public void addNewCategory(int parentCategory, String categoryName, String email, String url) {
        Utils.addCategory(parentCategory, categoryName, email, url, this, new OperationCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (Boolean.TRUE.equals(result)) {
                    Toast.makeText(AddCategoryActivity.this, "Category added successfully", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(AddCategoryActivity.this, "Failed to add category: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(galleryIntent,"Select Picture"), pickImageMultiple);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK  && data != null) {

            imagesEncodedList = new ArrayList<>();

            if (data.getData() != null) {
                imageUri = data.getData();
                parentCategoryImage.setImageURI(imageUri);
                BitmapDrawable drawable = (BitmapDrawable) parentCategoryImage.getDrawable();
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
                parentCategoryImage.setImageURI(imageUri);
                BitmapDrawable drawable = (BitmapDrawable) parentCategoryImage.getDrawable();
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

    private CategoryModel findCategoryByName(String categoryName) {
        runOnUiThread(() -> Log.e("Spinner", categoryModelList.get(0).getCategoryName()));
        for (CategoryModel category : categoryModelList) {
            if (category.getCategoryName().equalsIgnoreCase(categoryName)) {
                return category;
            }
        }
        return null; // Return null if no category with the given name is found
    }


    public boolean validateParentForm(String parentCategoryText) {

        if (TextUtils.isEmpty(parentCategoryText)) {
            parentCategoryEditText.setError("Parent Category is required.");
            parentCategoryEditText.requestFocus();
            return false;
        }

        return true;
    }

    private boolean validateSubCategoryForm() {
        String subCategoryText = Objects.requireNonNull(subCategory.getText()).toString().trim();

        if (Objects.equals(currentSelectedParent, "")) {
            Toast.makeText(AddCategoryActivity.this, currentSelectedParent, Toast.LENGTH_LONG).show();
            return false;
        }
        if(TextUtils.isEmpty(subCategoryText)){
            subCategory.setError("Sub Category is required.");
            subCategory.requestFocus();
            return false;
        }

        return true;
    }

    public void uploadParentCategoryImage(File parentCategoryImage)
    {
        StorageUploadFileOptions options = StorageUploadFileOptions.builder()
                .contentType("image/png") // Adjust based on file type
                .build();
        long time = System.nanoTime();
        String key= String.valueOf(time);
        String path="public/Category/"+key+".png";
        Amplify.Storage.uploadFile(
                StoragePath.fromString(path),
                parentCategoryImage,
                options,
                result -> Log.i("MyAmplifyApp", "Successfully uploaded: " + getObjectUrl(key)),
                storageFailure -> Log.e("MyAmplifyApp", "Upload failed", storageFailure)
        );
    }
    public String getObjectUrl(String key)
    {
        String url = "https://smart-storage-f0629f0176059-staging.s3.eu-north-1.amazonaws.com/public/Category/"+key+".png";
        addNewCategory(0, Objects.requireNonNull(parentCategoryEditText.getText()).toString(), "ezemakau@gmail.com", url);

        return "https://smart-storage-f0629f0176059-staging.s3.eu-north-1.amazonaws.com/public/Category/"+key+".png";
    }
}