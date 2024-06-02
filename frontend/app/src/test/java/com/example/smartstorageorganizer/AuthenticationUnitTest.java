package com.example.smartstorageorganizer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.auth.result.AuthSignInResult;
import com.amplifyframework.auth.result.AuthSignUpResult;
import com.amplifyframework.core.Amplify;

import static org.junit.Assert.*;

import android.util.Log;

import androidx.test.InstrumentationRegistry;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AuthenticationUnitTest {


    @Mock
    AuthSignUpResult signUpResult;

    @Before
    public void setup() {
        try {
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.configure(InstrumentationRegistry.getTargetContext());
        }catch(AmplifyException e)
        {
            Log.i("error testing", e.toString());
        }
    }

    @Test
    public void SignUpTest() {
        CountDownLatch latch = new CountDownLatch(1);

        Amplify.Auth.signUp(
                "testuser1@example.com",
                "password123",
                AuthSignUpOptions.builder().userAttribute(AuthUserAttributeKey.email(), "testuser@example.com").build(),
                result -> {
                    assertNotNull(result);
                    latch.countDown();
                },
                error -> {
                    fail("Sign-up failed: " + error.getMessage());
                    latch.countDown();
                }
        );



        try {
            latch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        Amplify.Auth.signUp(
                "testuser2@example.com",
                "password1234",
                AuthSignUpOptions.builder().userAttribute(AuthUserAttributeKey.email(), "testuser@example.com").build(),
                result -> {
                    assertNotNull(result);
                    latch.countDown();
                },
                error -> {
                    fail("Sign-up failed: " + error.getMessage());
                    latch.countDown();
                }
        );

        try {
            latch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }



    @Test
    public void SignInTest()
    {

    }
    @Test
    public void SignOut()
    {

    }

}
