package com.example.smartstorageorganizer;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.options.AuthResetPasswordOptions;
import com.amplifyframework.core.Amplify;
import android.util.Log;

import java.util.concurrent.CompletableFuture;

public class ResetPasswordActivity extends AppCompatActivity {
    private EditText etEmail;
    private Button btnSendResetLink;
    private EditText etVerificationCode;
    private EditText etNewPassword;
    private Button btnResetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password);

        etEmail = findViewById(R.id.etEmail);
        btnSendResetLink = findViewById(R.id.btnSendResetLink);
        etVerificationCode = findViewById(R.id.etVerificationCode);
        etNewPassword = findViewById(R.id.etNewPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);

        btnSendResetLink.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (!email.isEmpty()) {
                sendResetLink(email);
            } else {
                Toast.makeText(ResetPasswordActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
            }
        });

        btnResetPassword.setOnClickListener(v -> {
            String verificationCode = etVerificationCode.getText().toString().trim();
            String newPassword = etNewPassword.getText().toString().trim();
            if (!verificationCode.isEmpty() && !newPassword.isEmpty()) {
                resetPassword(verificationCode, newPassword);
            } else {
                Toast.makeText(ResetPasswordActivity.this, "Please enter the verification code and new password", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void sendResetLink(String email) {
        Amplify.Auth.resetPassword(
                email,
                result -> {
                    Log.i("AuthQuickstart", result.toString());
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Verification code sent, please check your email.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ResetPasswordActivity.this, ResetPasswordVerificationActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();
                    });
                },
                error -> Log.e("AuthQuickstart", error.toString())
        );
    }


    public CompletableFuture<Boolean> resetPassword(String verificationCode, String newPassword) {
        CompletableFuture<Boolean> future=new CompletableFuture<>();
        Amplify.Auth.confirmResetPassword(
                newPassword,
                verificationCode,
                "confirmation code you received",
                () -> {Log.i("AuthQuickstart", "New password confirmed");  future.complete(true);},
                error -> {Log.e("AuthQuickstart", error.toString());  future.complete(false);}
        );
        return future;
    }
}