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
               // Log.i("bonganizungu889@gmail.com "+ "+27864545691"+ "Test1"+ "Subject"+ "856 Ohio"+ "Uber1235#", "");
                //SignUp("fonew24803@javnoi.com", "+278523697", "Eze", "Testing","" , "Smart301!");
                //ConfimSignUp("fonew24803@javnoi.com","265892" );
            }
        });


    }

    public  void ConfimSignUp(String email , String Code)
    {
        Amplify.Auth.confirmSignUp(
                email,
                Code,
                result -> Log.i("AuthQuickstart", result.isSignUpComplete() ? "Confirm signUp succeeded" : "Confirm sign up not complete"),
                error -> Log.e("AuthQuickstart", error.toString())
        );
    }



    public void SignUp(String email, String CellNumber, String Name, String Surname, String Address, String Password )
    {
        // Add this line, to include the Auth plugin.
        ArrayList<AuthUserAttribute> attributes = new ArrayList<>();
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.name(), "msiya"));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.familyName(), "myom"));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.address(), "6420 ohio"));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.phoneNumber(), "+27825641238"));
       // Log.i("AuthQuickstart1", "");
//        AuthSignUpOptions options = AuthSignUpOptions.builder()
//                .userAttribute(AuthUserAttributeKey.email(), email)
//                .userAttribute(AuthUserAttributeKey.name(), Name)
//                .userAttribute(AuthUserAttributeKey.familyName(), Surname)
//                .userAttribute(AuthUserAttributeKey.address(), Address)
//                .userAttribute(AuthUserAttributeKey.phoneNumber(), CellNumber)
//                .build();
        //Log.i("AuthQuickstart2", "");
        try {
            Amplify.Auth.signUp(
                    email,
                    Password,
                    AuthSignUpOptions.builder().userAttributes(attributes).build(),
                    result -> Log.i("AuthQuickstart", result.toString()),
                    error -> Log.e("AuthQuickstart", error.toString())
            );
        } catch (Exception e) {
        Log.e("AuthSignUpError", "Exception during sign-up", e);
        //runOnUiThread(() -> Toast.makeText(SignUpActivity.this, "Exception during sign-up: " + e.getMessage(), Toast.LENGTH_SHORT).show());
         }

       // Log.i("AuthQuickstart3", "");
    }
}
