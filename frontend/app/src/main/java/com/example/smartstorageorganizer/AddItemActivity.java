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
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.StoragePath;
import com.amplifyframework.storage.options.StorageUploadFileOptions;
import com.example.smartstorageorganizer.adapters.RecentAdapter;
import com.example.smartstorageorganizer.model.CategoryModel;
import com.example.smartstorageorganizer.model.ItemModel;
import com.example.smartstorageorganizer.model.SearchResult;
import com.example.smartstorageorganizer.model.TokenManager;
import com.example.smartstorageorganizer.model.unitModel;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.Utils;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

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
    Spinner suggestionSpinner, colorSpinner;
    List<CategoryModel> suggestedCategory = new ArrayList<>();
    private String parentCategoryId, subcategoryId;
    private RelativeLayout categorycardView, itemDetailscardView;
    private String imageFilePath;
    ImageView itemImage;
    private List<CategoryModel> categoryModelList;
    private List<CategoryModel> subcategoryModelList;
    ProgressDialog progressDialogAddingItem;
    MyAmplifyApp app;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_item);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        app = (MyAmplifyApp) getApplicationContext();


        description = findViewById(R.id.inputDescription);
        name = findViewById(R.id.inputName);
        itemDetailscardView = findViewById(R.id.itemDetailscardView);
        categorycardView = findViewById(R.id.categorycardView);
        suggestionSpinner = findViewById(R.id.categorySpinner);
        itemImage = findViewById(R.id.item_image);

        categoryModelList = new ArrayList<>();
        subcategoryModelList = new ArrayList<>();
        findViewById(R.id.nextLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSuggestedCategory(name.getText().toString().trim(), description.getText().toString().trim());
                showSimilarItemPopup();
            }
        });

        itemImage.setOnClickListener(v -> showImagePickerDialog());

//        progressDialogAddingItem = new ProgressDialog(this);
//        progressDialogAddingItem.setMessage("Adding Item...");
//        progressDialog.setCancelable(false);

        findViewById(R.id.addButton).setOnClickListener(v -> {
//            progressDialogAddingItem.show();
            File compressedFile = compressImage(file);
            uploadItemImage(compressedFile);
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

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchResults = new ArrayList<>();

        adapter = new RecentAdapter(this, searchResults, "add");
        recyclerView.setAdapter(adapter);

        SearchForItem(name.getText().toString().trim(), "*", "*");

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                itemDetailscardView.setVisibility(View.GONE);
                itemImage.setVisibility(View.GONE);
                categorycardView.setVisibility(View.VISIBLE);
            }
        });

        alertDialog = builder.create();
        alertDialog.show();
    }

    public CompletableFuture<List<SearchResult>> SearchForItem(String target, String parentcategoryid, String subcategoryid) {
        CompletableFuture<List<SearchResult>> future = new CompletableFuture<>();
//        resultsNotFound.setVisibility(View.GONE);
//        loader.setVisibility(View.VISIBLE);
        searchResults.clear();
        adapter.notifyDataSetChanged();

        String json = "{\"target\":\"" + target + "\", \"parentcategoryid\":\"" + parentcategoryid + "\", \"subcategoryid\":\"" + subcategoryid + "\" }";
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
//                                progressDialog.dismiss();
                                loader.setVisibility(View.GONE);
                                noButton.setVisibility(View.VISIBLE);
//                                sortBySpinner.setVisibility(View.VISIBLE);
//                                loader.setVisibility(View.GONE);
                                if(searchResults.isEmpty()){
//                                    resultsNotFound.setVisibility(View.VISIBLE);
                                }
                            });

                        }catch (JSONException e) {
                            if(searchResults.isEmpty()){
                                loader.setVisibility(View.GONE);
                                noButton.setVisibility(View.VISIBLE);
//                                progressDialog.dismiss();
//                                resultsNotFound.setVisibility(View.VISIBLE);
                            }
                            throw new RuntimeException(e);
                        }

                    } else {
//                        loader.setVisibility(View.GONE);
                        future.completeExceptionally(new IOException("Search request failed: " + response.code()));
                    }
                }
            });

        }).exceptionally(ex -> {
//            loader.setVisibility(View.GONE);
            Log.e("TokenError", "Failed to get user token", ex);
            return null;
        });

        return future;
    }

    private void getSuggestedCategory(String itemName, String itemDescription) {
        Utils.fetchCategorySuggestions(itemName, itemDescription, "ezemakau@gmail.com", "1", this, new OperationCallback<List<CategoryModel>>() {
            @Override
            public void onSuccess(List<CategoryModel> result) {
                suggestedCategory.clear();
                suggestedCategory.addAll(result);
                parentCategoryId = suggestedCategory.get(0).getCategoryID();
                subcategoryId = suggestedCategory.get(1).getCategoryID();
                List<String> categories = new ArrayList<>();
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
//                            showCustomCategoryDialog(categories, adapter);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                // Hide the loading dialog and reload button after suggestions are fetched
//                progressDialog.dismiss();
//                reloadButton.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(AddItemActivity.this, "Category Fetching failed... ", Toast.LENGTH_LONG).show();

                // Hide the loading dialog and show the reload button in case of failure
//                progressDialog.dismiss();
//                reloadButton.setVisibility(View.VISIBLE);
            }
        });
    }

    public void uploadItemImage(File parentCategoryImage)
    {
        StorageUploadFileOptions options = StorageUploadFileOptions.builder()
                .contentType("image/png") // Adjust based on file type
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
        addItem(url, name.getText().toString().trim(), description.getText().toString().trim(), Integer.parseInt(subcategoryId), Integer.parseInt(parentCategoryId));

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
//                    Toast.makeText(context, "PhotoFile not null", Toast.LENGTH_SHORT).show();
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

    private void addItem(String itemImage, String itemName, String description, int category, int parentCategory) {
        ArrayList<unitModel> units = new ArrayList<>();
//        Utils.getAllUnitsForCategory(parentCategory).thenAccept(unitModels -> {
//            units.addAll(unitModels);
//            Log.i("progress", units.toString());
//            String allocated=Utils.AllocateUnitToItem(units);
//            Log.i("progress", "Allocated: "+allocated);
        Utils.postAddItem(itemImage, itemName, description, category, parentCategory, app.getEmail(),"unitRed", app.getOrganizationID(),this, new OperationCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                Toast.makeText(AddItemActivity.this, "Item Added Successfully ", Toast.LENGTH_LONG).show();
//                progressDialogAddingItem.hide();
                Intent intent = new Intent(AddItemActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(AddItemActivity.this, "Adding item failed... ", Toast.LENGTH_LONG).show();
//                progressDialogAddingItem.hide();
            }
        });
//        });

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

        return ""; // Return null if no category with the given name is found
    }
}