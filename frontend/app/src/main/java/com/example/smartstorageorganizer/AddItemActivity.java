package com.example.smartstorageorganizer;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.airbnb.lottie.LottieAnimationView;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.StoragePath;
import com.amplifyframework.storage.options.StorageUploadFileOptions;
import com.example.smartstorageorganizer.adapters.RecentAdapter;
import com.example.smartstorageorganizer.adapters.UnitsAdapter;
import com.example.smartstorageorganizer.model.CategoryModel;
import com.example.smartstorageorganizer.model.ColorCodeModel;
import com.example.smartstorageorganizer.model.ItemModel;
import com.example.smartstorageorganizer.model.SearchResult;
import com.example.smartstorageorganizer.model.TokenManager;
import com.example.smartstorageorganizer.model.unitModel;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.Utils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddItemActivity extends BaseActivity  {
    int PICK_IMAGE_MULTIPLE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    Uri ImageUri;
    File file;
    List<String> imagesEncodedList;
    RecyclerView recyclerView;
    private RecentAdapter adapter;
    private List<ItemModel> searchResults;
    private TextInputEditText name, description;
    private Button noButton;
    AlertDialog alertDialog;
    ProgressDialog progressDialog;
    private LottieAnimationView loader;
    Spinner suggestionSpinner, colorSpinner, unitsSpinner;
    List<CategoryModel> suggestedCategory = new ArrayList<>();
    private String parentCategoryId, subcategoryId;
    private RelativeLayout categorycardView, itemDetailscardView;
    private String imageFilePath;
    ImageView itemImage;
    private List<CategoryModel> categoryModelList;
    private List<CategoryModel> subcategoryModelList;
    ProgressDialog progressDialogAddingItem;
    MyAmplifyApp app;
    private long startTime;
    private ArrayAdapter<String> adapterCategories;
    private ScrollView moreLayout;
    private RelativeLayout mainLayout;
    private RelativeLayout moreOptionsLayout;
    private TextView moreText;
    private TextInputEditText inputWidth, inputHeight, inputDepth, inputWeight, inputLoadbear, inputUpdown;
    private List<unitModel> unitList;
    private LottieAnimationView buttonLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_item);

        app = (MyAmplifyApp) getApplicationContext();

        progressDialogAddingItem = new ProgressDialog(this);
        progressDialogAddingItem.setMessage("Adding Item...");
        progressDialogAddingItem.setCancelable(false);

        description = findViewById(R.id.inputDescription);
        name = findViewById(R.id.inputName);
//        itemDetailscardView = findViewById(R.id.itemDetailscardView);
        categorycardView = findViewById(R.id.categorycardView);
        suggestionSpinner = findViewById(R.id.categorySpinner);
        colorSpinner = findViewById(R.id.colorcodesSpinner);
        unitsSpinner = findViewById(R.id.unitsSpinner);
        itemImage = findViewById(R.id.item_image);
        moreLayout = findViewById(R.id.moreLayout);
        mainLayout = findViewById(R.id.mainLayout);
        moreText = findViewById(R.id.moreText);
        moreOptionsLayout = findViewById(R.id.moreOptionsLayout);
        inputWidth = findViewById(R.id.inputWidth);
        inputHeight = findViewById(R.id.inputHeight);
        inputDepth = findViewById(R.id.inputDepth);
        inputWeight = findViewById(R.id.inputWeight);
        inputLoadbear = findViewById(R.id.inputLoadbear);
        inputUpdown = findViewById(R.id.inputUpdown);
        buttonLoader = findViewById(R.id.buttonLoader);


        categoryModelList = new ArrayList<>();
        subcategoryModelList = new ArrayList<>();
        unitList = new ArrayList<>();

        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        findViewById(R.id.nextButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get values from input fields
                String itemName = name.getText().toString().trim();
                String itemDescription = description.getText().toString().trim();

                // Check if the name, description, and image are provided
                if (TextUtils.isEmpty(itemName)) {
                    name.setError("Item name is required");
                    name.requestFocus();
                } else if (TextUtils.isEmpty(itemDescription)) {
                    description.setError("Item description is required");
                    description.requestFocus();
                } else if (itemImage.getDrawable() == null || file == null || !file.exists()) {
                    Toast.makeText(AddItemActivity.this, "Please upload an item image", Toast.LENGTH_LONG).show();
                }
                else {
                    // If all fields are valid, proceed with the actions
                    buttonLoader.setVisibility(View.VISIBLE);
                    getSuggestedCategory(itemName, itemDescription);
                    assignItemToColor();
                    fetchParentCategories(0, app.getEmail(), app.getOrganizationID());
                    showSimilarItemPopup();
                }
            }
        });


        moreText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (moreOptionsLayout.getVisibility() == View.VISIBLE) {
                    moreOptionsLayout.setVisibility(View.GONE);
                    moreText.setText("Show Fields");
                } else {
                    moreOptionsLayout.setVisibility(View.VISIBLE);
                    moreText.setText("Hide Fields");
                }
            }
        });


        itemImage.setOnClickListener(v -> showImagePickerDialog());

        findViewById(R.id.addButton).setOnClickListener(v -> {
            if (shouldFillAllFields()) {
                Toast.makeText(AddItemActivity.this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
            } else {
                progressDialogAddingItem.show();
                File compressedFile = compressImage(file);
                uploadItemImage(compressedFile);
            }
        });


    }

    private File compressImage(File file) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
            File compressedFile = new File(getCacheDir(), "compressed_image.jpeg");
            try (FileOutputStream fos = new FileOutputStream(compressedFile)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos); // Compress to 50%
                fos.flush();
            }
            return compressedFile;
        } catch (IOException e) {
            e.printStackTrace();
            return file; // Fallback to original if compression fails
        }
    }

    private void showSimilarItemPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.suggested_items_popup, null);
        builder.setView(dialogView);

        loader = dialogView.findViewById(R.id.loader);
        recyclerView = dialogView.findViewById(R.id.similar_items_recycler_view);
        noButton = dialogView.findViewById(R.id.button_no);

        loader.setVisibility(View.VISIBLE);
        noButton.setVisibility(View.GONE);
        buttonLoader.setVisibility(View.VISIBLE);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchResults = new ArrayList<>();

        adapter = new RecentAdapter(this, searchResults, "add");
        recyclerView.setAdapter(adapter);

        SearchForItem(name.getText().toString().trim(), "*", "*");

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                mainLayout.setVisibility(View.GONE);
                moreLayout.setVisibility(View.VISIBLE);
            }
        });

        alertDialog = builder.create();
        alertDialog.show();
    }

    public CompletableFuture<List<SearchResult>> SearchForItem(String target, String parentcategoryid, String subcategoryid) {
        CompletableFuture<List<SearchResult>> future = new CompletableFuture<>();
        searchResults.clear();
        adapter.notifyDataSetChanged();

        String json = "{\"target\":\"" + target + "\", \"parentcategoryid\":\"" + parentcategoryid + "\", \"subcategoryid\":\"" + subcategoryid + "\", \"organizationid\":\"" + Integer.parseInt(app.getOrganizationID()) + "\" }";
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.SearchEndPoint;
        RequestBody body = RequestBody.create(json, JSON);

        TokenManager.getToken().thenAccept(results-> {
            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", results)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                    future.completeExceptionally(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            String bodyString = jsonObject.getString("body");
                            JSONArray bodyArray = new JSONArray(bodyString);
                            runOnUiThread(() -> Log.e("Search Results Body Array", bodyArray.toString()));

                            int size = Math.min(bodyArray.length(), 4);
                            for (int i = 0; i < size; i++) {
                                JSONObject itemObject = bodyArray.getJSONObject(i);

                                JSONObject itemObj = new JSONObject(itemObject.getString("_source"));

                                ItemModel item = new ItemModel();
                                item.setItemId(itemObj.getString("item_id"));
                                item.setItemName(itemObj.getString("item_name"));
                                item.setDescription(itemObj.getString("description"));
                                item.setColourCoding(itemObj.getString("colourcoding"));
                                item.setBarcode(itemObj.getString("barcode"));
                                item.setQrcode(itemObj.getString("qrcode"));
                                item.setQuantity(itemObj.getString("quanity"));
                                item.setLocation(itemObj.getString("location"));
                                item.setEmail(itemObj.getString("email"));
                                item.setItemImage(itemObj.getString("item_image"));
                                item.setParentCategoryId(itemObj.getString("parentcategoryid"));
                                item.setSubCategoryId(itemObj.getString("subcategoryid"));
//                            item.setCreatedAt(itemObject.getString("created_at"));

                                searchResults.add(item);
                            }
                            runOnUiThread(() -> {
                                adapter.notifyDataSetChanged();
                                loader.setVisibility(View.GONE);
                                noButton.setVisibility(View.VISIBLE);
                                buttonLoader.setVisibility(View.GONE);

                                if(searchResults.isEmpty()){
                                    alertDialog.dismiss();
                                    mainLayout.setVisibility(View.GONE);
                                    moreLayout.setVisibility(View.VISIBLE);
                                    buttonLoader.setVisibility(View.GONE);
                                }
                            });

                        }catch (JSONException e) {
                            alertDialog.dismiss();
                            mainLayout.setVisibility(View.GONE);
                            moreLayout.setVisibility(View.VISIBLE);
                            buttonLoader.setVisibility(View.GONE);
                            throw new RuntimeException(e);
                        }

                    } else {
                        SearchForItem(target, parentcategoryid, subcategoryid);
                        future.completeExceptionally(new IOException("Search request failed: " + response.code()));
                    }
                }
            });

        }).exceptionally(ex -> {
            Log.e("TokenError", "Failed to get user token", ex);
            return null;
        });

        return future;
    }

    private void getSuggestedCategory(String itemName, String itemDescription) {
        Utils.fetchCategorySuggestions(itemName, itemDescription, app.getEmail(), "1", this, new OperationCallback<List<CategoryModel>>() {
            @Override
            public void onSuccess(List<CategoryModel> result) {
                suggestedCategory.clear();
                suggestedCategory.addAll(result);
                parentCategoryId = suggestedCategory.get(0).getCategoryID();
                subcategoryId = suggestedCategory.get(1).getCategoryID();
                List<String> categories = new ArrayList<>();
                loadUnits(suggestedCategory.get(0).getCategoryName());
                categories.add(suggestedCategory.get(0).getCategoryName() + " - " + suggestedCategory.get(1).getCategoryName());
                categories.add("Add Custom Category");

                // Populate the Spinner
                ArrayAdapter<String> adapter = new ArrayAdapter<>(AddItemActivity.this, android.R.layout.simple_spinner_item, categories);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                suggestionSpinner.setAdapter(adapter);

                suggestionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (categories.get(position).equals("Add Custom Category")) {
                            // Show dialog to input custom category
                            showCustomCategoryDialog(categories, adapter);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(AddItemActivity.this, "Category Fetching failed... ", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void uploadItemImage(File parentCategoryImage)
    {
        StorageUploadFileOptions options = StorageUploadFileOptions.builder()
                .contentType("image/png")
                .build();
        long time = System.nanoTime();
        String key= String.valueOf(time);
        String path="public/ItemImages/"+key+".png";
        Amplify.Storage.uploadFile(
                StoragePath.fromString(path),
                parentCategoryImage,
                options,
                result -> Log.i("MyAmplifyApp", "Successfully uploaded: " + getObjectUrl(key)),
                storageFailure -> {Log.e("MyAmplifyApp", "Upload failed", storageFailure);}
        );
    }

    public String getObjectUrl(String key)
    {
        String url = "https://frontend-storage-5dbd9817acab2-dev.s3.amazonaws.com/public/ItemImages/"+key+".png";
        Log.i("MyAmplifyApp", "subCategory: "+subcategoryId + " Parent: "+ parentCategoryId);
        addItem(url, name.getText().toString().trim(), description.getText().toString().trim(), Integer.parseInt(subcategoryId), Integer.parseInt(parentCategoryId), unitsSpinner.getSelectedItem().toString(), inputWidth.getText().toString(), inputHeight.getText().toString(), inputDepth.getText().toString(), inputWeight.getText().toString(), inputLoadbear.getText().toString(), inputUpdown.getText().toString());

        return "https://frontend-storage-5dbd9817acab2-dev.s3.amazonaws.com/public/ItemImages/"+key+".png";
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

    private void takePhoto() {
        // Check if the CAMERA permission is granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)
        {
            // Request CAMERA permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        }
        else
        {
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
                    Log.d("Photo URI", "photoURI: " + photoURI.toString());
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                } else {
                    Log.e("PhotoFile", "photoFile is null");
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
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE_MULTIPLE && data != null) {
                imagesEncodedList = new ArrayList<>();

                if (data.getData() != null) {
                    ImageUri = data.getData();
                    itemImage.setImageURI(ImageUri);
//                    saveBitmapToFile();
                    saveBitmapToFile(getBitmapFromUri(ImageUri));
                } else if (data.getClipData() != null) {
                    ImageUri = data.getClipData().getItemAt(0).getUri();
                    itemImage.setImageURI(ImageUri);
//                    saveBitmapToFile();
                    saveBitmapToFile(getBitmapFromUri(ImageUri));
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                file = new File(imageFilePath);
                if (file.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    itemImage.setImageBitmap(myBitmap);
                    saveBitmapToGallery(myBitmap);
                    saveBitmapToFile(myBitmap);
                }
            }
        }
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
        // Create a file to save the image
        file = new File(getCacheDir(), "image.jpeg");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveBitmapToGallery(Bitmap bitmap) {
        String savedImageURL = MediaStore.Images.Media.insertImage(
                getContentResolver(),
                bitmap,
                "MyImage",
                "Image of something"
        );
        Uri savedImageURI = Uri.parse(savedImageURL);

        // Optional: Display a toast message
        Toast.makeText(AddItemActivity.this, "Image saved to gallery!\n" + savedImageURI.toString(), Toast.LENGTH_LONG).show();
    }

//    import java.util.concurrent.CountDownLatch;

    private void addItem(String itemImage, String itemName, String description, int category, int parentCategory, String unitName, String width, String height, String depth, String weight, String loadbear, String updown) {
        ArrayList<unitModel> units = new ArrayList<>();
        Utils.postAddItem(app.getEmail(), itemImage, itemName, description, category, parentCategory, app.getEmail(), unitName, app.getOrganizationID(), width, height, depth, weight, loadbear, updown, this, new OperationCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(AddItemActivity.this, "Item Added Successfully ", Toast.LENGTH_LONG).show();

                // Create CountDownLatch with count 3 for two async operations
                CountDownLatch latch = new CountDownLatch(3);
                ModifyItemDimension(result, width, height, depth, weight, loadbear, updown, latch);
                generateQRCodeAsync(result, latch);
                generateBarCodeAsync(result, latch);

                showSuccessDialog();

                // Run this in a background thread to wait for latch to finish
                new Thread(() -> {
                    try {
                        // Wait until both async tasks complete
                        latch.await();
                        runOnUiThread(() -> openSearchInsert(result)); // Call openSearchInsert after both tasks
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(AddItemActivity.this, "Adding item failed... ", Toast.LENGTH_LONG).show();
                progressDialogAddingItem.hide();
            }
        });
    }

    // Updated QR code generation to use CountDownLatch
    private void generateQRCodeAsync(String itemId, CountDownLatch latch) {
        ArrayList<unitModel> units = new ArrayList<>();
        Utils.GenerateQRCodeAsync(itemId, app.getOrganizationID(), app.getEmail(), this, new OperationCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (result) {
                    // Do whatever you need after successful generation
                }
                // Decrement latch count
                latch.countDown();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(AddItemActivity.this, "QR Code generation failed...", Toast.LENGTH_LONG).show();
                generateQRCodeAsync(itemId, latch);
//                latch.countDown(); // Ensure latch is decremented even in case of failure
            }
        });
    }

    // Updated Barcode generation to use CountDownLatch
    private void generateBarCodeAsync(String itemId, CountDownLatch latch) {
        ArrayList<unitModel> units = new ArrayList<>();
        Utils.GenerateBarCodeAsync(itemId, app.getOrganizationID(), app.getEmail(), this, new OperationCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (result) {
                    // Do whatever you need after successful generation
                }
                // Decrement latch count
                latch.countDown();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(AddItemActivity.this, "Barcode generation failed...", Toast.LENGTH_LONG).show();
                generateBarCodeAsync(itemId, latch);
//                latch.countDown(); // Ensure latch is decremented even in case of failure
            }
        });
    }

    private void ModifyItemDimension(String itemId, String width, String height, String depth, String weight, String loadbear, String updown, CountDownLatch latch) {
        ArrayList<unitModel> units = new ArrayList<>();
        Utils.ModifyItemDimension(itemId, width, height, depth, weight, loadbear, updown, this, new OperationCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (result) {
                    // Do whatever you need after successful generation
                }
                // Decrement latch count
                latch.countDown();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(AddItemActivity.this, "Modify Item Dimension failed: "+error, Toast.LENGTH_LONG).show();
                ModifyItemDimension(itemId, width, height, depth, weight, loadbear, updown, latch);
//                latch.countDown(); // Ensure latch is decremented even in case of failure
            }
        });
    }

    // Open search insert as a final step after both QR and Barcode are generated
    private void openSearchInsert(String itemId) {
        ArrayList<unitModel> units = new ArrayList<>();
        Utils.OpenSearchInsert(itemId, app.getOrganizationID(), app.getEmail(), this, new OperationCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (result) {
                    // Successfully opened search insert
                }
            }

            @Override
            public void onFailure(String error) {
                openSearchInsert(itemId);
                Toast.makeText(AddItemActivity.this, "Open search insert failed...", Toast.LENGTH_LONG).show();
            }
        });
    }


    private String findCategoryByName(String categoryName, String type) {
        if(Objects.equals(type, "parent")){
            for (CategoryModel category : categoryModelList) {
                if (category.getCategoryName().equalsIgnoreCase(categoryName)) {
                    runOnUiThread(() -> {
                        Log.e("Filtering: ", category.getCategoryID());
                    });
                    return category.getCategoryID();
                }
            }
        }
        else {
            for (CategoryModel category : subcategoryModelList) {
                if (category.getCategoryName().equalsIgnoreCase(categoryName)) {
                    runOnUiThread(() -> {
                        Log.e("Filtering: ", category.getCategoryID());
                    });
                    return category.getCategoryID();
                }
            }
        }

        return "";
    }

    public void assignItemToColor() {
        Utils.fetchAllColour(app.getOrganizationID(),this, new OperationCallback<List<ColorCodeModel>>() {
            @Override
            public void onSuccess(List<ColorCodeModel> colorCodeModelList) {
                String[] colorNames = new String[colorCodeModelList.size()+1];
                colorNames[0] = "Choose a color group (optional)";
                for (int i = 1, j = 0; i < colorCodeModelList.size()+1; i++, j++) {
                    colorNames[i] = colorCodeModelList.get(j).getName();
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(AddItemActivity.this, android.R.layout.simple_spinner_item, colorNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                colorSpinner.setAdapter(adapter);

                colorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(AddItemActivity.this, "Failed to fetch colors: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Spinner parentSpinner, subcategorySpinner;
    private List<String> parentCategories = new ArrayList<>();
    private String currentSelectedCategory, currentSelectedSubcategory;


    private void showCustomCategoryDialog(List<String> categories, ArrayAdapter<String> adapter2) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_custom_category_popup, null);

        parentSpinner = dialogView.findViewById(R.id.ParentSpinner);
        subcategorySpinner = dialogView.findViewById(R.id.subcategorySpinner);

        builder.setView(dialogView);
        adapterCategories = new ArrayAdapter<>(AddItemActivity.this, android.R.layout.simple_spinner_item, parentCategories);
        adapterCategories.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        parentSpinner.setAdapter(adapterCategories);

        parentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get selected item
                currentSelectedCategory = parentView.getItemAtPosition(position).toString();
                String parentCategory = "";
                parentCategory = findCategoryByName(currentSelectedCategory, "parent");
                parentCategoryId = parentCategory;
                if(!Objects.equals(parentCategory, "")){
                    parentCategoryId = parentCategory;
                    fetchParentCategories(Integer.parseInt(parentCategory), app.getEmail(), app.getOrganizationID());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do something here if nothing is selected
            }
        });
        subcategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                currentSelectedSubcategory = parentView.getItemAtPosition(position).toString();
                String subCategory = findCategoryByName(currentSelectedSubcategory, "sub");
                subcategoryId = subCategory;
                if(!Objects.equals(subCategory, "")) {
                    subcategoryId = subCategory;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do something here if nothing is selected
            }
        });
        builder.setPositiveButton("Add", (dialog, which) ->
        {
            categories.add(categories.size() - 1, currentSelectedCategory+" - "+currentSelectedSubcategory); // Add before "Add Custom Category"
            adapter2.notifyDataSetChanged();
            suggestionSpinner.setSelection(categories.indexOf(currentSelectedCategory+" - "+currentSelectedSubcategory));
            Toast.makeText(AddItemActivity.this, "Selected: " + currentSelectedCategory+" - "+currentSelectedSubcategory, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            suggestionSpinner.setSelection(0);
            dialog.cancel();
        });

        builder.show();
    }

    private List<String> subCategories = new ArrayList<>();
    private ArrayAdapter<String> subAdapter;
    private void fetchParentCategories(int categoryId, String email, String organizationID) {
        Utils.fetchParentCategories(categoryId, email, organizationID, this, new OperationCallback<List<CategoryModel>>() {
            @Override
            public void onSuccess(List<CategoryModel> result) {
                if(categoryId != 0) {
                    subCategories.clear();
                    subcategoryModelList.clear();
                    subcategoryModelList.addAll(result);
                    for (CategoryModel category : result) {
                        subCategories.add(category.getCategoryName());
                    }
                    subAdapter = new ArrayAdapter<>(AddItemActivity.this, android.R.layout.simple_spinner_item, subCategories);
                    subAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    subcategorySpinner.setAdapter(subAdapter);
                }
                else {
                    categoryModelList.clear();
                    CategoryModel allCategory = new CategoryModel("All", "all", "all");
                    CategoryModel uncategorizedCategory = new CategoryModel("Uncategorized", "uncategorized", "uncategorized");
                    categoryModelList.add(allCategory);
                    categoryModelList.add(uncategorizedCategory);
                    categoryModelList.addAll(result);
//                    categoryAdapter.notifyDataSetChanged();
                    for (CategoryModel category : result) {
                        parentCategories.add(category.getCategoryName());
                    }
                    adapterCategories = new ArrayAdapter<>(AddItemActivity.this, android.R.layout.simple_spinner_item, parentCategories);
                    adapterCategories.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                }
            }

            @Override
            public void onFailure(String error) {
//                showToast("Failed to fetch categories: " + error);
                Toast.makeText(AddItemActivity.this, "Category Fetching failed... ", Toast.LENGTH_LONG).show();

            }
        });
    }

    // Function to check if user should fill all fields
    private boolean shouldFillAllFields() {
        boolean isAnyFieldFilled = !TextUtils.isEmpty(inputWidth.getText().toString().trim()) ||
                !TextUtils.isEmpty(inputHeight.getText().toString().trim()) ||
                !TextUtils.isEmpty(inputDepth.getText().toString().trim()) ||
                !TextUtils.isEmpty(inputWeight.getText().toString().trim()) ||
                !TextUtils.isEmpty(inputLoadbear.getText().toString().trim()) ||
                !TextUtils.isEmpty(inputUpdown.getText().toString().trim());

        if (isAnyFieldFilled) {
            // Check if each field is empty and show an error if so
            if (TextUtils.isEmpty(inputWidth.getText().toString().trim())) {
                inputWidth.setError("Field required");
                inputWidth.requestFocus(); // Request focus to the empty field
                return true;
            } else if (TextUtils.isEmpty(inputHeight.getText().toString().trim())) {
                inputHeight.setError("Field required");
                inputHeight.requestFocus();
                return true;
            } else if (TextUtils.isEmpty(inputDepth.getText().toString().trim())) {
                inputDepth.setError("Field required");
                inputDepth.requestFocus();
                return true;
            } else if (TextUtils.isEmpty(inputWeight.getText().toString().trim())) {
                inputWeight.setError("Field required");
                inputWeight.requestFocus();
                return true;
            } else if (TextUtils.isEmpty(inputLoadbear.getText().toString().trim())) {
                inputLoadbear.setError("Field required");
                inputLoadbear.requestFocus();
                return true;
            } else if (TextUtils.isEmpty(inputUpdown.getText().toString().trim())) {
                inputUpdown.setError("Field required");
                inputUpdown.requestFocus();
                return true;
            }
        }

        // If no field is filled, return false (allow the user to proceed)
        return false;
    }

    private void loadUnits(String parentCategory) {
        Utils.FetchAllUnits(app.getOrganizationID(), this, new OperationCallback<List<unitModel>>() {
            @Override
            public void onSuccess(List<unitModel> result) {
                if(!result.isEmpty()){
                    unitList.clear();
                    unitList.addAll(result);
                    List<String> unitsNames = new ArrayList<>();

                    for(unitModel unit : result) {
                        if(unit.getCategories().contains(parentCategory)){
                            unitsNames.add(unit.getUnitName());
                        }
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AddItemActivity.this, android.R.layout.simple_spinner_item, unitsNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    unitsSpinner.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(AddItemActivity.this, "Failed to fetch units: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logActivityView(String activityName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = app.getEmail();

        Map<String, Object> activityView = new HashMap<>();
        activityView.put("user_id", userId);
        activityView.put("activity_name", activityName);
        activityView.put("view_time", new Timestamp(new Date()));

        db.collection("activity_views")
                .add(activityView)
                .addOnSuccessListener(documentReference -> Log.d("Firestore", "Activity view logged."))
                .addOnFailureListener(e -> Log.w("Firestore", "Error logging activity view", e));
    }

    private void logSessionDuration(String activityName, long sessionDuration) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = app.getEmail();

        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("user_id", userId);
        sessionData.put("activity_name", activityName);
        sessionData.put("session_duration", sessionDuration); // Duration in milliseconds

        db.collection("activity_sessions")
                .add(sessionData)
                .addOnSuccessListener(documentReference -> Log.d("Firestore", "Session duration logged."))
                .addOnFailureListener(e -> Log.w("Firestore", "Error logging session duration", e));
    }
    public void logUserFlow(String toActivity) {
        long sessionDuration = System.currentTimeMillis() - startTime;
        logSessionDuration("AddItemActivity", (sessionDuration));
        long transitionTime = System.currentTimeMillis();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = app.getEmail();

        Map<String, Object> userFlowData = new HashMap<>();
        userFlowData.put("user_id", userId);
        userFlowData.put("previous_activity", "AddItemActivity");
        userFlowData.put("next_activity", toActivity);
        userFlowData.put("transition_time", new Timestamp(new Date(transitionTime)));

        db.collection("user_flow")
                .add(userFlowData)
                .addOnSuccessListener(documentReference -> Log.d("Firestore", "User flow logged."))
                .addOnFailureListener(e -> Log.w("Firestore", "Error logging user flow", e));
    }

    public void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.send_request_popup, null);

        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        Button closeButton = dialogView.findViewById(R.id.finishButton);
        TextView textView = dialogView.findViewById(R.id.textView);
        TextView textView3 = dialogView.findViewById(R.id.textView3);

        textView.setText("Sucess");
        textView3.setText("Item Added Successfully");

        closeButton.setOnClickListener(v -> {
            alertDialog.dismiss();
            Intent intent = new Intent(AddItemActivity.this, HomeActivity.class);
            logUserFlow("HomeFragment");
            startActivity(intent);
            finish();
        });

        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

//    public void logUser

    @Override
    public void onStart() {
        super.onStart();
        logActivityView("AddItemActivity");
    }

    @Override
    public void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}