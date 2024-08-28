package com.example.smartstorageorganizer;
import android.app.Application;
import android.util.Log;


import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.AmplifyException;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
//import com.onesignal.Continue;
//import com.onesignal.OneSignal;
//import com.onesignal.debug.LogLevel;

public class MyAmplifyApp extends Application {
    private static final String ONESIGNAL_APP_ID = "152f0f5c-d21d-4e43-89b1-5e02acc42abe";
    private String organizationID;
    @Override
    public void onCreate() {
        super.onCreate();

        // Verbose Logging set to help debug issues, remove before releasing your app.
//
////        //   OneSignal.getDebug().setLogLevel(LogLevel.VERBOSE);
// OneSignal Initialization
//        OneSignal.initWithContext(this, ONESIGNAL_APP_ID);
//
//        // requestPermission will show the native Android notification permission prompt.
//        // NOTE: It's recommended to use a OneSignal In-App Message to prompt instead.
//        OneSignal.getNotifications().requestPermission(false, Continue.none());

        try {

            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.addPlugin(new AWSS3StoragePlugin());
            Amplify.configure(getApplicationContext());
            Log.i("MyAmplifyApp", "Initialized Amplify");
        } catch (AmplifyException error) {
            Log.e("MyAmplifyApp", "Could not initialize Amplify " +error);
        }
    }

    public String getOrganizationID() {
        return organizationID;
    }

    public void setOrganizationID(String organizationID) {
        this.organizationID = organizationID;
    }




}
