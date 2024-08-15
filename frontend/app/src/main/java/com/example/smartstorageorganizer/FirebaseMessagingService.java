package com.example.smartstorageorganizer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import com.amazonaws.mobile.client.results.Tokens;
import com.google.firebase.messaging.RemoteMessage;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.results.SignInResult;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FirebaseMessagingService extends Service {
    public FirebaseMessagingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    //Implementation

    //    Handles the Incoming msgs from FCM
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle FCM messages here.
        Log.d("FCMService", "Message received: " + remoteMessage.getMessageId());
        if (remoteMessage.getNotification() != null) {
//            Display the notification msg if it exists
            sendNotification(remoteMessage.getNotification().getBody());
        }
    }



//    Handles the newly generated fcmToken (i.e if the token is refreshed)
    public void onNewToken(String token) {
        Log.d("FCMService", "New token: " + token);
//        super.onNewToken(token);
        // Send the new token to your server
        sendRegistrationToServer(token);
    }

    // Receives the AWS Mobile Client token (IdToken, AccessToken, and RefreshToken) and..
    //    ...handles sending the FCM token to backend server
    private void sendRegistrationToServer(String fcmToken) {
        // Get the user's authentication tokens
        AWSMobileClient.getInstance().getTokens(new Callback<Tokens>() {
            @Override
            public void onResult(Tokens tokens) {
                String idToken = tokens.getIdToken().getTokenString();
                String accessToken = tokens.getAccessToken().getTokenString();
                String refreshToken = tokens.getRefreshToken().getTokenString();

                // Use these tokens and the FCM token as needed, e.g., send them to your server
                updateUserTokenOnServer(idToken, accessToken, refreshToken, fcmToken);
            }

            @Override
            public void onError(Exception e) {
                Log.e("AWSMobileClient", "Failed to get user tokens.", e);
            }
        });
    }

    private void updateUserTokenOnServer(String idToken, String accessToken, String refreshToken, String fcmToken) {
        //backend endpoint
        String backendUrl = "https://ueolonfa4j.execute-api.eu-north-1.amazonaws.com/testing/";

        // Create a JSON object to hold the request data
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("idToken", idToken);
            requestBody.put("accessToken", accessToken);
            requestBody.put("refreshToken", refreshToken);
            requestBody.put("fcmToken", fcmToken);
        } catch (Exception e) {
            Log.e("updateUserTokens", "Failed to create JSON request body.", e);
            return;
        }

        // Send the request in a new thread to avoid blocking the main thread
        new Thread(() -> {
            try {
                // Create and configure the HttpURLConnection
                URL url = new URL(backendUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json; utf-8");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setDoOutput(true);

                // Write the JSON data to the output stream
                try(OutputStream os = urlConnection.getOutputStream()) {
                    byte[] input = requestBody.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                // Get the response code to determine success or failure
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Success
                    Log.d("updateUserTokens", "Tokens updated successfully.");
                } else {
                    // Error response
                    Log.e("updateUserTokens", "Failed to update tokens: " + responseCode);
                }

            } catch (Exception e) {
                // Handle any exceptions that occur during the request
                Log.e("updateUserTokens", "Failed to update tokens.", e);
            }
        }).start();
    }


    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        String channelId = "default_channel_id";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.outline_notifications_24)
                        .setContentTitle("New Message")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human-readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }
}