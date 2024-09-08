package com.example.smartstorageorganizer;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession;
import com.amplifyframework.core.Amplify;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.UserUtils;
import com.example.smartstorageorganizer.utils.Utils;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "AmplifyQuickstart";
    public TextView signUpLink;
    public TextView loginButtonText;
    public ImageView loginButtonIcon;
    TextInputEditText email;
    TextInputEditText password;
    public LottieAnimationView buttonLoader;
    public TextView resetPasswordLink;
    RelativeLayout registerButton;
    public static final String API_REQUEST = "API Request";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        initializeUI();
        checkIfSignedIn();
        configureInsets();
        setOnClickListeners();
    }

    public void initializeUI() {
        signUpLink = findViewById(R.id.signUpLink);
        registerButton = findViewById(R.id.buttonLogin);
        email = findViewById(R.id.inputLoginEmail);
        password = findViewById(R.id.inputLoginPassword);
        buttonLoader = findViewById(R.id.buttonLoader);
        loginButtonIcon = findViewById(R.id.login_button_icon);
        loginButtonText = findViewById(R.id.login_button_text);
        resetPasswordLink = findViewById(R.id.resetPasswordLink);
    }

    public void checkIfSignedIn() {
        isSignedIn().thenAccept(isSignedIn -> {
            if (Boolean.TRUE.equals(isSignedIn)) {
                navigateToHome();
            } else {
                Toast.makeText(this, "User is not signed in.", Toast.LENGTH_LONG).show();
//                Log.i(TAG, "User is not signed in.");
            }
        });
    }

    public void configureInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void setOnClickListeners() {
        registerButton.setOnClickListener(v -> attemptLogin());
        signUpLink.setOnClickListener(v -> navigateToRegistration());
        resetPasswordLink.setOnClickListener(v -> navigateToResetPassword());
    }

    public CompletableFuture<Boolean> isSignedIn() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Amplify.Auth.fetchAuthSession(
                result -> future.complete(result.isSignedIn()),
                error -> {
                    Log.e(TAG, error.toString());
                    future.completeExceptionally(error);
                }
        );
        return future;
    }

    public void attemptLogin() {
        String emailInput = email.getText().toString().trim();
        String passwordInput = password.getText().toString().trim();
        if (validateForm(emailInput, passwordInput)) {
            showLoading();
            checkUserVerificationStatus(emailInput, "");
//            signIn(emailInput, passwordInput);
        }
    }

    public void showLoading() {
        buttonLoader.setVisibility(View.VISIBLE);
        buttonLoader.playAnimation();
        loginButtonText.setVisibility(View.GONE);
        loginButtonIcon.setVisibility(View.GONE);
    }

    public void hideLoading() {
        buttonLoader.setVisibility(View.GONE);
        buttonLoader.cancelAnimation();
        loginButtonText.setVisibility(View.VISIBLE);
        loginButtonIcon.setVisibility(View.VISIBLE);
    }

    boolean validateForm(String email, String password) {
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

    CompletableFuture<Boolean> signIn(String email, String password) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Amplify.Auth.signIn(
                email,
                password,
                result -> {
                    if (result.isSignedIn()) {
                        fetchUserSession(email);
                        future.complete(true);
                    } else {
                        handleSignInFailure("Sign in not complete.");
                        future.complete(false);
                    }
                },
                error -> handleSignInError(error, email, future)
        );
        return future;
    }

    public void fetchUserSession(String email) {
        Amplify.Auth.fetchAuthSession(
                session -> {
                    AWSCognitoAuthSession cognitoSession = (AWSCognitoAuthSession) session;
                    String token = cognitoSession.getUserPoolTokensResult().getValue().getIdToken();
                    Log.i("TOKEN", token);
                    fetchUserAttributes();
                    makeApiRequest(token);
                    runOnUiThread(() -> navigateToHome(email));
                },
                error -> Log.e(TAG, error.toString())
        );
    }

    public void fetchUserAttributes() {
        Amplify.Auth.fetchUserAttributes(
                attributes -> {
                    for (AuthUserAttribute attribute : attributes) {
                        Log.i(TAG, attribute.getKey().getKeyString() + ": " + attribute.getValue());
                    }
                },
                error -> Log.e(TAG, "Failed to fetch user attributes.", error)
        );
    }

    public void makeApiRequest(String token) {
        String url = "https://0ul0kovff1.execute-api.eu-north-1.amazonaws.com/depolyment/ss-result/ViewProfile";
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(API_REQUEST, "Request failed", e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (!response.isSuccessful()) {
                    logResponseError(response);
                } else {
                    logResponseSuccess(response);
                }
            }
        });
    }

    public void logResponseError(Response response) {
        try {
            Log.e(API_REQUEST, "Failure: " + response.code() + " " + response.message() + " " + response.body().string());
        } catch (IOException e) {
            Log.e(API_REQUEST, "Error reading response", e);
        }
    }

    public void logResponseSuccess(Response response) {
        try {
            Log.i(API_REQUEST, "Success: " + response.body().string());
        } catch (IOException e) {
            Log.e(API_REQUEST, "Error reading response", e);
        }
    }

    public void handleSignInFailure(String message) {
        runOnUiThread(() -> {
            hideLoading();
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        });
    }

    public void handleSignInError(Throwable error, String email, CompletableFuture<Boolean> future) {
        Log.e(TAG, error.toString());
        String errorMessage = error.toString().toLowerCase(Locale.ROOT);

        if (errorMessage.contains("user is not confirmed")) {
            handleUserNotConfirmed(email, future);
        } else {
            handleSignInFailure("Wrong credentials.");
            future.complete(false);
        }
    }

    public void handleUserNotConfirmed(String email, CompletableFuture<Boolean> future) {
        runOnUiThread(() -> {
            Toast.makeText(this, "User is not verified. Please verify your account.", Toast.LENGTH_LONG).show();
            resendSignUpCode(email);
            navigateToEmailVerification(email);
            future.complete(true);
        });
    }

    public void resendSignUpCode(String email) {
        Amplify.Auth.resendSignUpCode(
                email,
                result -> Log.i(TAG, "ResendSignUp succeeded:"),
                error -> Log.e(TAG, "ResendSignUp failed", error)
        );
    }

    public void navigateToHome() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    public void navigateToHome(String email) {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.putExtra("email", email);
        startActivity(intent);
        finish();
    }

    public void navigateToRegistration() {
        Intent intent = new Intent(LoginActivity.this, SearchOrganizationActivity.class);
        startActivity(intent);
    }

    public void navigateToResetPassword() {
        Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
        startActivity(intent);
    }

    public void navigateToEmailVerification(String email) {
        Intent intent = new Intent(LoginActivity.this, EmailVerificationActivity.class);
        intent.putExtra("email", email);
        startActivity(intent);
    }

    public void checkUserVerificationStatus(String username, String authorization) {
        UserUtils.checkUserVerificationStatus(username, authorization, this, new OperationCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (!Objects.equals(result, "")) {
                    if(Objects.equals(result, "unverified")){
                        hideLoading();
                        showUnverifiedDialog();
                    }
                    else {
                        String emailInput = email.getText().toString().trim();
                        String passwordInput = password.getText().toString().trim();
                        signIn(emailInput, passwordInput);
                    }
//                    Toast.makeText(LoginActivity.this, "check user verification successful: "+ result, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(String error) {
                hideLoading();
                Toast.makeText(LoginActivity.this, "check user verification failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void showUnverifiedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.unverified_popup, null);
        builder.setView(dialogView);

        Button closeButton = dialogView.findViewById(R.id.closeButton);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailInput = email.getText().toString().trim();
                String passwordInput = password.getText().toString().trim();
//                setUserToVerified(emailInput, "");
            }
        });

        builder.show();
    }
}
