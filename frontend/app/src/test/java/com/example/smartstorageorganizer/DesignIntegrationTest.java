package com.example.smartstorageorganizer;

import android.content.Intent;
import android.os.Build;
import android.view.View;

import androidx.test.core.app.ActivityScenario;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class DesignIntegrationTest {

    private ActivityScenario<DesignActivity> scenario;

    @Before
    public void setUp() {
        scenario = ActivityScenario.launch(DesignActivity.class);
    }

    @After
    public void tearDown() {
        scenario.close();
    }

    @Test
    public void testActivityNotNull() {
        scenario.onActivity(activity -> assertNotNull(activity));
    }

    @Test
    public void testLoginButtonClickStartsHomeActivity() {
        scenario.onActivity(activity -> {
            activity.findViewById(R.id.loginButton).performClick();
            Intent expectedIntent = new Intent(activity, HomeActivity.class);
            Intent actual = shadowOf(activity).getNextStartedActivity();
            assertEquals(expectedIntent.getComponent(), actual.getComponent());
        });
    }


    @Test
    public void testFinishTransition() {
        scenario.onActivity(activity -> {
            activity.finish();
            assertEquals(R.anim.slide_in_left, shadowOf(activity).getPendingTransitionEnterAnimationResourceId());
            assertEquals(R.anim.slide_out_right, shadowOf(activity).getPendingTransitionExitAnimationResourceId());
        });
    }
}