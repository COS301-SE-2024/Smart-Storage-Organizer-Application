package com.example.smartstorageorganizer;
import java.io.IOException;
import java.util.Locale;

import java.util.concurrent.CompletableFuture;
import java.util.logging.XMLFormatter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Callback;
import okhttp3.Call;
import okhttp3.Response;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.amplifyframework.api.rest.RestOptions;
import com.airbnb.lottie.LottieAnimationView;
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession;

import com.amplifyframework.auth.cognito.exceptions.invalidstate.SignedInException;
import com.amplifyframework.auth.cognito.result.AWSCognitoAuthSignOutResult;
import com.amplifyframework.auth.exceptions.InvalidStateException;
import com.amplifyframework.auth.result.AuthSignInResult;
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


import okhttp3.MediaType;


import okhttp3.Response;


public class LoginActivity extends AppCompatActivity {
    TextView signUpLink, loginButtonText;
    ImageView loginButtonIcon;
    public RelativeLayout registerButton;
    public  TextInputEditText Email;
    public TextInputEditText Password;
    LottieAnimationView buttonLoader;

    String Result;
    String  Error;

    TextView resetPasswordLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        isSignedIn().thenAccept(isSignedIn -> {
            if (isSignedIn) {
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(this, "User is not signed in.", Toast.LENGTH_LONG).show();
                Log.i("AmplifyQuickstart", "User is not signed in.");
            }
        });
        signUpLink = findViewById(R.id.signUpLink);
        registerButton = findViewById(R.id.buttonLogin);
        Email = findViewById(R.id.inputLoginEmail);
        Password = findViewById(R.id.inputLoginPassword);
        buttonLoader = findViewById(R.id.buttonLoader);
        loginButtonIcon = findViewById(R.id.login_button_icon);
        loginButtonText = findViewById(R.id.login_button_text);


        resetPasswordLink = findViewById(R.id.resetPasswordLink);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        registerButton.setOnClickListener(v -> {

            String email = Email.getText().toString().trim();
            String password = Password.getText().toString().trim();
            if(validateForm(email, password)){
                buttonLoader.setVisibility(View.VISIBLE);
                buttonLoader.playAnimation();
                loginButtonText.setVisibility(View.GONE);
                loginButtonIcon.setVisibility(View.GONE);

                SignIn(email, password);
            }
        });

        signUpLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });

        resetPasswordLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
            startActivity(intent);
        });
    }

    private CompletableFuture<Boolean> isSignedIn(){
        CompletableFuture<Boolean> future=new CompletableFuture<>();

        Amplify.Auth.fetchAuthSession(

                result->{
                    if(result.isSignedIn()){
                        Log.i("AmplifyQuickstart", "User is signed in");
                        future.complete(true);
                    }
                    else {
                        Log.i("AmplifyQuickstart", "User is not signed in");
                        future.complete(false);
                    }},
                error -> {
                    Log.e("AmplifyQuickstart", error.toString());
                    future.completeExceptionally(error);
                }

        );
        return future;
    }

    public boolean validateForm(String email, String password){

        if (TextUtils.isEmpty(email)) {
            Email.setError("Employee ID is required.");
            Email.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Email.setError("Enter a valid email.");
            Email.requestFocus();
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
    public void SetErrorAndResult(String error, String Result)
    {
        this.Error=error;
        this.Result=Result;
    }

    public CompletableFuture<Boolean> SignIn(String email, String Password) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Amplify.Auth.signIn(
                email,
                Password,
                result -> {
                    Log.i("AuthQuickstart", result.isSignedIn() ? "Sign in succeeded" : "Sign in not complete");



                    Amplify.Auth.fetchAuthSession(
                            session -> {
                                Amplify.Auth.fetchUserAttributes(
                                        attributes -> {
                                            for (AuthUserAttribute attribute : attributes) {

                                                Log.i("AuthDemo", attribute.getKey().getKeyString() + ": " + attribute.getValue());
                                            }
                                        },
                                        error -> Log.e("AuthDemo", "Failed to fetch user attributes.", error)
                                );
                                AWSCognitoAuthSession cognitoSession = (AWSCognitoAuthSession) session;
                                String token = cognitoSession.getUserPoolTokensResult().getValue().getIdToken();

                                String url = "https://0ul0kovff1.execute-api.eu-north-1.amazonaws.com/depolyment/ss-result/ViewProfile";

                                OkHttpClient client = new OkHttpClient();

                                Request request = new Request.Builder()
                                        .url(url)
                                        .addHeader("Authorization", token)
                                        .build();
                                client.newCall(request).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        Log.e("API Requests", "Request failed", e);
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) {
                                        if (!response.isSuccessful()) {
                                            try {
                                                Log.e("API RequestFailed", "Failure: " + response.code() + " " + response.message() + " " + response.body().string());
                                            } catch (IOException e) {
                                                Log.e("API RequestYoh", "Error reading response", e);
                                            }
                                        } else {
                                            try {
                                                Log.i("API RequestSuccess", "Success: " + response.body().string());
                                            } catch (IOException e) {
                                                Log.e("API RequestHere", "Error reading response", e);
                                            }
                                        }
                                    }
                                });

                                // Use the token to make a request to the API
                            },
                            error -> Log.e("AuthQuickstart", error.toString())
                    );

                    runOnUiThread(() -> {
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);

                        finish();
                        future.complete(true);
                    });
                },
                error -> {
                    Log.e("AuthQuickstart", error.toString());


                    if (error.toString().toLowerCase(Locale.ROOT).contains("user is not confirmed")) {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "User is not verified. Please verify your account.", Toast.LENGTH_LONG).show();
                            // You can also redirect the user to a verification page
                            Amplify.Auth.resendSignUpCode(
                                    email,
                                    result -> Log.i("AuthQuickstart", "ResendSignUp succeeded:"),
                                    errorResendCode -> Log.e("AuthQuickstart", "ResendSignUp failed", errorResendCode)


                            );
                            Intent intent = new Intent(LoginActivity.this, EmailVerificationActivity.class);
                            intent.putExtra("email", email);
                            future.complete(true);
                            startActivity(intent);

                        });
                    } else {
                        runOnUiThread(() -> {
                            buttonLoader.setVisibility(View.GONE);
                            buttonLoader.playAnimation();
                            loginButtonText.setVisibility(View.VISIBLE);
                            loginButtonIcon.setVisibility(View.VISIBLE);

                            Toast.makeText(this, "Wrong Credentials.", Toast.LENGTH_LONG).show();
                            future.complete(true);

                        });
                    }
                    Log.e("AuthQuickstart", error.toString());
                }
        );

        return future;
    }

}
