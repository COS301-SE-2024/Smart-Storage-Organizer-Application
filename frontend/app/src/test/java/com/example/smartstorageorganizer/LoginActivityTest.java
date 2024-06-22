package com.example.smartstorageorganizer;
import org.robolectric.RobolectricTestRunner;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.text.SpannableStringBuilder;

import com.google.android.material.textfield.TextInputEditText;

@RunWith(RobolectricTestRunner.class)
public class LoginActivityTest {
    @Mock
    TextInputEditText Password;
    TextInputEditText Email;
    @Mock
    LoginActivity loginActivity;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        loginActivity = new LoginActivity();
    }

//    @Test
//    public void signInSuccessfulWhenValidCredentialsProvided() {
//        String validEmail = "validEmail@example.com";
//        String validPassword = "validPassword";
//
//        when(loginActivity.SignIn(validEmail, validPassword)).thenReturn(CompletableFuture.completedFuture(true));
//
//        loginActivity.SignIn(validEmail, validPassword);
//
//        verify(loginActivity).SignIn(validEmail, validPassword);
//    }
//
//    @Test
//    public void signInUnsuccessfulWhenInvalidCredentialsProvided() {
//        String invalidEmail = "invalidEmail";
//        String invalidPassword = "invalidPassword";
//
//        when(loginActivity.SignIn(invalidEmail, invalidPassword)).thenReturn(CompletableFuture.completedFuture(false));
//
//        loginActivity.SignIn(invalidEmail, invalidPassword);
//
//        verify(loginActivity).SignIn(invalidEmail, invalidPassword);
//    }

    @Test
    public void Add() {
        assertEquals(4, loginActivity.addNum(2, 2));
    }
//    @Test
//    public void validateFormReturnsFalseWhenInvalidEmailProvided() {
//        String invalidEmail = "invalidEmail";
//        String validPassword = "validPassword";
//
//        when(loginActivity.validateForm()).thenReturn(false);
//
//        assertFalse(loginActivity.validateForm());
//    }

//    @Test
//    public void validateFormReturnsFalseWhenInvalidPasswordProvided() {
//        String validEmail = "validEmail@example.com";
//        String invalidPassword = "invalidPassword";
//
//        when(loginActivity.validateForm()).thenReturn(false);
//
//        assertFalse(loginActivity.validateForm());
//    }
//
//    @Test
//    public void validateFormReturnsFalseWhenEmptyCredentialsProvided() {
//        String emptyEmail = "";
//        String emptyPassword = "";
//
//        when(loginActivity.validateForm()).thenReturn(false);
//
//        assertFalse(loginActivity.validateForm());
//    }
}