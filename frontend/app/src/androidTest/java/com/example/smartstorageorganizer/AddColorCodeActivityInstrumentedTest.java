package com.example.smartstorageorganizer;

import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class AddColorCodeActivityInstrumentedTest {
    @Rule
    public ActivityScenarioRule<AddColorCodeActivity> activityRule =
            new ActivityScenarioRule<>(AddColorCodeActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void addNewColorCode_ValidInput_NavigatesToHome() {
        // Type text into the title field
        onView(withId(R.id.colorCodeName)).perform(typeText("title"));
        // Close the keyboard to ensure it doesn't obscure the next view
        closeSoftKeyboard();
        // Type text into the description field
        onView(withId(R.id.colorCodeDescription)).perform(typeText("description"));
        // Close the keyboard again to ensure it doesn't obscure the button
        closeSoftKeyboard();
        // Click the add color code button
        onView(withId(R.id.add_colorcode_button)).perform(click());

        // Add assertions to verify that the app navigates to the HomeActivity
        Intents.intended(IntentMatchers.hasComponent(HomeActivity.class.getName()));
    }
}
