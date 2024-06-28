package com.example.smartstorageorganizer;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.os.CountDownTimer;
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
    EditText inputCode1;
    EditText inputCode2;
    EditText inputCode3;
    EditText inputCode4;
    EditText inputCode5;
    EditText inputCode6;
    CountDownTimer countDownTimer;
    TextView resendOtpTextView;
    private static final String AUTH_QUICK_START =  "AuthQuickstart";
    private static final String EMAIL =  "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_email_verification);

        RelativeLayout buttonNext = findViewById(R.id.buttonNext);
        resendOtpTextView = findViewById(R.id.resendOtp);

        resendOtpTextView.setText("Resend OTP in 60 sec");

        TextView email = findViewById(R.id.textEmail);
        email.setText(getIntent().getStringExtra(EMAIL));

        inputCode1 = findViewById(R.id.inputCode1);
        inputCode2 = findViewById(R.id.inputCode2);
        inputCode3 = findViewById(R.id.inputCode3);
        inputCode4 = findViewById(R.id.inputCode4);
        inputCode5 = findViewById(R.id.inputCode5);
        inputCode6 = findViewById(R.id.inputCode6);

        setupOTPInputs();
        startCountdown();
        inputCode1.requestFocus();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        buttonNext.setOnClickListener(v -> {
            if(validateForm()){
                String code1 = inputCode1.getText().toString().trim();
                String code2 = inputCode2.getText().toString().trim();
                String code3 = inputCode3.getText().toString().trim();
                String code4 = inputCode4.getText().toString().trim();
                String code5 = inputCode5.getText().toString().trim();
                String code6 = inputCode6.getText().toString().trim();
                confirmSignUp(email.getText().toString(), code1+code2+code3+code4+code5+code6);
            }
        });

        resendOtpTextView.setOnClickListener(v -> {
            Log.i(EMAIL, email.getText().toString());
            Amplify.Auth.resendSignUpCode(
                    email.getText().toString(),
                    result -> Log.i(AUTH_QUICK_START, "ResendSignUp succeeded: "),
                    error -> Log.e(AUTH_QUICK_START, "ResendSignUp failed", error)

            );
        });    }

    public boolean validateForm(){
        String code1 = inputCode1.getText().toString().trim();
        String code2 = inputCode2.getText().toString().trim();
        String code3 = inputCode3.getText().toString().trim();
        String code4 = inputCode4.getText().toString().trim();
        String code5 = inputCode5.getText().toString().trim();
        String code6 = inputCode6.getText().toString().trim();

        return !TextUtils.isEmpty(code1) && !TextUtils.isEmpty(code2) && !TextUtils.isEmpty(code3) && !TextUtils.isEmpty(code4) && !TextUtils.isEmpty(code5) && !TextUtils.isEmpty(code6);

    }

    public CompletableFuture<Boolean> confirmSignUp(String email , String code)
    {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Amplify.Auth.confirmSignUp(
                email,
                code,
                result ->{ Log.i(AUTH_QUICK_START, result.isSignUpComplete() ? "Confirm signUp succeeded" : "Confirm sign up not complete");
                    future.complete(true);
                    //change to new page

                    runOnUiThread(() -> {
                        Toast.makeText(this, "Registration Successful.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(EmailVerificationActivity.this, MainActivity.class);
                        intent.putExtra(EMAIL, email);
                        startActivity(intent);
                        finish();
                    });

                },
                error ->{future.complete(false);
                    Log.e(AUTH_QUICK_START, error.toString());
                    //dont change to different page
                    Toast.makeText(this, "Wrong Verification Pin.", Toast.LENGTH_LONG).show();
                }
        );
        return future;
    }

    private void setupOTPInputs() {
        inputCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Do something after text changed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()){
                    inputCode2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Do something after text changed
            }
        });
        inputCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Do something after text changed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()){
                    inputCode3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Do something after text changed
            }
        });
        inputCode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Do something after text changed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()){
                    inputCode4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Do something after text changed
            }
        });
        inputCode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Do something after text changed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()){
                    inputCode5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Do something after text changed
            }
        });
        inputCode5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Do something after text changed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()){
                    inputCode6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Do something after text changed
            }
        });


    }

    private void startCountdown() {
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update the text on each tick
                resendOtpTextView.setText("Resend OTP in " + millisUntilFinished / 1000 + " sec");
            }

            @Override
            public void onFinish() {
                // Enable the TextView and set its text when the countdown finishes
                resendOtpTextView.setEnabled(true);
                resendOtpTextView.setText("Resend OTP");
            }
        };

        // Disable the TextView during the countdown
        resendOtpTextView.setEnabled(false);

        // Start the countdown
        countDownTimer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Make sure to cancel the countdown when the activity is destroyed to prevent memory leaks
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}