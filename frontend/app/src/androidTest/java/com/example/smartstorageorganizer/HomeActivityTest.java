package com.example.smartstorageorganizer;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class HomeActivityTest {

    @Rule
    public ActivityScenarioRule<HomeActivity> activityRule =
            new ActivityScenarioRule<>(HomeActivity.class);

    @Before
    public void setUp() {
        // Initialize anything needed before tests
    }

    @Test
    public void testNavigationDrawerOpens() {
        // Open the navigation drawer
        onView(ViewMatchers.withContentDescription(R.string.navigation_drawer_open)).perform(click());

        // Check if a navigation item is displayed
        onView(withId(R.id.nav_view)).check(matches(ViewMatchers.isDisplayed()));
    }



//    @Test
//    public void testFetchUserDetails() {
//        // Check if user details are fetched and displayed correctly
//        onView(withId(R.id.fullName)).check(matches(withText("Ezekiel Makau")));
//    }
}