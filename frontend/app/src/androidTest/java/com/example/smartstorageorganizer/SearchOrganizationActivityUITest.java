package com.example.smartstorageorganizer;

import android.content.Intent;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.smartstorageorganizer.RegistrationActivity;
import com.example.smartstorageorganizer.SearchOrganizationActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class SearchOrganizationActivityUITest {

    @Rule
    public ActivityScenarioRule<SearchOrganizationActivity> activityRule =
            new ActivityScenarioRule<>(SearchOrganizationActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

//    @Test
//    public void searchAutoCompleteShowsSuggestions() {
//        onView(withId(R.id.autoCompleteTextView))
//                .perform(replaceText("S"));
//
//        // Check if the suggestion "Shoprite" appears
//        onView(withText("Shoprite")).check(matches(withText("Shoprite")));
//    }

    @Test
    public void clickingNextButtonStartsRegistrationActivity() {
        // Click the Next button
        onView(withId(R.id.nextButton)).perform(click());

        // Verify that RegistrationActivity is started
        intended(hasComponent(RegistrationActivity.class.getName()));
    }
}