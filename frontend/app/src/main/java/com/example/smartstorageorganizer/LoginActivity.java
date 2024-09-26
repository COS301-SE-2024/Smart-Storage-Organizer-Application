package com.example.smartstorageorganizer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.amplifyframework.auth.cognito.result.AWSCognitoAuthSignOutResult;
import com.amplifyframework.core.Amplify;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.UserUtils;
import com.example.smartstorageorganizer.utils.Utils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

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
    MyAmplifyApp app;
    FirebaseFirestore db;
    FirebaseAuth auth;
    public static final String API_REQUEST = "API Request";
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        app = (MyAmplifyApp) getApplicationContext();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        initializeUI();
        signOut();
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

    public void signOut() {
        Amplify.Auth.signOut(
                signOutResult -> {
                    if (signOutResult instanceof AWSCognitoAuthSignOutResult) {
                    } else if (signOutResult instanceof AWSCognitoAuthSignOutResult.FailedSignOut) {
//                        handleFailedSignOut(((AWSCognitoAuthSignOutResult.FailedSignOut) signOutResult).getException());
                    }
                }
        );
    }

    public void checkIfSignedIn() {
        isSignedIn().thenAccept(isSignedIn -> {
            if (Boolean.TRUE.equals(isSignedIn)) {
                navigateToHome();
            } else {
                Toast.makeText(this, "User is not signed in.", Toast.LENGTH_LONG).show();
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
            signIn(emailInput, passwordInput);
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
                        checkUserVerificationStatus(email, "");
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
        app.setLoggedIn(true);
        updateActiveUser(email);
        logUserFlow("HomeActivity");
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.putExtra("email", email);
        startActivity(intent);
        finish();
    }

    private void updateActiveUser(String email) {
        Map<String, Object> activeUserData = new HashMap<>();
        activeUserData.put("last_active_time", Timestamp.now());

        db.collection("active_users").document(email)
                .set(activeUserData)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "User activity updated"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error updating user", e));
    }

    public void navigateToRegistration() {
        logUserFlow("SearchOrganizationActivity");
        Intent intent = new Intent(LoginActivity.this, SearchOrganizationActivity.class);
        startActivity(intent);
    }

    public void navigateToResetPassword() {
        logUserFlow("ResetPasswordActivity");
        Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
        startActivity(intent);
    }

    public void navigateToEmailVerification(String email) {
        logUserFlow("EmailVerificationActivity");
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
                        navigateToHome(username);
                    }
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

        closeButton.setOnClickListener(v -> {
            String emailInput = email.getText().toString().trim();
            String passwordInput = password.getText().toString().trim();
        });

        builder.show();
    }

    private void logActivityView(String activityName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = app.getEmail();

        Map<String, Object> activityView = new HashMap<>();
        activityView.put("user_id", userId);
        activityView.put("activity_name", activityName);
        activityView.put("view_time", new Timestamp(new Date()));

        db.collection("activity_views")
                .add(activityView)
                .addOnSuccessListener(documentReference -> Log.d("Firestore", "Activity view logged."))
                .addOnFailureListener(e -> Log.w("Firestore", "Error logging activity view", e));
    }

    private void logSessionDuration(String activityName, long sessionDuration) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = app.getEmail();

        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("user_id", userId);
        sessionData.put("activity_name", activityName);
        sessionData.put("session_duration", sessionDuration); // Duration in milliseconds

        db.collection("activity_sessions")
                .add(sessionData)
                .addOnSuccessListener(documentReference -> Log.d("Firestore", "Session duration logged."))
                .addOnFailureListener(e -> Log.w("Firestore", "Error logging session duration", e));
    }
    public void logUserFlow(String toActivity) {
        long sessionDuration = System.currentTimeMillis() - startTime;
        logSessionDuration("LoginActivity", (sessionDuration));
        long transitionTime = System.currentTimeMillis();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = app.getEmail();

        Map<String, Object> userFlowData = new HashMap<>();
        userFlowData.put("user_id", userId);
        userFlowData.put("previous_activity", "LoginActivity");
        userFlowData.put("next_activity", toActivity);
        userFlowData.put("transition_time", new Timestamp(new Date(transitionTime)));

        db.collection("user_flow")
                .add(userFlowData)
                .addOnSuccessListener(documentReference -> Log.d("Firestore", "User flow logged."))
                .addOnFailureListener(e -> Log.w("Firestore", "Error logging user flow", e));
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    public boolean hasInternetAccess() {
        try {
            HttpURLConnection urlConnection = (HttpURLConnection)
                    (new URL("https://www.google.com").openConnection());
            urlConnection.setRequestProperty("User-Agent", "ConnectionTest");
            urlConnection.setRequestProperty("Connection", "close");
            urlConnection.setConnectTimeout(1500); // Timeout if no internet
            urlConnection.connect();
            return (urlConnection.getResponseCode() == 200);
        } catch (IOException e) {
            Log.e(TAG, "Error checking internet connection", e);
            return false;
        }
    }


    private AlertDialog noInternetDialog;

    public void showNoInternetDialog() {
        if (noInternetDialog == null || !noInternetDialog.isShowing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Connect to a network")
                    .setMessage("To use Smart Storage Organizer, turn on mobile data or connect to Wi-Fi.")
                    .setCancelable(false)
                    .setPositiveButton("OK", (dialog, which) -> {
                        finish();
                    });
            noInternetDialog = builder.create();
            noInternetDialog.show();
        }
    }


    public void dismissNoInternetDialog() {
        if (noInternetDialog != null && noInternetDialog.isShowing()) {
            noInternetDialog.dismiss();
        }
    }

    private BroadcastReceiver networkChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isNetworkConnected()) {
                dismissNoInternetDialog();  // If connected to the internet, dismiss the popup
            } else {
                showNoInternetDialog();  // If not connected to the internet, show the popup
            }
        }
    };

    public void checkInternetAccessInBackground() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            boolean hasInternet = hasInternetAccess();  // Run this on a background thread
            runOnUiThread(() -> {
                // Update the UI based on the result
                if (hasInternet) {
                    Log.i(TAG, "Internet connection available");
                    dismissNoInternetDialog();
                } else {
                    showNoInternetDialog();
                }
            });
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        logActivityView("LoginActivity");

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, filter);

        checkInternetAccessInBackground();  // Call this to run the check off the main thread

    }

    @Override
    public void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(networkChangeReceiver);
    }
}
