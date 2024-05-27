package com.example.smartstorageorganizer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.util.Log;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;

import java.util.ArrayList;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);

        RelativeLayout registerButton = findViewById(R.id.buttonRegister);
        ImageView registerBackButton = findViewById(R.id.registerBackButton);
        TextView loginLink = findViewById(R.id.loginLink);
        //SignUp("bonganizungu889@gmail.com", "0586454569", "Test1", "Subject", "856 Ohio", "Uber1235#");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
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
                    },
                    error -> {
                        Log.e("AuthQuickstart", error.toString());
                        //dont move to different page

                    }
            );
        } catch (Exception e) {
        Log.e("AuthSignUpError", "Exception during sign-up", e);
            //dont change pages
         }

    }
}

