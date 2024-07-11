package com.example.smartstorageorganizer;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ValidateUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_validate_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.backButton).setOnClickListener(v -> {
            Intent intent = new Intent(ValidateUserActivity.this, LandingActivity.class);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.nextButton).setOnClickListener(v -> {
            showRequestSentDialog();
//            Intent intent = new Intent(ValidateUserActivity.this, DesignActivity.class);
//            startActivity(intent);
//            finish();
        });
    }

    private void showRequestSentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.request_popup, null);
        builder.setView(dialogView);

//        itemImage  = dialogView.findViewById(R.id.item_image);
//        itemName = dialogView.findViewById(R.id.item_name);
//        itemDescription = dialogView.findViewById(R.id.item_description);
//        buttonNext = dialogView.findViewById(R.id.button_next_item);

//        alertDialog = builder.create();

//        itemImage.setOnClickListener(v -> OpenGallery());
//
//        buttonNext.setOnClickListener(v -> {
//            showSuggestionPopup(itemName.getText().toString(), itemDescription.getText().toString());
//            alertDialog.dismiss();
//        });

        builder.show();
    }
}