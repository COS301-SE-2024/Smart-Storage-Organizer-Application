package com.example.smartstorageorganizer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.textfield.TextInputEditText;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 30)
public class AddOrganizationActivityTest {

    private AddOrganizationActivity activity;
    private TextInputEditText organizationEditText, nameEditText, surnameEditText, emailEditText, phoneNumberEditText, passwordEditText;
    private LottieAnimationView buttonLoader;
    private TextView registerButtonText;

    @Before
    public void setUp() {
        activity = Robolectric.buildActivity(AddOrganizationActivity.class).create().resume().get();
        organizationEditText = activity.findViewById(R.id.organization);
        nameEditText = activity.findViewById(R.id.name);
        surnameEditText = activity.findViewById(R.id.surname);
        emailEditText = activity.findViewById(R.id.email);
        phoneNumberEditText = activity.findViewById(R.id.phone);
        passwordEditText = activity.findViewById(R.id.password);
        buttonLoader = activity.findViewById(R.id.buttonLoader);
        registerButtonText = activity.findViewById(R.id.register_button_text);
    }

    @Test
    public void formValidation_validForm_shouldPassValidation() {
        // Fill all fields with valid data
        organizationEditText.setText("Test Organization");
        nameEditText.setText("John");
        surnameEditText.setText("Doe");
        emailEditText.setText("john.doe@example.com");
        phoneNumberEditText.setText("9876543210");
        passwordEditText.setText("password123");

        // Perform click
        activity.findViewById(R.id.buttonRegister).performClick();

        // Ensure no validation errors
        assertNull(organizationEditText.getError());
        assertNull(nameEditText.getError());
        assertNull(surnameEditText.getError());
        assertNull(emailEditText.getError());
        assertNull(phoneNumberEditText.getError());
        assertNull(passwordEditText.getError());
    }

    @Test
    public void testToastOnSignUpFailure() {
        // Simulate sign-up failure
        activity.handleSignUpFailure("john.doe@example.com", "password123", new Exception("Sign-up failed"));

        // Check that the correct toast message was shown
        assertEquals("Sign Up failed, please try again later.", ShadowToast.getTextOfLatestToast());
    }
}