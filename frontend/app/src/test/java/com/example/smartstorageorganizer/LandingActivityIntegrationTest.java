
package com.example.smartstorageorganizer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Intent;
import android.os.Build;
import android.widget.Button;

import androidx.constraintlayout.widget.ConstraintLayout;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;


//@RunWith(RobolectricTestRunner.class)
//@Config(sdk = Build.VERSION_CODES.P)
public class LandingActivityIntegrationTest {

    private LandingActivity activity;

//    @Before
//    public void setup() {
//        activity = Robolectric.buildActivity(LandingActivity.class)
//                .create()
//                .resume()
//                .get();
//    }

//    @Test
//    public void testLoginButton() {
//        ConstraintLayout loginButton = activity.findViewById(R.id.loginButton);
//        loginButton.performClick();
//
//        Intent expectedIntent = new Intent(activity, LoginActivity.class);
//        ShadowActivity shadowActivity = Shadows.shadowOf(activity);
//        Intent actualIntent = shadowActivity.getNextStartedActivity();
//        assertNotNull(actualIntent);
//        assertEquals(expectedIntent.getComponent(), actualIntent.getComponent());
//    }

//    @Test
//    public void testJoinButton() {
//        ConstraintLayout joinButton = activity.findViewById(R.id.joinButton);
//        joinButton.performClick();
//
//        Intent expectedIntent = new Intent(activity, SearchOrganizationActivity.class);
//        ShadowActivity shadowActivity = Shadows.shadowOf(activity);
//        Intent actualIntent = shadowActivity.getNextStartedActivity();
//        assertNotNull(actualIntent);
//        assertEquals(expectedIntent.getComponent(), actualIntent.getComponent());
//    }
}