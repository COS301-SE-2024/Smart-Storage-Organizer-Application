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

import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.StoragePath;
import com.amplifyframework.storage.options.StorageUploadFileOptions;
import com.example.smartstorageorganizer.model.ItemModel;
import com.example.smartstorageorganizer.model.CategoryModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddCategoryActivity extends AppCompatActivity {
    int PICK_IMAGE_MULTIPLE = 1;
    private static final int GALLERY_CODE = 1;
    Uri ImageUri;
    File file;
    List<String> imagesEncodedList;
    ArrayList<Uri> ChooseImageList;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private TextInputLayout subcategoryInput;
    private TextInputLayout parentCategoryInput;
    private ImageView parentCategoryImage;
    ConstraintLayout uploadButton;
    TextView spinnerHeaderText;
    public ArrayList<CategoryModel> categoryModelList = new ArrayList<>();
    private String currentSelectedParent;
    private String currentEmail;

    private List<String> parentCategories = new ArrayList<>();
    private List<String> parentCategoriesIcons = new ArrayList<>();
    private Spinner mySpinner;
    private ConstraintLayout addButton;
    private TextInputEditText parentCategoryEditText, subCategory;
    private boolean flag = true;

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
            subCategory = findViewById(R.id.subcategory);
            parentCategoryImage = findViewById(R.id.parentCategoryImage);
            uploadButton = findViewById(R.id.uploadButton);
            //flash sale

            if(flag) {
                FetchCategory(0, getIntent().getStringExtra("email"));
                currentSelectedParent = "";
            }

            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            uploadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OpenGallery();
                }
            });

            addButton.setOnClickListener(v1 -> {
                Log.i("Spinner",radioButton.getText().toString());
                if(radioButton.getText().toString().equals("Parent Category")){
                    Log.i("Spinner","Inside the Parent category if statement");
                    if(validateParentForm()) {
                        Log.i("Spinner","Inside the Parent category if statement");
                        UploadParentCategoryImage(file);
                    }
                }
                else if(radioButton.getText().toString().equals("Sub Category")){
                    Log.i("Spinner","Inside the Sub category if statement");
                    if(validateSubCategoryForm()) {
                        Log.i("Spinner","Inside the Sub category validate "+currentSelectedParent);
//                            Log.i("Spinner",parentCategoryModelList.get(0).getCategoryName());

                            CategoryModel parent = findCategoryByName(categoryModelList, currentSelectedParent);
                            Log.i("Spinner", parent.getCategoryID() + " : " + parent.getCategoryName());
                            AddCategory(Integer.parseInt(parent.getCategoryID()), Objects.requireNonNull(subCategory.getText()).toString(), "ezemakau@gmail.com", "");
                        }
                    }
//                    AddCategory(0, "Gaming", "ezemakau@gmail.com");
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
                    Toast.makeText(AddCategoryActivity.this, "Selected: " + radioButton.getText(), Toast.LENGTH_SHORT).show();
                }
                else if(radioButton.getText().toString().equals("Parent Category")){
                    mySpinner.setVisibility(View.GONE);
                    spinnerHeaderText.setVisibility(View.GONE);
                    subcategoryInput.setVisibility(View.GONE);
                    parentCategoryInput.setVisibility(View.VISIBLE);
                    parentCategoryImage.setVisibility(View.VISIBLE);
                    uploadButton.setVisibility(View.VISIBLE);

                    Toast.makeText(AddCategoryActivity.this, "Selected: " + radioButton.getText(), Toast.LENGTH_SHORT).show();
                }
            });
            mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    // Get selected item
                    currentSelectedParent = parentView.getItemAtPosition(position).toString();

                    // Do something with the selected item
                    Log.i("Spinner","CurrentParent: "+currentSelectedParent);
                    Toast.makeText(AddCategoryActivity.this, "Selected: " + currentSelectedParent, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // Do something here if nothing is selected
                }
            });

            return insets;
        });
    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        //startActivityForResult(galleryIntent, GalleryPick);
        startActivityForResult(Intent.createChooser(galleryIntent,"Select Picture"), PICK_IMAGE_MULTIPLE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK  && data != null) {

            imagesEncodedList = new ArrayList<>();

//            Toast.makeText(EditProfileActivity.this, "Image URI: "+Objects.requireNonNull(data.getData()).toString(), Toast.LENGTH_LONG).show();

            if (data.getData() != null) {
                ImageUri = data.getData();
                parentCategoryImage.setImageURI(ImageUri);
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
                ImageUri = data.getClipData().getItemAt(0).getUri();
                parentCategoryImage.setImageURI(ImageUri);
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

    private CategoryModel findCategoryByName(ArrayList<CategoryModel> categories, String categoryName) {
        runOnUiThread(() -> {
            Log.e("Spinner", categoryModelList.get(0).getCategoryName());
        });
        for (CategoryModel category : categoryModelList) {
            if (category.getCategoryName().equalsIgnoreCase(categoryName)) {
                return category;
            }
        }
        return null; // Return null if no category with the given name is found
    }


    private boolean validateParentForm() {
        String parentCategoryText = Objects.requireNonNull(parentCategoryEditText.getText()).toString().trim();

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

    public void FetchCategory(int ParentCategory, String email)
    {
        if(flag) {
            String json = "{\"useremail\":\""+email+"\", \"parentcategory\":\""+Integer.toString(ParentCategory)+"\" }";


            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            String API_URL = BuildConfig.FetchCategoryEndPoint;
            RequestBody body = RequestBody.create(json, JSON);

            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .build();


            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Log.e("Category Request Method", "GET request failed", e));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String responseData = response.body().string();
                        runOnUiThread(() -> {
                            runOnUiThread(() -> Log.e("Category Response Results", responseData));

                            try {
                                JSONObject jsonObject = new JSONObject(responseData);

                                int statusCode = jsonObject.getInt("statusCode");
                                String status = jsonObject.getString("status");

                                String bodyString = jsonObject.getString("body");
                                JSONArray bodyArray = new JSONArray(bodyString);

                                List<ItemModel> items = new ArrayList<>();

                                runOnUiThread(() -> Log.e("Category Details Array", bodyArray.toString()));
                                parentCategories.add("");
                                for (int i = 0; i < bodyArray.length(); i++) {
                                    JSONObject itemObject = bodyArray.getJSONObject(i);

                                    CategoryModel parentCategory = new CategoryModel();
                                    parentCategory.setCategoryID(itemObject.getString("id"));
                                    parentCategory.setCategoryName(itemObject.getString("categoryname"));
//                                    parentCategory.setImageUrl(itemObject.getString("icon"));

                                    categoryModelList.add(parentCategory);
                                    parentCategories.add(itemObject.getString("categoryname"));
//                                    String temp = itemObject.getString("icon");
//                                    runOnUiThread(() -> Log.e("Image Url", temp));
                                }
                                runOnUiThread(() -> {
                                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AddCategoryActivity.this, android.R.layout.simple_spinner_item, parentCategories);
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    mySpinner.setAdapter(adapter);
                                });


//                            requireActivity().runOnUiThread(() -> {
//                                fetchItemsLoader.setVisibility(View.GONE);
//                                itemRecyclerView.setVisibility(View.VISIBLE);
//                            });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });

                    } else {
                        runOnUiThread(() -> Log.e("Request Method", "GET request failed: " + response));
                    }
                }
            });
            flag = false;
        }

    }

    public void AddCategory(int ParentCategory, String CategoryName, String email, String url)
    {
        String json = "{\"useremail\":\""+email+"\", \"parentcategory\":\""+Integer.toString(ParentCategory)+"\", \"categoryname\":\""+CategoryName+"\", \"icon\": \""+url+"\" }";

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.AddCategoryEndPoint;
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Log.e("Request Method", "POST request failed", e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    runOnUiThread(() -> {
                        runOnUiThread(() -> Log.i("Request Method", "POST request succeeded: " + responseData));
                        Intent intent = new Intent(AddCategoryActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(AddCategoryActivity.this, "New Category Added Successfully.", Toast.LENGTH_LONG).show();
                    });
                } else {
                    runOnUiThread(() -> Log.e("Request Method", "POST request failed: " + response.code()));
                }
            }
        });

    }

    public void UploadParentCategoryImage(File ParentCategoryImage)
    {
        StorageUploadFileOptions options = StorageUploadFileOptions.builder()
                .contentType("image/png") // Adjust based on file type
                .build();
        long Time = System.nanoTime();
        String key= String.valueOf(Time);
        String Path="public/Category/"+key+".png";
        Amplify.Storage.uploadFile(
                StoragePath.fromString(Path),
                ParentCategoryImage,
                options,
                result -> Log.i("MyAmplifyApp", "Successfully uploaded: " + GetObjectUrl(key)),
                storageFailure -> {Log.e("MyAmplifyApp", "Upload failed", storageFailure);}
        );
    }
    public String GetObjectUrl(String key)
    {
        String url = "https://smart-storage-f0629f0176059-staging.s3.eu-north-1.amazonaws.com/public/Category/"+key+".png";
        AddCategory(0, Objects.requireNonNull(parentCategoryEditText.getText()).toString(), "ezemakau@gmail.com", url);
//        Toast.makeText(AddCategoryActivity.this, "Parent Category Added Successfully.", Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(AddCategoryActivity.this, HomeActivity.class);
//        startActivity(intent);
//        finish();

        return "https://smart-storage-f0629f0176059-staging.s3.eu-north-1.amazonaws.com/public/Category/"+key+".png";
    }
}