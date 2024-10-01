package com.example.smartstorageorganizer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;

import android.content.Intent;
import android.view.View;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class GetStartedActivityTest {

    private GetStartedActivity getStartedActivity;

    @Mock
    FirebaseFirestore mockFirestore;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        getStartedActivity = Robolectric.buildActivity(GetStartedActivity.class)
                .create()
                .resume()
                .get();
        // Assume the activity uses a mock instance for testing
    }

    @Test
    public void testActivityNotNull() {
        assertNotNull(getStartedActivity);
    }

    @Test
    public void testLoginButtonClick() {
        View loginButton = getStartedActivity.findViewById(R.id.login);
        assertNotNull(loginButton);

        // Simulate login button click
        loginButton.performClick();

        Intent expectedIntent = new Intent(getStartedActivity, LandingActivity.class);
        Intent actualIntent = ShadowApplication.getInstance().getNextStartedActivity();
        assertEquals(expectedIntent.getComponent(), actualIntent.getComponent());
    }

    @Test
    public void testJoinButtonClick() {
        View joinButton = getStartedActivity.findViewById(R.id.joinButton);
        assertNotNull(joinButton);

        // Simulate join button click
        joinButton.performClick();

        Intent expectedIntent = new Intent(getStartedActivity, SearchOrganizationActivity.class);
        Intent actualIntent = ShadowApplication.getInstance().getNextStartedActivity();
        assertEquals(expectedIntent.getComponent(), actualIntent.getComponent());
    }

    @Test
    public void testAddButtonClick() {
        View addButton = getStartedActivity.findViewById(R.id.addButton);
        assertNotNull(addButton);

        // Simulate add button click
        addButton.performClick();

        Intent expectedIntent = new Intent(getStartedActivity, AddOrganizationActivity.class);
        Intent actualIntent = ShadowApplication.getInstance().getNextStartedActivity();
        assertEquals(expectedIntent.getComponent(), actualIntent.getComponent());
    }

    @Test
    public void testLogSessionDurationCalledOnActivityPause() {
        getStartedActivity.onPause(); // Simulate activity pause

        // Verify that logSessionDuration was called (this requires making logSessionDuration public or using a spy)
        // This assumes logSessionDuration can be tracked by observing mockFirestore
        verify(mockFirestore).collection("activity_sessions");
    }

    // Add any additional tests as needed to validate other functionalities
}
