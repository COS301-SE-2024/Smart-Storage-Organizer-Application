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

public class EmailVerificationActivity extends AppCompatActivity {
    private EditText inputCode1, inputCode2, inputCode3, inputCode4, inputCode5, inputCode6;
    private CountDownTimer countDownTimer;
    String phoneNumber;
    Long timeoutSeconds = 60L;
    String verificationCode;
    RelativeLayout ButtonNext;
    TextView resendOtpTextView, next_button_text_otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_email_verification);

        ButtonNext = findViewById(R.id.buttonNext);
        resendOtpTextView = findViewById(R.id.resendOtp);
        next_button_text_otp = findViewById(R.id.next_button_text_otp);

//        resendOtpTextView.setText("Checking");
        resendOtpTextView.setText("Resend OTP in 60 sec");

        TextView email = findViewById(R.id.textEmail);
        email.setText(getIntent().getStringExtra("email"));
        phoneNumber = getIntent().getStringExtra("email");

//        sendOtp(phoneNumber, false);

        inputCode1 = findViewById(R.id.inputCode1);
        inputCode2 = findViewById(R.id.inputCode2);
        inputCode3 = findViewById(R.id.inputCode3);
        inputCode4 = findViewById(R.id.inputCode4);
        inputCode5 = findViewById(R.id.inputCode5);
        inputCode6 = findViewById(R.id.inputCode6);

        setupOTPInputs();
        startCountdown();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateForm()){
                    String Code1 = inputCode1.getText().toString().trim();
                    String Code2 = inputCode2.getText().toString().trim();
                    String Code3 = inputCode3.getText().toString().trim();
                    String Code4 = inputCode4.getText().toString().trim();
                    String Code5 = inputCode5.getText().toString().trim();
                    String Code6 = inputCode6.getText().toString().trim();
                    ConfirmSignUp(email.getText().toString(), Code1+Code2+Code3+Code4+Code5+Code6);
                }
            }
        });
    }

    public boolean validateForm(){
        String Code1 = inputCode1.getText().toString().trim();
        String Code2 = inputCode2.getText().toString().trim();
        String Code3 = inputCode3.getText().toString().trim();
        String Code4 = inputCode4.getText().toString().trim();
        String Code5 = inputCode5.getText().toString().trim();
        String Code6 = inputCode6.getText().toString().trim();

        return !TextUtils.isEmpty(Code1) && !TextUtils.isEmpty(Code2) && !TextUtils.isEmpty(Code3) && !TextUtils.isEmpty(Code4) && !TextUtils.isEmpty(Code5) && !TextUtils.isEmpty(Code6);

    }

    public  void ConfirmSignUp(String email , String Code)
    {
        Amplify.Auth.confirmSignUp(
                email,
                Code,
                result ->{ Log.i("AuthQuickstart", result.isSignUpComplete() ? "Confirm signUp succeeded" : "Confirm sign up not complete");
                    //change to new page
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Registration Successful.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(EmailVerificationActivity.this, ProfileManagementActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();
                    });

                },
                error ->{ Log.e("AuthQuickstart", error.toString());
                    //dont change to different page
                    Toast.makeText(this, "Wrong Verification Pin.", Toast.LENGTH_LONG).show();
                }
        );
    }

    private void setupOTPInputs() {
        inputCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()){
                    inputCode2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()){
                    inputCode3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputCode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()){
                    inputCode4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputCode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()){
                    inputCode5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputCode5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()){
                    inputCode6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

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