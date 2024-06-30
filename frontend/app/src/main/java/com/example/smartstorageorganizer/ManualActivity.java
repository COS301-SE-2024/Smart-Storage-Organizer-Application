package com.example.smartstorageorganizer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ManualActivity extends AppCompatActivity {

    private static final String TAG = "ManualActivity";
    private static final int REQUEST_WRITE_STORAGE = 112;
    private static final String USER_MANUAL = "user_manual.pdf";

    private ImageView pdfImageView;
    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page currentPage;
    private ParcelFileDescriptor parcelFileDescriptor;
    private int currentPageIndex = 0;
    private Button prevButton;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

        initializeViews();
        setupWindowInsets();
        setupButtonListeners();

        try {
            openRenderer();
            showPage(currentPageIndex);
        } catch (IOException e) {
            Log.e(TAG, "Error opening PDF renderer", e);
        }
    }

    private void initializeViews() {
        pdfImageView = findViewById(R.id.pdfImageView);
        prevButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);
        Button downloadButton = findViewById(R.id.downloadButton);
        downloadButton.setOnClickListener(v -> checkPermissionsAndDownload());
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupButtonListeners() {
        prevButton.setOnClickListener(v -> showPage(currentPageIndex - 1));
        nextButton.setOnClickListener(v -> showPage(currentPageIndex + 1));
    }

    private void openRenderer() throws IOException {
        File file = copyPdfToCache();
        parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
        pdfRenderer = new PdfRenderer(parcelFileDescriptor);
    }

    private File copyPdfToCache() throws IOException {
        File file = new File(getCacheDir(), USER_MANUAL);
        try (InputStream asset = getAssets().open(USER_MANUAL);
             FileOutputStream output = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int size;
            while ((size = asset.read(buffer)) != -1) {
                output.write(buffer, 0, size);
            }
        }
        return file;
    }

    private void showPage(int index) {
        if (pdfRenderer.getPageCount() <= index || index < 0) return;

        if (currentPage != null) {
            currentPage.close();
        }

        currentPage = pdfRenderer.openPage(index);
        currentPageIndex = index;

        Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(), Bitmap.Config.ARGB_8888);
        currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        pdfImageView.setImageBitmap(bitmap);

        updateButtonStates();
    }

    private void updateButtonStates() {
        prevButton.setEnabled(currentPageIndex > 0);
        nextButton.setEnabled(currentPageIndex < pdfRenderer.getPageCount() - 1);
    }

    private void checkPermissionsAndDownload() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        } else {
            downloadPdf();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_STORAGE && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            downloadPdf();
        } else {
            Toast.makeText(this, "Write permission is required to download the PDF", Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadPdf() {
        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File destinationFile = new File(downloadDir, USER_MANUAL);

        try (InputStream inputStream = getAssets().open(USER_MANUAL);
             FileOutputStream outputStream = new FileOutputStream(destinationFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            Toast.makeText(this, "PDF downloaded to " + destinationFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e(TAG, "Failed to download PDF", e);
            Toast.makeText(this, "Failed to download PDF", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        closeRenderer();
        super.onDestroy();
    }

    private void closeRenderer() {
        try {
            if (currentPage != null) {
                currentPage.close();
            }
            pdfRenderer.close();
            parcelFileDescriptor.close();
        } catch (IOException e) {
            Log.e(TAG, "Error closing PDF renderer", e);
        }
    }
}
