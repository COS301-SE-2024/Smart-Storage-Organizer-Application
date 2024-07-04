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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ItemInfoActivity extends AppCompatActivity {
    private TextView itemName;
    private TextView itemDescription;
    private TextView itemLocation;
    private TextView itemQuantity;
    private AppCompatButton editItemButton;
    private static final String REQ = "Request Method";
    private Button increaseQuantityButton, decreaseQuantityButton;
    private int currentQuantity;
    private int itemId;
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
        itemId = Integer.parseInt(getIntent().getStringExtra("item_id"));
        itemDescription.setText(getIntent().getStringExtra("item_description"));
        itemLocation.setText(getIntent().getStringExtra("location"));
        //currentQuantity = Integer.parseInt(getIntent().getStringExtra("quantity"));
        //itemQuantity.setText(String.valueOf(currentQuantity));

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
        EditText itemName = dialogView.findViewById(R.id.item_name);
        EditText itemDescription = dialogView.findViewById(R.id.item_description);
        EditText itemLocation = dialogView.findViewById(R.id.item_location);
        EditText itemColorCode = dialogView.findViewById(R.id.item_color_code);
        Button buttonNext = dialogView.findViewById(R.id.button_edit_item);
        Button increaseQuantityButton = dialogView.findViewById(R.id.button_increase_quantity);
        Button decreaseQuantityButton = dialogView.findViewById(R.id.button_decrease_quantity);

        itemName.setText(getIntent().getStringExtra("item_name"));
        itemDescription.setText(getIntent().getStringExtra("item_description"));
        itemLocation.setText(getIntent().getStringExtra("location"));
        itemColorCode.setText(getIntent().getStringExtra("color_code"));

        // Create the AlertDialog
        AlertDialog alertDialog = builder.create();

        // Set the button click listener
        buttonNext.setOnClickListener(v -> {
            String name = itemName.getText().toString().trim();
            String description = itemDescription.getText().toString().trim();
            String location = itemLocation.getText().toString().trim();
            String colorCode = itemColorCode.getText().toString().trim();

            postEditItem(name, description, colorCode, "asdffd", "00111100", currentQuantity, location, itemId);
            alertDialog.dismiss();
        });
        increaseQuantityButton.setOnClickListener(v -> changeQuantity(itemId, "+"));
        decreaseQuantityButton.setOnClickListener(v -> changeQuantity(itemId, "-"));

        // Show the AlertDialog
        alertDialog.show();
    }

    private void postEditItem(String itemname, String description, String colourcoding, String barcode, String qrcode, int quantity, String location, int itemId) {
        String json = "{\"item_name\":\"" + itemname + "\",\"description\":\"" + description + "\" ,\"colourcoding\":\"" + colourcoding + "\",\"barcode\":\"" + barcode + "\",\"qrcode\":\"" + qrcode + "\",\"quantity\":" + quantity + ",\"location\":\"" + location + "\", \"item_id\":\"" + itemId + "\" }";
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
                        itemName.setText(itemname);
                        itemDescription.setText(description);
                        itemLocation.setText(location);
                    });
                } else {
                    runOnUiThread(() -> Log.e("Request Method", "POST request failed: " + response.code()));
                }
            }
        });
    }


    private void changeQuantity(int id, String sign) {
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.ChangeQuantityEndPoint;

        String json = "{\"item_id\":\"" + id + "\", \"sign\":\"" + sign + "\" }";
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
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
//                    runOnUiThread(() -> {
//                        Log.i("Request Method", "POST request succeeded: " + responseData);
//                        if (action.equals("increase")) {
//                            currentQuantity++;
//                        } else if (action.equals("decrease") && currentQuantity > 0) {
//                            currentQuantity--;
//                        }
//                        itemQuantity.setText(String.valueOf(currentQuantity));
//                    });
                } else {
                    runOnUiThread(() -> Log.e("Request Method", "POST request failed: " + response.code()));
                }
            }
        });
    }

}