package com.example.smartstorageorganizer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.amplifyframework.core.Amplify;

public class NewPasswordActivity extends AppCompatActivity {
    private TextInputEditText inputNewPassword, inputConfirmPassword, inputVerificationCode;
    private RelativeLayout buttonResetPassword;
    private ImageView buttonLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        // Link XML components to Java
        inputNewPassword = findViewById(R.id.inputNewPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
//        inputVerificationCode = findViewById(R.id.inputVerificationCode); // Verification code field
        buttonResetPassword = findViewById(R.id.buttonResetPassword);
        buttonLoader = findViewById(R.id.buttonLoader);

        buttonResetPassword.setOnClickListener(v -> handlePasswordReset());
    }

    private void handlePasswordReset() {
        String newPassword = inputNewPassword.getText().toString().trim();
        String confirmPassword = inputConfirmPassword.getText().toString().trim();
//        String verificationCode = inputVerificationCode.getText().toString().trim(); // Get the code
        String OTPCode = getIntent().getStringExtra("verificationCode");

        // Validate inputs
        if (TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword) || TextUtils.isEmpty(OTPCode)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show the loader and start password reset
        buttonLoader.setVisibility(View.VISIBLE);
        resetPassword(newPassword, OTPCode);
    }

//    private void resetPassword(String newPassword, String verificationCode) {
//        // Amplify password reset confirmation
//        Amplify.Auth.confirmResetPassword(
//                "Username",
//                newPassword,
//                verificationCode,
//                () -> Log.i("AuthQuickstart", "New password confirmed"),
//                error -> Log.e("AuthQuickstart", error.toString())
//        );
//    }

    private void resetPassword(String newPassword, String verificationCode) {
        // Amplify password reset confirmation
        String email = getIntent().getStringExtra("email");

        Amplify.Auth.confirmResetPassword(
                email,
                newPassword,
                verificationCode,
                () -> {
                    Log.i("AuthQuickstart", "New password confirmed");

                    // Hide loader
                    runOnUiThread(() -> buttonLoader.setVisibility(View.GONE));

                    // Navigate to the login activity
                    runOnUiThread(() -> {
                        Toast.makeText(NewPasswordActivity.this, "Password reset successful. Please log in.", Toast.LENGTH_SHORT).show();
                        // Start the login activity
                        Intent intent = new Intent(NewPasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish(); // Close NewPasswordActivity
                    });
                },
                error -> {
                    Log.e("AuthQuickstart", error.toString());

                    // Hide loader and show error message
                    runOnUiThread(() -> {
                        buttonLoader.setVisibility(View.GONE);
                        Toast.makeText(NewPasswordActivity.this, "Password reset failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    });
                }
        );
    }
}
