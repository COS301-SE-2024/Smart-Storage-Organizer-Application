package com.example.smartstorageorganizer;
import android.app.Application;
import android.util.Log;

import com.amplifyframework.storage.s3.AWSS3StoragePlugin;

public class MyAmplifyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

//        try {
//
//            Amplify.addPlugin(new AWSApiPlugin());
//            Amplify.addPlugin(new AWSCognitoAuthPlugin());
//            Amplify.addPlugin(new AWSS3StoragePlugin());
//            Amplify.configure(getApplicationContext());
//            Log.i("MyAmplifyApp", "Initialized Amplify");
//        } catch (AmplifyException error) {
//            Log.e("MyAmplifyApp", "Could not initialize Amplify " +error);
//        }
    }
}
