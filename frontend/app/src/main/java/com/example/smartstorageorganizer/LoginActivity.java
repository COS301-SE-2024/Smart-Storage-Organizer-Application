package com.example.smartstorageorganizer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.amplifyframework.auth.cognito.exceptions.invalidstate.SignedInException;
import com.amplifyframework.auth.exceptions.InvalidStateException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;
import org.json.JSONException;
import org.json.JSONObject;
//import com.amplifyframework.auth.result.authResult;


public class LoginActivity extends AppCompatActivity {
    TextView signUpLink;
    RelativeLayout registerButton;
    TextInputEditText inputLoginEmployeeID;
    TextInputEditText inputLoginPassword;

    String Result;
    String  Error;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        signUpLink = findViewById(R.id.signUpLink);
        registerButton = findViewById(R.id.buttonLogin);
        inputLoginEmployeeID = findViewById(R.id.inputLoginEmployeeID);
        inputLoginPassword = findViewById(R.id.inputLoginPassword);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                validateForm();
                Intent intent = new Intent(LoginActivity.this, ProfileManagementActivity.class);
                startActivity(intent);
            }
        });

        signUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
                //JSONObject j=SignIn("fonew24803@javnoi.com", "Smart301!");
               // Log.i("Testing J", Result);
            }
        });
    }

    private void validateForm() {
        String employeeID = inputLoginEmployeeID.getText().toString().trim();
        String password = inputLoginPassword.getText().toString().trim();

        if (TextUtils.isEmpty(employeeID)) {
            inputLoginEmployeeID.setError("Employee ID is required");
            inputLoginEmployeeID.requestFocus();
            return;
        }
//        !Patterns.EMAIL_ADDRESS.matcher(email).matches()
        if (employeeID.length() < 8) {
            inputLoginEmployeeID.setError("Enter a valid employee ID");
            inputLoginEmployeeID.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            inputLoginPassword.setError("Password is required");
            inputLoginPassword.requestFocus();
            return;
        }

        if (password.length() < 8) {
            inputLoginPassword.setError("Password should be at least 8 characters long");
            inputLoginPassword.requestFocus();
            return;
        }

        // If all validations pass
        // Proceed with further logic (e.g., submit data to server)
        Toast.makeText(this, "Form Submitted Successfully", Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//        startActivity(intent);
    }
    public void SetErrorAndResult(String error, String Result)
    {
        this.Error=error;
        this.Result=Result;
    }

    public JSONObject SignIn(String email, String Password) {


        Amplify.Auth.signIn(
                email,
                Password,
                result -> {
                    Log.i("AuthQuickstart", result.isSignedIn() ? "Sign in succeeded" : "Sign in not complete");
                    if (result.isSignedIn()) {
                        SetErrorAndResult("","Sign in succeeded");
                    } else {
                        SetErrorAndResult("","Sign in not complete Verify Email" );
                    }
                },
                error -> {
                    Log.e("AuthQuickstart", error.toString());
                    SetErrorAndResult(error.toString(), "");
                }
        );

        String Jsonstring="{\"result\": "+Result+", \"error\" : "+Error+"}";
        JSONObject json=null;
        try {
            json = new JSONObject(Jsonstring);
        }catch(JSONException r){
            Log.i("Json Error ",r.toString());
        }
        //AuthResultCallback callback
        return json;
    }



}


