package com.example.smartstorageorganizer;
import android.app.Application;
import android.util.Log;


import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.configuration.AmplifyOutputs;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
//import com.amplifyframework.core.configuration.AmplifyConfiguration;

public class MyAmplifyApp extends Application {
    private AmplifyException error;

    @Override
    public void onCreate() {
        super.onCreate();

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
}
