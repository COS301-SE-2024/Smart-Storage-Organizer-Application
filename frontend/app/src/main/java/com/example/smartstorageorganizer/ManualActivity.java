package com.example.smartstorageorganizer;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.File;
import android.view.View;
import android.widget.Button;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.widget.Toast;
import android.Manifest;

public class ManualActivity extends BaseActivity {

    public ImageView pdfImageView;
    public PdfRenderer pdfRenderer;
    public PdfRenderer.Page currentPage;
    public ParcelFileDescriptor parcelFileDescriptor;
    public int currentPageIndex = 0;
    public Button prevButton;
    public Button nextButton;
    public Button downloadButton;
    public static final int REQUEST_WRITE_STORAGE = 112;
    public static final String USER_MANUAL = "user_manual.pdf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

        initViews();
        setupPrevButton();
        setupNextButton();
        setupDownloadButton();

        try {
            openRenderer();
            showPage(currentPageIndex);  // Display the first page
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initViews() {
        pdfImageView = findViewById(R.id.pdfImageView);
        prevButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);
        downloadButton = findViewById(R.id.downloadButton);
    }

    public void setupDownloadButton() {
        downloadButton.setOnClickListener(v -> checkPermissionsAndDownload());
    }

    public void setupNextButton() {
        nextButton.setOnClickListener(v -> showPage(currentPageIndex + 1));
    }
    public void setupPrevButton() {
        prevButton.setOnClickListener(v -> showPage(currentPageIndex - 1));
    }



    public void openRenderer() throws IOException {
        // Copy the PDF file from assets to a file in the app's cache directory
        File file = new File(getCacheDir(), USER_MANUAL);
        try (InputStream asset = getAssets().open(USER_MANUAL);
             FileOutputStream output = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int size;
            while ((size = asset.read(buffer)) != -1) {
                output.write(buffer, 0, size);
            }
        }

        parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
        pdfRenderer = new PdfRenderer(parcelFileDescriptor);
    }

    public void showPage(int index) {
        if (pdfRenderer.getPageCount() <= index || index < 0) return;

        // Close the current page before opening another one.
        if (currentPage != null) {
            currentPage.close();
        }

        // Use openPage to open a specific page in PDF.
        currentPage = pdfRenderer.openPage(index);
        currentPageIndex = index;

        // Important: the destination bitmap must be ARGB (not RGB).
        Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(), Bitmap.Config.ARGB_8888);
        // Here, we render the page onto the Bitmap.
        currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        // We are ready to show the Bitmap to the user.
        pdfImageView.setImageBitmap(bitmap);

        // Update button states
        prevButton.setEnabled(currentPageIndex > 0);
        nextButton.setEnabled(currentPageIndex < pdfRenderer.getPageCount() - 1);
    }

    public void checkPermissionsAndDownload() {
        // Check if the write permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Request write permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        } else {
            // Permission already granted, download the PDF
            downloadPdf();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, download the PDF
                downloadPdf();
            } else {
                // Permission denied
                Toast.makeText(this, "Write permission is required to download the PDF", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void downloadPdf() {
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
            e.printStackTrace();
            Toast.makeText(this, "Failed to download PDF", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            if (currentPage != null) {
                currentPage.close();
            }
            pdfRenderer.close();
            parcelFileDescriptor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}