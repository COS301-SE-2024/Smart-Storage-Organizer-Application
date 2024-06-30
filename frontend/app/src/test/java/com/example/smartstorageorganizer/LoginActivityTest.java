package com.example.smartstorageorganizer;
import static org.mockito.Mockito.spy;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;
import static org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks;

import android.os.Build;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class LoginActivityTest {

    LoginActivity loginActivity;
    @Mock
    LoginActivity lg = mock(LoginActivity.class);

    @Before
    public void setup() {
        loginActivity = spy(Robolectric.buildActivity(LoginActivity.class)
                .create()
                .resume()
                .get());
    }

    @Test
    public void shouldNotBeNull() {
        assertNotNull(loginActivity);
    }

    @Test
    public void validateFormShouldReturnTrueForValidEmailAndPassword() {
        // Given
        String validEmail = "test@example.com";
        String validPassword = "password123";

        // When
        boolean result = loginActivity.validateForm(validEmail, validPassword);

        // Then
        assertEquals(true, result);
    }

    @Test
    public void ValidateFormShouldReturnFalseForEmptyEmail() {
        String email = "";
        String validPassword = "password123";

        boolean result = loginActivity.validateForm(email, validPassword);
        assertEquals(false, result);
    }

    @Test
    public void ValidateFormShouldReturnFalseWhenPasswordEmpty() {
        // Given
        String validEmail = "test@example.com";
        String validPassword = "";

        // When
        boolean result = loginActivity.validateForm(validEmail, validPassword);

        // Then
        assertEquals(false, result);
    }
    @Test
    public void signInWithValidEmailAndPassword() {
        String validEmail = "test@example.com";
        String validPassword = "password123";
        AtomicBoolean result = new AtomicBoolean(false);

        LoginActivity mockLoginActivity = mock(LoginActivity.class);
        when(mockLoginActivity.signIn(validEmail, validPassword)).thenReturn(CompletableFuture.completedFuture(true));

        mockLoginActivity.signIn(validEmail, validPassword).thenAccept(isResult -> {
            if (isResult) {
                result.set(true);
            } else {
                result.set(false);
            }
        });

        assertEquals(true, result.get());
    }

    @Test
    public void signInWithInvalidEmailAndPassword() {
        String invalidEmail = "invalid@example.com";
        String invalidPassword = "wrongpassword";
        AtomicBoolean result = new AtomicBoolean(false);

        LoginActivity mockLoginActivity = mock(LoginActivity.class);
        when(mockLoginActivity.signIn(invalidEmail, invalidPassword)).thenReturn(CompletableFuture.completedFuture(false));

        mockLoginActivity.signIn(invalidEmail, invalidPassword).thenAccept(isResult -> {
            if (isResult) {
                result.set(true);
            } else {
                result.set(false);
            }
        });

        assertEquals(false, result.get());
    }

    @Test
    public void testSetErrorAndResult() {
        String testError = "Test Error";
        String testResult = "Test Result";

        loginActivity.setErrorAndResult(testError, testResult);

        assertEquals(testError, loginActivity.errorString);
        assertEquals(testResult, loginActivity.resultString);
    }

    @Test
    public void clickingRegisterButton_callsSignInWithCorrectParameters() {
        // Given
        String email = "test@example.com";
        String password = "password123";

        // Set the text fields
        loginActivity.email.setText(email);
        loginActivity.password.setText(password);

        // When
        loginActivity.registerButton.performClick();

        runUiThreadTasksIncludingDelayedTasks();
        // Then
        verify(loginActivity).signIn(email, password);
    }
}






