package com.example.smartstorageorganizer;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static java.util.concurrent.CompletableFuture.allOf;

import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.widget.Button;

import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLooper;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class NewPasswordActivityUnitTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    NewPasswordActivity newPasswordActivity;
    @Mock
    NewPasswordActivity mockNewPasswordActivity;

    @Before
    public void setup() {
        newPasswordActivity = spy(Robolectric.buildActivity(NewPasswordActivity.class)
                .create()
                .resume()
                .get());
        mockNewPasswordActivity = spy(newPasswordActivity);
    }

    @Test
    public void shouldNotBeNull() {
        assertNotNull(newPasswordActivity);
    }

    @Test
    public void resetPasswordWithValidNewPasswordAndVerificationCode() {
        String validNewPassword = "newPassword123";
        String validVerificationCode = "123456";

        // Simulate the behavior of resetPassword method
        doAnswer(invocation -> {
            String newPassword = invocation.getArgument(0);
            String verificationCode = invocation.getArgument(1);
            // Simulate success callback
            if (newPassword.equals(validNewPassword) && verificationCode.equals(validVerificationCode)) {
                return null;
            }
            return null;
        }).when(mockNewPasswordActivity).resetPassword(validNewPassword, validVerificationCode);

        mockNewPasswordActivity.resetPassword(validNewPassword, validVerificationCode);

        ArgumentCaptor<String> newPasswordCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> verificationCodeCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockNewPasswordActivity).resetPassword(newPasswordCaptor.capture(), verificationCodeCaptor.capture());

        assertEquals(validNewPassword, newPasswordCaptor.getValue());
        assertEquals(validVerificationCode, verificationCodeCaptor.getValue());
    }

    @Test
    public void resetPasswordWithInvalidNewPasswordAndVerificationCode() {
        String invalidNewPassword = "short";
        String invalidVerificationCode = "wrongcode";

        // Simulate the behavior of resetPassword method
        doAnswer(invocation -> {
            String newPassword = invocation.getArgument(0);
            String verificationCode = invocation.getArgument(1);
            // Simulate failure callback
            return null;
        }).when(mockNewPasswordActivity).resetPassword(invalidNewPassword, invalidVerificationCode);

        mockNewPasswordActivity.resetPassword(invalidNewPassword, invalidVerificationCode);

        ArgumentCaptor<String> newPasswordCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> verificationCodeCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockNewPasswordActivity).resetPassword(newPasswordCaptor.capture(), verificationCodeCaptor.capture());

        assertEquals(invalidNewPassword, newPasswordCaptor.getValue());
        assertEquals(invalidVerificationCode, verificationCodeCaptor.getValue());
    }

    @Test
    public void clickingResetPasswordButton_callsResetPasswordWithCorrectParameters() {
        // Given
        String newPassword = "newPassword123";
        String verificationCode = "123456";

        // Set the text fields
        newPasswordActivity.newPasswordField.setText(newPassword);
        newPasswordActivity.verificationCode = verificationCode;

        Button resetPasswordButton = newPasswordActivity.findViewById(R.id.buttonConfirm);

        // When
        resetPasswordButton.performClick();

        // Then
        ArgumentCaptor<String> newPasswordCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> verificationCodeCaptor = ArgumentCaptor.forClass(String.class);
        verify(newPasswordActivity).resetPassword(newPasswordCaptor.capture(), verificationCodeCaptor.capture());

        assertEquals(newPassword, newPasswordCaptor.getValue());
        assertEquals(verificationCode, verificationCodeCaptor.getValue());
    }


}