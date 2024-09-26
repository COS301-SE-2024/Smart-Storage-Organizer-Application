package com.example.smartstorageorganizer.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
//import android.util.Log;
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
import com.example.smartstorageorganizer.model.TokenManager;
import com.example.smartstorageorganizer.model.UserModel;
import com.example.smartstorageorganizer.model.unitModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
//                    Log.d("MyAmplifyApp", "POST request failed", e);
                    callback.onFailure(e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
//                    activity.runOnUiThread(() -> Log.e("MyAmplifyApp", responseData));

                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        String bodyString = jsonObject.getString("body");

                        JSONObject bodyObject = new JSONObject(bodyString);
                        String status = bodyObject.getString("status");

                        activity.runOnUiThread(() -> {
//                            Log.e("MyAmplifyApp", "POST request succeeded: " + responseData);
//                            Log.e("MyAmplifyApp", "POST request succeeded: " + status);

                            callback.onSuccess(status);
                        });
                    }catch (JSONException e){
                        activity.runOnUiThread(() -> {
//                            Log.e("MyAmplifyApp", "JSON parsing error: " + e.getMessage());
                            callback.onFailure(e.getMessage());
                        });
                    }
                } else {
                    activity.runOnUiThread(() -> {
//                        Log.d("MyAmplifyApp", "POST request failed: " + response.code());
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
        TokenManager.getToken().thenAccept(results-> {
//            activity.runOnUiThread(() -> Log.d("MyAmplifyApp", results));
            Request request = new Request.Builder()
                            .url(API_URL)
                            .addHeader("Authorization", results)
                            .post(body)
                            .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(() -> {
//                        Log.d("MyAmplifyApp", "POST request failed", e);
                        callback.onFailure(e.getMessage());
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String responseData = response.body().string();
//                        activity.runOnUiThread(() -> Log.e("MyAmplifyApp", responseData));

                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            String bodyString = jsonObject.getString("body");

                            activity.runOnUiThread(() -> {
//                                Log.e("MyAmplifyApp", "POST request succeeded: " + responseData);
//                                Log.e("MyAmplifyApp", "POST request succeeded: " + bodyString);

                                callback.onSuccess(true);
                            });
                        }catch (JSONException e){
                            activity.runOnUiThread(() -> {
//                                Log.e("MyAmplifyApp", "JSON parsing error: " + e.getMessage());
                                callback.onFailure(e.getMessage());
                            });
                        }
                    } else {
                        activity.runOnUiThread(() -> {
//                            Log.d("MyAmplifyApp", "POST request failed: " + response.code());
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

    public static void setUserToUnverified(String username, String authorizationToken, Activity activity, OperationCallback<Boolean> callback) {
        String json = "{"
                + "\"header\": {"
                + "\"Authorization\": \"" + authorizationToken + "\""
                + "},"
                + "\"body\": {"
                + "\"username\": \"" + username + "\""
                + "}"
                + "}";

        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

        String API_URL = BuildConfig.setUserToUnverified;

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
                        activity.runOnUiThread(() -> {
//                        Log.d("MyAmplifyApp", "POST request failed", e);
                            callback.onFailure(e.getMessage());
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            final String responseData = response.body().string();
//                        activity.runOnUiThread(() -> Log.e("MyAmplifyApp", responseData));

                            try {
                                JSONObject jsonObject = new JSONObject(responseData);
                                String bodyString = jsonObject.getString("body");

                                activity.runOnUiThread(() -> {
//                                Log.e("MyAmplifyApp", "POST request succeeded: " + responseData);
//                                Log.e("MyAmplifyApp", "POST request succeeded: " + bodyString);

                                    callback.onSuccess(true);
                                });
                            }catch (JSONException e){
                                activity.runOnUiThread(() -> {
//                                Log.e("MyAmplifyApp", "JSON parsing error: " + e.getMessage());
                                    callback.onFailure(e.getMessage());
                                });
                            }
                        } else {
                            activity.runOnUiThread(() -> {
//                            Log.d("MyAmplifyApp", "POST request failed: " + response.code());
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

    public static void getUsersInGroup(String username, String type, String authorizationToken, Activity activity, OperationCallback<List<UserModel>> callback) {
        String json = "{"
                + "\"body\": {"
                + "\"username\": \"" + username + "\","
                + "\"type\": \"" + type + "\""
                + "}"
                + "}";

//        activity.runOnUiThread(() -> Log.e("View Response Results Body Array", username+" "+type));


        List<UserModel> usersList = new ArrayList<>();

        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

        String API_URL = BuildConfig.getUsersInGroup;

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
                    activity.runOnUiThread(() -> {
//                        Log.d("MyAmplifyApp Group", "POST request failed", e);
                        callback.onFailure(e.getMessage());
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String responseData = response.body().string();
//                        activity.runOnUiThread(() -> Log.e("MyAmplifyApp Group", responseData));

                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            String bodyString = jsonObject.getString("body");
                            JSONArray bodyArray = new JSONArray(bodyString);
//                            activity.runOnUiThread(() -> Log.e("View Response Results Body Array", bodyArray.toString()));

                            for (int i = 0; i < bodyArray.length(); i++) {
                                JSONObject itemObject = bodyArray.getJSONObject(i);

                                UserModel user = new UserModel();
                                user.setEmail(itemObject.getString("email"));
                                user.setName(itemObject.getString("name"));
                                user.setSurname(itemObject.getString("surname"));
                                if(Objects.equals(type, "verifiedUsers")){
                                    user.setStatus("Active");
                                }
                                else {
                                    user.setStatus("Pending");
                                }

                                usersList.add(user);
                            }

                            activity.runOnUiThread(() -> callback.onSuccess(usersList));


                        }catch (JSONException e){
                            activity.runOnUiThread(() -> {
//                                Log.e("MyAmplifyApp Group", "JSON parsing error: " + e.getMessage());
                                callback.onFailure(e.getMessage());
                            });
                        }
                    } else {
                        activity.runOnUiThread(() -> {
//                            Log.d("MyAmplifyApp Group", "POST request failed: " + response.code());
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

    public static void setUserRole(String username, String role, String authorizationToken, Activity activity, OperationCallback<Boolean> callback) {
        String json = "{"
                + "\"body\": {"
                + "\"username\": \"" + username + "\","
                + "\"role\": \"" + role + "\""
                + "}"
                + "}";

        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

        String API_URL = BuildConfig.SetRoleEndPoint;

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

                }).exceptionally(ex -> {
//            Log.e("TokenError", "Failed to get user token", ex);
            return null;
        });
    }


    public static void getUserRole(String username, String authorizationToken, Activity activity, OperationCallback<String> callback) {
        String json = "{"
                + "\"body\": {"
                + "\"username\": \"" + username + "\""
                + "}"
                + "}";

        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

        String API_URL = BuildConfig.getUserRoles;

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
                    activity.runOnUiThread(() -> {
//                        Log.d("MyAmplifyApp", "POST request failed", e);
                        callback.onFailure(e.getMessage());
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String responseData = response.body().string();
//                        activity.runOnUiThread(() -> Log.e("MyAmplifyApp", responseData));

                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            String bodyString = jsonObject.getString("body");

                            JSONObject roleObject = new JSONObject(bodyString);
                            String roleString = roleObject.getString("role");

                            activity.runOnUiThread(() -> {
//                                Log.e("MyAmplifyApp", "POST request succeeded: " + responseData);
//                                Log.e("MyAmplifyApp", "POST request succeeded: " + bodyString);

                                callback.onSuccess(roleString);
                            });
                        }catch (JSONException e){
                            activity.runOnUiThread(() -> {
//                                Log.e("MyAmplifyApp", "JSON parsing error: " + e.getMessage());
                                callback.onFailure(e.getMessage());
                            });
                        }
                    } else {
                        activity.runOnUiThread(() -> {
//                            Log.d("MyAmplifyApp", "POST request failed: " + response.code());
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

//    "email":"admin",
//            "name":"victor",
//            "surname":"zhou",
//            "type":"sign_in",
//            "organization_id":1,
//            "time":"2021-05-20"


    public static void loginActivities(String email, String name, String surname, String type,String organization_id, String time, Activity activity, OperationCallback<Boolean> callback) {
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
                    activity.runOnUiThread(() -> {
//                        Log.d("MyAmplifyApp Group", "POST request failed", e);
                        callback.onFailure(e.getMessage());
                    });
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

                            activity.runOnUiThread(() -> callback.onSuccess(true));


                        }catch (JSONException e){
                            activity.runOnUiThread(() -> {
//                                Log.e("MyAmplifyApp Group", "JSON parsing error: " + e.getMessage());
                                callback.onFailure(e.getMessage());
                            });
                        }
                    } else {
                        activity.runOnUiThread(() -> {
//                            Log.d("MyAmplifyApp Group", "POST request failed: " + response.code());
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