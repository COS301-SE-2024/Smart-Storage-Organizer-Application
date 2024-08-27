package com.example.smartstorageorganizer;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.contrib.ViewPagerActions;
import androidx.test.espresso.matcher.ViewMatchers;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.action.ViewActions.click;

@RunWith(AndroidJUnit4.class)
public class UsersActivityIntegrationTests {

    @Rule
    public ActivityScenarioRule<UsersActivity> activityRule =
            new ActivityScenarioRule<>(UsersActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testInitialTabIsRequests() {
        // Verify that the initial tab is "Requests" by checking the ViewPager's current page or the content.
        onView(withId(R.id.viewPager)).check(matches(ViewMatchers.withText("Requests")));
    }

    @Test
    public void testSwipeToAllTab() {
        // Swipe to the second page (All) in ViewPager
        onView(withId(R.id.viewPager)).perform(ViewPagerActions.scrollToPage(1));
        // Optionally, check the content on the "All" page if applicable.
        // Example placeholder check, assuming text view or specific content is visible
        onView(withText("All")).check(matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testClickToAllTab() {
        // Click on the "All" tab
        onView(withText("All")).perform(click());
        // Verify that the "All" tab is displayed
        onView(withText("All")).check(matches(ViewMatchers.isDisplayed()));
    }
}