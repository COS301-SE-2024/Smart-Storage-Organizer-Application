package com.example.smartstorageorganizer;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;


import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.AmplifyException;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.UserUtils;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OSNotificationReceivedEvent;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyAmplifyApp extends Application {
    private static final String ONESIGNAL_APP_ID = "152f0f5c-d21d-4e43-89b1-5e02acc42abe";
    private String organizationID;
    private String organizationName;
    private String name;
    private String surname;
    private String email;
    private String userRole = "";
    private boolean loggedIn;
    private boolean startService = false;
    @Override
    public void onCreate() {
        super.onCreate();

        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean notificationPromptShown = sharedPreferences.getBoolean("notificationPromptShown", false);

        OneSignal.setNotificationOpenedHandler(new OneSignal.OSNotificationOpenedHandler() {
            public void notificationOpened(OSNotificationOpenedResult result) {
                Amplify.Auth.fetchAuthSession(
                        authResult -> {
                            if (authResult.isSignedIn()) {
                                Intent intent = new Intent(getApplicationContext(), NotificationsActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.putExtra("data_key", result.getNotification().getAdditionalData().toString());
                                startActivity(intent);

                                // Check if the notification prompt has been shown before
                                if (!notificationPromptShown) {
                                    // Show dialog to allow notifications only for first login
                                    new AlertDialog.Builder(getApplicationContext())
                                            .setTitle("Allow Notifications")
                                            .setMessage("Would you like to allow notifications?")
                                            .setPositiveButton("Allow", (dialog, which) -> {
                                                // Store the flag that the prompt has been shown
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putBoolean("notificationPromptShown", true);
                                                editor.apply();
                                            })
                                            .setNegativeButton("Don't Allow", null)
                                            .show();
                                }
                            } else {
                                // If not signed in, redirect to login
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                            }
                        },
                        error -> {
                            Log.e("AuthSession", "Session check failed: " + error);
                            // Optional: Handle session check failure (e.g., show an error)
                        }
                );
            }

        });

        // Handle notification when app is in the foreground (in-app notifications)
        OneSignal.setNotificationWillShowInForegroundHandler(new OneSignal.OSNotificationWillShowInForegroundHandler() {
            @Override
            public void notificationWillShowInForeground(OSNotificationReceivedEvent notificationReceivedEvent) {
                OSNotification notification = notificationReceivedEvent.getNotification();
                String message = notification.getBody();

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

    public void setupOneSignalForUser(String email, String organizationID, String userRole) {
        OneSignal.setExternalUserId(email, new OneSignal.OSExternalUserIdUpdateCompletionHandler() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                Log.i("OneSignal", "External user ID set: " + jsonObject.toString());
            }


            @Override
            public void onFailure(OneSignal.ExternalIdError externalIdError) {
                Log.e("OneSignal", "Failed to set external user ID: " + externalIdError.toString());
            }
        });

        OneSignal.sendTag("organizationID", organizationID);
        OneSignal.sendTag("userRole", userRole);
    }
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

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }
}
