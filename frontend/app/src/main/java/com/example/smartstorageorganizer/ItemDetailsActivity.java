package com.example.smartstorageorganizer;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

public class ItemDetailsActivity extends AppCompatActivity {
    private TextView itemName, itemDescription, itemUnit, itemCategory, itemColor;
    private LinearLayout expandableLayoutDescription, expandableLayoutUnit, expandableLayoutCategory, expandableLayoutColor;
    private ImageView expandArrowDescription, expandArrowUnit, expandArrowCategory, expandArrowColor;
    private ImageView itemImage;
    private int itemId;
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
        //currentQuantity = Integer.parseInt(getIntent().getStringExtra("quantity"));
        //itemQuantity.setText(String.valueOf(currentQuantity));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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

        Glide.with(this)
                .load(getIntent().getStringExtra("item_qrcode"))
                .placeholder(R.drawable.no_image)
                .error(R.drawable.no_image)
                .into(qrCodeImage);

        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}
