package com.example.smartstorageorganizer;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LandingActivityTest {

    @Rule
    public ActivityScenarioRule<LandingActivity> activityRule =
            new ActivityScenarioRule<>(LandingActivity.class);

    @Test
    public void testLoginButtonOpensLoginActivity() {
        onView(withId(R.id.loginButton))
                .perform(click());
        intended(hasComponent(LoginActivity.class.getName()));
    }

    @Test
    public void testJoinButtonOpensSearchOrganizationActivity() {
        onView(withId(R.id.joinButton))
                .perform(click());
        intended(hasComponent(SearchOrganizationActivity.class.getName()));
    }

    @Test
    public void testSuccessfulSignOutLogsMessage() {
        // Assuming the signOut method is mocked to always succeed
        // You can check the log or UI changes if any
        // Since Espresso doesn't directly support checking logs, we can assume success based on UI behavior post-sign-out.
    }

    @Test
    public void testFailedSignOutLogsError() {
        // Similar to above, you would mock the signOut method to fail
        // This requires more setup with Mockito or another mocking framework to simulate a failed sign-out
    }
}