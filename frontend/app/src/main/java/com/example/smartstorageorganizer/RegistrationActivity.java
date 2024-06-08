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

                    SignUp(email, phone, name, surname, "364 Hygrogen, Hatfield", password);
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
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.picture(), "https://smart-storage-f0629f0176059-staging.s3.eu-north-1.amazonaws.com/public/ProfilePictures/617722016757185.jpeg?x-id=GetObject&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=ASIA6ODVAP7O5WY3XPVM%2F20240608%2Feu-north-1%2Fs3%2Faws4_request&X-Amz-Date=20240608T130412Z&X-Amz-Expires=604800&X-Amz-Security-Token=IQoJb3JpZ2luX2VjEH0aCmV1LW5vcnRoLTEiRzBFAiEA8Gy1rB9psNYM6qdl2OrjjulB5pd7%2FZW3umnCOziIJGMCIDSpRFgsS0ph4r0XEdKHmz2%2BbgwVa41cf5uL7dw72bj%2FKsoECBYQABoMOTkyMzgyODQ0ODkzIgwVDaZHgOHwnQFFufYqpwSqY5IwAVMBENzuZaWWTLI7JAl32T%2Frl0lGVEzXlwv%2FM1NId3XtEtvv2FCr59i6JuhRItVAvOu7c2x%2BcJqdijjlG23Ya3zxfqn1OeMeNchD1Z2uzxpMBinka0004v8kRnTqIHUieeVIHuVcnXX6du0fN%2FVCexeE%2BmOSDOvTK7yBlxKFC%2BGO%2BB0eGXFoYb%2Fh%2BWAy5IiN3u0NouLWif%2BPSciCLYl7yH%2BM4PZ9yYtjeXDHsi5pUb7X1RhaJKyCdYwtgc2mMsYMlzMElRG6oa6w8ahJrma%2FvnZCH7NGQEiXgF7Vl%2BgLctRRgXQxTRGv2tv2hSjj9nv5HRANsV2pg4tBVBaR6OlzJZSdF%2BSW7KO72JJRCVTGiVKGGjJbv2XUcYLF1NIrVW4%2FN507ydUf7d%2FqLYg%2Fx%2F8YcAi3XhpiJa86wHI1HSsiyzayI6Oz5nQtPZRmBtjYudfwHOjyhXpckrVc5VFTWCA00j4fWbiesmCcIrr4Bgh5QIXjM4s2sINOqS6GHSJKT6Ak0bwdSZCGUd9j3Q4r1FAloIGtX4L%2FmC%2FeSqn%2B1bt1kerabukQJ1mEfiwItshznR8I38gO1LmvsUX59XMG4%2FZ8D%2BZyzLslVHh9hjGDJURoZNahIGJFgRNqejiuwa8VAD4ImFtMZ69aPWvdTLYBqzsX1rnLzQCbbcDdg08r2rBhxgMzvwIN6DYrFC0kRkBI0aGcCPHKAQ30smwlJX0ceiBt1NPZ8TCirZGzBjqFAmqYm5jm3l0aU2zEkpeRTfq%2FnwH2dkTo1PMEARqPhoyxzL4ycqBSEVLI619MMegL4CPRY6OU9OOT3Eitb%2Fum4S85ZZzdm0PKRuikXu1RiJ9PSfkJCLXjWSrp5TAtr0NRkQVM4MYYpVQgSd6mtwJydv36RXhIh6aai6LkMSlScW8nztOqgE4P9uXyjENZIegCekYA%2FyknIIZuFL0G9Oqs8A19yHs%2BuptAYbnT%2F6GFDUkC9xXWhdgiPc4TqG8lEdGFCcgQQPQCvIgVOIf1TTj2Oe1pluxWiu3YWSKbGmx5p4AAQny2N2S06Stylbx9si7sLRXjtwN0r3E%2BMUC9mbA58yleWqUriw%3D%3D&X-Amz-SignedHeaders=host&X-Amz-Signature=0619ddd174294791a53cbd8fa2a33e007e6a2ae90cf200e3b4f6f8dd73eaa80a"));
        //we need to find a default picure
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

