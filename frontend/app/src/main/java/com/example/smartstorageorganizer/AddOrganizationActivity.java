package com.example.smartstorageorganizer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
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
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;
import com.example.smartstorageorganizer.model.OrganizationModel;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.OrganizationUtils;
import com.example.smartstorageorganizer.utils.Utils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddOrganizationActivity extends BaseActivity {
    private TextInputEditText organizationEditText;
    private TextInputEditText nameEditText;
    private TextInputEditText surnameEditText;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private TextInputEditText phoneNumberEditText;
    private CountryCodePicker countryCodePicker;
    private LottieAnimationView buttonLoader;
    private TextView registerButtonText;
    private ImageView registerButtonIcon;
    private long startTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_organization);

        initializeViews();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.buttonRegister).setOnClickListener(v -> {
            String organizationName = organizationEditText.getText().toString().trim();
            String name = nameEditText.getText().toString().trim();
            String surname = surnameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String phone = phoneNumberEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            if (validateForm(organizationName, name, surname, email, phone, password)) {
                addNewOrganization(organizationName, email, password);
            }
        });

        findViewById(R.id.registerBackButton).setOnClickListener(v -> finish());
    }

    private void initializeViews() {
        organizationEditText = findViewById(R.id.organization);
        nameEditText = findViewById(R.id.name);
        surnameEditText = findViewById(R.id.surname);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        phoneNumberEditText = findViewById(R.id.phone);
        countryCodePicker = findViewById(R.id.cpp);
        buttonLoader = findViewById(R.id.buttonLoader);
        registerButtonText = findViewById(R.id.register_button_text);
        registerButtonIcon = findViewById(R.id.register_button_icon);
    }

    boolean validateForm(String organizationName, String name, String surname, String email, String phone, String password) {
        if (TextUtils.isEmpty(organizationName)) {
            organizationEditText.setError("First Name is required.");
            organizationEditText.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("First Name is required.");
            nameEditText.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(surname)) {
            surnameEditText.setError("Surname is required.");
            surnameEditText.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required.");
            emailEditText.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter a valid email.");
            emailEditText.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(phone)) {
            phoneNumberEditText.setError("Phone Number is required.");
            phoneNumberEditText.requestFocus();
            return false;
        }

        if (phone.length() < 9 || phone.length() > 10) {
            phoneNumberEditText.setError("Enter a valid phone number.");
            phoneNumberEditText.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required.");
            passwordEditText.requestFocus();
            return false;
        }

        if (password.length() < 8) {
            passwordEditText.setError("Password should be at least 8 characters long.");
            passwordEditText.requestFocus();
            return false;
        }

        return true;
    }

    private void performSignUp(String organizationId) {
        String organizationText = organizationEditText.getText().toString().trim();
        String nameText = nameEditText.getText().toString().trim();
        String surnameText = surnameEditText.getText().toString().trim();
        String emailText = emailEditText.getText().toString().trim();
        String phoneText = countryCodePicker.getSelectedCountryCodeWithPlus() + phoneNumberEditText.getText().toString().trim();
        String passwordText = passwordEditText.getText().toString().trim();

        ArrayList<AuthUserAttribute> attributes = new ArrayList<>();
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.name(), nameText));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.familyName(), surnameText));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.phoneNumber(), phoneText));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.picture(), BuildConfig.DefaultImage));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.address(), organizationId));

        try {
            buttonLoader.setVisibility(View.VISIBLE);
            buttonLoader.playAnimation();
            registerButtonText.setVisibility(View.GONE);
            registerButtonIcon.setVisibility(View.GONE);

            Amplify.Auth.signUp(
                    emailText,
                    passwordText,
                    AuthSignUpOptions.builder().userAttributes(attributes).build(),
                    result -> {
                        Log.i("MyAmplifyApp", "Sign-up successful: " + result.toString());
//                        addNewOrganization(organizationText, emailText, passwordText);
                        moveToVerificationActivity(emailText, passwordText);
                    },
                    error -> {
                        Log.e("MyAmplifyApp", "Sign-up failed", error);
                        handleSignUpFailure(emailText, passwordText, error);
                    }
            );
        } catch (Exception e) {
            Log.e("MyAmplifyApp", "Sign-up exception", e);
            handleSignUpException();
        }
    }

    void moveToVerificationActivity(String email, String password) {
        runOnUiThread(() -> {
            Intent intent = new Intent(AddOrganizationActivity.this, EmailVerificationActivity.class);
            intent.putExtra("email", email);
            intent.putExtra("password", password);
            intent.putExtra("organization", organizationEditText.getText().toString().trim());
            intent.putExtra("type", "organization");
            logUserFlow("EmailVerificationActivity");
            startActivity(intent);
            finish();
        });
    }

    void handleSignUpFailure(String email, String password, Exception error) {
        runOnUiThread(() -> {
            if (error.toString().toLowerCase(Locale.ROOT).contains("already exists in the system")) {
                Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show();
                moveToVerificationActivity(email, password);
            } else {
                resetSignUpButton();
                Toast.makeText(this, "Sign Up failed, please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleSignUpException() {
        runOnUiThread(() -> {
            resetSignUpButton();
            Toast.makeText(this, "Sign Up failed, please try again later.", Toast.LENGTH_LONG).show();
        });
    }

    private void resetSignUpButton() {
        buttonLoader.setVisibility(View.GONE);
        buttonLoader.pauseAnimation();
        registerButtonText.setVisibility(View.VISIBLE);
        registerButtonIcon.setVisibility(View.VISIBLE);
    }

    public void addNewOrganization(String organizationName, String ownerEmail, String password) {
        buttonLoader.setVisibility(View.VISIBLE);
        buttonLoader.playAnimation();
        registerButtonText.setVisibility(View.GONE);
        registerButtonIcon.setVisibility(View.GONE);
        OrganizationUtils.addOrganization(organizationName, ownerEmail, this, new OperationCallback<OrganizationModel>() {
            @Override
            public void onSuccess(OrganizationModel result) {
                performSignUp(result.getOrganizationId());
                Toast.makeText(AddOrganizationActivity.this, "Organization Added Successfully."+ result.getOrganizationId(), Toast.LENGTH_LONG).show();
                moveToVerificationActivity(ownerEmail, password);
            }

            @Override
            public void onFailure(String error) {
                resetSignUpButton();
                Toast.makeText(AddOrganizationActivity.this, "Adding Organization failed, please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void logActivityView(String activityName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = "";

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
        String userId = "";

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
        logSessionDuration("AddOrganizationActivity", (sessionDuration));
        long transitionTime = System.currentTimeMillis();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = "";

        Map<String, Object> userFlowData = new HashMap<>();
        userFlowData.put("user_id", userId);
        userFlowData.put("previous_activity", "AddOrganizationActivity");
        userFlowData.put("next_activity", toActivity);
        userFlowData.put("transition_time", new Timestamp(new Date(transitionTime)));

        db.collection("user_flow")
                .add(userFlowData)
                .addOnSuccessListener(documentReference -> Log.d("Firestore", "User flow logged."))
                .addOnFailureListener(e -> Log.w("Firestore", "Error logging user flow", e));
    }

//    public void logUser

    @Override
    public void onStart() {
        super.onStart();
        logActivityView("AddOrganizationActivity");
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
}