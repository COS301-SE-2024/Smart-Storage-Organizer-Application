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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_item_info);

        itemName = findViewById(R.id.item_name);
        itemDescription = findViewById(R.id.item_description);
        itemLocation = findViewById(R.id.item_location);
        AppCompatButton editItemButton = findViewById(R.id.edit_item_button);
        ImageView backButton = findViewById(R.id.backButton);

        itemName.setText(getIntent().getStringExtra("item_name"));
        itemDescription.setText(getIntent().getStringExtra("item_description"));
        itemLocation.setText(getIntent().getStringExtra("location"));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editItemButton.setOnClickListener(v -> showEditItemPopup());

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ItemInfoActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });
    }

    public void showEditItemPopup() {
        // Create an AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Inflate the dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.edit_item_popup, null);
        builder.setView(dialogView);

        // Get the EditTexts and Button from the dialog layout
        EditText itemNameText = dialogView.findViewById(R.id.item_name);
        EditText itemDescriptionText = dialogView.findViewById(R.id.item_description);
        EditText itemLocationText = dialogView.findViewById(R.id.item_location);
        EditText itemColorCode = dialogView.findViewById(R.id.item_color_code);
        Button buttonNext = dialogView.findViewById(R.id.button_edit_item);

        itemNameText.setText(getIntent().getStringExtra("item_name"));
        itemDescriptionText.setText(getIntent().getStringExtra("item_description"));
        itemLocationText.setText(getIntent().getStringExtra("location"));
        itemColorCode.setText(getIntent().getStringExtra("color_code"));

        // Create the AlertDialog
        AlertDialog alertDialog = builder.create();

        // Set the button click listener
        buttonNext.setOnClickListener(v -> {
            String name = itemNameText.getText().toString().trim();
            String description = itemDescriptionText.getText().toString().trim();
            String location = itemLocationText.getText().toString().trim();
            String colorCode = itemColorCode.getText().toString().trim();

            postEditItem(name, description, colorCode, "asdffd",  "00111100", Integer.parseInt("1"), location, Integer.parseInt(getIntent().getStringExtra("item_id")));
            alertDialog.dismiss();
        });

        // Show the AlertDialog
        alertDialog.show();
    }

    private void postEditItem(String itemname, String description, String colourcoding, String barcode, String qrcode, int quanity, String location, int itemId ) {
        String json = "{\"item_name\":\""+itemname+"\",\"description\":\""+description+"\" ,\"colourcoding\":\""+colourcoding+"\",\"barcode\":\""+barcode+"\",\"qrcode\":\""+qrcode+"\",\"quanity\":"+quanity+",\"location\":\""+location+"\", \"item_id\":\""+itemId+"\" }";
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String apiUrl = BuildConfig.EditItemEndPoint;
        RequestBody body = RequestBody.create(json, mediaType);

        String message = "Request Method";

        Request request = new Request.Builder()
                .url(apiUrl)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Log.e(message, "POST request failed", e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    runOnUiThread(() -> {
                        runOnUiThread(() -> Log.i(message, "POST request succeeded: " + responseData));
                        itemName.setText(itemname);
                        itemDescription.setText(description);
                        itemLocation.setText(location);
                    });
                } else {
                    runOnUiThread(() -> Log.e(message, "POST request failed: " + response.code()));
                }
            }
        });
    }
}