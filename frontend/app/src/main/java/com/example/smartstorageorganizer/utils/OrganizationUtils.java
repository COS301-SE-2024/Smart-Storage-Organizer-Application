package com.example.smartstorageorganizer.utils;

import android.app.Activity;
import android.util.Log;

import com.example.smartstorageorganizer.BuildConfig;
import com.example.smartstorageorganizer.model.TokenManager;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OrganizationUtils {
    private static String type = "application/json; charset=utf-8";
    private OrganizationUtils() {}

    public static void addOrganization(String organizationName,String ownerEmail, Activity activity, OperationCallback<Boolean> callback) {
        String json = "{\"name\":\"" + organizationName + "\", \"owner\":\"" + ownerEmail + "\"}";

        String message = "Add Category";
        Log.i(message, "POST request succeeded: Outside");
        MediaType mediaType = MediaType.get(type);
        OkHttpClient client = new OkHttpClient();
        String apiUrl = BuildConfig.CreateOrganization;
        RequestBody body = RequestBody.create(json, mediaType);
        TokenManager.getToken().thenAccept(results->{
            Log.i(message, "POST request succeeded: Inside");
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .addHeader("Authorization", results)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(() -> {
                        Log.e(message, "POST request failed", e);
                        callback.onFailure(e.getMessage());
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String responseData = response.body().string();
                        activity.runOnUiThread(() -> {
                            Log.i(message, "POST request succeeded: " + responseData);
                            callback.onSuccess(true);
                        });
                    } else {
                        activity.runOnUiThread(() -> {
                            Log.e(message, "POST request failed: " + response.code());
                            callback.onFailure("Response code" + response.code());
                        });
                    }
                }
            });
        }).exceptionally(ex -> {
//            Log.e("TokenError", "Failed to get user token", ex);
            return null;
        });
    }
}
