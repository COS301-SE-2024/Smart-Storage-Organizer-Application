package com.example.smartstorageorganizer;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.amplifyframework.core.Amplify;

import java.util.concurrent.CompletableFuture;

public class EmailVerificationActivity extends AppCompatActivity {

    // UI Elements
    private EditText inputCode1;
    private EditText inputCode2;
    private EditText inputCode3;
    private EditText inputCode4;
    private EditText inputCode5;
    private EditText inputCode6;
    private TextView resendOtpTextView;
    private TextView emailTextView;
    private CountDownTimer countDownTimer;

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

    private boolean validateForm() {
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

    private CompletableFuture<Boolean> confirmSignUp(String email, String code) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Amplify.Auth.confirmSignUp(
                email,
                code,
                result -> {
                    Log.i(AUTH_QUICK_START, result.isSignUpComplete() ? "Confirm signUp succeeded" : "Confirm sign up not complete");
                    future.complete(true);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Registration Successful.", Toast.LENGTH_LONG).show();
                        navigateToMainActivity(email);
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

    private void navigateToMainActivity(String email) {
        Intent intent = new Intent(EmailVerificationActivity.this, MainActivity.class);
        intent.putExtra(EMAIL, email);
        startActivity(intent);
        finish();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
