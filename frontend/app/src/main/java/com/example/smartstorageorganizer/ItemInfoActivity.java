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
    private TextView itemName, itemDescription, itemLocation;
    private AppCompatButton editItemButton;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_item_info);

        itemName = findViewById(R.id.item_name);
        itemDescription = findViewById(R.id.item_description);
        itemLocation = findViewById(R.id.item_location);
        editItemButton = findViewById(R.id.edit_item_button);
        backButton = findViewById(R.id.backButton);

        itemName.setText(getIntent().getStringExtra("item_name"));
        itemDescription.setText(getIntent().getStringExtra("item_description"));
        itemLocation.setText(getIntent().getStringExtra("location"));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditItemPopup();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemInfoActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void showEditItemPopup() {
        // Create an AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Inflate the dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.edit_item_popup, null);
        builder.setView(dialogView);

        // Get the EditTexts and Button from the dialog layout
        EditText itemName = dialogView.findViewById(R.id.item_name);
        EditText itemDescription = dialogView.findViewById(R.id.item_description);
        EditText itemLocation = dialogView.findViewById(R.id.item_location);
        EditText itemColorCode = dialogView.findViewById(R.id.item_color_code);
        Button buttonNext = dialogView.findViewById(R.id.button_edit_item);

        itemName.setText(getIntent().getStringExtra("item_name"));
        itemDescription.setText(getIntent().getStringExtra("item_description"));
        itemLocation.setText(getIntent().getStringExtra("location"));
        itemColorCode.setText(getIntent().getStringExtra("color_code"));

        // Create the AlertDialog
        AlertDialog alertDialog = builder.create();

        // Set the button click listener
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = itemName.getText().toString().trim();
                String description = itemDescription.getText().toString().trim();
                String location = itemLocation.getText().toString().trim();
                String colorCode = itemColorCode.getText().toString().trim();

                PostEditItem(name, description, colorCode, "asdffd",  "00111100", Integer.parseInt("1"), location, Integer.parseInt(getIntent().getStringExtra("item_id")));
                alertDialog.dismiss();
            }
        });

        // Show the AlertDialog
        alertDialog.show();
    }

        private void PostEditItem(String item_name, String description, String colourcoding, String barcode, String qrcode, int quanity, String location, int item_id ) {
        String json = "{\"item_name\":\""+item_name+"\",\"description\":\""+description+"\" ,\"colourcoding\":\""+colourcoding+"\",\"barcode\":\""+barcode+"\",\"qrcode\":\""+qrcode+"\",\"quanity\":"+quanity+",\"location\":\""+location+"\", \"item_id\":\""+item_id+"\" }";
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
                        runOnUiThread(() -> Log.i("Request Method", "POST request succeeded: " + responseData));
                        itemName.setText(item_name);
                        itemDescription.setText(description);
                        itemLocation.setText(location);
                    });
                } else {
                    runOnUiThread(() -> Log.e("Request Method", "POST request failed: " + response.code()));
                }
            }
        });
    }
}