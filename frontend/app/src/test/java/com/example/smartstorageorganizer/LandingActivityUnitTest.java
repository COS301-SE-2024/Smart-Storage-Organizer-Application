package com.example.smartstorageorganizer;

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
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class LandingActivityUnitTest {

    private LandingActivity activity;

    @Before
    public void setup() {
        activity = Robolectric.buildActivity(LandingActivity.class)
                .create()
                .resume()
                .get();
    }

    @Test
    public void testUiElements() {
        ConstraintLayout loginButton = activity.findViewById(R.id.loginButton);
        ConstraintLayout joinButton = activity.findViewById(R.id.joinButton);

        assertNotNull(loginButton);
        assertNotNull(joinButton);
    }
}