package com.example.smartstorageorganizer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.view.View;
import android.widget.FrameLayout;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.smartstorageorganizer.adapters.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 30) // Target Android SDK version for the test
public class RequestsActivityTest {

    private RequestsActivity activity;

    @Before
    public void setUp() {
        activity = Robolectric.buildActivity(RequestsActivity.class)
                .create()
                .start()
                .resume()
                .get();
    }

    @Test
    public void testActivityIsNotNull() {
        // Test that the activity is created successfully
        assertNotNull(activity);
    }

    @Test
    public void testTabLayoutIsSetUpCorrectly() {
        // Check if TabLayout and ViewPager2 are set up correctly
        TabLayout tabLayout = activity.findViewById(R.id.tabLayout);
        ViewPager2 viewPager = activity.findViewById(R.id.viewPager);

        assertNotNull(tabLayout);
        assertNotNull(viewPager);

        // Test that ViewPager2 adapter is set correctly
        ViewPagerAdapter adapter = (ViewPagerAdapter) viewPager.getAdapter();
        assertNotNull(adapter);

        // Check if TabLayout contains correct tabs
        assertEquals(2, tabLayout.getTabCount());
        assertEquals("Pending", tabLayout.getTabAt(0).getText());
        assertEquals("Approved", tabLayout.getTabAt(1).getText());
    }

    @Test
    public void testBackButtonNavigatesBack() {
        // Find and simulate a click on the back button
        View backButton = activity.findViewById(R.id.backButton);
        backButton.performClick();

        // Verify that the activity is finishing after clicking the back button
        assertTrue(activity.isFinishing());
    }

    @Test
    public void testWindowInsetsAppliedCorrectly() {
        // Test that insets are applied properly
        FrameLayout mainLayout = activity.findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // The insets padding should be set by the listener
        assertEquals(mainLayout.getPaddingTop(), activity.getWindow().getDecorView().getPaddingTop());
    }
}
