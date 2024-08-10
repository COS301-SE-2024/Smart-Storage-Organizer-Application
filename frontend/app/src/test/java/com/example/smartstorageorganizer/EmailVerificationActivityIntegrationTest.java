package com.example.smartstorageorganizer;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;

import com.amplifyframework.core.Amplify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class EmailVerificationActivityIntegrationTest {

    private EmailVerificationActivity activity;
    private EditText inputCode1, inputCode2, inputCode3, inputCode4, inputCode5, inputCode6;
    private TextView resendOtpTextView, emailTextView;
    private RelativeLayout buttonNext;
    private CountDownTimer countDownTimer;

    @Before
    public void setUp() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EmailVerificationActivity.class);
        intent.putExtra("email", "test@example.com");
        activity = Robolectric.buildActivity(EmailVerificationActivity.class, intent).create().start().resume().get();

        inputCode1 = activity.findViewById(R.id.inputCode1);
        inputCode2 = activity.findViewById(R.id.inputCode2);
        inputCode3 = activity.findViewById(R.id.inputCode3);
        inputCode4 = activity.findViewById(R.id.inputCode4);
        inputCode5 = activity.findViewById(R.id.inputCode5);
        inputCode6 = activity.findViewById(R.id.inputCode6);
        resendOtpTextView = activity.findViewById(R.id.resendOtp);
        emailTextView = activity.findViewById(R.id.textEmail);
        buttonNext = activity.findViewById(R.id.buttonNext);
    }

    @Test
    public void testActivityInitialization() {
        assertNotNull(activity);
        assertEquals("test@example.com", emailTextView.getText().toString());
        assertNotNull(inputCode1);
        assertNotNull(inputCode2);
        assertNotNull(inputCode3);
        assertNotNull(inputCode4);
        assertNotNull(inputCode5);
        assertNotNull(inputCode6);
        assertNotNull(resendOtpTextView);
        assertNotNull(buttonNext);
    }

    @Test
    public void testOtpInputFocusChange() {
        inputCode1.setText("1");
        assertEquals(inputCode2.isFocused(), true);

        inputCode2.setText("2");
        assertEquals(inputCode3.isFocused(), true);

        inputCode3.setText("3");
        assertEquals(inputCode4.isFocused(), true);

        inputCode4.setText("4");
        assertEquals(inputCode5.isFocused(), true);

        inputCode5.setText("5");
        assertEquals(inputCode6.isFocused(), true);
    }

    @Test
    public void testFormValidation() {
        inputCode1.setText("1");
        inputCode2.setText("2");
        inputCode3.setText("3");
        inputCode4.setText("4");
        inputCode5.setText("5");
        inputCode6.setText("6");

        assertEquals(activity.validateForm(), true);
    }

    @Test
    public void testResendOtp() {
        resendOtpTextView.performClick();
        assertEquals(resendOtpTextView.isEnabled(), false);
    }

//    @Test
//    public void testNextButtonClick() {
//        // Setting up mock Amplify to verify method calls
//        Amplify.Auth = mock(Amplify.Auth.class);
//
//        inputCode1.setText("1");
//        inputCode2.setText("2");
//        inputCode3.setText("3");
//        inputCode4.setText("4");
//        inputCode5.setText("5");
//        inputCode6.setText("6");
//
//        buttonNext.performClick();
//
//        verify(Amplify.Auth).confirmSignUp(
//                "test@example.com",
//                "123456",
//                activity.any(),
//                activity.any()
//        );
//    }

//    @Test
//    public void testShowRequestSentDialog() {
//        activity.runOnUiThread(() -> activity.showRequestSentDialog());
//
//        AlertDialog alertDialog = ShadowAlertDialog.getLatestAlertDialog();
//        assertNotNull(alertDialog);
//
//        Button finishButton = alertDialog.findViewById(R.id.finishButton);
//        assertNotNull(finishButton);
//
//        finishButton.performClick();
//
//        Intent expectedIntent = new Intent(activity, LandingActivity.class);
//        Intent actualIntent = Robolectric.shadowOf(activity).getNextStartedActivity();
//        assertEquals(expectedIntent.getComponent(), actualIntent.getComponent());
//    }
}
