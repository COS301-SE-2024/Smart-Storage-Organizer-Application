package com.example.smartstorageorganizer;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.smartstorageorganizer.model.ItemModel;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class ItemDetailsActivity extends AppCompatActivity {
    private TextView itemName, itemDescription, itemUnit, itemCategory, itemColor;
    private LinearLayout expandableLayoutDescription, expandableLayoutUnit, expandableLayoutCategory, expandableLayoutColor;
    private ImageView expandArrowDescription, expandArrowUnit, expandArrowCategory, expandArrowColor;
    private ImageView itemImage;
    private int itemId;
    private String qrCodeUrl;
    private boolean isDescriptionVisible = false;
    private boolean isUnitVisible = false;
    private boolean isCategoryVisible = false;
    private boolean isColorVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        // Initialize views
        ImageView backButton = findViewById(R.id.back_button);
        ImageView qrCodeButton = findViewById(R.id.qr_code_button);

        initViews();
        setupWindowInsets();

        // Set click listeners
        expandableLayoutDescription.setOnClickListener(v -> {
            toggleVisibility(itemDescription, expandArrowDescription, isDescriptionVisible);
            isDescriptionVisible = !isDescriptionVisible;
        });

        expandableLayoutUnit.setOnClickListener(v -> {
            toggleVisibility(itemUnit, expandArrowUnit, isUnitVisible);
            isUnitVisible = !isUnitVisible;
        });

        expandableLayoutCategory.setOnClickListener(v -> {
            toggleVisibility(itemCategory, expandArrowCategory, isCategoryVisible);
            isCategoryVisible = !isCategoryVisible;
        });

        expandableLayoutColor.setOnClickListener(v -> {
            toggleVisibility(itemColor, expandArrowColor, isColorVisible);
            isColorVisible = !isColorVisible;
        });

        // Back button functionality
        backButton.setOnClickListener(v -> onBackPressed());

        // QR code button functionality
        qrCodeButton.setOnClickListener(v -> showQRCodeDialog());
    }

    private void initViews() {
        itemName = findViewById(R.id.item_name);
        itemDescription = findViewById(R.id.item_description);
        itemUnit = findViewById(R.id.item_unit);
        itemCategory = findViewById(R.id.item_category);
        itemColor = findViewById(R.id.item_color);
        itemImage = findViewById(R.id.item_image);

        expandableLayoutDescription = findViewById(R.id.expandable_layout_description);
        expandableLayoutUnit = findViewById(R.id.expandable_layout_unit);
        expandableLayoutCategory = findViewById(R.id.expandable_layout_category);
        expandableLayoutColor = findViewById(R.id.expandable_layout_color);

        expandArrowDescription = findViewById(R.id.expand_arrow_description);
        expandArrowUnit = findViewById(R.id.expand_arrow_unit);
        expandArrowCategory = findViewById(R.id.expand_arrow_category);
        expandArrowColor = findViewById(R.id.expand_arrow_color);
        if (!Objects.equals(getIntent().getStringExtra("item_name"), "")) {
            Glide.with(this)
                    .load(getIntent().getStringExtra("item_image"))
                    .placeholder(R.drawable.no_image)
                    .error(R.drawable.no_image)
                    .into(itemImage);

            itemName.setText(getIntent().getStringExtra("item_name"));
            itemId = Integer.parseInt(getIntent().getStringExtra("item_id"));
            itemDescription.setText(getIntent().getStringExtra("item_description"));
            itemUnit.setText(getIntent().getStringExtra("location"));
            itemColor.setText(getIntent().getStringExtra("color_code"));
            qrCodeUrl = getIntent().getStringExtra("item_qrcode");
            //currentQuantity = Integer.parseInt(getIntent().getStringExtra("quantity"));
            //itemQuantity.setText(String.valueOf(currentQuantity));
        }

        else {
            itemId = Integer.parseInt(getIntent().getStringExtra("item_id"));
            fetchItemDetails(itemId);
        }
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void fetchItemDetails(int itemId) {
        Utils.fetchByID(itemId,this, new OperationCallback<List<ItemModel>>() {
            @Override
            public void onSuccess(List<ItemModel> result) {
                Glide.with(ItemDetailsActivity.this)
                        .load(result.get(0).getItemImage())
                        .placeholder(R.drawable.no_image)
                        .error(R.drawable.no_image)
                        .into(itemImage);

                itemName.setText(result.get(0).getItemName());
                itemDescription.setText(result.get(0).getDescription());
                itemUnit.setText(result.get(0).getLocation());
                itemColor.setText(result.get(0).getColourCoding());
                qrCodeUrl = result.get(0).getQrcode();
                Toast.makeText(ItemDetailsActivity.this, "Item details fetched successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
//                fetchItemsLoader.setVisibility(View.GONE);
//                itemRecyclerView.setVisibility(View.VISIBLE);
                Toast.makeText(ItemDetailsActivity.this, "Failed to fetch items: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleVisibility(View view, ImageView arrow, boolean isVisible) {
        if (isVisible) {
            view.setVisibility(View.GONE);
            arrow.setImageResource(R.drawable.baseline_keyboard_arrow_down_24);
        } else {
            view.setVisibility(View.VISIBLE);
            arrow.setImageResource(R.drawable.baseline_keyboard_arrow_up_24);
        }
    }

    private void showQRCodeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View qrView = getLayoutInflater().inflate(R.layout.dialog_qr_code, null);
        builder.setView(qrView);

        ImageView qrCodeImage = qrView.findViewById(R.id.qr_code_image);
        Button shareButton = qrView.findViewById(R.id.share_button);
        Button downloadButton = qrView.findViewById(R.id.download_button);

//        String qrCodeUrl = getIntent().getStringExtra("item_qrcode");

        Glide.with(this)
                .load(qrCodeUrl)
                .placeholder(R.drawable.no_image)
                .error(R.drawable.no_image)
                .into(qrCodeImage);

        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();

        shareButton.setOnClickListener(v -> shareImage(qrCodeUrl));
        downloadButton.setOnClickListener(v -> downloadImage(qrCodeUrl));
    }

    private void shareImage(String imageUrl) {
        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        try {
                            File file = new File(getExternalCacheDir(), "shared_image.png");
                            FileOutputStream fOut = new FileOutputStream(file);
                            resource.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                            fOut.flush();
                            fOut.close();
                            file.setReadable(true, false);

                            Uri uri = FileProvider.getUriForFile(ItemDetailsActivity.this, BuildConfig.APPLICATION_ID + ".provider", file);
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra(Intent.EXTRA_STREAM, uri);
                            intent.setType("image/png");
                            startActivity(Intent.createChooser(intent, "Share image via"));
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(ItemDetailsActivity.this, "Failed to share image", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }

    private void downloadImage(String imageUrl) {
        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        try {
                            File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "SmartStorage");
                            if (!directory.exists()) {
                                directory.mkdirs();
                            }

                            File file = new File(directory, "qr_code_" + System.currentTimeMillis() + ".png");
                            FileOutputStream fOut = new FileOutputStream(file);
                            resource.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                            fOut.flush();
                            fOut.close();

                            Toast.makeText(ItemDetailsActivity.this, "Image downloaded", Toast.LENGTH_SHORT).show();

                            // Refresh gallery
                            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            Uri contentUri = Uri.fromFile(file);
                            mediaScanIntent.setData(contentUri);
                            sendBroadcast(mediaScanIntent);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(ItemDetailsActivity.this, "Failed to download image", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }
}
