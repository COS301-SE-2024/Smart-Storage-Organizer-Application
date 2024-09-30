package com.example.smartstorageorganizer;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NotificationsActivity extends AppCompatActivity {


    private EditText notificationTitle;
    private EditText notificationMessage;
    private Button sendButton;
    MyAmplifyApp app ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        app = (MyAmplifyApp) getApplicationContext();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        notificationTitle = findViewById(R.id.notificationTitle);
        notificationMessage = findViewById(R.id.notificationMessage);
        sendButton = findViewById(R.id.sendButton);

        sendButton.setOnClickListener(v -> {
            String title = notificationTitle.getText().toString();
            String message = notificationMessage.getText().toString();

            if (!title.isEmpty() && !message.isEmpty()) {
                sendNotificationFromPhone(message, title);
            } else {
                Log.e("Notification", "Title or message is empty");
                Toast.makeText(this, "Please enter both title and message", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void sendNotificationFromPhone(String message, String title) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://onesignal.com/api/v1/notifications";

        // Build JSON payload for the notification
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("app_id", "152f0f5c-d21d-4e43-89b1-5e02acc42abe");
            // jsonBody.put("included_segments", new JSONArray().put("All"));  // Target audience (e.g., All users)
            jsonBody.put("include_external_user_ids", new JSONArray().put("zhouvel7@gmail.com"));
            jsonBody.put("contents", new JSONObject().put("en", message));  // The notification message
            jsonBody.put("headings", new JSONObject().put("en", title));    // The notification title
           // jsonBody.put("tags", new JSONObject().put("organizationID", app.getOrganizationID()));
            jsonBody.put("filters", new JSONArray()
                    .put(new JSONObject().put("field", "tag").put("key", "organizationID").put("relation", "=").put("value", app.getOrganizationID())));


        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating JSON payload", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create request body with JSON payload
        RequestBody body = RequestBody.create(
                jsonBody.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );

        // Build the request with headers (including Authorization with your REST API key)
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "Basic ZGFlMzY3MjktOGIyOC00ZDI4LTg1MzQtMWE5NjY2ZDJkOGZh")  // Replace with your REST API key
                .addHeader("Content-Type", "application/json")
                .build();

        // Execute the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("OneSignal", "Failed to send notification", e);
                runOnUiThread(() -> Toast.makeText(NotificationsActivity.this, "Failed to send notification", Toast.LENGTH_SHORT).show());

            }

            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    Log.d("OneSignal", "Notification sent successfully");
                    runOnUiThread(() -> Toast.makeText(NotificationsActivity.this, "Notification sent successfully", Toast.LENGTH_SHORT).show());
                    finish();
                } else {
                    Log.e("OneSignal", "Failed to send notification, response code: " + response.code());
                    runOnUiThread(() -> Toast.makeText(NotificationsActivity.this, "Failed to send notification", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}