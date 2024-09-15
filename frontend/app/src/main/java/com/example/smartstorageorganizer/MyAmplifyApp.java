package com.example.smartstorageorganizer;
import android.app.Application;
import android.util.Log;
import android.widget.Toast;


import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.AmplifyException;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.UserUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
//import com.onesignal.Continue;
//import com.onesignal.OneSignal;
//import com.onesignal.debug.LogLevel;

public class MyAmplifyApp extends Application {
    private static final String ONESIGNAL_APP_ID = "152f0f5c-d21d-4e43-89b1-5e02acc42abe";
    private String organizationID;
    private String name;
    private String surname;
    private String email;
    private String userRole = "";
    private boolean loggedIn;
    private boolean startService = false;
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

//    @Override
//    public void onTerminate() {
//        super.onTerminate();
//        Date currentDate = new Date();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String formattedDate = dateFormat.format(currentDate);
//        // Called when the app is explicitly terminated (but not guaranteed to be called)
//        loginActivities(formattedDate);  // API call when the app is closed
//    }

//    public void loginActivities(String time) {
//        UserUtils.loginActivities(email, name, surname, "sign_out", organizationID, time, , new OperationCallback<Boolean>() {
//            @Override
//            public void onSuccess(Boolean result) {
//
////                Toast.makeText(HomeActivity.this, "Login Activities Failed to Save"+ result, Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onFailure(String error) {
////                hideLoading();
////                loginActivities(email, name, surname, "sign_out", organization_id, time);
//            }
//        });
//    }

    public String getOrganizationID() {
        return organizationID;
    }

    public void setOrganizationID(String organizationID) {
        this.organizationID = organizationID;
    }
    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isStartService() {
        return startService;
    }

    public void setStartService(boolean startService) {
        this.startService = startService;
    }
}
