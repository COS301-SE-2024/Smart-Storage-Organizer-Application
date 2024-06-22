package com.example.smartstorageorganizer;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class RegistrationActivityTest {
    @Mock
    RegistrationActivity registrationActivity;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        registrationActivity = new RegistrationActivity();
    }

    @Test
    public void Add() {
        assertEquals(4, registrationActivity.AddNumbers(2, 2));
    }
}