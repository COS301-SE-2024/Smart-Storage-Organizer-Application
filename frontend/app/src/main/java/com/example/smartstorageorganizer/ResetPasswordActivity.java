package com.example.smartstorageorganizer;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amplifyframework.core.Amplify;
import android.util.Log;

import java.util.concurrent.CompletableFuture;

public class ResetPasswordActivity extends AppCompatActivity {
    static final String AMPLIFY_QUICK_START = "AmplifyQuickstart";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EditText etNewPassword;
        EditText etVerificationCode;
        EditText etEmail;
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password);

        etEmail = findViewById(R.id.etEmail);
        Button btnSendResetLink = findViewById(R.id.btnSendResetLink);
        etVerificationCode = findViewById(R.id.etVerificationCode);
        etNewPassword = findViewById(R.id.etNewPassword);
        Button btnResetPassword = findViewById(R.id.btnResetPassword);

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
                result -> Log.i(AMPLIFY_QUICK_START, result.toString()),
                error -> Log.e(AMPLIFY_QUICK_START, error.toString())
        );
    }

    public CompletableFuture<Boolean> resetPassword(String verificationCode, String newPassword) {
        CompletableFuture<Boolean> future=new CompletableFuture<>();
        Amplify.Auth.confirmResetPassword(
                newPassword,
                verificationCode,
                "confirmation code you received",
                () -> {Log.i(AMPLIFY_QUICK_START, "New password confirmed");  future.complete(true);},
                error -> {Log.e(AMPLIFY_QUICK_START, error.toString());  future.complete(false);}
        );
        return future;
    }
}