package com.example.smartstorageorganizer;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.robolectric.annotation.Config;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.os.Build;
import android.text.SpannableStringBuilder;

import com.google.android.material.textfield.TextInputEditText;

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
        assertFalse(result);
    }
//    @Test
//    public void addNumShouldReturnCorrectSum() {
//        // Given
//        int a = 2;
//        int b = 2;
//
//        // When
//        int result = loginActivity.addNum(a, b);
//
//        // Then
//        assertEquals(4, result);
//    }






}