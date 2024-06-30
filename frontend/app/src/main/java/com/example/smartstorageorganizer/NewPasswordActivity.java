package com.example.smartstorageorganizer;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.amplifyframework.core.Amplify;


import java.util.concurrent.CompletableFuture;

public class NewPasswordActivity extends AppCompatActivity {

    private EditText newPasswordField;
    private String verificationCode;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        newPasswordField = findViewById(R.id.newPassword);
        Button resetPasswordButton = findViewById(R.id.buttonConfirm);


        String email = getIntent().getStringExtra("email");
        verificationCode = getIntent().getStringExtra("verificationCode");
        assert email != null;
        Log.i("1Email",email);
        Log.i("1code",verificationCode);

        resetPasswordButton.setOnClickListener(v -> {
            String newPassword = newPasswordField.getText().toString();


            if (newPassword.isEmpty()) {
                Toast.makeText(NewPasswordActivity.this, "Please fill in the field", Toast.LENGTH_LONG).show();
            }
            else {
                resetPassword(newPassword, verificationCode);
            }
        });
    }

//    private CompletableFuture<Boolean> confirmResetPassword(String newPassword, String verificationCode) {
//        Amplify.Auth.confirmPassword(
//                newPassword,
//                verificationCode,
//                () -> {
//                    Log.i("AuthQuickstart", "Password reset succeeded");
//                    runOnUiThread(() -> {
//                        Toast.makeText(this, "Password reset successful. Please log in.", Toast.LENGTH_LONG).show();
//                        Intent intent = new Intent(NewPasswordActivity.this, MainActivity.class);
//                        startActivity(intent);
//                        finish();
//                    });
//                },
//                error -> {
//                    Log.e("AuthQuickstart", error.toString());
//                    runOnUiThread(() -> Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show());
//                }
//        );
//    }
    public void resetPassword(String newPassword, String verificationCode) {
        CompletableFuture<Boolean> future=new CompletableFuture<>();
        Amplify.Auth.confirmResetPassword(
                newPassword,
                verificationCode,
                "confirmation code you received",
                () -> {Log.i("21AuthQuickstart", "New password confirmed");  future.complete(true);},
                error -> {Log.e("AuthQuickstart", error.toString());  future.complete(false);}
        );
    }
}
