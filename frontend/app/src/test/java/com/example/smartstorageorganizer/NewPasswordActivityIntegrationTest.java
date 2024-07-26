package com.example.smartstorageorganizer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowLooper;

import static org.junit.Assert.assertEquals;
import static org.robolectric.Shadows.shadowOf;

import android.content.Intent;
import android.os.Build;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class NewPasswordActivityIntegrationTest {
    private NewPasswordActivity newPasswordActivity;
    private ActivityController<NewPasswordActivity> controller;
    private NewPasswordActivity spyNewPasswordActivity;

    @Before
    public void setUp() {
        controller = Robolectric.buildActivity(NewPasswordActivity.class);
        newPasswordActivity = controller.create().visible().get();
        spyNewPasswordActivity = Mockito.spy(newPasswordActivity);
    }

    @Test
    public void clickingResetPassword_withValidNewPasswordAndVerificationCode_startsMainActivity() throws InterruptedException {
        // Given
        String newPassword = "newPassword123";
        String verificationCode = "123456";

        // Set the text fields
        newPasswordActivity.newPasswordField.setText(newPassword);
        newPasswordActivity.verificationCode = verificationCode;

        Mockito.doAnswer(invocation -> {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            future.complete(true); // Simulate successful password reset
            return future;
        }).when(spyNewPasswordActivity).resetPassword(newPassword, verificationCode);

        // Create a CountDownLatch initialized with a count of 1
        CountDownLatch latch = new CountDownLatch(1);

        // When
        newPasswordActivity.findViewById(R.id.buttonConfirm).performClick();

        // Wait for the resetPassword method to complete
        ShadowLooper.idleMainLooper();
        latch.await(10, TimeUnit.SECONDS);
        ShadowActivity shadowActivity = shadowOf(newPasswordActivity);
        Intent nextIntent = shadowActivity.getNextStartedActivity();

        // Then
        assertEquals("Expected MainActivity to be started", MainActivity.class.getName(), Objects.requireNonNull(nextIntent.getComponent()).getClassName());
    }

    @Test
    public void clickingResetPassword_withInvalidNewPassword_doesNotStartMainActivity() throws InterruptedException {
        // Given
        String newPassword = ""; // Invalid new password
        String verificationCode = "123456";

        // Set the text fields
        newPasswordActivity.newPasswordField.setText(newPassword);
        newPasswordActivity.verificationCode = verificationCode;

        // When
        newPasswordActivity.findViewById(R.id.buttonConfirm).performClick();

        // Wait for the resetPassword method to complete
        ShadowLooper.idleMainLooper();
        CountDownLatch latch = new CountDownLatch(1);
        latch.await(10, TimeUnit.SECONDS);
        ShadowActivity shadowActivity = shadowOf(newPasswordActivity);
        Intent nextIntent = shadowActivity.getNextStartedActivity();

        // Then
        assertEquals("Expected no activity to be started", null, nextIntent);
    }

    @Test
    public void clickingResetPassword_withInvalidVerificationCode_doesNotStartMainActivity() throws InterruptedException {
        // Given
        String newPassword = "newPassword123";
        String verificationCode = "wrongcode"; // Invalid verification code

        // Set the text fields
        newPasswordActivity.newPasswordField.setText(newPassword);
        newPasswordActivity.verificationCode = verificationCode;

        Mockito.doAnswer(invocation -> {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            future.complete(false); // Simulate failed password reset
            return future;
        }).when(spyNewPasswordActivity).resetPassword(newPassword, verificationCode);

        // Create a CountDownLatch initialized with a count of 1
        CountDownLatch latch = new CountDownLatch(1);

        // When
        newPasswordActivity.findViewById(R.id.buttonConfirm).performClick();

        // Wait for the resetPassword method to complete
        ShadowLooper.idleMainLooper();
        latch.await(10, TimeUnit.SECONDS);
        ShadowActivity shadowActivity = shadowOf(newPasswordActivity);
        Intent nextIntent = shadowActivity.getNextStartedActivity();

        // Then
        assertEquals("Expected no activity to be started", null, nextIntent);
    }
}
