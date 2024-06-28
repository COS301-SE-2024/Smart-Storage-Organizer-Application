package com.example.smartstorageorganizer;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;
import android.util.Patterns;

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
    TextInputEditText name;
    TextInputEditText surname;
    TextInputEditText email;
    TextInputEditText password;
    TextInputEditText phoneNumber;
    TextView registerButtonText;
    ImageView registerButtonIcon;
    LottieAnimationView buttonLoader;
    CountryCodePicker cpp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);

        RelativeLayout registerButton = findViewById(R.id.buttonRegister);
        ImageView registerBackButton = findViewById(R.id.registerBackButton);
        TextView loginLink = findViewById(R.id.loginLink);
        name = findViewById(R.id.name);
        surname = findViewById(R.id.surname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        phoneNumber = findViewById(R.id.phone);
        cpp = findViewById(R.id.cpp);
        buttonLoader = findViewById(R.id.buttonLoader);
        registerButtonText = findViewById(R.id.register_button_text);
        registerButtonIcon = findViewById(R.id.register_button_icon);

        cpp.setCountryForPhoneCode(27);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        registerButton.setOnClickListener(v -> {
            String nameText = name.getText().toString().trim();
            String surnameText = surname.getText().toString().trim();
            String emailText = email.getText().toString().trim();
            String phoneText = phoneNumber.getText().toString().trim();
            String passwordText = password.getText().toString().trim();
            if (validateForm(nameText, surnameText, emailText, phoneText, passwordText)){
                nameText = name.getText().toString().trim();
                surnameText = surname.getText().toString().trim();
                emailText = email.getText().toString().trim();
                phoneText = "+27"+ phoneNumber.getText().toString().trim();
                passwordText = password.getText().toString().trim();

                buttonLoader.setVisibility(View.VISIBLE);
                buttonLoader.playAnimation();
                registerButtonText.setVisibility(View.GONE);
                registerButtonIcon.setVisibility(View.GONE);

                signUp(emailText, phoneText, nameText, surnameText, "364 Hydrogen, Hatfield", passwordText);
            }
        });

        registerBackButton.setOnClickListener(v -> finish());

        loginLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });


    }

    public boolean validateForm(String name, String surname, String email, String phone, String password) {

        if (TextUtils.isEmpty(name)) {
            this.name.setError("First Name is required.");
            this.name.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(surname)) {
            this.surname.setError("First Surname is required.");
            this.surname.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            this.email.setError("Email is required.");
            this.email.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            this.email.setError("Enter a valid email.");
            this.email.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(phone)) {
            phoneNumber.setError("Phone Number is required.");
            phoneNumber.requestFocus();
            return false;
        }

        if (phone.length() < 9|| phone.length() > 10){

            phoneNumber.setError("Enter a valid phone number.");
            phoneNumber.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            this.password.setError("Password is required.");
            this.password.requestFocus();
            return false;
        }

        if (password.length() < 8) {
            this.password.setError("Password should be at least 8 characters long.");
            this.password.requestFocus();
            return false;
        }

        return true;
    }

    public void signUp(String email, String cellNumber, String name, String surname, String address, String password)
    {
        // Add this line, to include the Auth plugin.
        ArrayList<AuthUserAttribute> attributes = new ArrayList<>();
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.name(), name));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.familyName(), surname));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.address(), address));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.phoneNumber(), cellNumber));



        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.picture(), BuildConfig.DefaultImage));
        //updated and add a build
        try {
            Amplify.Auth.signUp(
                    email,
                    password,
                    AuthSignUpOptions.builder().userAttributes(attributes).build(),
                    result -> {
                        Log.i("AuthQuickstart", result.toString());
                        //move to different page
                        runOnUiThread(() -> {
                            Intent intent = new Intent(RegistrationActivity.this, EmailVerificationActivity.class);
                            intent.putExtra("email", email);
                            startActivity(intent);
                            finish();
                        });
                    },
                    error -> {
                        //do not move to different page
                        Log.e("AuthQuickstart Error: ", error.toString());
                        if (error.toString().toLowerCase(Locale.ROOT).contains("already exists in the system")) {
                            Log.i("Message: ", "User Already Exists.");
                            runOnUiThread(() -> {
                                Intent intent = new Intent(RegistrationActivity.this, EmailVerificationActivity.class);
                                intent.putExtra("email", email);
                                startActivity(intent);
                                finish();
                            });
                        }
                    }
            );
        }
        catch (Exception e) {
            Log.e("AuthSignUpError", "Exception during sign-up", e);
            //do not change pages
            runOnUiThread(() -> {
                buttonLoader.setVisibility(View.GONE);
                buttonLoader.playAnimation();
                registerButtonText.setVisibility(View.VISIBLE);
                registerButtonIcon.setVisibility(View.VISIBLE);

                Toast.makeText(this, "Sign Up failed, please try again later.", Toast.LENGTH_LONG).show();
            });
        }

    }


}

