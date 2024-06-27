package com.example.smartstorageorganizer.utils;

import android.app.Activity;
import android.util.Log;

import com.example.smartstorageorganizer.BuildConfig;
import com.example.smartstorageorganizer.model.CategoryModel;

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

public class Utils {

    private Utils() {}

    public static void fetchParentCategories(int categoryId, String email, Activity activity, OperationCallback<List<CategoryModel>> callback) {
        String json = "{\"useremail\":\"" + email + "\", \"parentcategory\":\"" + Integer.toString(categoryId) + "\" }";

        List<CategoryModel> categoryModelList = new ArrayList<>();
        String message = "Request Method";

        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String apiUrl = BuildConfig.FetchCategoryEndPoint;
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
                    Log.e("Category Request Method", "GET request failed", e);
                    callback.onFailure(e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    activity.runOnUiThread(() -> Log.e("Category Response Results", responseData));
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        String bodyString = jsonObject.getString("body");
                        JSONArray bodyArray = new JSONArray(bodyString);
                        activity.runOnUiThread(() -> Log.e("Category Details Array", bodyArray.toString()));

                        for (int i = 0; i < bodyArray.length(); i++) {
                            JSONObject itemObject = bodyArray.getJSONObject(i);

                            CategoryModel parentCategory = new CategoryModel();
                            parentCategory.setCategoryID(itemObject.getString("id"));
                            parentCategory.setCategoryName(itemObject.getString("categoryname"));

                            categoryModelList.add(parentCategory);
                        }
                        activity.runOnUiThread(() -> callback.onSuccess(categoryModelList));
                    } catch (JSONException e) {
                        activity.runOnUiThread(() -> {
                            Log.e(message, "GET request failed: " + response);
                            callback.onFailure(e.getMessage());
                        });
                    }
                } else {
                    activity.runOnUiThread(() -> {
                        Log.e(message, "GET request failed: " + response);
                        callback.onFailure("Response code: " + response.code());
                    });
                }
            }
        });
    }

    public static void addCategory(int parentCategory, String categoryName, String email, String url, Activity activity, OperationCallback<Boolean> callback) {
        String json = "{\"useremail\":\"" + email + "\", \"parentcategory\":\"" + Integer.toString(parentCategory) + "\", \"categoryname\":\"" + categoryName + "\", \"icon\": \"" + url + "\" }";

        String message = "AddCategory";
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String apiUrl = BuildConfig.AddCategoryEndPoint;
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
                        callback.onFailure("Response code: " + response.code());
                    });
                }
            }
        });
    }
}
