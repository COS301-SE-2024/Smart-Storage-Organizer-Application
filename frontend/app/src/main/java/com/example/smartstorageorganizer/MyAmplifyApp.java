package com.example.smartstorageorganizer;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.AmplifyException;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
//import com.onesignal.Continue;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OSNotificationReceivedEvent;
import com.onesignal.OneSignal;
//import com.onesignal.debug.LogLevel;
//import com.onesignal.OSNotificationOpenedResult;
//import com.onesignal.OSNotification;
//import com.onesignal.OneSignal.OSNotificationOpenedResult;


public class MyAmplifyApp extends Application {
    private static final String ONESIGNAL_APP_ID = "152f0f5c-d21d-4e43-89b1-5e02acc42abe";
    //f53cb2d5-e439-4641-8b9c-a27f6d4b57bb

    @Override
    public void onCreate() {
        super.onCreate();

//        setContentView(R.layout.activity_main);

        // Initialize OneSignal
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        // Prompt for push notifications permission
        OneSignal.promptForPushNotifications();

        // Notification click handler
        OneSignal.setNotificationOpenedHandler(new OneSignal.OSNotificationOpenedHandler() {
            @Override
            public void notificationOpened(OSNotificationOpenedResult result) {
                Intent intent = new Intent(getApplicationContext(), NotificationsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                // Pass additional data from the notification to the activity
                String additionalData = result.getNotification().getAdditionalData().toString();
                intent.putExtra("data_key", additionalData);

                startActivity(intent);
            }
        });

        // Handle notification when app is in the foreground (in-app notifications)
        OneSignal.setNotificationWillShowInForegroundHandler(new OneSignal.OSNotificationWillShowInForegroundHandler() {
            @Override
            public void notificationWillShowInForeground(OSNotificationReceivedEvent notificationReceivedEvent) {
                OSNotification notification = notificationReceivedEvent.getNotification();
                String message = notification.getBody();

                // Show the notification content in a custom dialog instead of just a Toast
                new AlertDialog.Builder(getApplicationContext())
                        .setTitle("New Notification")
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();

                // Optionally, show it in the system tray as well
                notificationReceivedEvent.complete(notification);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }

        });

        try {
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.addPlugin(new AWSS3StoragePlugin());
            Amplify.configure(getApplicationContext());
            Log.i("MyAmplifyApp", "Initialized Amplify");
        } catch (AmplifyException error) {
            Log.e("MyAmplifyApp", "Could not initialize Amplify " + error);
        }
    }
}
