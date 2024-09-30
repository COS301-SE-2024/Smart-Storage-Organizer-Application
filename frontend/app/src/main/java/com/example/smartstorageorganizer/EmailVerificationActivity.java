package com.example.smartstorageorganizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
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

import com.airbnb.lottie.LottieAnimationView;
import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession;
import com.amplifyframework.auth.cognito.result.AWSCognitoAuthSignOutResult;
import com.amplifyframework.core.Amplify;
import com.example.smartstorageorganizer.model.OrganizationModel;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.OrganizationUtils;
import com.example.smartstorageorganizer.utils.UserUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class EmailVerificationActivity extends BaseActivity {
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
    LottieAnimationView button_animation_otp;
    TextView next_button_text_otp;
    ProgressDialog progressDialog;

    // Constants
    private static final String AUTH_QUICK_START = "AuthQuickstart";
    private static final String EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_email_verification);

        initializeUI();
        signOut();
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
        button_animation_otp = findViewById(R.id.button_animation_otp);
        next_button_text_otp = findViewById(R.id.next_button_text_otp);

        inputCode1 = findViewById(R.id.inputCode1);
        inputCode2 = findViewById(R.id.inputCode2);
        inputCode3 = findViewById(R.id.inputCode3);
        inputCode4 = findViewById(R.id.inputCode4);
        inputCode5 = findViewById(R.id.inputCode5);
        inputCode6 = findViewById(R.id.inputCode6);

        resendOtpTextView.setText("Resend OTP in 60 sec");

        buttonNext.setOnClickListener(v -> {
            if (validateForm()) {
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Verifying Email...");
                progressDialog.setCancelable(false);

                progressDialog.show();

                next_button_text_otp.setVisibility(View.GONE);
                button_animation_otp.setVisibility(View.VISIBLE);
                String code = collectOtpCode();
                confirmSignUp(emailTextView.getText().toString(), code);
            }
        });

        resendOtpTextView.setOnClickListener(v -> resendOtp());
    }

    public void signOut() {
        Amplify.Auth.signOut(
                signOutResult -> {
                    if (signOutResult instanceof AWSCognitoAuthSignOutResult) {
//                        handleSuccessfulSignOut();
                    } else if (signOutResult instanceof AWSCognitoAuthSignOutResult.FailedSignOut) {
//                        handleFailedSignOut(((AWSCognitoAuthSignOutResult.FailedSignOut) signOutResult).getException());
                    }
                }
        );
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
                        if(Objects.equals(getIntent().getStringExtra("type"), "registration")){
                            setUserToUnverified(getIntent().getStringExtra(EMAIL), "");
                        }
                        else {
                            setUserRole("ezemakau@gmail.com", "Manager", getIntent().getStringExtra(EMAIL));
//                            setUserToVerified(getIntent().getStringExtra(EMAIL), "");
                        }
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
            Toast.makeText(this, "Failed: "+error, Toast.LENGTH_LONG).show();
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
//                        Toast.makeText(this, "Registration Successful.", Toast.LENGTH_LONG).show();
//                        new Handler().postDelayed(() -> {
                        checkIfSignedIn(email, getIntent().getStringExtra("password"));
//                        }, 2000);

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
                    progressDialog.dismiss();
                    next_button_text_otp.setVisibility(View.VISIBLE);
                    button_animation_otp.setVisibility(View.GONE);
//                    Toast.makeText(EmailVerificationActivity.this, "user unverified successful: ", Toast.LENGTH_LONG).show();
                    if(Objects.equals(getIntent().getStringExtra("type"), "registration")){
                        showRequestSentDialog();
                    }
                    else {
                        showOrganizationDialog();
                    }
                }
            }

            @Override
            public void onFailure(String error) {
                next_button_text_otp.setVisibility(View.VISIBLE);
                button_animation_otp.setVisibility(View.GONE);
                Toast.makeText(EmailVerificationActivity.this, "user verification failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setUserToVerified(String username, String authorization) {
        UserUtils.setUserToVerified(username, authorization, this, new OperationCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                progressDialog.dismiss();
                next_button_text_otp.setVisibility(View.VISIBLE);
                button_animation_otp.setVisibility(View.GONE);
                if(Objects.equals(getIntent().getStringExtra("type"), "registration")){
                    showRequestSentDialog();
                }
                else {
                    showOrganizationDialog();
                }
//                Toast.makeText(EmailVerificationActivity.this, "User approved successfully", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(String error) {
                next_button_text_otp.setVisibility(View.VISIBLE);
                button_animation_otp.setVisibility(View.GONE);
                Toast.makeText(EmailVerificationActivity.this, "User approval failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setUserRole(String username, String role, String authorization) {
        UserUtils.setUserRole(username, role, authorization, this, new OperationCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (Boolean.TRUE.equals(result)) {
                    setUserToVerified(username, authorization);
//                    Toast.makeText(EmailVerificationActivity.this, "User role set successfully", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(EmailVerificationActivity.this, "Setting user role failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void checkIfSignedIn(String email, String password) {
        isSignedIn().thenAccept(isSignedIn -> {
            if (Boolean.TRUE.equals(isSignedIn)) {
                SignOut(email, password);
            } else {
                // User is not signed in, just proceed to sign them in
                signIn(email, password);
            }
        });
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

    public CompletableFuture<Boolean> SignOut(String username, String password)
    {
        CompletableFuture<Boolean> future=new CompletableFuture<>();
        Amplify.Auth.signOut(signOutResult -> {
            if (signOutResult instanceof AWSCognitoAuthSignOutResult.CompleteSignOut) {
                runOnUiThread(() -> {
                signIn(username, password);
                });
                future.complete(true);

            } else if (signOutResult instanceof AWSCognitoAuthSignOutResult.PartialSignOut) {
                runOnUiThread(() -> {
                    signIn(username, password);
                });
                future.complete(true);
            } else if (signOutResult instanceof AWSCognitoAuthSignOutResult.FailedSignOut) {
                AWSCognitoAuthSignOutResult.FailedSignOut failedSignOutResult =
                        (AWSCognitoAuthSignOutResult.FailedSignOut) signOutResult;
                Log.e("AuthQuickStart", "Sign out Failed", failedSignOutResult.getException());
                future.complete(false);
            }
        });
        return future;
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
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    void showOrganizationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.created_organization_popup, null);
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
        alertDialog.setCanceledOnTouchOutside(false);
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
