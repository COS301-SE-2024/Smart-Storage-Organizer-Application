package com.example.smartstorageorganizer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.InputStream;

public class CodeScannerActivity extends BaseActivity {

    public static final int PICK_IMAGE = 1;
    private boolean isGroupScan = false; // Flag to track scan type

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_code_scanner);

        CardView scanButton = findViewById(R.id.scan_button);
        CardView scanGroupButton = findViewById(R.id.group_button);

        // Scan individual items (for ItemDetailsActivity)
        scanButton.setOnClickListener(view -> {
            isGroupScan = false; // Set flag to false (indicating item scan)
            IntentIntegrator integrator = new IntentIntegrator(CodeScannerActivity.this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
            integrator.setPrompt("Scan a QR Code");
            integrator.setCameraId(0);  // Use the default camera
            integrator.setBeepEnabled(true);
            integrator.setBarcodeImageEnabled(true);
            integrator.setOrientationLocked(true);  // Lock orientation to current
            integrator.initiateScan();
        });

        // Scan groups (for ViewColorsActivity)
        scanGroupButton.setOnClickListener(view -> {
            isGroupScan = true; // Set flag to true (indicating group scan)
            IntentIntegrator integrator = new IntentIntegrator(CodeScannerActivity.this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
            integrator.setPrompt("Scan a Group QR Code");
            integrator.setCameraId(0);  // Use the default camera
            integrator.setBeepEnabled(true);
            integrator.setBarcodeImageEnabled(true);
            integrator.setOrientationLocked(true);  // Lock orientation to current
            integrator.initiateScan();
        });

        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        CardView selectImageButton = findViewById(R.id.select_image_button);
        selectImageButton.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                try (InputStream inputStream = getContentResolver().openInputStream(imageUri)) {
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    scanQRCodeFromBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() == null) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                } else {
                    String scannedResult = result.getContents();
                    String format = result.getFormatName();

                    // If the format is not QR code, modify the result for barcodes
                    if (format != null && !format.equalsIgnoreCase("QR_CODE")) {
                        if (scannedResult != null && scannedResult.length() > 1) {
                            scannedResult = scannedResult.substring(0, scannedResult.length() - 1);
                        }
                    }

                    // Check the scan type based on the flag and navigate to the appropriate activity
                    if (isGroupScan) {
                        // If it's a group scan, go to ViewColorsActivity
                        Intent intent = new Intent(CodeScannerActivity.this, ViewItemActivity.class);
                        intent.putExtra("color_code_id", scannedResult);
                        intent.putExtra("category", "");
                        intent.putExtra("category_id", "");
                        startActivity(intent);
                    } else {
                        // If it's an item scan, go to ItemDetailsActivity
                        Intent intent = new Intent(CodeScannerActivity.this, ItemDetailsActivity.class);
                        intent.putExtra("item_name", "");
                        intent.putExtra("item_id", scannedResult);
                        startActivity(intent);
                    }
                    finish();
                }
            }
        }
    }

    public void scanQRCodeFromBitmap(Bitmap bitmap) {
        int[] intArray = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        RGBLuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), intArray);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

        try {
            Result result = new MultiFormatReader().decode(binaryBitmap);
            Toast.makeText(this, "Scanned: " + result.getText(), Toast.LENGTH_LONG).show();

            // Check the scan type based on the flag and navigate to the appropriate activity
            if (isGroupScan) {
                Intent intent = new Intent(CodeScannerActivity.this, ViewItemActivity.class);
                intent.putExtra("color_code_id", result.getText());
                intent.putExtra("category", "");
                intent.putExtra("category_id", "");
                startActivity(intent);
            } else {
                Intent intent = new Intent(CodeScannerActivity.this, ItemDetailsActivity.class);
                intent.putExtra("item_name", "");
                intent.putExtra("item_id", result.getText());
                startActivity(intent);
            }
            finish();
        } catch (ReaderException e) {
            Toast.makeText(this, "No QR code found in image", Toast.LENGTH_LONG).show();
        }
    }
}
