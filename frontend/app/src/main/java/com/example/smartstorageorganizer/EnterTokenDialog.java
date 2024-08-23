package com.example.smartstorageorganizer;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class EnterTokenDialog extends Dialog {

    private final ExecutorService backgroundExecutor = Executors.newSingleThreadExecutor();
    private final OnTokenSubmitListener tokenSubmitListener;

    public EnterTokenDialog(@NonNull Context context, OnTokenSubmitListener tokenSubmitListener) {
        super(context);
        this.tokenSubmitListener = tokenSubmitListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_enter_token);

        EditText tokenEditText = findViewById(R.id.tokenEditText);
        Button copyTokenButton = findViewById(R.id.copyTokenButton);
        Button submitButton = findViewById(R.id.submitButton);

        copyTokenButton.setOnClickListener(v -> copyTokenToClipboard(tokenEditText.getContext()));
        submitButton.setOnClickListener(v -> {
            String token = tokenEditText.getText().toString();
            tokenSubmitListener.onSubmit(token);
            dismiss();
        });
    }

    private void copyTokenToClipboard(Context context) {
        backgroundExecutor.execute(() -> {
            try {
                String token = FirebaseMessaging.getInstance().getToken().getResult();
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("FCM Token", token);
                clipboard.setPrimaryClip(clip);

                // Run on UI thread
                ((MainActivity) context).runOnUiThread(() ->
                        Toast.makeText(context, "Copied local token!", Toast.LENGTH_LONG).show());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public interface OnTokenSubmitListener {
        void onSubmit(String token);
    }
}