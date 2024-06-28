package com.example.smartstorageorganizer;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;

public class ItemInfoActivity extends AppCompatActivity {
    private TextView itemName;
    private TextView itemDescription;
    private TextView itemLocation;

    private static final String TAG = "ItemInfoActivity";
    private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_item_info);

        initializeUI();
        setItemDetails();
        configureInsets();
    }

    private void initializeUI() {
        itemName = findViewById(R.id.item_name);
        itemDescription = findViewById(R.id.item_description);
        itemLocation = findViewById(R.id.item_location);
        AppCompatButton editItemButton = findViewById(R.id.edit_item_button);
        ImageView backButton = findViewById(R.id.backButton);

        editItemButton.setOnClickListener(v -> showEditItemPopup());
        backButton.setOnClickListener(v -> navigateToHome());
    }

    private void setItemDetails() {
        Intent intent = getIntent();
        itemName.setText(intent.getStringExtra("item_name"));
        itemDescription.setText(intent.getStringExtra("item_description"));
        itemLocation.setText(intent.getStringExtra("location"));
    }

    private void configureInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void navigateToHome() {
        Intent intent = new Intent(ItemInfoActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    public void showEditItemPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.edit_item_popup, null);
        builder.setView(dialogView);

        EditText itemNameText = dialogView.findViewById(R.id.item_name);
        EditText itemDescriptionText = dialogView.findViewById(R.id.item_description);
        EditText itemLocationText = dialogView.findViewById(R.id.item_location);
        EditText itemColorCode = dialogView.findViewById(R.id.item_color_code);
        Button buttonNext = dialogView.findViewById(R.id.button_edit_item);

        setDialogFields(itemNameText, itemDescriptionText, itemLocationText, itemColorCode);

        AlertDialog alertDialog = builder.create();

        buttonNext.setOnClickListener(v -> {
            String name = itemNameText.getText().toString().trim();
            String description = itemDescriptionText.getText().toString().trim();
            String location = itemLocationText.getText().toString().trim();
            String colorCode = itemColorCode.getText().toString().trim();

            int itemId = Integer.parseInt(getIntent().getStringExtra("item_id"));
            postEditItem(name, description, colorCode, "asdffd", "00111100", 1, location, itemId);
            alertDialog.dismiss();
        });

        alertDialog.show();
    }

    private void setDialogFields(EditText itemNameText, EditText itemDescriptionText, EditText itemLocationText, EditText itemColorCode) {
        Intent intent = getIntent();
        itemNameText.setText(intent.getStringExtra("item_name"));
        itemDescriptionText.setText(intent.getStringExtra("item_description"));
        itemLocationText.setText(intent.getStringExtra("location"));
        itemColorCode.setText(intent.getStringExtra("color_code"));
    }

    private void postEditItem(String itemName, String description, String colorCode, String barcode, String qrCode, int quantity, String location, int itemId) {
        String json = createJsonRequest(itemName, description, colorCode, barcode, qrCode, quantity, location, itemId);
        OkHttpClient client = new OkHttpClient();
        String apiUrl = BuildConfig.EditItemEndPoint;
        RequestBody body = RequestBody.create(json, JSON_MEDIA_TYPE);

        Request request = new Request.Builder()
                .url(apiUrl)
                .post(body)
                .build();

        client.newCall(request).enqueue(new ItemCallback(itemName, description, location));
    }

    private String createJsonRequest(String itemName, String description, String colorCode, String barcode, String qrCode, int quantity, String location, int itemId) {
        return "{\"item_name\":\"" + itemName + "\",\"description\":\"" + description + "\",\"colourcoding\":\"" + colorCode + "\",\"barcode\":\"" + barcode + "\",\"qrcode\":\"" + qrCode + "\",\"quanity\":" + quantity + ",\"location\":\"" + location + "\",\"item_id\":" + itemId + "}";
    }

    private class ItemCallback implements Callback {
        private final String itemName;
        private final String description;
        private final String location;

        ItemCallback(String itemName, String description, String location) {
            this.itemName = itemName;
            this.description = description;
            this.location = location;
        }

        @Override
        public void onFailure(Call call, IOException e) {
            runOnUiThread(() -> Log.e(TAG, "POST request failed", e));
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (response.isSuccessful()) {
                final String responseData = response.body().string();
                runOnUiThread(() -> {
                    Log.i(TAG, "POST request succeeded: " + responseData);
                    updateItemDetails(itemName, description, location);
                });
            } else {
                runOnUiThread(() -> Log.e(TAG, "POST request failed: " + response.code()));
            }
        }

        private void updateItemDetails(String itemName, String description, String location) {
            ItemInfoActivity.this.itemName.setText(itemName);
            ItemInfoActivity.this.itemDescription.setText(description);
            ItemInfoActivity.this.itemLocation.setText(location);
        }
    }
}
