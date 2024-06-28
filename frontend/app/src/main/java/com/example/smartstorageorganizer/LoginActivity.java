package com.example.smartstorageorganizer;
import java.io.IOException;
import java.util.Locale;

import java.util.concurrent.CompletableFuture;
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

import com.airbnb.lottie.LottieAnimationView;
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession;

import com.google.android.material.textfield.TextInputEditText;
import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.core.Amplify;
public class LoginActivity extends AppCompatActivity {
    TextView signUpLink;
    TextView loginButtonText;
    ImageView loginButtonIcon;
    TextInputEditText email;
    TextInputEditText password;
    LottieAnimationView buttonLoader;

    String resultString;
    String errorString;

    TextView resetPasswordLink;
    RelativeLayout registerButton;
    static final String AMPLIFY_QUICK_START = "AmplifyQuickstart";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        isSignedIn().thenAccept(isSignedIn -> {
            if (Boolean.TRUE.equals(isSignedIn)) {
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(this, "User is not signed in.", Toast.LENGTH_LONG).show();
                Log.i(AMPLIFY_QUICK_START, "User is not signed in.");
            }
        });
        signUpLink = findViewById(R.id.signUpLink);
        registerButton = findViewById(R.id.buttonLogin);
        email = findViewById(R.id.inputLoginEmail);
        password = findViewById(R.id.inputLoginPassword);
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

            String email = this.email.getText().toString().trim();
            String password = this.password.getText().toString().trim();
            if(validateForm(email, password)){
                buttonLoader.setVisibility(View.VISIBLE);
                buttonLoader.playAnimation();
                loginButtonText.setVisibility(View.GONE);
                loginButtonIcon.setVisibility(View.GONE);

                signIn(email, password);
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
                        Log.i(AMPLIFY_QUICK_START, "User is signed in");
                        future.complete(true);
                    }
                    else {
                        Log.i(AMPLIFY_QUICK_START, "User is not signed in");
                        future.complete(false);
                    }},
                error -> {
                    Log.e(AMPLIFY_QUICK_START, error.toString());
                    future.completeExceptionally(error);
                }

        );
        return future;
    }

    public boolean validateForm(String email, String password){

        if (TextUtils.isEmpty(email)) {
            this.email.setError("Employee ID is required.");
            this.email.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            this.email.setError("Enter a valid email.");
            this.email.requestFocus();
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
    public void setErrorAndResult(String error, String result)
    {
        this.errorString =error;
        this.resultString =result;
    }

    public CompletableFuture<Boolean> signIn(String email, String password) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Amplify.Auth.signIn(
                email,
                password,
                result -> {
                    Log.i(AMPLIFY_QUICK_START, result.isSignedIn() ? "Sign in succeeded" : "Sign in not complete");



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
                            error -> Log.e(AMPLIFY_QUICK_START, error.toString())
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
                    Log.e(AMPLIFY_QUICK_START, error.toString());


                    if (error.toString().toLowerCase(Locale.ROOT).contains("user is not confirmed")) {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "User is not verified. Please verify your account.", Toast.LENGTH_LONG).show();
                            // You can also redirect the user to a verification page
                            Amplify.Auth.resendSignUpCode(
                                    email,
                                    result -> Log.i(AMPLIFY_QUICK_START, "ResendSignUp succeeded:"),
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
                    Log.e(AMPLIFY_QUICK_START, error.toString());
                }
        );

        return future;
    }

}
