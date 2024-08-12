package com.example.smartstorageorganizer;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class HelpActivityInstrumentedTest {

    @Rule
    public ActivityScenarioRule<HelpActivity> activityRule =
            new ActivityScenarioRule<>(HelpActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void clickingForgotPasswordLink_NavigatesToResetPasswordActivity() {
        // Click on the "Forgot Password" link
        onView(allOf(withId(R.id.help_reset_password_text_view), withText("Forgot Password")))
                .perform(click());

        // Verify that ResetPasswordActivity is started
        intended(IntentMatchers.hasComponent(ResetPasswordActivity.class.getName()));
    }

    @Test
    public void clickingHomepageLink_NavigatesToHomeActivity() {
        // Click on the "Homepage" link
        onView(allOf(withId(R.id.help_add_category_text_view), withText("Homepage")))
                .perform(click());

        // Verify that HomeActivity is started
        intended(IntentMatchers.hasComponent(HomeActivity.class.getName()));
    }

    @Test
    public void clickingEditProfileLink_NavigatesToProfileManagementActivity() {
        // Click on the "EDIT PROFILE" link
        onView(allOf(withId(R.id.help_edit_profile_text_view), withText("EDIT PROFILE")))
                .perform(click());

        // Verify that ProfileManagementActivity is started
        intended(IntentMatchers.hasComponent(ProfileManagementActivity.class.getName()));
    }

    @Test
    public void testTextViewsAreDisplayed() {
        // Check if all the TextViews are displayed
        onView(withId(R.id.help_reset_password_text_view))
                .check(matches(isDisplayed()));

        onView(withId(R.id.help_add_category_text_view))
                .check(matches(isDisplayed()));

        onView(withId(R.id.help_edit_profile_text_view))
                .check(matches(isDisplayed()));
    }
}