package com.example.smartstorageorganizer;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowLooper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.robolectric.Shadows.shadowOf;

import android.content.Intent;
import android.os.Build;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)

public class LoginActivityIntegrationTest {
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
    public void testRegisterButton() {
        RelativeLayout registerButton = loginActivity.findViewById(R.id.buttonLogin);
        registerButton.performClick();

        Intent expectedIntent = new Intent(loginActivity, HomeActivity.class);
        ShadowActivity shadowActivity = Shadows.shadowOf(loginActivity);
        Intent actualIntent = shadowActivity.getNextStartedActivity();
       // assertNotNull(actualIntent);
        //assertEquals(expectedIntent.getComponent(), actualIntent.getComponent());
    }
//    private LoginActivity loginActivity;
//    private ActivityController<LoginActivity> controller;
//    private LoginActivity spyLoginActivity;
//
//    @Before
//    public void setUp() {
//        controller = Robolectric.buildActivity(LoginActivity.class);
//        loginActivity = controller.create().visible().get();
//        spyLoginActivity = Mockito.spy(loginActivity);
//    }


//    @Test
//    public void clickingLogin_withValidCredentials_startsHomeActivity() throws InterruptedException {
//        // Given
//        String email = "zhouvel7@gmail.com";
//        String password = "Nivlac321#";
//
//        // Set the text fields
//        loginActivity.email.setText(email);
//        loginActivity.password.setText(password);
//
//        Mockito.doAnswer(invocation -> {
//            CompletableFuture<Boolean> future = new CompletableFuture<>();
//            future.complete(true); // Simulate successful sign-in
//            return future;
//        }).when(spyLoginActivity).signIn(email, password);
//
//        // Create a CountDownLatch initialized with a count of 1
//        CountDownLatch latch = new CountDownLatch(1);
//
//        // When
//        spyLoginActivity.registerButton.performClick();
//
//
//        // Wait for the SignIn method to complete
//        ShadowLooper.idleMainLooper();
//        latch.await(10, TimeUnit.SECONDS);
//        ShadowActivity shadowActivity = shadowOf(loginActivity);
//        Intent nextIntent = shadowActivity.getNextStartedActivity();
//
//
//        assertEquals("Expected HomeActivity to be started", HomeActivity.class.getName(), Objects.requireNonNull(nextIntent.getComponent()).getClassName());
//
//    }
}