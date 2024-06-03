package com.example.smartstorageorganizer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.text.Editable;

import com.google.android.material.textfield.TextInputEditText;

import org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)

public class AuthenticationUnitTest {
    @Mock
    private  LoginActivity loginObj;

    @Test
    public void SignUpTest()
    {

    }
    @Test
    public void SignInTest()
    {

    }
    @Test
    public void SignOut()
    {

    }
    public void validateForm()
    {
        TextInputEditText emailField = mock(TextInputEditText.class);
        TextInputEditText passwordField = mock(TextInputEditText.class);


        Editable mockEditableEmail = mock(Editable.class);
        Editable mockEditablePassword = mock(Editable.class);


        when(mockEditableEmail.toString()).thenReturn("test@example.com");
        when(mockEditablePassword.toString()).thenReturn("password123");

    }

}
