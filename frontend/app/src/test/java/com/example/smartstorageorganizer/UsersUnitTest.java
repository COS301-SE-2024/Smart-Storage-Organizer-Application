package com.example.smartstorageorganizer;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static java.util.concurrent.CompletableFuture.allOf;

import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;

import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLooper;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class UsersUnitTest {

    UsersActivity Page;
    @Mock
    UsersActivity MockedPage = mock(UsersActivity.class);

    @Before
    public void setup() {
        Page = Robolectric.buildActivity(UsersActivity.class)
                .create()
                .resume()
                .get();
    }

    @Test
    public void shouldNotBeNull() {
        assertNotNull(Page);
    }
}