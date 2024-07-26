package com.example.smartstorageorganizer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


import android.content.Intent;
import android.os.Build;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amplifyframework.core.Amplify;
import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession;
import com.amplifyframework.core.Amplify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.concurrent.CompletableFuture;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class NewPasswordActivityUnitTest {

    NewPasswordActivity Page;
    @Mock
    NewPasswordActivity MockedPage = mock(NewPasswordActivity.class);

    @Before
    public void setup() {
        Page = Robolectric.buildActivity(NewPasswordActivity.class)
                .create()
                .resume()
                .get();
    }

    @Test
    public void shouldNotBeNull() {
        assertNotNull(Page);
    }

}
