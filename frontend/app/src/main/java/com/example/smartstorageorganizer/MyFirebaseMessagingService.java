package com.example.smartstorageorganizer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.results.Tokens;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final String ACTION_NEW_NOTIFICATION = "com.example.smartstorageorganizer.NEW_NOTIFICATION";

    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        // Handle FCM messages here.
        Log.d("FCMService", "Message received: " + remoteMessage.getMessageId());
        if (remoteMessage.getNotification() != null) {
            // Display the notification message if it exists
            getFirebaseMessage(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());        }
    }

    private void getFirebaseMessage(String messageTitle, String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, "notify")
                        .setSmallIcon(R.drawable.outline_notifications_24)
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

//        NotificationCompat managerCompat = NotificationCompat.from(this);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        managerCompat.notify(999, notificationBuilder.build());
        notificationManager.notify(0, notificationBuilder.build());
    }

}