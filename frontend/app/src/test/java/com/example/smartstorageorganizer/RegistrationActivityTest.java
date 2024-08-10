package com.example.smartstorageorganizer;

import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.os.Build;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class RegistrationActivityTest {
    RegistrationActivity registrationActivity;

    @Before
    public void setup() {
        registrationActivity = Robolectric.buildActivity(RegistrationActivity.class)
                .create()
                .resume()
                .get();
    }

    @Test
    public void shouldNotBeNull() {
        assertNotNull(registrationActivity);
    }

    @Test
    public void testValidateForm_AllFieldsValid() {
        // Given
        String validName = "testName";
        String validSurname = "testSurname";
        String validEmail = "test@example.com";
        String validPhone = "123456789";
        String validPassword = "password123";

        // When
        boolean result = registrationActivity.validateForm(validName, validSurname, validEmail, validPhone, validPassword);

        // Then
        assertEquals(true, result);
    }

    @Test
    public void testValidateForm_EmptyName() {
        // Given
        String emptyName = "";
        String validSurname = "testSurname";
        String validEmail = "test@example.com";
        String validPhone = "123456789";
        String validPassword = "password123";

        // When
        boolean result = registrationActivity.validateForm(emptyName, validSurname, validEmail, validPhone, validPassword);

        // Then
        assertEquals(false, result);
    }

    @Test
    public void testValidateForm_EmptySurname() {
        // Given
        String validName = "testName";
        String emptySurname = "";
        String validEmail = "test@example.com";
        String validPhone = "123456789";
        String validPassword = "password123";

        // When
        boolean result = registrationActivity.validateForm(validName, emptySurname, validEmail, validPhone, validPassword);

        // Then
        assertEquals(false, result);
    }

    @Test
    public void testValidateForm_EmptyEmail() {
        // Given
        String validName = "testName";
        String validSurname = "testSurname";
        String emptyEmail = "";
        String validPhone = "123456789";
        String validPassword = "password123";

        // When
        boolean result = registrationActivity.validateForm(validName, validSurname, emptyEmail, validPhone, validPassword);

        // Then
        assertEquals(false, result);
    }

    @Test
    public void testValidateForm_InvalidEmail() {
        // Given
        String validName = "testName";
        String validSurname = "testSurname";
        String invalidEmail = "test@.com";
        String validPhone = "123456789";
        String validPassword = "password123";

        // When
        boolean result = registrationActivity.validateForm(validName, validSurname, invalidEmail, validPhone, validPassword);

        // Then
        assertEquals(false, result);
    }

    @Test
    public void testValidateForm_EmptyPhone() {
        // Given
        String validName = "testName";
        String validSurname = "testSurname";
        String validEmail = "test@example.com";
        String emptyPhone = "";
        String validPassword = "password123";

        // When
        boolean result = registrationActivity.validateForm(validName, validSurname, validEmail, emptyPhone, validPassword);

        // Then
        assertEquals(false, result);
    }

    @Test
    public void testValidateForm_ShortPhone() {
        // Given
        String validName = "testName";
        String validSurname = "testSurname";
        String validEmail = "test@example.com";
        String shortPhone = "12345678"; // 8 digits
        String validPassword = "password123";

        // When
        boolean result = registrationActivity.validateForm(validName, validSurname, validEmail, shortPhone, validPassword);

        // Then
        assertEquals(false, result);
    }

    @Test
    public void testValidateForm_LongPhone() {
        // Given
        String validName = "testName";
        String validSurname = "testSurname";
        String validEmail = "test@example.com";
        String longPhone = "12345678901"; // 11 digits
        String validPassword = "password123";

        // When
        boolean result = registrationActivity.validateForm(validName, validSurname, validEmail, longPhone, validPassword);

        // Then
        assertEquals(false, result);
    }

    @Test
    public void testValidateForm_EmptyPassword() {
        // Given
        String validName = "testName";
        String validSurname = "testSurname";
        String validEmail = "test@example.com";
        String validPhone = "123456789";
        String emptyPassword = "";

        // When
        boolean result = registrationActivity.validateForm(validName, validSurname, validEmail, validPhone, emptyPassword);

        // Then
        assertEquals(false, result);
    }

    @Test
    public void testValidateForm_ShortPassword() {
        // Given
        String validName = "testName";
        String validSurname = "testSurname";
        String validEmail = "test@example.com";
        String validPhone = "123456789";
        String shortPassword = "pass123"; // 7 characters

        // When
        boolean result = registrationActivity.validateForm(validName, validSurname, validEmail, validPhone, shortPassword);

        // Then
        assertEquals(false, result);
    }
}
