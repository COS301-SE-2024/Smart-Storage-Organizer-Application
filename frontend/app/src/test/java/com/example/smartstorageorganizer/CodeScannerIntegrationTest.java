package com.example.smartstorageorganizer;

import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.provider.MediaStore;

import com.google.zxing.integration.android.IntentIntegrator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class CodeScannerIntegrationTest {

    private CodeScannerActivity activity;

    @Before
    public void setUp() {
        activity = Robolectric.buildActivity(CodeScannerActivity.class)
                .create()
                .start()
                .resume()
                .get();
    }



    @Test
    public void testSelectImageButtonOpensImagePicker() {

        activity.findViewById(R.id.select_image_button).performClick();


        ShadowActivity shadowActivity = org.robolectric.Shadows.shadowOf(activity);
        Intent startedIntent = shadowActivity.getNextStartedActivity();


        assertEquals(Intent.ACTION_PICK, startedIntent.getAction());
        assertEquals(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, startedIntent.getData());
    }

    @Test
    public void testOnActivityResultWithQRCode() {

        Intent data = new Intent();
        data.putExtra("SCAN_RESULT", "sample_code");


        activity.onActivityResult(IntentIntegrator.REQUEST_CODE, Activity.RESULT_OK, data);


        ShadowActivity shadowActivity = org.robolectric.Shadows.shadowOf(activity);
        Intent startedIntent = shadowActivity.getNextStartedActivity();

        assertNotNull(startedIntent);
        assertEquals(ItemDetailsActivity.class.getName(), startedIntent.getComponent().getClassName());
        assertEquals("sample_code", startedIntent.getStringExtra("item_id"));
    }
}