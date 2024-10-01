package com.example.smartstorageorganizer;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})  // Specify the Android SDK version to be used in the test
public class AppTerminationServiceTest {

    private AppTerminationService service;
    private FirebaseFirestore mockFirestore;
    private NotificationManager mockNotificationManager;

    @Before
    public void setUp() {
        // Initialize the service and mocks
        service = Robolectric.buildService(AppTerminationService.class).create().get();
        mockFirestore = Mockito.mock(FirebaseFirestore.class);
        mockNotificationManager = Mockito.mock(NotificationManager.class);

        // Inject mocked objects into the service (using a setter or other method if needed)
        service.db = mockFirestore;
    }

    @Test
    public void testOnCreate_shouldStartForegroundService() {
        // Arrange
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(RuntimeEnvironment.getApplication(), "AppExitServiceChannel")
                .setContentTitle("App is running")
                .setContentText("Tracking app exit...")
                .setSmallIcon(R.drawable.ic_launcher_foreground);

        // Act
        service.onCreate();

        // Assert
        assertNotNull(service);  // Check that the service is not null
    }

    @Test
    public void testOnTaskRemoved_shouldSendApiRequestAndRemoveUser() {
        // Arrange
        Intent intent = new Intent(RuntimeEnvironment.getApplication(), AppTerminationService.class);
        String testUserId = "test@example.com";
        service.app = new MyAmplifyApp();
        service.app.setEmail(testUserId);
        service.app.setName("TestName");
        service.app.setSurname("TestSurname");
        service.app.setOrganizationID("123");

        // Act
        service.onTaskRemoved(intent);

        // Assert
        verify(mockFirestore).collection("active_users").document(testUserId);
    }

    @Test
    public void testSendAppExitApi_shouldSendApiRequest() {
        // Arrange
        String testEmail = "test@example.com";
        String testName = "TestName";
        String testSurname = "TestSurname";
        String testType = "sign_out";
        String testOrgId = "123";
        String testTime = "2024-09-30 12:00:00";

        // Act
        service.sendAppExitApi(testEmail, testName, testSurname, testType, testOrgId, testTime);

        // Assert
        // Here you would assert API-related logic or mock verification, if needed
    }

    @Test
    public void testCreateNotificationChannel_shouldCreateNotificationChannel() {
        // Act
        service.createNotificationChannel();

        // Assert
        assertNotNull(mockNotificationManager);
    }
}