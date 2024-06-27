package com.example.smartstorageorganizer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.core.Amplify;

public class NewPasswordActivity extends AppCompatActivity {

    private EditText newPasswordField;

    private Button resetPasswordButton;
    private String email;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        newPasswordField = findViewById(R.id.newPassword);
        resetPasswordButton = findViewById(R.id.buttonConfirm);

        // Get the email from the intent
        email = getIntent().getStringExtra("email");

        resetPasswordButton.setOnClickListener(v -> {
            String newPassword = newPasswordField.getText().toString();

            if (newPassword.isEmpty()) {
                Toast.makeText(NewPasswordActivity.this, "Please fill in the password", Toast.LENGTH_LONG).show();
            }
//            else {
////                Need to Amplify.resetPass to get email and password 'newpassword'
//                resetPassword(newPassword);
//            }
        });
    }

//  Reset Password function to be implemented
}
