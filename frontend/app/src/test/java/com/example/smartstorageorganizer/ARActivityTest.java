package com.example.smartstorageorganizer;

import static org.junit.Assert.assertNotNull;

import android.os.Build;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})  // You can adjust this to the SDK version you need
public class ARActivityTest {

    private ARActivity activity;

    @Before
    public void setUp() {
        // Setup the activity using Robolectric
        activity = Robolectric.buildActivity(ARActivity.class).create().get();
    }

    @Test
    public void testActivityShouldNotBeNull() {
        // Verify that the activity is created and not null
        assertNotNull(activity);
    }
}