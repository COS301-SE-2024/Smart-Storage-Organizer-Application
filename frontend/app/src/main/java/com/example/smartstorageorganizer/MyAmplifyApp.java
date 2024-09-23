package com.example.smartstorageorganizer;

import android.app.Application;
import android.util.Log;
import android.view.View;


import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.example.smartstorageorganizer.databinding.ActivityMainBinding;
import com.onesignal.Continue;
import com.onesignal.OneSignal;
import com.onesignal.debug.LogLevel;

public class MyAmplifyApp extends Application {
    private ActivityMainBinding binding;
    private static final String ONESIGNAL_APP_ID = "152f0f5c-d21d-4e43-89b1-5e02acc42abe";
    //f53cb2d5-e439-4641-8b9c-a27f6d4b57bb

    @Override
    public void onCreate() {
        super.onCreate();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
//        OneSignal.setAppId(ONESIGNAL_APP_ID);



        // Set notification opened handler
//        OneSignal.setNotificationOpenedHandler(new OneSignal.NotificationOpenedHandler() {
//            @Override
//            public void notificationOpened(OSNotificationOpenedResult result) {
//                // Handle the notification click
//                OSNotification notification = result.notification;
//                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//            }
//        });
        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OneSignal.getInAppMessages().addTrigger("push_prompt", "1");
            }
        });


        // Request permissions if necessary
        // Note: This line is not always required; adjust based on your needs and SDK version
//        OneSignal.promptForPushNotifications();
        // Verbose Logging set to help debug issues, remove before releasing your app.
        OneSignal.getDebug().setLogLevel(LogLevel.VERBOSE);

        // OneSignal Initialization
        OneSignal.initWithContext(this, ONESIGNAL_APP_ID);



        // requestPermission will show the native Android notification permission prompt.
        // NOTE: It's recommended to use a OneSignal In-App Message to prompt instead.
        OneSignal.getNotifications().requestPermission(false, Continue.none());



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
