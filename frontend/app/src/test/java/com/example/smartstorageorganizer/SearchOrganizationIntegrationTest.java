package com.example.smartstorageorganizer;

import android.content.Intent;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.test.core.app.ActivityScenario;

import com.example.smartstorageorganizer.RegistrationActivity;
import com.example.smartstorageorganizer.SearchOrganizationActivity;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

import java.util.Objects;

//@RunWith(RobolectricTestRunner.class)
//@Config(sdk = 30)
public class SearchOrganizationIntegrationTest {

//    @Test
//    public void searchAutoCompleteShowsSuggestions() {
//
//        try (ActivityScenario<SearchOrganizationActivity> scenario = ActivityScenario.launch(SearchOrganizationActivity.class)) {
//            scenario.onActivity(activity -> {
//
//                AutoCompleteTextView autoCompleteTextView = activity.findViewById(R.id.autoCompleteTextView);
//
//
//                autoCompleteTextView.setText("S");
//
//
//                Assert.assertTrue(autoCompleteTextView.getAdapter().getCount() > 0);
//                Assert.assertEquals("Shoprite", autoCompleteTextView.getAdapter().getItem(0));
//            });
//        }
//    }

//    @Test
//    public void clickingNextButtonStartsRegistrationActivity() {
//        SearchOrganizationActivity activity = Robolectric.buildActivity(SearchOrganizationActivity.class)
//                .create()
//                .resume()
//                .get();
//
//
//        Button nextButton = activity.findViewById(R.id.nextButton);
//
//
//        nextButton.performClick();
//
//
//        ShadowActivity shadowActivity = Shadows.shadowOf(activity);
//        Intent nextStartedActivity = shadowActivity.getNextStartedActivity();
//
//
//        Assert.assertNotNull(nextStartedActivity);
//        Assert.assertEquals(RegistrationActivity.class.getCanonicalName(), Objects.requireNonNull(nextStartedActivity.getComponent()).getClassName());
//
//
//        Assert.assertEquals(R.anim.slide_in_right, shadowActivity.getPendingTransitionEnterAnimationResourceId());
//        Assert.assertEquals(R.anim.slide_out_left, shadowActivity.getPendingTransitionExitAnimationResourceId());
//    }
}