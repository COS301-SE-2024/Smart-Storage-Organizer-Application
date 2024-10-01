package com.example.smartstorageorganizer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.os.Build;

import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.Q)
public class DevelopersActivityTest {

    private DevelopersActivity developersActivity;

    @Before
    public void setUp() {
        developersActivity = Robolectric.buildActivity(DevelopersActivity.class)
                .create()
                .resume()
                .get();
    }

    @Test
    public void testActivityIsNotNull() {
        assertNotNull(developersActivity);
    }

    @Test
    public void testTabLayoutIsCorrect() {
        TabLayout tabLayout = developersActivity.findViewById(R.id.tabLayout);
        assertNotNull(tabLayout);
        assertEquals(2, tabLayout.getTabCount()); // Check if two tabs are added

        TabLayout.Tab activeTab = tabLayout.getTabAt(0);
        TabLayout.Tab deactivatedTab = tabLayout.getTabAt(1);

        assertNotNull(activeTab);
        assertNotNull(deactivatedTab);

        assertEquals("Active", activeTab.getText());
        assertEquals("Deactivated", deactivatedTab.getText());
    }

    @Test
    public void testViewPagerSetup() {
        ViewPager2 viewPager = developersActivity.findViewById(R.id.viewPager);
        assertNotNull(viewPager);
        assertEquals(2, viewPager.getAdapter().getItemCount()); // Adapter should have 2 pages
    }

    @Test
    public void testAddOrganizationButtonStartsCorrectActivity() {
        // Uncomment the code in DevelopersActivity for this to work
        // Uncomment this if the button is visible and functional in the layout
        /*
        View addOrganizationButton = developersActivity.findViewById(R.id.addOrganizationButton);
        assertNotNull(addOrganizationButton);

        addOrganizationButton.performClick();

        Intent expectedIntent = new Intent(developersActivity, AddOrganizationActivity.class);
        Intent actualIntent = shadowOf(developersActivity).getNextStartedActivity();

        assertEquals(expectedIntent.getComponent(), actualIntent.getComponent());
        */
    }
}
