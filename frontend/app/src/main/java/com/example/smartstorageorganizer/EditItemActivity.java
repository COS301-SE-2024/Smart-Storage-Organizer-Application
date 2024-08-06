package com.example.smartstorageorganizer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.example.smartstorageorganizer.utils.Utils;
import com.google.android.material.textfield.TextInputEditText;

import com.example.smartstorageorganizer.model.CategoryModel;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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

public class EditItemActivity extends AppCompatActivity {

    private ImageView ItemImage;
    private LinearLayout content;
    private LottieAnimationView loadingScreen;
    private TextInputEditText ItemName;
    private TextInputEditText ItemDescription;
    private TextInputEditText ItemQuantity;
    private Spinner categorySpinner;
    private List<CategoryModel> categoryModelList = new ArrayList<>();

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_item);

        initializeUI();
        GetDetail();
        configureInsets();
        configureButtons();
    }

    private void initializeUI() {
        ItemImage = findViewById(R.id.itemImage);
        content = findViewById(R.id.content);
        loadingScreen = findViewById(R.id.loadingScreen);
        ItemName = findViewById(R.id.name);
        ItemDescription = findViewById(R.id.description);
        ItemQuantity = findViewById(R.id.quantity);
        Save=findViewById(R.id.save_button);
    }

    public void GetDetail()
    {

        ImageUrl=(getIntent().getStringExtra("item_image"));
        Glide.with(this).load(ImageUrl).placeholder(R.drawable.no_photo).error(R.drawable.no_photo).into(ItemImage);
        ItemName.setText(getIntent().getStringExtra("item_name"));
        ItemDescription.setText(getIntent().getStringExtra("item_description"));
        ItemQuantity.setText(getIntent().getStringExtra("quantity"));

        loadingScreen.setVisibility(View.GONE);

        content.setVisibility(View.VISIBLE);

        currentItemDescription= Objects.requireNonNull(ItemDescription.getText()).toString().trim();

        currentItemName= Objects.requireNonNull(ItemName.getText()).toString().trim();

        currentQuantity=  Objects.requireNonNull(ItemQuantity.getText()).toString().trim();

        currentDraw= ItemImage.getDrawable();

    }

    private void configureInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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

    private void showUpdateSuccessMessage() {
        Toast.makeText(EditItemActivity.this, "Details Updated", Toast.LENGTH_SHORT).show();
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

        findViewById(R.id.save_button).setOnClickListener(v  -> {
            loadingScreen.setVisibility(View.VISIBLE);
            loadingScreen.playAnimation();

            UpdateDetails();
            loadingScreen.setVisibility(View.GONE);
            loadingScreen.cancelAnimation();

            Save.setEnabled(false);

        });
    }

    private void UpdateDetails()
    {


        if(!OpenGallery)
        {

            String itemName= Objects.requireNonNull(ItemName.getText()).toString().trim();
            String description=Objects.requireNonNull(ItemDescription.getText()).toString().trim();
            String quantity= Objects.requireNonNull(ItemQuantity.getText()).toString().trim();
            String barcode=getIntent().getStringExtra("barcode");
            String qrcode=getIntent().getStringExtra("qrcode");
            String location=getIntent().getStringExtra("location");
            String itemid=getIntent().getStringExtra("item_id");
            String colourcoding=getIntent().getStringExtra("colour_code");
            String parentcategory=getIntent().getStringExtra("parentcategory_id");
            String subcategory=getIntent().getStringExtra("subcategory_id");




            postEditItem(itemName, description,colourcoding,barcode,qrcode,Integer.parseInt(quantity),location,ImageUrl,Integer.parseInt(itemid), Integer.parseInt(parentcategory), Integer.parseInt(subcategory));
            Log.i("2Before Post didnt open", " whats the error");
            currentItemName=itemName;
            currentItemDescription=description;
            currentQuantity=quantity;
            showUpdateSuccessMessage();
        }
        else {
            uploadProfilePicture(ImageFile);
            ClosedGallery();
            DisableSaveButton();
        }


    }

    private void postEditItem(String itemname, String description, String colourcoding, String barcode, String qrcode, int quantity, String location, String itemimage,int itemId, int parentcategory, int subcategory) {


        String json = "{\"item_name\":\"" + itemname + "\",\"description\":\"" + description + "\" ,\"colourcoding\":\"" + colourcoding + "\",\"barcode\":\"" + barcode + "\",\"qrcode\":\"" + qrcode + "\",\"quanity\":" + quantity + ",\"location\":\"" + location + "\", \"item_id\":\"" + itemId + "\", \"item_image\": \""+itemimage+"\", \"parentcategoryid\": \""+parentcategory+"\", \"subcategoryid\": \""+subcategory+"\" }";
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.EditItemEndPoint;
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
                        Log.i("Request Method", "POST request succeeded: " + responseData);
                    });
                } else {
                    runOnUiThread(() -> Log.e("Request Method", "POST request failed: " + response.code()));
                }
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

        String parentcategory=getIntent().getStringExtra("parentcategory_id");
        String subcategory=getIntent().getStringExtra("subcategory_id");


        postEditItem(itemName, description,colourcoding,barcode,qrcode,Integer.parseInt(quantity),location,ImageUrl,Integer.parseInt(itemid), Integer.parseInt(parentcategory), Integer.parseInt(subcategory));


        currentItemName=itemName;
        currentItemDescription=description;
        currentQuantity=quantity;
        showUpdateSuccessMessage();
        return url;
    }


}