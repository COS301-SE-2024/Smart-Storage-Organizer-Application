package com.example.smartstorageorganizer.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.smartstorageorganizer.BuildConfig;
import com.example.smartstorageorganizer.HomeActivity;
import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.model.CategoryModel;
import com.example.smartstorageorganizer.model.ColorCodeModel;
import com.example.smartstorageorganizer.model.ItemModel;
import com.example.smartstorageorganizer.model.unitModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserUtils {

    private UserUtils() {}

    public static void checkUserVerificationStatus(String username, String authorizationToken, Activity activity, OperationCallback<String> callback) {
        // Construct the JSON payload
        String json = "{"
                + "\"header\": {"
                + "\"Authorization\": \"" + authorizationToken + "\""
                + "},"
                + "\"body\": {"
                + "\"username\": \"" + username + "\""
                + "}"
                + "}";

        // Define the media type
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        // Create the OkHttpClient instance
        OkHttpClient client = new OkHttpClient();

        // Define the API URL
        String API_URL = BuildConfig.getUserVerifIcationStatus;

        // Create the request body with the JSON payload
        RequestBody body = RequestBody.create(json, JSON);

        // Build the request
        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                activity.runOnUiThread(() -> {
                    Log.d("MyAmplifyApp", "POST request failed", e);
                    callback.onFailure(e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    activity.runOnUiThread(() -> Log.e("MyAmplifyApp", responseData));

                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        String bodyString = jsonObject.getString("body");

                        JSONObject bodyObject = new JSONObject(bodyString);
                        String status = bodyObject.getString("status");

                        activity.runOnUiThread(() -> {
                            Log.e("MyAmplifyApp", "POST request succeeded: " + responseData);
                            Log.e("MyAmplifyApp", "POST request succeeded: " + status);

                            callback.onSuccess(status);
                        });
                    }catch (JSONException e){
                        activity.runOnUiThread(() -> {
                            Log.e("MyAmplifyApp", "JSON parsing error: " + e.getMessage());
                            callback.onFailure(e.getMessage());
                        });
                    }
                } else {
                    activity.runOnUiThread(() -> {
                        Log.d("MyAmplifyApp", "POST request failed: " + response.code());
                        callback.onFailure("Response code" + response.code());
                    });
                }
            }
        });
    }

    public static void setUserToVerified(String username, String authorizationToken, Activity activity, OperationCallback<Boolean> callback) {
        // Construct the JSON payload
        String json = "{"
                + "\"header\": {"
                + "\"Authorization\": \"" + authorizationToken + "\""
                + "},"
                + "\"body\": {"
                + "\"username\": \"" + username + "\""
                + "}"
                + "}";

        // Define the media type
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        // Create the OkHttpClient instance
        OkHttpClient client = new OkHttpClient();

        // Define the API URL
        String API_URL = BuildConfig.setUserToVerified;

        // Create the request body with the JSON payload
        RequestBody body = RequestBody.create(json, JSON);

        // Build the request
        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                activity.runOnUiThread(() -> {
                    Log.d("MyAmplifyApp", "POST request failed", e);
                    callback.onFailure(e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    activity.runOnUiThread(() -> Log.e("MyAmplifyApp", responseData));

                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        String bodyString = jsonObject.getString("body");

//                        JSONObject bodyObject = new JSONObject(bodyString);
//                        String status = bodyObject.getString("status");

                        activity.runOnUiThread(() -> {
                            Log.e("MyAmplifyApp", "POST request succeeded: " + responseData);
                            Log.e("MyAmplifyApp", "POST request succeeded: " + bodyString);

                            callback.onSuccess(true);
                        });
                    }catch (JSONException e){
                        activity.runOnUiThread(() -> {
                            Log.e("MyAmplifyApp", "JSON parsing error: " + e.getMessage());
                            callback.onFailure(e.getMessage());
                        });
                    }
                } else {
                    activity.runOnUiThread(() -> {
                        Log.d("MyAmplifyApp", "POST request failed: " + response.code());
                        callback.onFailure("Response code" + response.code());
                    });
                }
            }
        });
    }

    public static void setUserToUnverified(String username, String authorizationToken, Activity activity, OperationCallback<Boolean> callback) {
        // Construct the JSON payload
        String json = "{"
                + "\"header\": {"
                + "\"Authorization\": \"" + authorizationToken + "\""
                + "},"
                + "\"body\": {"
                + "\"username\": \"" + username + "\""
                + "}"
                + "}";

        // Define the media type
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        // Create the OkHttpClient instance
        OkHttpClient client = new OkHttpClient();

        // Define the API URL
        String API_URL = BuildConfig.setUserToUnverified;

        // Create the request body with the JSON payload
        RequestBody body = RequestBody.create(json, JSON);

        // Build the request
        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                activity.runOnUiThread(() -> {
                    Log.d("MyAmplifyApp", "POST request failed", e);
                    callback.onFailure(e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    activity.runOnUiThread(() -> Log.e("MyAmplifyApp", responseData));

                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        String bodyString = jsonObject.getString("body");

//                        JSONObject bodyObject = new JSONObject(bodyString);
//                        String status = bodyObject.getString("status");

                        activity.runOnUiThread(() -> {
                            Log.e("MyAmplifyApp", "POST request succeeded: " + responseData);
                            Log.e("MyAmplifyApp", "POST request succeeded: " + bodyString);

                            callback.onSuccess(true);
                        });
                    }catch (JSONException e){
                        activity.runOnUiThread(() -> {
                            Log.e("MyAmplifyApp", "JSON parsing error: " + e.getMessage());
                            callback.onFailure(e.getMessage());
                        });
                    }
                } else {
                    activity.runOnUiThread(() -> {
                        Log.d("MyAmplifyApp", "POST request failed: " + response.code());
                        callback.onFailure("Response code" + response.code());
                    });
                }
            }
        });
    }
}
