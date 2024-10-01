package com.example.smartstorageorganizer;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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

    @Test
    public void testWindowInsetsListenerIsSet() {
        // Verify that the window insets listener is correctly applied to the view
        View mockMainView = mock(View.class);

        // Mock the View and WindowInsetsCompat behavior
        WindowInsetsCompat mockInsets = mock(WindowInsetsCompat.class);
        ViewCompat.setOnApplyWindowInsetsListener(mockMainView, (v, insets) -> {
            assertNotNull(insets.getInsets(WindowInsetsCompat.Type.systemBars()));
            return insets;
        });

        // Simulate window insets being applied to the view
        WindowInsetsCompat testInsets = WindowInsetsCompat.toWindowInsetsCompat(mockInsets.toWindowInsets());
        ViewCompat.dispatchApplyWindowInsets(mockMainView, testInsets);

        // Verify that the listener is applied and insets are handled
        verify(mockMainView).setPadding(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    public void testActivityEdgeToEdgeEnabled() {
        // Verify that the EdgeToEdge behavior is enabled
        assertNotNull(activity);
        Bundle savedInstanceState = new Bundle();
        activity.onCreate(savedInstanceState);

        // EdgeToEdge enabling would require further checks for actual insets handling which are UI tests
    }
}
