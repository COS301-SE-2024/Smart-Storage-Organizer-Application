package com.example.smartstorageorganizer;

import static org.junit.Assert.assertEquals;

import android.content.Intent;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

@RunWith(RobolectricTestRunner.class)
public class ViewNotificationActivityTest {

    private ViewNotificationActivity activity;
    private TextView titleTextView;
    private TextView messageTextView;

    @Before
    public void setUp() {
        // Set up the activity with an intent that contains the notification title and message
        Intent intent = new Intent();
        intent.putExtra("notification_title", "Test Title");
        intent.putExtra("notification_message", "Test Message");

        // Initialize the activity
        ActivityController<ViewNotificationActivity> controller = Robolectric.buildActivity(ViewNotificationActivity.class, intent);
        activity = controller.create().start().resume().get();

        // Get references to the TextViews
        titleTextView = activity.findViewById(R.id.textView_title);
        messageTextView = activity.findViewById(R.id.textView_message);
    }

    @Test
    public void testActivityIsNotNull() {
        // Verify the activity is not null
        assertEquals(true, activity != null);
    }

    @Test
    public void testTitleIsDisplayedCorrectly() {
        // Verify the title is correctly set from the intent
        String expectedTitle = "Test Title";
        String actualTitle = titleTextView.getText().toString();
        assertEquals(expectedTitle, actualTitle);
    }

    @Test
    public void testMessageIsDisplayedCorrectly() {
        // Verify the message is correctly set from the intent
        String expectedMessage = "Test Message";
        String actualMessage = messageTextView.getText().toString();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testNoTitleOrMessage() {
        // Build the activity without passing any intent extras
        Intent emptyIntent = new Intent();
        ActivityController<ViewNotificationActivity> controller = Robolectric.buildActivity(ViewNotificationActivity.class, emptyIntent);
        ViewNotificationActivity emptyActivity = controller.create().start().resume().get();

        // Ensure the text fields are empty if no title/message is provided
        TextView titleView = emptyActivity.findViewById(R.id.textView_title);
        TextView messageView = emptyActivity.findViewById(R.id.textView_message);

        assertEquals("", titleView.getText().toString());
        assertEquals("", messageView.getText().toString());
    }
}
