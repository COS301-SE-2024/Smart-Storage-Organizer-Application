package com.example.smartstorageorganizer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
//import com.amplifyframework.auth.AuthUserAttribute;
//import com.amplifyframework.auth.AuthUserAttributeKey;
//import com.amplifyframework.auth.options.AuthSignUpOptions;
//import com.amplifyframework.core.Amplify;

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
    public void SignUp(String email, String CellNumber, String Name, String Surname, String Address, String Password )
    {
        ArrayList<AuthUserAttribute> attributes = new ArrayList<AuthUserAttribute>();
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.email(), email));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.phoneNumber(), CellNumber));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey. familyName(), Surname));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey. firstName(), Name));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.address(), Address));


        Amplify.Auth.signUp(
                email,
                Password,
                AuthSignUpOptions.builder().userAttributes(attributes).build(),
                result -> Log.i("AuthQuickstart", result.toString()),
                error -> Log.e("AuthQuickstart", error.toString())
        );
    }
}

