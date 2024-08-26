package com.example.smartstorageorganizer.utils;

import android.app.Activity;
import android.util.Log;

import com.example.smartstorageorganizer.BuildConfig;
import com.example.smartstorageorganizer.model.ColorCodeModel;
import com.example.smartstorageorganizer.model.OrganizationModel;
import com.example.smartstorageorganizer.model.TokenManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

        Request request = new Request.Builder()
                .url(apiUrl)
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

    }

    public static void fetchOrganizationDetails(String organizationId, Activity activity, OperationCallback<OrganizationModel> callback)
    {
//        String json = "{}";
        String json = "{\"id\":\""+Integer.parseInt(organizationId)+"\" }";

        OrganizationModel organization = new OrganizationModel();

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.FetchOrganization;
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                activity.runOnUiThread(() -> {
//                    Log.e(message, "GET request failed", e);
                    callback.onFailure(e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
//                    activity.runOnUiThread(() -> Log.e(message, responseData));

                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        String bodyString = jsonObject.getString("organization");
                        JSONArray bodyArray = new JSONArray(bodyString);
//                        activity.runOnUiThread(() -> Log.e("View Response Results Body Array", bodyArray.toString()));

                        for (int i = 0; i < bodyArray.length(); i++) {
                            JSONObject itemObject = bodyArray.getJSONObject(i);

                            organization.setOrganizationName(itemObject.getString("name"));
                            organization.setOrganizationId(itemObject.getString("id"));

//                            organizationModelList.add(organization);
                        }

                        activity.runOnUiThread(() -> callback.onSuccess(organization));
                    } catch (JSONException e) {
                        activity.runOnUiThread(() -> {
//                            Log.e(message, "JSON parsing error: " + e.getMessage());
                            callback.onFailure(e.getMessage());
                        });
                    }
                } else {
                    activity.runOnUiThread(() -> {
//                        Log.e(message, "GET request failed:" + response);
                        callback.onFailure("Response code:" + response.code());
                    });
                }
            }
        });

    }

    public static void fetchOrganizationsDetails(Activity activity, OperationCallback<List<OrganizationModel>> callback)
    {
//        String json = "{}";
//        String json = "{\"id\":\""+Integer.parseInt(organizationId)+"\" }";

        List<OrganizationModel> organizationsList = new ArrayList<>();

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.FetchOrganization;
//        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(API_URL)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                activity.runOnUiThread(() -> {
//                    Log.e(message, "GET request failed", e);
                    callback.onFailure(e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
//                    activity.runOnUiThread(() -> Log.e(message, responseData));

                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        String bodyString = jsonObject.getString("organization");
                        JSONArray bodyArray = new JSONArray(bodyString);
//                        activity.runOnUiThread(() -> Log.e("View Response Results Body Array", bodyArray.toString()));

                        for (int i = 0; i < bodyArray.length(); i++) {
                            JSONObject itemObject = bodyArray.getJSONObject(i);

                            OrganizationModel organization = new OrganizationModel();
                            organization.setOrganizationName(itemObject.getString("name"));
                            organization.setOrganizationId(itemObject.getString("id"));

                            organizationsList.add(organization);
                        }

                        activity.runOnUiThread(() -> callback.onSuccess(organizationsList));
                    } catch (JSONException e) {
                        activity.runOnUiThread(() -> {
//                            Log.e(message, "JSON parsing error: " + e.getMessage());
                            callback.onFailure(e.getMessage());
                        });
                    }
                } else {
                    activity.runOnUiThread(() -> {
//                        Log.e(message, "GET request failed:" + response);
                        callback.onFailure("Response code:" + response.code());
                    });
                }
            }
        });

    }
}
