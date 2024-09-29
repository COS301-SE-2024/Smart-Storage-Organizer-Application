package com.example.smartstorageorganizer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.StoragePath;
import com.amplifyframework.storage.options.StorageUploadFileOptions;
import com.bumptech.glide.Glide;
import com.example.smartstorageorganizer.model.CategoryModel;
import com.example.smartstorageorganizer.model.TokenManager;
import com.example.smartstorageorganizer.model.unitModel;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.Utils;
import com.google.android.material.textfield.TextInputEditText;

import com.example.smartstorageorganizer.model.CategoryModel;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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

public class EditItemActivity extends BaseActivity {
    private ImageView ItemImage;
    private LinearLayout content;
    private LottieAnimationView loadingScreen;
    private TextInputEditText ItemName;
    private TextInputEditText ItemDescription;
    private TextInputEditText ItemQuantity;
    private Spinner categorySpinner;
    private Spinner subcategorySpinner;
    private List<CategoryModel> categoryModelList = new ArrayList<>();
    private List<CategoryModel> subcategoryModelList = new ArrayList<>();

    private Button Save;

    private static final int PICK_IMAGE_MULTIPLE = 1;
    private static final int GALLERY_CODE = 1;
    private static final String AUTH_DEMO = "AuthDemo";

    private String currentItemName="";
    private String currentItemDescription="";
    private String currentQuantity="0";

    private Boolean OpenGallery=false;

    private Drawable currentDraw;
    private String ImageUrl="";
    private File ImageFile;
    private boolean isFirstTime = true;
    private boolean isFirstTimeParentApi = true;
    private boolean isFirstTimeSubApi = true;
    ProgressDialog progressDialog;
    private RelativeLayout moreOptionsLayout;
    private TextView moreText;
    MyAmplifyApp app;
//    private Map<String, Object> changedFields = new HashMap<>();
    private Map<String, Map<String, String>> changedFields = new HashMap<>(); // Track changed fields
    private String categoryNames;
    private String parentCategoryName;
    private String subcategoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_item);

        app = (MyAmplifyApp) getApplicationContext();

        categoryNames = getIntent().getStringExtra("CategoryName");

        String[] categories = categoryNames.split(" - ");

        if (categories.length == 2) {
            parentCategoryName = categories[0].trim();
            subcategoryName = categories[1].trim();
        }

        runOnUiThread(() -> {
            Log.d("Request Method", "Name: "+getIntent().getStringExtra("item_name"));
            Log.d("Request Method", "Description: "+getIntent().getStringExtra("item_description"));
            Log.d("Request Method", "Location: "+getIntent().getStringExtra("location"));
            Log.d("Request Method", "Color: "+getIntent().getStringExtra("color_code"));
            Log.d("Request Method", "item_id: "+getIntent().getStringExtra("item_id"));
            Log.d("Request Method", "item_image: "+getIntent().getStringExtra("item_image"));
            Log.d("Request Method", "subcategory_id: "+getIntent().getStringExtra("subcategory_id"));
            Log.d("Request Method", "parentcategory_id: "+getIntent().getStringExtra("parentcategory_id"));
            Log.d("Request Method", "qrcode: "+getIntent().getStringExtra("item_qrcode"));
            Log.d("Request Method", "barcode: "+getIntent().getStringExtra("item_barcode"));
            Log.d("Request Method", "quantity: "+getIntent().getStringExtra("quantity"));
            Log.d("Request Method", "Parent Category Name: "+getIntent().getStringExtra("parentCategory"));
            Log.d("Request Method", "Sub Category Name: "+getIntent().getStringExtra("subcategory"));
            Log.d("Request Method", "Category Name: "+getIntent().getStringExtra("CategoryName"));
            Log.d("Request Method", "Organization Id: "+getIntent().getStringExtra("organization_id"));

        });

        initializeUI();
        GetDetail();
        configureButtons();
        trackFieldChanges();
    }

    private void trackFieldChanges() {
        // Track changes for item name
        ItemName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newName = s.toString().trim();
                if (!newName.equals(currentItemName)) {
                    changedFields.put("ItemName", new HashMap<String, String>() {{
                        put("oldValue", currentItemName);
                        put("newValue", newName);
                    }});
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Track changes for item description
        ItemDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newDescription = s.toString().trim();
                if (!newDescription.equals(currentItemDescription)) {
                    changedFields.put("ItemDescription", new HashMap<String, String>() {{
                        put("oldValue", currentItemDescription);
                        put("newValue", newDescription);
                    }});
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Track changes for quantity
        ItemQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newQuantity = s.toString().trim();
                if (!newQuantity.equals(currentQuantity)) {
                    changedFields.put("ItemQuantity", new HashMap<String, String>() {{
                        put("oldValue", currentQuantity);
                        put("newValue", newQuantity);
                    }});
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        if(getIntent().getStringExtra("parentcategory_id") != null && !Objects.equals(getIntent().getStringExtra("parentcategory_id"), "-1")){
            // Track changes for category
            categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(!isFirstTimeParentApi){
                        String newCategory = categorySpinner.getSelectedItem().toString();
                        categoryNames = getIntent().getStringExtra("CategoryName");

                        String[] categories = categoryNames.split(" - ");

                        if (categories.length == 2) {
                            parentCategoryName = categories[0].trim();
                            subcategoryName = categories[1].trim();
                        }
                        String oldCategory = categories[0].trim();;
                        if (!newCategory.equals(oldCategory)) {
                            Save.setEnabled(true);
                            int selectedParentCategory = getCategoryId((String) categorySpinner.getSelectedItem(), "parent");
//                    int categoryId = getCategoryId(String.valueOf(selectedParentCategory), "parent");
                            fetchCategories(selectedParentCategory);
                            changedFields.put("Category", new HashMap<String, String>() {{
                                put("oldValue", oldCategory);
                                put("newValue", newCategory +", "+selectedParentCategory);
                            }});
                        } else if(!subcategorySpinner.getSelectedItem().toString().equals(categories[1].trim())){
                            if(getIntent().getStringExtra("parentcategory_id") != null){
                                fetchCategories(Integer.parseInt(getIntent().getStringExtra("parentcategory_id")));
                            }
                        }
                    }
                    else {
                        isFirstTimeParentApi = false;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            // Track changes for subcategory
            subcategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    categoryNames = getIntent().getStringExtra("CategoryName");

                    String[] categories = categoryNames.split(" - ");

                    if (categories.length == 2) {
                        parentCategoryName = categories[0].trim();
                        subcategoryName = categories[1].trim();
                    }
                    String newSubCategory = subcategorySpinner.getSelectedItem().toString();
                    String oldSubCategory = categories[1].trim();
                    if (!newSubCategory.equals(oldSubCategory)) {
                        Save.setEnabled(true);
                        int selectedSubCategory = getCategoryId((String) subcategorySpinner.getSelectedItem(), "sub");
                        changedFields.put("Subcategory", new HashMap<String, String>() {{
                            put("oldValue", oldSubCategory);
                            put("newValue", newSubCategory +", "+selectedSubCategory);
                        }});
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }
        else {
            categorySpinner.setVisibility(View.GONE);
            subcategorySpinner.setVisibility(View.GONE);
        }
    }

    private void initializeUI() {
        ItemImage = findViewById(R.id.itemImage);
        content = findViewById(R.id.content);
        loadingScreen = findViewById(R.id.loadingScreen);
        ItemName = findViewById(R.id.name);
        ItemDescription = findViewById(R.id.description);
        ItemQuantity = findViewById(R.id.quantity);
        categorySpinner = findViewById(R.id.categorySpinner);
        subcategorySpinner = findViewById(R.id.subcategorySpinner);
        Save=findViewById(R.id.save_button);
        moreText = findViewById(R.id.moreText);
        moreOptionsLayout = findViewById(R.id.moreOptionsLayout);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating details...");
        progressDialog.setCancelable(false);

        moreText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (moreOptionsLayout.getVisibility() == View.VISIBLE) {
                    // If the layout is visible, hide it
                    moreOptionsLayout.setVisibility(View.GONE);
                    moreText.setText("Show Fields"); // Change text to "Show Fields"
                } else {
                    // If the layout is hidden, show it
                    moreOptionsLayout.setVisibility(View.VISIBLE);
                    moreText.setText("Hide Fields"); // Change text to "Hide Fields"
                }
            }
        });
        if(getIntent().getStringExtra("parentcategory_id") != null && !Objects.equals(getIntent().getStringExtra("parentcategory_id"), "-1")){
            fetchCategories(0);
            fetchCategories(Integer.parseInt(getIntent().getStringExtra("parentcategory_id")));
        }

//        private void setupSpinnerListener() {
//        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                if (isFirstTime) {
//                    isFirstTime = false;
//                    return;
//                }
//                int categoryId = getCategoryId(parentView.getItemAtPosition(position).toString(), "parent");
//                fetchCategories(categoryId);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parentView) {
//                //Handle when nothing is not selected.
//            }
//        });
//        }
    }

    public void GetDetail()
    {

        // Set current item details for tracking
        currentItemName = getIntent().getStringExtra("item_name");
        currentItemDescription = getIntent().getStringExtra("item_description");
        currentQuantity = getIntent().getStringExtra("quantity");

        ImageUrl=(getIntent().getStringExtra("item_image"));
        Glide.with(this).load(ImageUrl).placeholder(R.drawable.no_photo).error(R.drawable.no_photo).into(ItemImage);
        ItemName.setText(getIntent().getStringExtra("item_name"));
        ItemDescription.setText(getIntent().getStringExtra("item_description"));
        ItemQuantity.setText(getIntent().getStringExtra("quantity"));

        loadingScreen.setVisibility(View.GONE);

        content.setVisibility(View.VISIBLE);

//        currentItemDescription= Objects.requireNonNull(ItemDescription.getText()).toString().trim();
//
//        currentItemName= Objects.requireNonNull(ItemName.getText()).toString().trim();
//
//        currentQuantity=  Objects.requireNonNull(ItemQuantity.getText()).toString().trim();

        currentDraw= ItemImage.getDrawable();

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

    public void UserOpenGallery()
    {
        OpenGallery=true;
    }
    public void ClosedGallery()
    {
        OpenGallery=false;
    }

    public void EnableSaveButton()
    {
        Save.setEnabled(true);
    }

    public void DisableSaveButton()
    {
        Save.setEnabled(false);
    }
    private void processImageUri(Uri uri) {
        ItemImage.setImageURI(uri);
        BitmapDrawable drawable = (BitmapDrawable) ItemImage.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        ImageFile = new File(getCacheDir(), "image.png");
        saveBitmapToFile(bitmap, ImageFile);

    }

    private void saveBitmapToFile(Bitmap bitmap, File file) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            UserOpenGallery();
            EnableSaveButton();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        textView3.setText("Item Details Updated Successfully!!!");

        closeButton.setOnClickListener(v -> {
            alertDialog.dismiss();
            showUpdateSuccessMessage();
        });

        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void showUpdateSuccessMessage() {
        int selectedParentCategory = getCategoryId((String) categorySpinner.getSelectedItem(), "parent");
        int selectedSubCategory = getCategoryId((String) subcategorySpinner.getSelectedItem(), "sub");

        if(getIntent().getStringExtra("parentcategory_id") != null && Objects.equals(getIntent().getStringExtra("parentcategory_id"), "-1")){
            selectedParentCategory = -1;
            selectedSubCategory = -1;
        }

        Intent intent = new Intent(EditItemActivity.this, ItemDetailsActivity.class);
        intent.putExtra("item_name", Objects.requireNonNull(ItemName.getText()).toString().trim());
        intent.putExtra("item_description", Objects.requireNonNull(ItemDescription.getText()).toString().trim());
        intent.putExtra("location", getIntent().getStringExtra("location"));
        intent.putExtra("color_code", getIntent().getStringExtra("color_code"));
        intent.putExtra("item_id", getIntent().getStringExtra("item_id"));
        //Change this one
        intent.putExtra("item_image", getIntent().getStringExtra("item_image"));
        intent.putExtra("subcategory_id", String.valueOf(selectedSubCategory));
        intent.putExtra("parentcategory_id", String.valueOf(selectedParentCategory));
        intent.putExtra("item_qrcode", getIntent().getStringExtra("item_qrcode"));
        intent.putExtra("item_barcode", getIntent().getStringExtra("item_barcode"));
        intent.putExtra("quantity", Objects.requireNonNull(ItemQuantity.getText()).toString().trim());

        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);  // You can pass extra data if needed
        startActivity(intent);
        finish();
    }


    private void configureButtons() {
        findViewById(R.id.editItemBackButton).setOnClickListener(v -> finish());
        ItemImage.setOnClickListener(v -> openGallery());
        ItemName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //We not performing anything before text changes
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newName = s.toString().trim();
                Save.setEnabled(!newName.equals(currentItemName));
            }

            @Override
            public void afterTextChanged(Editable s) {
                //We not performing anything after text changes
            }
        });

        ItemDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //We not performing anything before text changes
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newName = s.toString().trim();
                Save.setEnabled(!newName.equals(currentItemDescription));
            }

            @Override
            public void afterTextChanged(Editable s) {
                //We not performing anything after text changes
            }
        });

        ItemQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //We not performing anything before text changes
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newName = s.toString().trim();
                Save.setEnabled(!newName.equals(currentQuantity));
            }

            @Override
            public void afterTextChanged(Editable s) {
                //We not performing anything after text changes
            }
        });

        findViewById(R.id.save_button).setOnClickListener(v -> {
            loadingScreen.setVisibility(View.VISIBLE);
            loadingScreen.playAnimation();

            UpdateDetails();  // Save changes and upload
            loadingScreen.setVisibility(View.GONE);
            loadingScreen.cancelAnimation();
        });
    }

    private void UpdateDetails()
    {


        if(!OpenGallery)
        {

            String itemName= Objects.requireNonNull(ItemName.getText()).toString().trim();
            String description=Objects.requireNonNull(ItemDescription.getText()).toString().trim();
            String quantity= Objects.requireNonNull(ItemQuantity.getText()).toString().trim();
            String barcode=getIntent().getStringExtra("item_barcode");
            String qrcode=getIntent().getStringExtra("item_qrcode");
            String location=getIntent().getStringExtra("location");
            String itemid=getIntent().getStringExtra("item_id");
            String colourcoding=getIntent().getStringExtra("color_code");

            int selectedParentCategory = getCategoryId((String) categorySpinner.getSelectedItem(), "parent");
            int selectedSubCategory = getCategoryId((String) subcategorySpinner.getSelectedItem(), "sub");

            if(getIntent().getStringExtra("parentcategory_id") != null && Objects.equals(getIntent().getStringExtra("parentcategory_id"), "-1")){
                selectedParentCategory = -1;
                selectedSubCategory = -1;
            }

            if(Objects.equals(app.getUserRole(), "normalUser")){
                sendRequestToModifyItem();
            }
            else if (Objects.equals(app.getUserRole(), "Manager") || Objects.equals(app.getUserRole(), "Admin")){
                EditItem(itemName, description,colourcoding,barcode,qrcode,Integer.parseInt(quantity),location,ImageUrl,Integer.parseInt(itemid), selectedParentCategory, selectedSubCategory);
            }
            currentItemName=itemName;
            currentItemDescription=description;
            currentQuantity=quantity;
//            showUpdateSuccessMessage();
        }
        else {
            uploadProfilePicture(ImageFile);
            ClosedGallery();
            DisableSaveButton();
        }


    }

    private int getCategoryId(String categoryName, String type){
        if(Objects.equals(type, "parent")){
            for(CategoryModel category: categoryModelList) {
                if(Objects.equals(category.getCategoryName(), categoryName)){
                    return Integer.parseInt(category.getCategoryID());
                }
            }
        }
        else {
            for(CategoryModel category: subcategoryModelList) {
                if(Objects.equals(category.getCategoryName(), categoryName)){
                    return Integer.parseInt(category.getCategoryID());
                }
            }
        }
        return 0;
    }

    private void EditItem(String itemname, String description, String colourcoding, String barcode, String qrcode, int quantity, String location, String itemimage,int itemId, int parentcategory, int subcategory) {
        progressDialog.show();
        Utils.postEditItem(itemname, description, colourcoding, barcode, qrcode, String.valueOf(quantity), location, itemimage, String.valueOf(itemId), parentcategory, subcategory, app.getEmail(), app.getOrganizationID(), this, new OperationCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (result) {
                    progressDialog.dismiss();
                    showSuccessDialog();
                }
                // Decrement latch count
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(EditItemActivity.this, "Modify Item failed: "+parentcategory+" - "+subcategory+error, Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
//                ModifyItemDimension(itemId, width, height, depth, weight, loadbear, updown, latch);
//                latch.countDown(); // Ensure latch is decremented even in case of failure
            }
        });
    }

    CompletableFuture<Boolean> uploadProfilePicture(File profilePicture) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        StorageUploadFileOptions options = StorageUploadFileOptions.builder()
                .contentType("image/png") // Adjust based on file type
                .build();
        long time = System.nanoTime();
        String key = String.valueOf(time);
        String path = "public/ItemImages/" + key + ".png";
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

    String getObjectUrl(String key) {
        String url ="https://frontend-storage-5dbd9817acab2-dev.s3.amazonaws.com/public/ItemImages/"+key+".png";
        ImageUrl=url;
        String itemName= Objects.requireNonNull(ItemName.getText()).toString().trim();
        String description=Objects.requireNonNull(ItemDescription.getText()).toString().trim();
        String quantity= Objects.requireNonNull(ItemQuantity.getText()).toString().trim();
        String barcode=getIntent().getStringExtra("barcode");
        String qrcode=getIntent().getStringExtra("qrcode");
        String location=getIntent().getStringExtra("location");
        String itemid=getIntent().getStringExtra("item_id");
        String colourcoding=getIntent().getStringExtra("colour_code");

        int selectedParentCategory = getCategoryId((String) categorySpinner.getSelectedItem(), "parent");
        int selectedSubCategory = getCategoryId((String) subcategorySpinner.getSelectedItem(), "sub");

        if(getIntent().getStringExtra("parentcategory_id") != null && Objects.equals(getIntent().getStringExtra("parentcategory_id"), "-1")){
            selectedParentCategory = -1;
            selectedSubCategory = -1;
        }

        if(Objects.equals(app.getUserRole(), "normalUser")){
            sendRequestToModifyItem();
        }
        else if(Objects.equals(app.getUserRole(), "Manager") || Objects.equals(app.getUserRole(), "Admin")) {
            EditItem(itemName, description,colourcoding,barcode,qrcode,Integer.parseInt(quantity),location,ImageUrl,Integer.parseInt(itemid), selectedParentCategory, selectedSubCategory);
        }


        currentItemName=itemName;
        currentItemDescription=description;
        currentQuantity=quantity;
        showSuccessDialog();
        return url;
    }

    private void fetchCategories(int categoryId) {

        Utils.fetchParentCategories(categoryId, app.getEmail(), app.getOrganizationID(), this, new OperationCallback<List<CategoryModel>>() {
            @Override
            public void onSuccess(List<CategoryModel> result) {
                if (!result.isEmpty()) {
                    if (categoryId == 0) {
                        categoryModelList.clear();
                        categoryModelList = result;
                        setupSpinnerAdapter();
                    } else {
                        subcategoryModelList.clear();
                        subcategoryModelList = result;
                        setupSubcategorySpinnerAdapter();
                    }
                }
            }

            @Override
            public void onFailure(String error) {
                // Completing the future exceptionally in case of failure
            }
        });
    }

    private void setupSpinnerAdapter() {
        runOnUiThread(() -> {
            Log.d("Request Method", "Setting up Parent One");
        });
        categoryNames = getIntent().getStringExtra("CategoryName");

        String[] categories = categoryNames.split(" - ");

        if (categories.length == 2) {
            parentCategoryName = categories[0].trim();
            subcategoryName = categories[1].trim();
        }
        runOnUiThread(() -> {
            Log.d("Request Method", "Setting up Parent Two");
        });
        List<String> parentCategories = new ArrayList<>();
        parentCategories.add(categories[0].trim());
        for (CategoryModel category : categoryModelList) {
            if(!Objects.equals(category.getCategoryName(), categories[0].trim())){
                parentCategories.add(category.getCategoryName());
            }
        }
        runOnUiThread(() -> {
            Log.d("Request Method", "Setting up Parent Three");
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, parentCategories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }
    private void setupSubcategorySpinnerAdapter() {
        categoryNames = getIntent().getStringExtra("CategoryName");

        String[] categories = categoryNames.split(" - ");

        if (categories.length == 2) {
            parentCategoryName = categories[0].trim();
            subcategoryName = categories[1].trim();
        }
        runOnUiThread(() -> {
            Log.d("Request Method", "Setting up One");
        });
        List<String> subCategories = new ArrayList<>();
        if(isFirstTimeSubApi){
            subCategories.add(categories[1].trim());
        }
        runOnUiThread(() -> {
            Log.d("Request Method", "Setting up Two");
        });
        for (CategoryModel category : subcategoryModelList) {
            if(isFirstTimeSubApi){
                if(!Objects.equals(category.getCategoryName(), categories[1].trim())){
                    subCategories.add(category.getCategoryName());
                }
            }
            else {
                subCategories.add(category.getCategoryName());
            }
        }
        runOnUiThread(() -> {
            Log.d("Request Method", "Setting up Three");
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subCategories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subcategorySpinner.setAdapter(adapter);
        isFirstTimeSubApi = false;
    }

    public void sendRequestToModifyItem() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a map for the unit request
        Map<String, Object> unitRequest = new HashMap<>();
        unitRequest.put("itemId", getIntent().getStringExtra("item_id"));
        unitRequest.put("itemName", getIntent().getStringExtra("item_name"));
        unitRequest.put("itemDescription", getIntent().getStringExtra("item_description"));
        unitRequest.put("location", getIntent().getStringExtra("location"));
        unitRequest.put("image", getIntent().getStringExtra("item_image"));
        unitRequest.put("parentCategory", parentCategoryName);
        unitRequest.put("parentCategoryId", getIntent().getStringExtra("parentcategory_id"));
        unitRequest.put("colorCode", getIntent().getStringExtra("color_code"));
        unitRequest.put("subcategory", parentCategoryName);
        unitRequest.put("subcategoryId", getIntent().getStringExtra("subcategory_id"));
        unitRequest.put("qrcode", getIntent().getStringExtra("item_qrcode"));
        unitRequest.put("barcode", getIntent().getStringExtra("item_barcode"));
        unitRequest.put("quantity", getIntent().getStringExtra("quantity"));
        unitRequest.put("userEmail", app.getEmail());
        unitRequest.put("requestType", "Modify Item");
        unitRequest.put("organizationId", app.getOrganizationID());
        unitRequest.put("status", "pending");  // Initially set to pending
        unitRequest.put("requestDate", FieldValue.serverTimestamp()); // Store request date and time

        unitRequest.put("changedFields", changedFields);

        // Store the request in Firestore
        db.collection("item_requests")
                .add(unitRequest)
                .addOnSuccessListener(documentReference -> {
                    // Get the unique document ID
                    String documentId = documentReference.getId();

                    // Update the document to include the document ID or use it as a unique ID
                    db.collection("item_requests").document(documentId)
                            .update("documentId", documentId) // Store documentId within the document itself
                            .addOnSuccessListener(aVoid -> {
                                showRequestDialog();
                                Log.i("Firestore", "Request stored successfully with documentId: " + documentId);
                                future.complete(true);
                            })
                            .addOnFailureListener(e -> {
                                Log.e("Firestore", "Error updating documentId", e);
                                future.complete(false);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error storing request", e);
                    future.complete(false);
                });

    }
    public void showRequestDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.send_request_popup, null);

        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        Button closeButton = dialogView.findViewById(R.id.finishButton);

        closeButton.setOnClickListener(v -> {
            alertDialog.dismiss();
//            Intent intent = new Intent(ViewItemActivity.this, HomeActivity.class);
//            startActivity(intent);
            finish();
        });

        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

}