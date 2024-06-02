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
import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.Call;
import okhttp3.Callback;

public class RegistrationActivity extends AppCompatActivity {
    TextInputEditText Name, Surname, Email, Password, PhoneNumber;
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
        Name = findViewById(R.id.name);
        Surname = findViewById(R.id.surname);
        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        PhoneNumber = findViewById(R.id.phone);
        cpp = findViewById(R.id.cpp);
        buttonLoader = findViewById(R.id.buttonLoader);
        registerButtonText = findViewById(R.id.register_button_text);
        registerButtonIcon = findViewById(R.id.register_button_icon);

        cpp.setCountryForPhoneCode(27);


        //SignUp("bonganizungu889@gmail.com", "0586454569", "Test1", "Subject", "856 Ohio", "Uber1235#");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()){
                    String name = Name.getText().toString().trim();
                    String surname = Surname.getText().toString().trim();
                    String email = Email.getText().toString().trim();
                    String phone = "+27"+PhoneNumber.getText().toString().trim();
                    String password = Password.getText().toString().trim();

                    buttonLoader.setVisibility(View.VISIBLE);
                    buttonLoader.playAnimation();
                    registerButtonText.setVisibility(View.GONE);
                    registerButtonIcon.setVisibility(View.GONE);

                    SignUp(email, phone, name, surname, "856 Hydrogen Street", password);
                }
            }
        });

        registerBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    private boolean validateForm() {
        String name = Name.getText().toString().trim();
        String surname = Surname.getText().toString().trim();
        String email = Email.getText().toString().trim();
        String phone = PhoneNumber.getText().toString().trim();
        String password = Password.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Name.setError("First Name is required.");
            Name.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(surname)) {
            Surname.setError("First Surname is required.");
            Surname.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            Email.setError("Email is required.");
            Email.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Email.setError("Enter a valid email.");
            Email.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(phone)) {
            PhoneNumber.setError("Phone Number is required.");
            PhoneNumber.requestFocus();
            return false;
        }

        if (phone.length() < 9|| phone.length() > 10){

            PhoneNumber.setError("Enter a valid phone number.");
            PhoneNumber.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            Password.setError("Password is required.");
            Password.requestFocus();
            return false;
        }

        if (password.length() < 8) {
            Password.setError("Password should be at least 8 characters long.");
            Password.requestFocus();
            return false;
        }

        return true;
    }

    public  void ConfimSignUp(String email , String Code)
    {
        Amplify.Auth.confirmSignUp(
                email,
                Code,
                result ->{ Log.i("AuthQuickstart", result.isSignUpComplete() ? "Confirm signUp succeeded" : "Confirm sign up not complete");
                    //change to new page
                },
                error ->{ Log.e("AuthQuickstart", error.toString());
                    //dont change to different page
                }
        );
    }



    public void SignUp(String email, String CellNumber, String Name, String Surname, String Address, String Password )
    {
        // Add this line, to include the Auth plugin.
        ArrayList<AuthUserAttribute> attributes = new ArrayList<>();
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.name(), Name));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.familyName(), Surname));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.address(), Address));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.phoneNumber(), CellNumber));

        try {
            Amplify.Auth.signUp(
                    email,
                    Password,
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
//            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
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

