package com.example.smartstorageorganizer;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.facebook.shimmer.ShimmerFrameLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class ItemDetailsActivity extends AppCompatActivity {
    float v = 0;
    private TextView itemName, itemDescription, itemUnit, itemCategory, itemColorCode;
    private ImageView arrow, arrowUnit, arrowCategory, arrowColorCode, share;
    private boolean isExpanded = false, isUnitExpanded = false, isCategoryExpanded = false, isColorCodeExpanded = false;
    private ImageView itemImage, qrCode;
    private int itemId;
    private String qrCodeUrl;
    private CardView cardViewDescription, cardViewUnit, cardViewCategory, cardViewColorCode;
    private ShimmerFrameLayout shimmerFrameLayout;
    private ConstraintLayout detailedLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_item_details);
        initViews();
        setupWindowInsets();

        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
        detailedLayout = findViewById(R.id.detailedLayout_one);
                new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                detailedLayout.setVisibility(View.VISIBLE);
            }
        }, 2000);
        cardViewDescription.setOnClickListener(v -> {
            if (isExpanded) {
                collapse(itemDescription);
                rotateArrow(arrow, 180, 0);
            } else {
                expand(itemDescription);
                rotateArrow(arrow, 0, 180);
            }
            isExpanded = !isExpanded;
        });

        //Share button open EditItemActivity
        share = findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ItemDetailsActivity.this, EditItemActivity.class);
                intent.putExtra("item_name", itemName.getText().toString());
                intent.putExtra("item_description", itemDescription.getText().toString());
                intent.putExtra("location", itemUnit.getText().toString());
                intent.putExtra("quantity", getIntent().getStringExtra("quantity"));
                intent.putExtra("item_id", getIntent().getStringExtra("item_id"));
                intent.putExtra("item_image", getIntent().getStringExtra("item_image"));
                intent.putExtra("color_code", getIntent().getStringExtra("color_code"));
                intent.putExtra("item_qrcode", getIntent().getStringExtra("item_qrcode"));
                intent.putExtra("subcategory_id", getIntent().getStringExtra("subcategory_id"));
                intent.putExtra("parentcategory_id", getIntent().getStringExtra("parentcategory_id"));
                intent.putExtra("item_barcode", getIntent().getStringExtra("item_barcode"));

                startActivity(intent);
            }
        });

        // Initialize views for Unit accordion

        cardViewUnit.setOnClickListener(v -> {
            if (isUnitExpanded) {
                collapse(itemUnit);
                rotateArrow(arrowUnit, 180, 0);
            } else {
                expand(itemUnit);
                rotateArrow(arrowUnit, 0, 180);
            }
            isUnitExpanded = !isUnitExpanded;
        });

        // Initialize views for Category accordion

        cardViewCategory.setOnClickListener(v -> {
            if (isCategoryExpanded) {
                collapse(itemCategory);
                rotateArrow(arrowCategory, 180, 0);
            } else {
                expand(itemCategory);
                rotateArrow(arrowCategory, 0, 180);
            }
            isCategoryExpanded = !isCategoryExpanded;
        });

        // Initialize views for Color Code accordion

        cardViewColorCode.setOnClickListener(v -> {
            if (isColorCodeExpanded) {
                collapse(itemColorCode);
                rotateArrow(arrowColorCode, 180, 0);
            } else {
                expand(itemColorCode);
                rotateArrow(arrowColorCode, 0, 180);
            }
            isColorCodeExpanded = !isColorCodeExpanded;
        });

        qrCode.setOnClickListener(v -> showQRCodeDialog());

        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void expand(final View v) {
        v.measure(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? RelativeLayout.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    private void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    private void rotateArrow(ImageView arrow, float fromDegree, float toDegree) {
        RotateAnimation rotate = new RotateAnimation(fromDegree, toDegree,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.startAnimation(rotate);
    }

    private void initViews() {
        itemName = findViewById(R.id.itemName);
        itemImage = findViewById(R.id.itemImage);
        qrCode = findViewById(R.id.qrCode);

        cardViewDescription = findViewById(R.id.cardViewDescription);
        itemDescription = findViewById(R.id.itemDescription);
        arrow = findViewById(R.id.arrow);

        cardViewDescription.setTranslationX(800);
        cardViewDescription.setAlpha(v);
        cardViewDescription.animate().translationX(0).alpha(1).setDuration(400).setStartDelay(300).start();

        cardViewUnit = findViewById(R.id.cardViewUnit);
        itemUnit = findViewById(R.id.itemUnit);
        arrowUnit = findViewById(R.id.arrowUnit);

        cardViewUnit.setTranslationX(800);
        cardViewUnit.setAlpha(v);
        cardViewUnit.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(300).start();

        cardViewCategory = findViewById(R.id.cardViewCategory);
        itemCategory = findViewById(R.id.itemCategory);
        arrowCategory = findViewById(R.id.arrowCategory);

        cardViewCategory.setTranslationX(800);
        cardViewCategory.setAlpha(v);
        cardViewCategory.animate().translationX(0).alpha(1).setDuration(600).setStartDelay(300).start();

        cardViewColorCode = findViewById(R.id.cardViewColorCode);
        itemColorCode = findViewById(R.id.itemColorCode);
        arrowColorCode = findViewById(R.id.arrowColorCode);

        cardViewColorCode.setTranslationX(800);
        cardViewColorCode.setAlpha(v);
        cardViewColorCode.animate().translationX(0).alpha(1).setDuration(700).setStartDelay(300).start();


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
            itemColorCode.setText(getIntent().getStringExtra("color_code"));
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
                itemColorCode.setText(result.get(0).getColourCoding());
                qrCodeUrl = result.get(0).getQrcode();
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                detailedLayout.setVisibility(View.VISIBLE);
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