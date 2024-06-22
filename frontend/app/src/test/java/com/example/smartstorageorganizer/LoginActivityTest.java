package com.example.smartstorageorganizer;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.os.Build;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class LoginActivityTest {

    LoginActivity loginActivity;

    @Before
    public void setup() {
        loginActivity = Robolectric.buildActivity(LoginActivity.class)
                .create()
                .resume()
                .get();
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
    public void ValidateFormShouldReturnFalseForEmptyEmail(){
        String email="";
        String validPassword = "password123";

        boolean result=loginActivity.validateForm(email,validPassword);
        assertEquals(false,result);
    }
    @Test
    public void ValidateFormShouldReturnFalseWhenPasswordEmpty(){
        // Given
        String validEmail = "test@example.com";
        String validPassword = "";

        // When
        boolean result = loginActivity.validateForm(validEmail, validPassword);

        // Then
        assertEquals(false,result);
    }
    @Test
    public void SignInWithValidEmailAndPassword(){
        String validEmail = "test@example.com";
        String validPassword = "password123";
        AtomicBoolean result = new AtomicBoolean(false);
        // When
        when(loginActivity.SignIn(validEmail,validPassword)).thenReturn(CompletableFuture.completedFuture(true));
        loginActivity.SignIn(validEmail,validPassword).thenAccept(isResult->{
            if(isResult)
                result.set(true);
            else
                result.set(false);

        });
        assertEquals(true,result);
    }







}