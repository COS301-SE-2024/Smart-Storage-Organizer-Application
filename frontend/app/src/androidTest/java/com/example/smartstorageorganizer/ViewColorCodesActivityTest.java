package com.example.smartstorageorganizer;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class ViewColorCodesActivityTest {

    @Rule
    public ActivityScenarioRule<ViewColorCodesActivity> activityRule =
            new ActivityScenarioRule<>(ViewColorCodesActivity.class);

    @Test
    public void testActivityLaunchesAndDisplaysUIComponents() {
        // Check that the RecyclerView and FAB are displayed
        onView(withId(R.id.color_code_rec)).check(matches(isDisplayed()));
        onView(withId(R.id.delete_fab)).check(matches(isDisplayed()));

        // Check that the shimmer layout is initially visible
        onView(withId(R.id.shimmer_view_container)).check(matches(isDisplayed()));
    }
}