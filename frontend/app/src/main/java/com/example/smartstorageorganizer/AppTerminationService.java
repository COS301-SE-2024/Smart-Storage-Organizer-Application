package com.example.smartstorageorganizer;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.core.app.NotificationCompat;

import com.example.smartstorageorganizer.model.TokenManager;
import com.example.smartstorageorganizer.model.UserModel;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AppTerminationService extends Service {
    MyAmplifyApp app;
    FirebaseFirestore db;
    FirebaseAuth auth;
    private static final String CHANNEL_ID = "AppExitServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("AppTerminationService", "Service started");
        app = (MyAmplifyApp) getApplicationContext();

        // Call startForeground() as soon as the service starts
        createNotificationChannel();  // Create notification channel for Android O and above
        startForeground(1, getNotification());  // Start the service in the foreground with a notification
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Service logic here (if needed)
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d("AppTerminationService", "App removed from recent apps");
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = dateFormat.format(currentDate);
        // Send API request when the app is force-closed
        sendAppExitApi(app.getEmail(), app.getName(), app.getSurname(), "sign_out", app.getOrganizationID(), formattedDate);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        String userId = app.getEmail();
        db.collection("active_users").document(userId)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "User removed from active users"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error removing user", e));


        // Stop the service after sending the API
        stopSelf();
    }

    private void sendAppExitApi(String email, String name, String surname, String type, String organization_id, String time) {
        Log.d("AppTerminationService", "Sending API for app exit time");

        String json = "{"
                + "\"body\": {"
                + "\"email\": \"" + email + "\","
                + "\"name\": \"" + name + "\","
                + "\"surname\": \"" + surname + "\","
                + "\"type\": \"" + type + "\","
                + "\"organization_id\": \"" + Integer.parseInt(organization_id) + "\","
                + "\"time\": \"" + time + "\""
                + "}"
                + "}";

//        activity.runOnUiThread(() -> Log.e("View Response Results Body Array", username+" "+type));


        List<UserModel> usersList = new ArrayList<>();

        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

        String API_URL = BuildConfig.loginActivities;

        RequestBody body = RequestBody.create(json, JSON);

        TokenManager.getToken().thenAccept(results-> {

            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", results)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String responseData = response.body().string();
//                        activity.runOnUiThread(() -> Log.e("MyAmplifyApp Group", responseData));

                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            String bodyString = jsonObject.getString("body");
//                            JSONArray bodyArray = new JSONArray(bodyString);
//                            activity.runOnUiThread(() -> Log.e("View Response Results Body Array", bodyArray.toString()));

                        }catch (JSONException e){

                        }
                    } else {

                    }
                }
            });

        }).exceptionally(ex -> {
//            Log.e("TokenError", "Failed to get user token", ex);
            return null;
        });
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;  // We don't need to bind this service
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "App Exit Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    private Notification getNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("App is running")
                .setContentText("Tracking app exit...")
                .setSmallIcon(R.drawable.ic_launcher_foreground)  // Replace with your app icon
                .build();
    }
}
