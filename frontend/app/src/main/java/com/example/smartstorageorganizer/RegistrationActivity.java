package com.example.smartstorageorganizer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;
import com.google.android.material.textfield.TextInputEditText;
import com.hbb20.CountryCodePicker;

import java.util.ArrayList;
import java.util.Locale;

public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG = "RegistrationActivity";

    private TextInputEditText nameEditText;
    private TextInputEditText surnameEditText;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private TextInputEditText phoneNumberEditText;
    private CountryCodePicker countryCodePicker;
    private LottieAnimationView buttonLoader;
    private TextView registerButtonText;
    private ImageView registerButtonIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);

        initializeViews();
        setupWindowInsets();

        findViewById(R.id.buttonRegister).setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String surname = surnameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String phone = phoneNumberEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            if (validateForm(name, surname, email, phone, password)) {
                performSignUp();
            }
        });

        findViewById(R.id.registerBackButton).setOnClickListener(v -> finish());

        findViewById(R.id.loginLink).setOnClickListener(v -> {
            Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        countryCodePicker.setCountryForPhoneCode(27);
    }

    private void initializeViews() {
        nameEditText = findViewById(R.id.name);
        surnameEditText = findViewById(R.id.surname);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        phoneNumberEditText = findViewById(R.id.phone);
        countryCodePicker = findViewById(R.id.cpp);
        buttonLoader = findViewById(R.id.buttonLoader);
        registerButtonText = findViewById(R.id.register_button_text);
        registerButtonIcon = findViewById(R.id.register_button_icon);
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    boolean validateForm(String name, String surname, String email, String phone, String password) {
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("First Name is required.");
            nameEditText.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(surname)) {
            surnameEditText.setError("Surname is required.");
            surnameEditText.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required.");
            emailEditText.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter a valid email.");
            emailEditText.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(phone)) {
            phoneNumberEditText.setError("Phone Number is required.");
            phoneNumberEditText.requestFocus();
            return false;
        }

        if (phone.length() < 9 || phone.length() > 10) {
            phoneNumberEditText.setError("Enter a valid phone number.");
            phoneNumberEditText.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required.");
            passwordEditText.requestFocus();
            return false;
        }

        if (password.length() < 8) {
            passwordEditText.setError("Password should be at least 8 characters long.");
            passwordEditText.requestFocus();
            return false;
        }

        return true;
    }

    private void performSignUp() {
        String nameText = nameEditText.getText().toString().trim();
        String surnameText = surnameEditText.getText().toString().trim();
        String emailText = emailEditText.getText().toString().trim();
        String phoneText = countryCodePicker.getSelectedCountryCodeWithPlus() + phoneNumberEditText.getText().toString().trim();
        String passwordText = passwordEditText.getText().toString().trim();

        ArrayList<AuthUserAttribute> attributes = new ArrayList<>();
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.name(), nameText));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.familyName(), surnameText));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.phoneNumber(), phoneText));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.picture(), BuildConfig.DefaultImage));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.address(), getIntent().getStringExtra("organization_id")));

        try {
            buttonLoader.setVisibility(View.VISIBLE);
            buttonLoader.playAnimation();
            registerButtonText.setVisibility(View.GONE);
            registerButtonIcon.setVisibility(View.GONE);

            Amplify.Auth.signUp(
                    emailText,
                    passwordText,
                    AuthSignUpOptions.builder().userAttributes(attributes).build(),
                    result -> {
                        Log.i("MyAmplifyApp", "Sign-up successful: " + result.toString());
                        moveToVerificationActivity(emailText, passwordText);
                    },
                    error -> {
                        Log.e("MyAmplifyApp", "Sign-up failed", error);
                        handleSignUpFailure(emailText, passwordText, error);
                    }
            );
        } catch (Exception e) {
            Log.e("MyAmplifyApp", "Sign-up exception", e);
            handleSignUpException();
        }
    }

    private void moveToVerificationActivity(String email, String password) {
        runOnUiThread(() -> {
            Intent intent = new Intent(RegistrationActivity.this, EmailVerificationActivity.class);
            intent.putExtra("email", email);
            intent.putExtra("password", password);
            intent.putExtra("type", "registration");
            startActivity(intent);
            finish();
        });
    }

    private void handleSignUpFailure(String email, String password, Exception error) {
        runOnUiThread(() -> {
            if (error.toString().toLowerCase(Locale.ROOT).contains("already exists in the system")) {
                Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show();
                moveToVerificationActivity(email, password);
            } else {
                resetSignUpButton();
                Toast.makeText(this, "Sign Up failed, please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleSignUpException() {
        runOnUiThread(() -> {
            resetSignUpButton();
            Toast.makeText(this, "Sign Up failed, please try again later.", Toast.LENGTH_LONG).show();
        });
    }

    private void resetSignUpButton() {
        buttonLoader.setVisibility(View.GONE);
        buttonLoader.pauseAnimation();
        registerButtonText.setVisibility(View.VISIBLE);
        registerButtonIcon.setVisibility(View.VISIBLE);
    }

}
