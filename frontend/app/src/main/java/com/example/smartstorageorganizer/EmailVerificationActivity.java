package com.example.smartstorageorganizer;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.amplifyframework.auth.cognito.AWSCognitoAuthSession;
import com.amplifyframework.auth.cognito.result.AWSCognitoAuthSignOutResult;
import com.amplifyframework.core.Amplify;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.UserUtils;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class EmailVerificationActivity extends AppCompatActivity {
    public static final String TAG = "AmplifyQuickstart";
    // UI Elements
    EditText inputCode1;
    EditText inputCode2;
    EditText inputCode3;
    EditText inputCode4;
    EditText inputCode5;
    EditText inputCode6;
    TextView resendOtpTextView;
    private TextView emailTextView;
    CountDownTimer countDownTimer;

    // Constants
    private static final String AUTH_QUICK_START = "AuthQuickstart";
    private static final String EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_email_verification);

        initializeUI();
        setupOTPInputs();
        startCountdown();
        configureInsets();

        emailTextView.setText(getIntent().getStringExtra(EMAIL));
        inputCode1.requestFocus();
    }

    private void initializeUI() {
        RelativeLayout buttonNext = findViewById(R.id.buttonNext);
        resendOtpTextView = findViewById(R.id.resendOtp);
        emailTextView = findViewById(R.id.textEmail);

        inputCode1 = findViewById(R.id.inputCode1);
        inputCode2 = findViewById(R.id.inputCode2);
        inputCode3 = findViewById(R.id.inputCode3);
        inputCode4 = findViewById(R.id.inputCode4);
        inputCode5 = findViewById(R.id.inputCode5);
        inputCode6 = findViewById(R.id.inputCode6);

        resendOtpTextView.setText("Resend OTP in 60 sec");

        buttonNext.setOnClickListener(v -> {
            if (validateForm()) {
                String code = collectOtpCode();
                confirmSignUp(emailTextView.getText().toString(), code);
            }
        });

        resendOtpTextView.setOnClickListener(v -> resendOtp());
    }

    private void configureInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    boolean validateForm() {
        return !TextUtils.isEmpty(inputCode1.getText().toString().trim())
                && !TextUtils.isEmpty(inputCode2.getText().toString().trim())
                && !TextUtils.isEmpty(inputCode3.getText().toString().trim())
                && !TextUtils.isEmpty(inputCode4.getText().toString().trim())
                && !TextUtils.isEmpty(inputCode5.getText().toString().trim())
                && !TextUtils.isEmpty(inputCode6.getText().toString().trim());
    }

    private String collectOtpCode() {
        return inputCode1.getText().toString().trim()
                + inputCode2.getText().toString().trim()
                + inputCode3.getText().toString().trim()
                + inputCode4.getText().toString().trim()
                + inputCode5.getText().toString().trim()
                + inputCode6.getText().toString().trim();
    }

    CompletableFuture<Boolean> signIn(String email, String password) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Amplify.Auth.signIn(
                email,
                password,
                result -> {
                    if (result.isSignedIn()) {
                        setUserToUnverified(getIntent().getStringExtra(EMAIL), "");
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

    public void handleSignInFailure(String message) {
        runOnUiThread(() -> {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        });
    }

    public void handleSignInError(Throwable error, String email, CompletableFuture<Boolean> future) {
        Log.e(TAG, error.toString());
        String errorMessage = error.toString().toLowerCase(Locale.ROOT);

        if (errorMessage.contains("user is not confirmed")) {
            Toast.makeText(this, "Failed to confirm email address. Please try again.", Toast.LENGTH_LONG).show();
//            handleUserNotConfirmed(email, future);
        } else {
            handleSignInFailure("Failed to confirm email address. Please try again.");
            future.complete(false);
        }
    }

    CompletableFuture<Boolean> confirmSignUp(String email, String code) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Amplify.Auth.confirmSignUp(
                email,
                code,
                result -> {
                    Log.i(AUTH_QUICK_START, result.isSignUpComplete() ? "Confirm signUp succeeded" : "Confirm sign up not complete");
                    future.complete(true);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Registration Successful.", Toast.LENGTH_LONG).show();
                        signIn(email, getIntent().getStringExtra("password"));
//                        showRequestSentDialog();
                    });
                },
                error -> {
                    future.complete(false);
                    Log.e(AUTH_QUICK_START, error.toString());
                    runOnUiThread(() -> Toast.makeText(this, "Wrong Verification Pin.", Toast.LENGTH_LONG).show());
                }
        );
        return future;
    }

    private void resendOtp() {
        String email = emailTextView.getText().toString();
        Log.i(EMAIL, email);
        Amplify.Auth.resendSignUpCode(
                email,
                result -> Log.i(AUTH_QUICK_START, "ResendSignUp succeeded"),
                error -> Log.e(AUTH_QUICK_START, "ResendSignUp failed", error)
        );
    }

    private void setupOTPInputs() {
        setupOtpInputListener(inputCode1, inputCode2);
        setupOtpInputListener(inputCode2, inputCode3);
        setupOtpInputListener(inputCode3, inputCode4);
        setupOtpInputListener(inputCode4, inputCode5);
        setupOtpInputListener(inputCode5, inputCode6);
    }

    private void setupOtpInputListener(EditText currentInput, EditText nextInput) {
        currentInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //perform action before text changes
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    nextInput.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //perform action after text changes
            }
        });
    }

    private void startCountdown() {
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                resendOtpTextView.setText("Resend OTP in " + millisUntilFinished / 1000 + " sec");
            }

            @Override
            public void onFinish() {
                resendOtpTextView.setEnabled(true);
                resendOtpTextView.setText("Resend OTP");
            }
        };

        resendOtpTextView.setEnabled(false);
        countDownTimer.start();
    }

    private void setUserToUnverified(String username, String authorization) {
        UserUtils.setUserToUnverified(username, authorization, this, new OperationCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (Boolean.TRUE.equals(result)) {
//                    navigateToMainActivity(username);
                    Toast.makeText(EmailVerificationActivity.this, "user unverified successful: ", Toast.LENGTH_LONG).show();
                    signOut();
                    showRequestSentDialog();
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(EmailVerificationActivity.this, "user verification failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void signOut() {
        Amplify.Auth.signOut(
                signOutResult -> {
                    if (signOutResult instanceof AWSCognitoAuthSignOutResult) {
                        showRequestSentDialog();
//                        handleSuccessfulSignOut();
                    } else if (signOutResult instanceof AWSCognitoAuthSignOutResult.FailedSignOut) {
//                        handleFailedSignOut(((AWSCognitoAuthSignOutResult.FailedSignOut) signOutResult).getException());
                    }
                }
        );
    }

    void showRequestSentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.request_popup, null);
        builder.setView(dialogView);

        Button finishButton = dialogView.findViewById(R.id.finishButton);

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmailVerificationActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
