package com.example.smartstorageorganizer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.UUID;

public class CropperActivity extends AppCompatActivity {
    String result;
    Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cropper);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        readIntent();

        String destUri = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();

        // UCrop options
        UCrop.Options options = new UCrop.Options();

        // Set UCrop with square aspect ratio (1:1)
        UCrop.of(fileUri, Uri.fromFile(new File(getCacheDir(), destUri)))
                .withOptions(options)
                .withAspectRatio(1, 1) // Ensuring a square crop
                .withMaxResultSize(800, 800) // Max result size, adjust as needed
                .start(CropperActivity.this);
    }

    private void readIntent() {
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            result = intent.getStringExtra("DATA");
            fileUri = Uri.parse(result);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            Intent returnIntent = new Intent();
            returnIntent.putExtra("RESULT", resultUri + "");
            setResult(-1, returnIntent);
            finish();
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            finish();
        }
        else {
            finish();
        }
    }
}
