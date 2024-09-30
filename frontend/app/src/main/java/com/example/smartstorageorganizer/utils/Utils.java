package com.example.smartstorageorganizer.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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

import com.amplifyframework.core.Amplify;
import  com.amplifyframework.auth.AuthUser;
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession;
import com.amplifyframework.auth.result.AuthSessionResult;
import com.amplifyframework.auth.options.AuthFetchSessionOptions;

import com.example.smartstorageorganizer.BuildConfig;
import com.example.smartstorageorganizer.HomeActivity;
import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.model.ArrangementModel;
import com.example.smartstorageorganizer.model.BinItemModel;
import com.example.smartstorageorganizer.model.CategoryModel;
import com.example.smartstorageorganizer.model.CategoryReportModel;
import com.example.smartstorageorganizer.model.ColorCodeModel;
import com.example.smartstorageorganizer.model.ItemModel;
import com.example.smartstorageorganizer.model.LoginReportsModel;
import com.example.smartstorageorganizer.model.SubcategoryReportModel;
import com.example.smartstorageorganizer.model.SuggestedCategoryModel;
import com.example.smartstorageorganizer.model.TokenManager;
import com.example.smartstorageorganizer.model.unitModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import aws.smithy.kotlin.runtime.content.Document;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Utils
{
    private static String type = "application/json; charset=utf-8";
    private static String message = "Request Method";

    private Utils() {}

    public static void fetchParentCategories(int categoryId, String email, String organizationID, Activity activity, OperationCallback<List<CategoryModel>> callback) {
//        String json = "{\"parentcategory\":\"" + Integer.toString(categoryId) + "\" }";
        String json = "{\"parentcategory\":\"" + Integer.toString(categoryId) +"\", \"organizationid\":\""+Integer.parseInt(organizationID)+"\" }";


        List<CategoryModel> categoryModelList = new ArrayList<>();

        MediaType mediaType = MediaType.get(type);
        OkHttpClient client = new OkHttpClient();
        String apiUrl = BuildConfig.FetchCategoryEndPoint;
        RequestBody body = RequestBody.create(json, mediaType);
        TokenManager.getToken().thenAccept(results->{
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
                        Log.e(message, "GET request failed", e);
                        callback.onFailure(e.getMessage());
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String responseData = response.body().string();
                        activity.runOnUiThread(() -> Log.e(message, responseData));
                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            String bodyString = jsonObject.getString("body");
                            JSONArray bodyArray = new JSONArray(bodyString);
                            activity.runOnUiThread(() -> Log.e(message, bodyArray.toString()));

                            for (int i = 0; i < bodyArray.length(); i++) {
                                JSONObject itemObject = bodyArray.getJSONObject(i);

                                CategoryModel parentCategory = new CategoryModel();
                                parentCategory.setCategoryID(itemObject.getString("id"));
                                parentCategory.setCategoryName(itemObject.getString("categoryname"));
                                if(categoryId == 0){
                                    parentCategory.setImageUrl(itemObject.getString("icon"));
                                }

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
        }).exceptionally(ex -> {
//            Log.e("TokenError", "Failed to get user token", ex);
            return null;
        });
    }

    public static void addCategory(int parentCategory, String categoryName, String email, String url, String organizationID, Activity activity, OperationCallback<Boolean> callback) {
        String json = "{\"useremail\":\"" + email + "\", \"parentcategory\":\"" + Integer.toString(parentCategory) + "\", \"categoryname\":\"" + categoryName + "\", \"icon\": \"" + url + "\", \"organizationid\":\""+Integer.parseInt(organizationID)+"\" }";

        String message = "Add Category";
        MediaType mediaType = MediaType.get(type);
        OkHttpClient client = new OkHttpClient();
        String apiUrl = BuildConfig.AddCategoryEndPoint;
        RequestBody body = RequestBody.create(json, mediaType);
        TokenManager.getToken().thenAccept(results->{
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

    public static void filterByCategory(int parentCategory, int howMany, int pageNumber, String organizationID, Activity activity, OperationCallback<List<ItemModel>> callback) {
//        String json = "{\"parentcategory\":\"" + parentCategory + "\" }";
        String json = "{\"parentcategory\":\"" + parentCategory + "\", \"limit\":\""+Integer.toString(howMany)+"\", \"offset\":\""+Integer.toString(pageNumber)+"\", \"organizationid\":\""+ Integer.parseInt(organizationID)+"\" }";
        String message = "View Response Results";

        List<ItemModel> itemModelList = new ArrayList<>();
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();
        String apiUrl = BuildConfig.CategoryFilterEndPoint;
        RequestBody body = RequestBody.create(json, JSON);
        TokenManager.getToken().thenAccept(results->{

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
//                        Log.e(message, "GET request failed One", e);
                        callback.onFailure(e.getMessage());
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    if (response.isSuccessful()) {
                        final String responseData = response.body().string();
//                        activity.runOnUiThread(() -> Log.e(message, responseData));

                        try {

                            JSONObject jsonObject = new JSONObject(responseData);
                            String bodyString = jsonObject.getString("body");
                            JSONArray bodyArray = new JSONArray(bodyString);

                            for (int i = 0; i < bodyArray.length(); i++) {
                                JSONObject itemObject = bodyArray.getJSONObject(i);

                                ItemModel item = new ItemModel();
                                item.setItemId(itemObject.getString("item_id"));
                                item.setItemName(itemObject.getString("item_name"));
                                item.setDescription(itemObject.getString("description"));
                                item.setColourCoding(itemObject.getString("colourcoding"));
                                item.setBarcode(itemObject.getString("barcode"));
                                item.setQrcode(itemObject.getString("qrcode"));
                                item.setQuantity(itemObject.getString("quanity"));
                                item.setLocation(itemObject.getString("location"));
                                item.setEmail(itemObject.getString("email"));
                                item.setItemImage(itemObject.getString("item_image"));
                                item.setParentCategoryId(itemObject.getString("parentcategoryid"));
                                item.setSubCategoryId(itemObject.getString("subcategoryid"));
//                            item.setCreatedAt(itemObject.getString("created_at"));

                                itemModelList.add(item);
                            }

                            activity.runOnUiThread(() -> callback.onSuccess(itemModelList));
                        } catch (JSONException e) {
                            activity.runOnUiThread(() -> {
//                                Log.e(message, "JSON parsing error: " + e.getMessage());
                                callback.onFailure(e.getMessage());
                            });
                        }
                    } else {
                        activity.runOnUiThread(() -> {
//                            Log.e(message, "GET request failed:" + response);
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

    public static void filterBySubCategory(int parentCategory, int subcategory, int howMany, int pageNumber, String organizationID, Activity activity, OperationCallback<List<ItemModel>> callback) {
        String json = "{\"parentcategory\":\"" + parentCategory + "\", \"subcategory\":\"" + subcategory + "\", \"limit\":\""+Integer.toString(howMany)+"\", \"offset\":\""+Integer.toString(pageNumber)+"\", \"organizationid\":\"" + Integer.parseInt(organizationID) +"\" }";

        String message = "View Request Method";

        List<ItemModel> itemModelList = new ArrayList<>();
        MediaType mediaType = MediaType.get(type);
        OkHttpClient client = new OkHttpClient();
        String apiUrl = BuildConfig.SubCategoryFilterEndPoint;
        RequestBody body = RequestBody.create(json, mediaType);
        TokenManager.getToken().thenAccept(results->{
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
//                        Log.e(message, "GET request failed", e);
                        callback.onFailure(e.getMessage());
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String responseData = response.body().string();
//                        activity.runOnUiThread(() -> Log.e(message, responseData));

                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            String bodyString = jsonObject.getString("body");
                            String numPagesString = jsonObject.getString("totalpages");
                            JSONArray bodyArray = new JSONArray(bodyString);
//                            activity.runOnUiThread(() -> Log.e("View Response Results Body Array", bodyArray.toString()));

                            for (int i = 0; i < bodyArray.length(); i++) {
                                JSONObject itemObject = bodyArray.getJSONObject(i);

                                ItemModel item = new ItemModel();
                                item.setItemId(itemObject.getString("item_id"));
                                item.setItemName(itemObject.getString("item_name"));
                                item.setDescription(itemObject.getString("description"));
                                item.setColourCoding(itemObject.getString("colourcoding"));
                                item.setBarcode(itemObject.getString("barcode"));
                                item.setQrcode(itemObject.getString("qrcode"));
                                item.setQuantity(itemObject.getString("quanity"));
                                item.setLocation(itemObject.getString("location"));
                                item.setEmail(itemObject.getString("email"));
                                item.setItemImage(itemObject.getString("item_image"));
                                item.setParentCategoryId(itemObject.getString("parentcategoryid"));
                                item.setSubCategoryId(itemObject.getString("subcategoryid"));
//                            item.setCreatedAt(itemObject.getString("created_at"));

                                itemModelList.add(item);
                            }

                            activity.runOnUiThread(() -> callback.onSuccess(itemModelList));
                        } catch (JSONException e) {
                            activity.runOnUiThread(() -> {
//                                Log.e(message, "JSON parsing error: " + e.getMessage());
                                callback.onFailure(e.getMessage());
                            });
                        }
                    } else {
                        activity.runOnUiThread(() -> {
//                            Log.e(message, "GET request failed:" + response);
                            callback.onFailure("Response code:" + response.code());
                        });
                    }
                }
            });
        }).exceptionally(ex -> {
//            Log.e("TokenError", "Failed to get user token", ex);
            return null;
        });
    }

    public static void deleteCategory(int id, String email, String username, Activity activity, OperationCallback<Boolean> callback)
    {
        String json = "{\"useremail\":\""+email+"\", \"id\":\""+Integer.toString(id)+"\", \"username\" :\""+username+"\"}";

        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String apiUrl = BuildConfig.DeleteCategoryEndPoint;
        RequestBody body = RequestBody.create(json, mediaType);
        TokenManager.getToken().thenAccept(results->{
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

    public static void modifyCategoryName(int id, String newCategoryName, String username, String organizationid, Activity activity, OperationCallback<Boolean> callback)
    {
        String json = "{\"id\":\""+Integer.toString(id)+"\", \"categoryname\":\""+newCategoryName+"\", \"username\":\""+username+"\", \"organizationid\":\""+organizationid+"\"}";

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.ModifyCategoryName;
        RequestBody body = RequestBody.create(json, JSON);
        TokenManager.getToken().thenAccept(results->{
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

    public static void categoryToUncategorized(int id, Activity activity, OperationCallback<Boolean> callback)
    {
        String json = "{\"parentcategoryid\":\""+Integer.toString(id)+"\" }";

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.CategoryToUncategorized;
        RequestBody body = RequestBody.create(json, JSON);
        TokenManager.getToken().thenAccept(results->{
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
//                        Log.e(message, "POST request failed", e);
                        callback.onFailure(e.getMessage());
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String responseData = response.body().string();
                        activity.runOnUiThread(() -> {
//                            Log.i(message, "POST request succeeded: " + responseData);
                            callback.onSuccess(true);
                        });
                    } else {
                        activity.runOnUiThread(() -> {
//                            Log.e(message, "POST request failed: " + response.code());
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

    public static void fetchAllItems(int howMany, int pageNumber,String organizationID, Activity activity, OperationCallback<List<ItemModel>> callback)
    {
        String json = "{\"limit\":\""+Integer.toString(howMany)+"\", \"offset\":\""+Integer.toString(pageNumber)+"\", \"organizationid\":\"" + Integer.parseInt(organizationID)+"\"}";

        List<ItemModel> itemModelList = new ArrayList<>();

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.FetchAllEndPoint;
        RequestBody body = RequestBody.create(json, JSON);
        TokenManager.getToken().thenAccept(results->{
            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", results)
                    .post(body)
                    .build();

            activity.runOnUiThread(() -> {
                Log.i(message, "Authorization Token"+ results);

            });

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(() -> {
//                        Log.e(message, "GET request failed", e);
                        callback.onFailure(e.getMessage());
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String responseData = response.body().string();
                        activity.runOnUiThread(() -> Log.e("View Response Results Body Array", responseData));

                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            String bodyString = jsonObject.getString("body");
                            JSONArray bodyArray = new JSONArray(bodyString);
                            activity.runOnUiThread(() -> Log.e("View Response Results Body Array", bodyArray.toString()));

                            for (int i = 0; i < bodyArray.length(); i++) {
                                JSONObject itemObject = bodyArray.getJSONObject(i);

                                ItemModel item = new ItemModel();
                                item.setItemId(itemObject.getString("item_id"));
                                item.setItemName(itemObject.getString("item_name"));
                                item.setDescription(itemObject.getString("description"));
                                item.setColourCoding(itemObject.getString("colourcoding"));
                                item.setBarcode(itemObject.getString("barcode"));
                                item.setQrcode(itemObject.getString("qrcode"));
                                item.setQuantity(itemObject.getString("quanity"));
                                item.setLocation(itemObject.getString("location"));
                                item.setEmail(itemObject.getString("email"));
                                item.setItemImage(itemObject.getString("item_image"));
                                item.setParentCategoryId(itemObject.getString("parentcategoryid"));
                                item.setSubCategoryId(itemObject.getString("subcategoryid"));
                                item.setCreatedAt(itemObject.getString("created_at"));
                                item.setExpiryDate(itemObject.getString("expiry_date"));

                                itemModelList.add(item);
                            }

                            activity.runOnUiThread(() -> callback.onSuccess(itemModelList));
                        } catch (JSONException e) {
                            activity.runOnUiThread(() -> {
//                                Log.e(message, "JSON parsing error: " + e.getMessage());
                                callback.onFailure(e.getMessage());
                            });
                        }
                    } else {
                        activity.runOnUiThread(() -> {
//                            Log.e(message, "GET request failed:" + response);
                            callback.onFailure("Response code:" + response.code());
                        });
                    }
                }
            });
        }).exceptionally(ex -> {
//            Log.e("TokenError", "Failed to get user token", ex);
            return null;
        });
    }

    public static void addColourGroup(String colourcode, String title, String description, String email, String organizationId, Activity activity, OperationCallback<Boolean> callback)
    {
        String json = "{\"colourcode\":\""+colourcode+"\", \"description\":\""+description+"\", \"title\":\""+title+"\", \"createremail\":\""+email+"\", \"organizationid\":\""+Integer.parseInt(organizationId)+"\", \"username\":\""+email+"\"}";


        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.AddColourEndPoint;
        RequestBody body = RequestBody.create(json, JSON);
        TokenManager.getToken().thenAccept(results->{
            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .addHeader("Authorization", results)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(() -> {
                        Log.e("Color Coding", "POST request failed", e);
                        callback.onFailure(e.getMessage());
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String responseData = response.body().string();
                        activity.runOnUiThread(() -> {
                            Log.i("Color Coding", "POST request succeeded: " + responseData);
                            callback.onSuccess(true);
                        });
                    } else {
                        activity.runOnUiThread(() -> {
                            Log.e("Color Coding", "POST request failed: " + response.code());
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

    public static void fetchAllColour(String organizationId, Activity activity, OperationCallback<List<ColorCodeModel>> callback)
    {
//        String json = "{}";
        String json = "{\"organizationid\":\""+organizationId+"\" }";

        List<ColorCodeModel> colorCodeModelList = new ArrayList<>();

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.FetchAllColours;
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
                        String bodyString = jsonObject.getString("body");
                        JSONArray bodyArray = new JSONArray(bodyString);
//                        activity.runOnUiThread(() -> Log.e("View Response Results Body Array", bodyArray.toString()));

                        for (int i = 0; i < bodyArray.length(); i++) {
                            JSONObject itemObject = bodyArray.getJSONObject(i);

                            ColorCodeModel colorCode = new ColorCodeModel();
                            colorCode.setColor(itemObject.getString("colourcode"));
                            colorCode.setName(itemObject.getString("title"));
                            colorCode.setDescription(itemObject.getString("description"));
                            colorCode.setId(itemObject.getString("id"));
                            colorCode.setQrCode(itemObject.getString("qrcode"));

                            colorCodeModelList.add(colorCode);
                        }

                        activity.runOnUiThread(() -> callback.onSuccess(colorCodeModelList));
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

    public static void deleteColour(int colourId, String organizationId, String username, Activity activity, OperationCallback<Boolean> callback) {
        String json = "{\"id\":\"" + Integer.toString(colourId) + "\", \"organizationid\":\""+Integer.parseInt(organizationId)+"\", \"username\":\""+username+"\"}";

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.DeleteColour;
        RequestBody body = RequestBody.create(json, JSON);
        TokenManager.getToken().thenAccept(results->{
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
//                        Log.e(message, "POST request failed", e);
                        callback.onFailure(e.getMessage());
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String responseData = response.body().string();
                        activity.runOnUiThread(() -> {
//                            Log.i(message, "POST request succeeded: " + responseData);
                            callback.onSuccess(true);
                        });
                    } else {
                        activity.runOnUiThread(() -> {
//                            Log.e(message, "POST request failed: " + response.code());
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

    public static void fetchByColour(int colourId, String organizationId, String username, Activity activity, OperationCallback<List<ItemModel>> callback) {
        String json = "{\"colourid\":\"" + Integer.toString(colourId) + "\", \"organizationid\":\"" + Integer.parseInt(organizationId)+"\", \"username\":\"" + username + "\"}";
        List<ItemModel> itemModelList = new ArrayList<>();


        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.FetchByColourEndPoint;
        RequestBody body = RequestBody.create(json, JSON);
        TokenManager.getToken().thenAccept(results->{
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
//                        Log.e(message, "GET request failed", e);
                        callback.onFailure(e.getMessage());
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String responseData = response.body().string();
                        activity.runOnUiThread(() -> Log.e(message, responseData));

                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            String bodyString = jsonObject.getString("body");
                            if(new JSONArray(bodyString) != null){
                                JSONArray bodyArray = new JSONArray(bodyString);
                                activity.runOnUiThread(() -> Log.e(message, responseData));

                                for (int i = 0; i < bodyArray.length(); i++) {
                                    JSONObject itemObject = bodyArray.getJSONObject(i);

                                    ItemModel item = new ItemModel();
                                    item.setItemId(itemObject.getString("item_id"));
                                    item.setItemName(itemObject.getString("item_name"));
                                    item.setDescription(itemObject.getString("description"));
                                    item.setColourCoding(itemObject.getString("colourcoding"));
                                    item.setBarcode(itemObject.getString("barcode"));
                                    item.setQrcode(itemObject.getString("qrcode"));
                                    item.setQuantity(itemObject.getString("quanity"));
                                    item.setLocation(itemObject.getString("location"));
                                    item.setEmail(itemObject.getString("email"));
                                    item.setItemImage(itemObject.getString("item_image"));
                                    item.setParentCategoryId(itemObject.getString("parentcategoryid"));
                                    item.setSubCategoryId(itemObject.getString("subcategoryid"));
//                            item.setCreatedAt(itemObject.getString("created_at"));

                                    itemModelList.add(item);
                                }
                            }


                            activity.runOnUiThread(() -> callback.onSuccess(itemModelList));
                        } catch (JSONException e) {
                            activity.runOnUiThread(() -> {
                                Log.e(message, "JSON parsing error: " + e.getMessage());
                                callback.onFailure(e.getMessage());
                            });
                        }
                    } else {
                        activity.runOnUiThread(() -> {
                            Log.e(message, "GET request failed:" + response);
                            callback.onFailure("Response code:" + response.code());
                        });
                    }
                }
            });
        }).exceptionally(ex -> {
//            Log.e("TokenError", "Failed to get user token", ex);
            return null;
        });
    }

    public static void fetchRecentItems(String email, String organizationID, Activity activity, OperationCallback<List<ItemModel>> callback) {
//        String json = "{\"email\":\"" + email + "\" }";
        String json = "{\"email\":\""+ email +"\", \"organizationid\":\""+Integer.parseInt(organizationID)+"\" }";


        List<ItemModel> itemModelList = new ArrayList<>();

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.FetchByEmailEndPoint;
        RequestBody body = RequestBody.create(json, JSON);
        TokenManager.getToken().thenAccept(results->{
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
//                        Log.e(message, "GET request failed", e);
                        callback.onFailure(e.getMessage());
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String responseData = response.body().string();
//                        activity.runOnUiThread(() -> Log.e(message, responseData));

                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            String bodyString = jsonObject.getString("body");
                            JSONArray bodyArray = new JSONArray(bodyString);
//                            activity.runOnUiThread(() -> Log.e("View Response Results Body Array", bodyArray.toString()));

                            for (int i = 0; i < bodyArray.length(); i++) {
                                JSONObject itemObject = bodyArray.getJSONObject(i);

                                ItemModel item = new ItemModel();
                                item.setItemId(itemObject.getString("item_id"));
                                item.setItemName(itemObject.getString("item_name"));
                                item.setDescription(itemObject.getString("description"));
                                item.setColourCoding(itemObject.getString("colourcoding"));
                                item.setBarcode(itemObject.getString("barcode"));
                                item.setQrcode(itemObject.getString("qrcode"));
                                item.setQuantity(itemObject.getString("quanity"));
                                item.setLocation(itemObject.getString("location"));
                                item.setEmail(itemObject.getString("email"));
                                item.setItemImage(itemObject.getString("item_image"));
                                item.setParentCategoryId(itemObject.getString("parentcategoryid"));
                                item.setSubCategoryId(itemObject.getString("subcategoryid"));
//                            item.setCreatedAt(itemObject.getString("created_at"));

                                itemModelList.add(item);
                            }

                            activity.runOnUiThread(() -> callback.onSuccess(itemModelList));
                        } catch (JSONException e) {
                            activity.runOnUiThread(() -> {
//                                Log.e(message, "JSON parsing error: " + e.getMessage());
                                callback.onFailure(e.getMessage());
                            });
                        }
                    } else {
                        activity.runOnUiThread(() -> {
//                            Log.e(message, "GET request failed:" + response);
                            callback.onFailure("Response code:" + response.code());
                        });
                    }
                }
            });
        }).exceptionally(ex -> {
//            Log.e("TokenError", "Failed to get user token", ex);
            return null;
        });
    }

    public static void fetchCategorySuggestions(String name, String description, String email,String organizationId, Activity activity, OperationCallback<List<CategoryModel>> callback) {
        // API endpoint that can return category suggestions based on the item
        String API_URL = BuildConfig.RecommendCategoryEndPoint;
        OkHttpClient client = new OkHttpClient();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("itemname", name);
            jsonObject.put("itemdescription", description);
            jsonObject.put("useremail", email);
            jsonObject.put("organizationid", Integer.parseInt(organizationId));
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.get("application/json; charset=utf-8"));
        TokenManager.getToken().thenAccept(results-> {
            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", results)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) //in case of network error i.e if HTTP req fails
                {
                    e.printStackTrace();

                    activity.runOnUiThread(() ->
                            Toast.makeText(activity, "Failed-03 to fetch category suggestions", Toast.LENGTH_SHORT).show()
                    );
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    // if HTTP req is successful
                    if (response.isSuccessful()) {
                        // response body converted to string
                        final String responseData = response.body().string();
//                        Log.i("1Response Data", responseData);
                        activity.runOnUiThread(() ->
                        {
                            try {
                                if (responseData.isEmpty()) {
                                    throw new JSONException("Empty or null response data");
                                }

                                // Parse the response as a JSON object
                                JSONObject jsonObject = new JSONObject(responseData);

                                JSONObject categoryObject = jsonObject.getJSONObject("category");
                                CategoryModel parentCategory = new CategoryModel();
                                parentCategory.setCategoryID(categoryObject.getString("id"));
                                parentCategory.setCategoryName(categoryObject.getString("categoryname"));

                                // Extract the subcategory name
                                JSONObject subcategoryObject = jsonObject.getJSONObject("subcategory");
                                CategoryModel subCategory = new CategoryModel();
                                subCategory.setCategoryID(subcategoryObject.getString("id"));
                                subCategory.setCategoryName(subcategoryObject.getString("categoryname"));

                                List<CategoryModel> categoryModelList = new ArrayList<>();
                                categoryModelList.add(parentCategory);
                                categoryModelList.add(subCategory);

                                activity.runOnUiThread(() -> callback.onSuccess(categoryModelList));

                            } catch (JSONException e) {
                                activity.runOnUiThread(() -> {
//                                    Log.e(message, "JSON parsing error: " + e.getMessage());
                                    callback.onFailure(e.getMessage());
                                });
                            }
                        });
                    } else {
                        activity.runOnUiThread(() ->
                                Toast.makeText(activity, "Failed-02 to fetch category suggestions", Toast.LENGTH_SHORT).show()
                        );
                    }
                }
            });
        }).exceptionally(ex -> {
//            Log.e("TokenError", "Failed to get user token", ex);
            return null;
        });
    }

    public static void postAddItem(String username, String item_image, String item_name, String description, int category, int parentCategory, String userEmail,String location, String organizationId, String width, String height, String depth, String weight, String loadbear, String updown, Activity activity, OperationCallback<String> callback) {
        // Provide default values for the remaining attributes
        int subCategory = 0;
        String colourcoding = "default";
        String barcode = "default";
        String qrcode = "default";
        int quantity = 1;

        String email = userEmail;
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.AddItemSynchronous;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("item_image", item_image);
            jsonObject.put("item_name", item_name);
            jsonObject.put("description", description);
            jsonObject.put("category", parentCategory);
            jsonObject.put("sub_category", category);
            jsonObject.put("colourcoding", colourcoding);
            jsonObject.put("barcode", barcode);
            jsonObject.put("qrcode", qrcode);
            jsonObject.put("quanity", quantity);
            jsonObject.put("location", location);
            jsonObject.put("email", email);
            jsonObject.put("username", username);
            jsonObject.put("width", width);
            jsonObject.put("height", height);
            jsonObject.put("depth", depth);
            jsonObject.put("weight", weight);
            jsonObject.put("loadbear", loadbear);
            jsonObject.put("updown", updown);
            jsonObject.put("organizationid", Integer.parseInt(organizationId));
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        TokenManager.getToken().thenAccept(results-> {
            RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.get("application/json; charset=utf-8"));

            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .addHeader("Authorization", results)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(() -> {
                        Log.e(message, "POST request failed Add", e);
                        callback.onFailure(e.getMessage());
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String responseData = response.body().string();
                        activity.runOnUiThread(() -> {Log.i(message, "POST request succeeded: " + responseData);});
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(responseData);
                            String bodyString = jsonObject.getString("body");

                            JSONObject itemIdObject = new JSONObject(bodyString);
                            String insertedItemId = itemIdObject.getString("inserted_item_id");

                            activity.runOnUiThread(() -> {
                                Log.e(message, "JSON parsing error: " + insertedItemId);
                                callback.onSuccess(insertedItemId);
                            });

                        } catch (JSONException e) {
                            activity.runOnUiThread(() -> {
                                Log.e(message, "JSON parsing error: " + e.getMessage());
                                callback.onFailure(e.getMessage());
                            });
                        }
                    } else {
                        activity.runOnUiThread(() -> {
                            Log.e(message, "POST request failed: Add" + response.code());
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

    public static void fetchByID(int id, String organizationId, String username, Activity activity, OperationCallback<List<ItemModel>> callback)
    {
        String json = "{\"item_id\":\""+Integer.toString(id)+"\", \"organizationid\":\""+Integer.parseInt(organizationId)+"\", \"username\" : \""+username+"\"}";

        List<ItemModel> itemModelList = new ArrayList<>();

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.FetchByIDEndPoint;
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
                        Log.e("QR Code", "GET request failed", e);
                        callback.onFailure(e.getMessage());
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String responseData = response.body().string();
                        activity.runOnUiThread(() -> Log.e(message, responseData));

                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            String bodyString = jsonObject.getString("body");
//                            JSONArray bodyArray = new JSONArray(bodyString);
                            JSONObject results = new JSONObject(bodyString);
                            activity.runOnUiThread(() -> Log.e("QR Code", results.toString()));

//                            for (int i = 0; i < bodyArray.length(); i++) {
//                                JSONObject itemObject = bodyString.getJSONObject(i);

                                ItemModel item = new ItemModel();
                                item.setItemId(results.getString("item_id"));
                                item.setItemName(results.getString("item_name"));
                                item.setDescription(results.getString("description"));
                                item.setColourCoding(results.getString("colourcoding"));
                                item.setBarcode(results.getString("barcode"));
                                item.setQrcode(results.getString("qrcode"));
                                item.setQuantity(results.getString("quanity"));
                                item.setLocation(results.getString("location"));
                                item.setEmail(results.getString("email"));
                                item.setItemImage(results.getString("item_image"));
                                item.setParentCategoryId(results.getString("parentcategoryid"));
                                item.setSubCategoryId(results.getString("subcategoryid"));
//                            item.setCreatedAt(itemObject.getString("created_at"));

                                itemModelList.add(item);
//                            }

                            activity.runOnUiThread(() -> callback.onSuccess(itemModelList));
                        } catch (JSONException e) {
                            activity.runOnUiThread(() -> {
                                Log.e("QR Code", "JSON parsing error: " + e.getMessage());
                                callback.onFailure(e.getMessage());
                            });
                        }
                    } else {
                        activity.runOnUiThread(() -> {
//                            Log.e(message, "GET request failed:" + response);
                            callback.onFailure("Response code:" + response.code());
                        });
                    }
                }
            });
        }).exceptionally(ex -> {
//            Log.e("TokenError", "Failed to get user token", ex);
            return null;
        });
    }

    public static void FetchUncategorizedItems(int howMany, int pageNumber, String organizationID, String username, Activity activity, OperationCallback<List<ItemModel>> callback)
    {
        String json = "{\"limit\":\""+Integer.toString(howMany)+"\", \"offset\":\""+Integer.toString(pageNumber)+"\", \"organizationid\":\"" + Integer.parseInt(organizationID) + "\", \"username\":\"" + username + "\" }";

        List<ItemModel> itemModelList = new ArrayList<>();

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.FetchUncategorizedEndPoint;
        RequestBody body = RequestBody.create(json, JSON);

        TokenManager.getToken().thenAccept(results-> {
            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .addHeader("Authorization", results)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(() -> {
                        Log.e(message, "GET request failed", e);
                        callback.onFailure(e.getMessage());
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String responseData = response.body().string();
                        activity.runOnUiThread(() -> Log.e(message, responseData));

                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            String bodyString = jsonObject.getString("body");
                            JSONArray bodyArray = new JSONArray(bodyString);
                            activity.runOnUiThread(() -> Log.e("View Response Results Body Array", bodyArray.toString()));

                            for (int i = 0; i < bodyArray.length(); i++) {
                                JSONObject itemObject = bodyArray.getJSONObject(i);

                                ItemModel item = new ItemModel();
                                item.setItemId(itemObject.getString("item_id"));
                                item.setItemName(itemObject.getString("item_name"));
                                item.setDescription(itemObject.getString("description"));
                                item.setColourCoding(itemObject.getString("colourcoding"));
                                item.setBarcode(itemObject.getString("barcode"));
                                item.setQrcode(itemObject.getString("qrcode"));
                                item.setQuantity(itemObject.getString("quanity"));
                                item.setLocation(itemObject.getString("location"));
                                item.setEmail(itemObject.getString("email"));
                                item.setItemImage(itemObject.getString("item_image"));
                                item.setParentCategoryId(itemObject.getString("parentcategoryid"));
                                item.setSubCategoryId(itemObject.getString("subcategoryid"));
//                            item.setCreatedAt(itemObject.getString("created_at"));

                                itemModelList.add(item);
                            }

                            activity.runOnUiThread(() -> callback.onSuccess(itemModelList));
                        } catch (JSONException e) {
                            activity.runOnUiThread(() -> {
                                Log.e(message, "JSON parsing error: " + e.getMessage());
                                callback.onFailure(e.getMessage());
                            });
                        }
                    } else {
                        activity.runOnUiThread(() -> {
                            Log.e(message, "GET request failed:" + response);
                            callback.onFailure("Response code:" + response.code());
                        });
                    }
                }
            });
        }).exceptionally(ex -> {
//            Log.e("TokenError", "Failed to get user token", ex);
            return null;
        });
    }

    public static void RecommendMultipleCategories(String id, String organizationId, Activity activity, OperationCallback<List<SuggestedCategoryModel>> callback)
    {
        String json = "{\"id\":\""+id+"\", \"organizationid\":\""+organizationId+"\"}";

        List<SuggestedCategoryModel> categoryModelList = new ArrayList<>();

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.RecommendMultipleEndPoint;
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
//                        Log.e(message, "GET Suggested request failed", e);
                        callback.onFailure(e.getMessage());
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String responseData = response.body().string();
//                        activity.runOnUiThread(() -> Log.e(message, responseData));

                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            String bodyString = jsonObject.getString("response");
                            JSONArray bodyArray = new JSONArray(bodyString);
//                            activity.runOnUiThread(() -> Log.e("View Suggested Response Results Body Array", bodyArray.toString()));

                            for (int i = 0; i < bodyArray.length(); i++) {
                                JSONObject itemObject = bodyArray.getJSONObject(i);

                                String itemIdString = itemObject.getString("id");
                                String categoryString = itemObject.getString("category");
                                JSONObject categoryObject = new JSONObject(categoryString);
                                String subcategoryString = itemObject.getString("subcategory");
                                JSONObject subcategoryObject = new JSONObject(subcategoryString);

                                SuggestedCategoryModel suggestedCategory = new SuggestedCategoryModel();

                                suggestedCategory.setItemId(itemIdString);
                                suggestedCategory.setCategoryId(categoryObject.getString("id"));
                                suggestedCategory.setCategoryName(categoryObject.getString("categoryname"));
                                suggestedCategory.setSubcategoryId(subcategoryObject.getString("id"));
                                suggestedCategory.setSubcategoryName(subcategoryObject.getString("categoryname"));

//                                activity.runOnUiThread(() -> Log.e("View Suggested Response Results Body Array", categoryString.toString()));
//                                activity.runOnUiThread(() -> Log.e("View Suggested Response Results Body Array", subcategoryString.toString()));

                                categoryModelList.add(suggestedCategory);
                            }

                            activity.runOnUiThread(() -> callback.onSuccess(categoryModelList));
                        } catch (JSONException e) {
                            activity.runOnUiThread(() -> {
//                                Log.e(message, "JSON Suggested parsing error: " + e.getMessage());
                                callback.onFailure(e.getMessage());
                            });
                        }
                    } else {
                        activity.runOnUiThread(() -> {
//                            Log.e(message, "GET Suggested request failed:" + response);
                            callback.onFailure("Response code:" + response.code());
                        });
                    }
                }
            });
        }).exceptionally(ex -> {
//            Log.e("TokenError", "Failed to get user token", ex);
            return null;
        });
    }

    public static void deleteItem(String itemId, String organizationId,Activity activity, OperationCallback<Boolean> callback) {
        int itemIdInt = Integer.parseInt(itemId);
        String json = "{\"item_id\":\"" + itemIdInt + "\", \"organizationid\":\""+organizationId+"\"}";
//        Log.d("Delete Item Payload", "JSON Payload: " + json);
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        String API_URL = BuildConfig.DeleteItemEndPoint;
//        Log.d("Delete Item Endpoint", "API URL: " + BuildConfig.DeleteItemEndPoint);
        RequestBody body = RequestBody.create(json, JSON);
        TokenManager.getToken().thenAccept(result-> {
            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", result)
                    .post(body)
                    .build();

            OkHttpClient client = new OkHttpClient();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(() -> {
//                        Log.e(message, "POST request failed", e);
                        callback.onFailure(e.getMessage());
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String responseData = response.body().string();
                        activity.runOnUiThread(() -> {
//                            Log.i(message, "POST request succeeded: " + responseData);
                            callback.onSuccess(true);
                        });
                    } else {
                        activity.runOnUiThread(() -> {
//                            Log.e(message, "POST request failed: " + response.code());
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

    public static void getCategory(String categoryId, String authorizationToken, Activity activity, OperationCallback<String> callback) {
        String json = "{"
                + "\"header\": {"
                + "\"Authorization\": \"" + authorizationToken + "\""
                + "},"
                + "\"body\": {"
                + "\"categoryId\": \"" + categoryId + "\""
                + "}"
                + "}";

        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

        String API_URL = BuildConfig.getCategoryName;

        RequestBody body = RequestBody.create(json, JSON);

        TokenManager.getToken().thenAccept(result->{
                    Request request = new Request.Builder()
                            .url(API_URL)
                            .addHeader("Authorization", result)
                            .post(body)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                            activity.runOnUiThread(() -> {
//                                Log.d("MyAmplifyApp", "POST request failed", e);
                                callback.onFailure(e.getMessage());
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()) {
                                final String responseData = response.body().string();
//                                activity.runOnUiThread(() -> Log.e("MyAmplifyApp", responseData));

                                try {
                                    JSONObject jsonObject = new JSONObject(responseData);
                                    String bodyString = jsonObject.getString("body");

                                    JSONObject roleObject = new JSONObject(bodyString);
                                    String categoryString = roleObject.getString("categoryName");

                                    activity.runOnUiThread(() -> {
//                                        Log.e("MyAmplifyApp", "POST request succeeded: " + responseData);
//                                        Log.e("MyAmplifyApp", "POST request succeeded: " + bodyString);

                                        callback.onSuccess(categoryString);
                                    });
                                }catch (JSONException e){
                                    activity.runOnUiThread(() -> {
//                                        Log.e("MyAmplifyApp", "JSON parsing error: " + e.getMessage());
                                        callback.onFailure(e.getMessage());
                                    });
                                }
                            } else {
                                activity.runOnUiThread(() -> {
//                                    Log.d("MyAmplifyApp", "POST request failed: " + response.code());
                                    callback.onFailure("Response code" + response.code());
                                });
                            }
                        }
                    });

                }
        ).exceptionally(ex -> {
//            Log.e("TokenError", "Failed to get user token", ex);
            return null;
        });

    }

    public static void FetchAllUnits(String organizationId,  Activity activity, OperationCallback<List<unitModel>> callback)
    {
        String json = "{"
                + "\"body\": {"
                + "\"organization_id\": \"" + Integer.parseInt(organizationId) + "\""
                + "}"
                + "}";
        List<unitModel> unitModelList = new ArrayList<>();

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.GetAllUnits;
        RequestBody body = RequestBody.create(json, JSON);

        TokenManager.getToken().thenAccept(results-> {
                    Request request = new Request.Builder()
                            .url(API_URL)
                            .post(body)
                            .addHeader("Authorization", results)
                            .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(() -> {
//                        Log.e(message, "GET request failed", e);
                        callback.onFailure(e.getMessage());
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String responseData = response.body().string();
                        if(responseData == null){
                            activity.runOnUiThread(() -> callback.onSuccess(unitModelList));
                        }
                        else {
                            activity.runOnUiThread(() -> Log.e(message, responseData));

                            try {
                                JSONObject jsonObject = new JSONObject(responseData);
                                String bodyString = jsonObject.getString("body");

                                // Try to convert bodyString to a JSONArray
                                try {
                                    JSONArray bodyArray = new JSONArray(bodyString);
                                    // If bodyString is a valid JSONArray, proceed with parsing the items
                                    for (int i = 0; i < bodyArray.length(); i++) {
                                        JSONObject itemObject = bodyArray.getJSONObject(i);

                                        String unitName = itemObject.getString("name");
                                        String capacity = itemObject.getString("capacity");
                                        String unitId = itemObject.getString("id");
                                        String capacityUsed = itemObject.getString("capacity_used");
                                        String categories = itemObject.getString("category_name");

                                        // Clean up the categories string
                                        categories = categories.replaceAll("[\\[\\]\"]", "");

                                        // Split categories into a list
                                        List<String> list = new ArrayList<>(Arrays.asList(categories.split(", ")));

                                        // Create the unitModel object and add it to the list
                                        unitModel item = new unitModel(unitName, unitId, Integer.parseInt(capacity), Integer.parseInt(capacityUsed), categories);
                                        item.setCategories(list);

                                        unitModelList.add(item);
                                    }

                                    // If JSONArray parsing and processing is successful, return the result
                                    activity.runOnUiThread(() -> callback.onSuccess(unitModelList));

                                } catch (JSONException jsonArrayException) {
                                    // If bodyString is not a valid JSONArray, return the existing unitModelList
                                    activity.runOnUiThread(() -> callback.onSuccess(unitModelList));
                                }

                            } catch (JSONException e) {
                                // Handle any JSON parsing errors and call onFailure
                                activity.runOnUiThread(() -> {
                                    activity.runOnUiThread(() -> callback.onSuccess(unitModelList));
                                });
                            }
                        }
                } else {
                        activity.runOnUiThread(() -> {
//                            Log.e(message, "GET request failed:" + response);
                            callback.onFailure("Response code:" + response.code());
                        });
                    }
                }
            });

                }).exceptionally(ex -> {
//            Log.e("TokenError", "Failed to get user token", ex);
            return null;
        });
    }

    public static CompletableFuture<String> getUserToken(){
        CompletableFuture<String> future=new CompletableFuture<String>();
        Amplify.Auth.fetchAuthSession(
                result -> {
                    AWSCognitoAuthSession cognitoAuthSession=(AWSCognitoAuthSession) result;
                    if (result.isSignedIn()) {
                        String idToken=cognitoAuthSession.getUserPoolTokensResult().getValue().getIdToken();
//                        Log.i("Hello",idToken);
                        future.complete(idToken);
                        future.completeExceptionally(new Exception("User not signed in"));
                    }
                },
                error -> {
                    future.completeExceptionally(new Exception("Failed to fetch user session"));
                }
        );
        return future;
    }

    public static void fetchItemsUnderUnit(String unitName, String organizationId, Activity activity, OperationCallback<List<ItemModel>> callback)
    {
        String json = "{"
                + "\"body\": {"
                + "\"unit_name\": \"" + unitName + "\","
                + "\"organization_id\": \"" + Integer.parseInt(organizationId) + "\""
                + "}"
                + "}";


        List<ItemModel> itemModelList = new ArrayList<>();

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.GetItemsUnderUnit;
        RequestBody body = RequestBody.create(json, JSON);

        TokenManager.getToken().thenAccept(results-> {
            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .addHeader("Authorization", results)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(() -> {
                        Log.e(message, "GET request failed", e);
                        callback.onFailure(e.getMessage());
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String responseData = response.body().string();
                        activity.runOnUiThread(() -> Log.e(message, "responseData: "+responseData));

                        try {
                            if(responseData != null && !responseData.isEmpty()){
                                JSONObject jsonObject = new JSONObject(responseData);
                                String bodyString = jsonObject.getString("body");
                                if(bodyString.equals("{\"error\": \"No items found\"}")){
                                    activity.runOnUiThread(() -> callback.onSuccess(itemModelList));
                                }
                                else{
                                    JSONArray bodyArray = new JSONArray(bodyString);
                                    activity.runOnUiThread(() -> Log.e(message, "bodyArray"+bodyArray.toString()));

                                    for (int i = 0; i < bodyArray.length(); i++) {
                                        JSONObject itemObject = bodyArray.getJSONObject(i);

                                        ItemModel item = new ItemModel();
                                        item.setItemId(itemObject.getString("item_id"));
                                        item.setItemName(itemObject.getString("item_name"));
                                        item.setDescription(itemObject.getString("description"));
                                        item.setColourCoding(itemObject.getString("colour_coding"));
                                        item.setBarcode(itemObject.getString("barcode"));
                                        item.setQrcode(itemObject.getString("qrcode"));
                                        item.setQuantity(itemObject.getString("quanity"));
                                        item.setLocation(itemObject.getString("location"));
                                        item.setEmail(itemObject.getString("email"));
                                        item.setItemImage(itemObject.getString("item_image"));
                                        item.setParentCategoryId(itemObject.getString("parentcategoryid"));
                                        item.setSubCategoryId(itemObject.getString("subcategoryid"));
//                            item.setCreatedAt(itemObject.getString("created_at"));

                                        itemModelList.add(item);
                                    }

                                    activity.runOnUiThread(() -> callback.onSuccess(itemModelList));
                                }
                            }
                            else {
                                activity.runOnUiThread(() -> callback.onSuccess(itemModelList));
                            }
                        } catch (JSONException e) {
                            activity.runOnUiThread(() -> {
                                Log.e(message, "JSON parsing error: " + e.getMessage());
                                callback.onFailure(e.getMessage());
                            });
                        }
                    } else {
                        activity.runOnUiThread(() -> {
                            Log.e(message, "GET request failed:" + response);
                            callback.onFailure("Response code:" + response.code());
                        });
                    }
                }
            });
        }).exceptionally(ex -> {
//            Log.e("TokenError", "Failed to get user token", ex);
            return null;
        });
    }

    public static void incrementQuantity(String item_id, int quantity, String username, Activity activity, OperationCallback<Boolean> callback) {
        String json = "{"
                + "\"body\": {"
                + "\"item_id\": \"" + Integer.parseInt(item_id) + "\","
                + "\"quantity\": \"" + quantity + "\","
                + "\"username\": \"" + username + "\""
                + "}"
                + "}";

        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

        String API_URL = BuildConfig.increaseQuantity;

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
                        Log.d("MyAmplifyApp Group", "POST request failed", e);
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

    public static void fetchCategoryStats(String organizationid, Activity activity, OperationCallback<List<CategoryReportModel>> callback)
    {
        String json = "{"
                + "\"body\": {"
                + "\"organizationid\": \"" + Integer.parseInt(organizationid) + "\""
                + "}"
                + "}";


        List<CategoryReportModel> categoriesModelList = new ArrayList<>();

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.reports_getTotalItemsUnderAllParentCategories;
        RequestBody body = RequestBody.create(json, JSON);

        TokenManager.getToken().thenAccept(results-> {
            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .addHeader("Authorization", results)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(() -> {
                        Log.e("View Response Results Body Array", "GET request failed", e);
                        callback.onFailure(e.getMessage());
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String responseData = response.body().string();
                        activity.runOnUiThread(() -> Log.e("View Response Results Body Array", responseData));

                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            String bodyString = jsonObject.getString("body");
                            JSONArray bodyArray = new JSONArray(bodyString);
                            activity.runOnUiThread(() -> Log.e("View Response Results Body Array", bodyString));

                            for (int i = 0; i < bodyArray.length(); i++) {
                                JSONObject categoryObject = bodyArray.getJSONObject(i);

                                CategoryReportModel category = new CategoryReportModel();
                                category.setParentCategory(categoryObject.getString("Category"));
                                category.setTotalNumberOfItems(Double.parseDouble(categoryObject.getString("totalAMount")));
                                if(categoryObject.getString("Subcategories").equals("No subcategories")){
                                    List<SubcategoryReportModel> subcategory = new ArrayList<>();
                                    category.setSubCategories(subcategory);
                                }
                                else {
                                    String subcategories = categoryObject.getString("Subcategories");
                                    JSONObject subcategoryObject = new JSONObject(subcategories);

                                    List<SubcategoryReportModel> modelList = new ArrayList<>();

                                    Iterator<String> keys = subcategoryObject.keys();
                                    while (keys.hasNext()) {
                                        String key = keys.next();

                                        JSONObject subcategoryDetails = subcategoryObject.getJSONObject(key);

                                        int totalItems = subcategoryDetails.getInt("total_items");
                                        int totalQuantity = subcategoryDetails.getInt("total_quantity");

                                        SubcategoryReportModel item = new SubcategoryReportModel();
                                        item.setSubcategory(key);
                                        item.setTotalNumberOfItems(totalItems);
                                        item.setTotalQuantity(totalQuantity);

                                        modelList.add(item);
                                    }
                                    category.setSubCategories(modelList);
                                }
                                categoriesModelList.add(category);
                            }

                            activity.runOnUiThread(() -> callback.onSuccess(categoriesModelList));
                        } catch (JSONException e) {
                            activity.runOnUiThread(() -> {
                                Log.e(message, "View Response Results Body Array" + e.getMessage());
                                callback.onFailure(e.getMessage());
                            });
                        }
                    } else {
                        activity.runOnUiThread(() -> {
//                            Log.e(message, "GET request failed:" + response);
                            callback.onFailure("Response code:" + response.code());
                        });
                    }
                }
            });
        }).exceptionally(ex -> {
//            Log.e("TokenError", "Failed to get user token", ex);
            return null;
        });
    }

    public static void fetchAllCategories(String organizationid, Activity activity, OperationCallback<List<CategoryModel>> callback)
    {
        String json = "{"
                + "\"body\": {"
                + "\"organizationid\": \"" + Integer.parseInt(organizationid) + "\""
                + "}"
                + "}";


        List<CategoryModel> categoriesModelList = new ArrayList<>();

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.GetAllCategories;
        RequestBody body = RequestBody.create(json, JSON);

        TokenManager.getToken().thenAccept(results-> {
            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .addHeader("Authorization", results)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(() -> {
                        Log.e("View Response Results Body Array", "GET request failed", e);
                        callback.onFailure(e.getMessage());
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String responseData = response.body().string();
                        activity.runOnUiThread(() -> Log.e("View Response Results Body Array", responseData));

                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            String bodyString = jsonObject.getString("body");
                            JSONArray bodyArray = new JSONArray(bodyString);
                            activity.runOnUiThread(() -> Log.e("View Response Results Body Array", bodyString));

                            for (int i = 0; i < bodyArray.length(); i++) {
                                JSONObject categoryObject = bodyArray.getJSONObject(i);

                                CategoryModel category = new CategoryModel();
                                category.setCategoryID(categoryObject.getString("id"));
                                category.setCategoryName(categoryObject.getString("categoryname"));
                                category.setParentCategoryId(categoryObject.getString("parentcategory"));

                                categoriesModelList.add(category);
                            }

                            activity.runOnUiThread(() -> callback.onSuccess(categoriesModelList));
                        } catch (JSONException e) {
                            activity.runOnUiThread(() -> {
                                Log.e(message, "View Response Results Body Array" + e.getMessage());
                                callback.onFailure(e.getMessage());
                            });
                        }
                    } else {
                        activity.runOnUiThread(() -> {
                            Log.e(message, "View Response Results Body Array" + response);
                            callback.onFailure("Response code:" + response.code());
                        });
                    }
                }
            });
        }).exceptionally(ex -> {
//            Log.e("TokenError", "Failed to get user token", ex);
            return null;
        });
    }

    public static void getLoginReports(String organizationid, String username, Activity activity, OperationCallback<List<LoginReportsModel>> callback)
    {
        String json = "{"
                + "\"body\": {"
                + "\"organization_id\": \"" + Integer.parseInt(organizationid) + "\","
                + "\"username\": \"" + username + "\""
                + "}"
                + "}";


        List<LoginReportsModel> reportsModelList = new ArrayList<>();

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.GetLoginReports;
        RequestBody body = RequestBody.create(json, JSON);

        TokenManager.getToken().thenAccept(results-> {
            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .addHeader("Authorization", results)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(() -> {
                        Log.e("View Response Results Body Array", "GET request failed", e);
                        callback.onFailure(e.getMessage());
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String responseData = response.body().string();
                        activity.runOnUiThread(() -> Log.e("View Response Results Body Array", responseData));

                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            String bodyString = jsonObject.getString("body");
                            JSONArray bodyArray = new JSONArray(bodyString);
                            activity.runOnUiThread(() -> Log.e("View Response Results Body Array", bodyString));

                            for (int i = 0; i < bodyArray.length(); i++) {
                                JSONObject reportObject = bodyArray.getJSONObject(i);

                                LoginReportsModel loginReport = new LoginReportsModel();
                                loginReport.setEmail(reportObject.getString("email"));
                                loginReport.setName(reportObject.getString("name"));
                                loginReport.setSurname(reportObject.getString("surname"));
                                loginReport.setTimeIn(reportObject.getString("time_in"));
                                loginReport.setTimeOut(reportObject.getString("time_out"));

                                reportsModelList.add(loginReport);
                            }

                            activity.runOnUiThread(() -> callback.onSuccess(reportsModelList));
                        } catch (JSONException e) {
                            activity.runOnUiThread(() -> {
                                Log.e(message, "View Response Results Body Array" + e.getMessage());
                                callback.onFailure(e.getMessage());
                            });
                        }
                    } else {
                        activity.runOnUiThread(() -> {
                            Log.e(message, "View Response Results Body Array" + response);
                            callback.onFailure("Response code:" + response.code());
                        });
                    }
                }
            });
        }).exceptionally(ex -> {
//            Log.e("TokenError", "Failed to get user token", ex);
            return null;
        });
    }

    public static void generateProcess(String unitId, String unitName, Activity activity, OperationCallback<ArrangementModel> callback)
    {
        String json = "{\"unit_id\":\""+Integer.parseInt(unitId)+"\", \"unit_name\":\"" + unitName + "\" }";

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.GenerateProcess;
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
                    Log.e("View Response Results Body Array", "GET request failed", e);
                    callback.onFailure(e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    activity.runOnUiThread(() -> Log.e("View Response Results Body Array", responseData));
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
//                        jsonObject.getString("statusCode");
                        if(jsonObject.toString().contains("\""+"statusCode"+"\"") && jsonObject.getString("statusCode").equals("200")){
                            activity.runOnUiThread(() -> Log.e("Arrangement", responseData));
                            String bodyString = jsonObject.getString("response");
                            activity.runOnUiThread(() -> Log.e("View Response Results Body Array", bodyString));
                            JSONObject jsonObject1 = new JSONObject(bodyString);
                            String imageUrl = jsonObject1.getString("path");
                            String items = jsonObject1.getString("items");

                            JSONArray itemsArray = new JSONArray(items);
                            List<BinItemModel> itemsList = new ArrayList<>();
                            List<BinItemModel> unfittedItemsList = new ArrayList<>();

                            for (int i = 0; i < itemsArray.length(); i++) {
                                JSONObject itemObject = itemsArray.getJSONObject(i);

                                String name = itemObject.getString("name");
                                String color = itemObject.getString("color");

                                BinItemModel item = new BinItemModel(name, color);
                                itemsList.add(item);
                            }

                            if(jsonObject.toString().contains("\""+"binRespibse"+"\"")){
                                String binRespibse = jsonObject.getString("binRespibse");
                                JSONObject jsonObjectbinRespibse = new JSONObject(binRespibse);
                                String body = jsonObjectbinRespibse.getString("body");

                                JSONArray bodyArray = new JSONArray(body);

                                JSONObject itemObject = bodyArray.getJSONObject(0);

                                String unfittedItem = itemObject.getString("unfitted_items");

                                JSONArray unfittedItemArray = new JSONArray(unfittedItem);

                                activity.runOnUiThread(() -> Log.e("View Response Results Body Array", unfittedItem.toString()));

                                for (int i = 0; i < unfittedItemArray.length(); i++) {
                                    JSONObject unfittedItemObject = unfittedItemArray.getJSONObject(i);

                                    String name = unfittedItemObject.getString("id");
                                    String color = unfittedItemObject.getString("width");

                                    BinItemModel item = new BinItemModel(name, color);
                                    unfittedItemsList.add(item);
                                }
                            }

                            ArrangementModel obj = new ArrangementModel(imageUrl, itemsList, unfittedItemsList);

                            activity.runOnUiThread(() -> callback.onSuccess(obj));
                        } // item with no dimension
                        else if(jsonObject.toString().contains("\""+"status"+"\"") && jsonObject.getString("status").equals("400")){
                            List<BinItemModel> itemList = new ArrayList<>();
                            List<BinItemModel> unfittedItemsList = new ArrayList<>();
                            ArrangementModel obj = new ArrangementModel("400", itemList, unfittedItemsList);
                            activity.runOnUiThread(() -> callback.onSuccess(obj));
                        }
                        else if(jsonObject.toString().contains("\""+"statusCode"+"\"") && jsonObject.getString("statusCode").equals("500")){
                            List<BinItemModel> itemList = new ArrayList<>();
                            List<BinItemModel> unfittedItemsList = new ArrayList<>();
                            ArrangementModel obj = new ArrangementModel("500", itemList, unfittedItemsList);
                            activity.runOnUiThread(() -> callback.onSuccess(obj));
                        }
                    } catch (JSONException e) {
                        activity.runOnUiThread(() -> {
                            Log.e("Arrangement", "" + e.getMessage());
                            callback.onFailure(e.getMessage());
                        });
                    }
                } else {
                    activity.runOnUiThread(() -> {
                        Log.e(message, "Arrangement:" + response);
                        callback.onFailure("Response code:" + response.code());
                    });
                }
            }
        });
    }

    public static void GenerateQRCodeAsync(String item_id, String organizationId, String username, Activity activity, OperationCallback<Boolean> callback) {
        String json = "{"
                + "\"body\": {"
                + "\"item_id\": \"" + Integer.parseInt(item_id) + "\","
                + "\"organization_id\": \"" + Integer.parseInt(organizationId) + "\","
                + "\"username\": \"" + username + "\""
                + "}"
                + "}";

        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

        String API_URL = BuildConfig.GenerateQRCodeAsync;

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
                        Log.d(message, "POST request failed QR", e);
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
                            Log.e(message, "POST request failed QR: " + response.code());
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

    public static void GenerateBarCodeAsync(String item_id, String organizationId, String username, Activity activity, OperationCallback<Boolean> callback) {
        String json = "{"
                + "\"body\": {"
                + "\"item_id\": \"" + Integer.parseInt(item_id) + "\","
                + "\"organization_id\": \"" + Integer.parseInt(organizationId) + "\","
                + "\"username\": \"" + username + "\""
                + "}"
                + "}";

        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

        String API_URL = BuildConfig.GenerateBarCodeAsync;

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
                        Log.d(message, "POST request failed BAR", e);
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
                            Log.e(message, "POST request failed: BAR" + response.code());
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

    public static void OpenSearchInsert(String item_id, String organizationId, String username, Activity activity, OperationCallback<Boolean> callback) {
        String json = "{"
                + "\"body\": {"
                + "\"item_id\": \"" + Integer.parseInt(item_id) + "\","
                + "\"organization_id\": \"" + Integer.parseInt(organizationId) + "\","
                + "\"username\": \"" + username + "\""
                + "}"
                + "}";

        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

        String API_URL = BuildConfig.OpenSearchInsert;

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
                        Log.d("MyAmplifyApp Group", "POST request failed", e);
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
                            Log.e(message, "POST request failed: Search" + response.code());
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

    public static void ModifyItemDimension(String item_id, String width, String height, String depth, String weight, String loadbear, String updown, Activity activity, OperationCallback<Boolean> callback) {
        String json = "{"
                + "\"itemid\": \"" + Integer.parseInt(item_id) + "\","
                + "\"width\": \"" + Integer.parseInt(width) + "\","
                + "\"height\": \"" + Integer.parseInt(height) + "\","
                + "\"depth\": \"" + Integer.parseInt(depth) + "\","
                + "\"weight\": \"" + Integer.parseInt(weight) + "\","
                + "\"loadbear\": \"" + Integer.parseInt(loadbear) + "\","
                + "\"updown\": \"" + "TRUE" + "\""
                + "}";

        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

        String API_URL = BuildConfig.ModifyItemDimension;

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
                        Log.d(message, "POST request failed", e);
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
                            Log.e(message, "POST request failed: Modify " + response);
                            callback.onFailure("Response code" + response.code());
                        });
                    }
                }
            });

        }).exceptionally(ex -> {
            Log.e("TokenError", "Failed to get user token", ex);
            return null;
        });
    }

    public static void createUnitAPI(String unitName, String capacity, String constraints, String width, String height, String depth, String maxweight, String username, String organizationId,Activity activity, OperationCallback<Boolean> callback) {
        String json = "{\"Unit_Name\":\"" + unitName + "\", \"Unit_Capacity\":\"" + capacity + "\", \"constraints\":\"" + constraints + "\",\"Unit_QR\":\"1\",\"unit_capacity_used\":\"0\", \"width\":\"" + width + "\", \"height\":\"" + height + "\", \"depth\":\"" + depth + "\", \"maxweight\":\"" + maxweight + "\", \"username\":\"" + username + "\", \"organization_id\":\"" + organizationId + "\", \"Unit_QR\":\"" + "QR1" + "\"}";

        MediaType jsonObject = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String apiUrl = BuildConfig.AddUnitEndpoint;
        RequestBody body = RequestBody.create(json, jsonObject);

        Utils.getUserToken().thenAccept(token -> {
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .header("Authorization", token)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                    Log.e("Unit Request Method", "POST request failed", e);
                    callback.onFailure(e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        Log.i("Unit Response", "Unit created successfully");
                        callback.onSuccess(true);
                    } else {
                        Log.e("Unit Request Method", "POST request failed: " + response);
                        callback.onFailure("Response code" + response.code());
                    }
                }
            });
        }).exceptionally(ex -> {
            Log.e("TokenError", "Failed to get user token", ex);
            return null;
        });
    }
    public static void postEditItem(String itemname, String description, String colourcoding, String barcode, String qrcode, String quantity, String location, String itemimage,String itemId, int parentcategory, int subcategory, String username, String organizationId, Activity activity, OperationCallback<Boolean> callback) {

        String json = "{\"item_name\":\"" + itemname + "\",\"description\":\"" + description + "\" ,\"colourcoding\":\"" + colourcoding + "\",\"barcode\":\"" + barcode + "\",\"qrcode\":\"" + qrcode + "\",\"quanity\":" + quantity + ",\"location\":\"" + location + "\", \"item_id\":\"" + itemId + "\", \"item_image\": \""+itemimage+"\", \"parentcategoryid\": \""+parentcategory+"\", \"subcategoryid\": \""+subcategory+"\", \"username\": \""+username+"\", \"organizationid\":\""+organizationId+"\" }";
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.EditItemEndPoint;
        RequestBody body = RequestBody.create(json, JSON);

        TokenManager.getToken().thenAccept(results-> {
            activity.runOnUiThread(() -> {
                Log.i(message, "JSON: "+ json.toString());
            });
            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", results)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    activity.runOnUiThread(() -> {
                        Log.d(message, "POST request failed", e);
                        Log.i(message, "POST request failed"+ json.toString());
                        callback.onFailure(e.getMessage());
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String responseData = response.body().string();
                        activity.runOnUiThread(() -> {
                            Log.i(message, "POST request succeeded: " + responseData);
                            Log.i(message, "POST request success: "+ json.toString());
                            callback.onSuccess(true);
                        });
                    } else {
                        activity.runOnUiThread(() -> {
                            Log.e(message, "POST request failed: Modify " + response);
                            Log.e(message, "POST request failed: Modify"+ json.toString());
                            callback.onFailure("Response code" + response.code());
                        });
                    }
                }
            });

        }).exceptionally(ex -> {
            Log.e("TokenError", "Failed to get user token", ex);
            return null;
        });
    }

    public static void FetchUnitsConstraint(String organizationId, String categoryId,  Activity activity, OperationCallback<List<unitModel>> callback)
    {
        String json = "{"
                + "\"body\": {"
                + "\"category_id\": \"" + Integer.parseInt(categoryId) + "\","
                + "\"organization_id\": \"" + Integer.parseInt(organizationId) + "\""
                + "}"
                + "}";
        List<unitModel> unitModelList = new ArrayList<>();

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.GetUnitConstraints;
        RequestBody body = RequestBody.create(json, JSON);

        TokenManager.getToken().thenAccept(results-> {
            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .addHeader("Authorization", results)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(() -> {
                        Log.e(message, "GET request failed", e);
                        callback.onFailure(e.getMessage());
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String responseData = response.body().string();
                        if(responseData == null){
                            activity.runOnUiThread(() -> callback.onSuccess(unitModelList));
                        }
                        else {
                            activity.runOnUiThread(() -> Log.e(message, responseData));

                            try {
                                JSONObject jsonObject = new JSONObject(responseData);
                                String bodyString = jsonObject.getString("body");

                                // Try to convert bodyString to a JSONArray
                                try {
                                    JSONArray bodyArray = new JSONArray(bodyString);
                                    // If bodyString is a valid JSONArray, proceed with parsing the items
                                    for (int i = 0; i < bodyArray.length(); i++) {
                                        JSONObject itemObject = bodyArray.getJSONObject(i);

                                        String unitName = itemObject.getString("name");
                                        String capacity = itemObject.getString("capacity");
                                        String unitId = itemObject.getString("id");
                                        String capacityUsed = itemObject.getString("capacity_used");
//                                        String categories = itemObject.getString("category_name");

                                        // Clean up the categories string
//                                        categories = categories.replaceAll("[\\[\\]\"]", "");
//
//                                        // Split categories into a list
//                                        List<String> list = new ArrayList<>(Arrays.asList(categories.split(", ")));

                                        // Create the unitModel object and add it to the list
                                        unitModel item = new unitModel(unitName, unitId, Integer.parseInt(capacity), Integer.parseInt(capacityUsed), "");
//                                        item.setCategories(list);

                                        unitModelList.add(item);
                                    }

                                    // If JSONArray parsing and processing is successful, return the result
                                    activity.runOnUiThread(() -> callback.onSuccess(unitModelList));

                                } catch (JSONException jsonArrayException) {
                                    // If bodyString is not a valid JSONArray, return the existing unitModelList
                                    activity.runOnUiThread(() -> callback.onSuccess(unitModelList));
                                }

                            } catch (JSONException e) {
                                // Handle any JSON parsing errors and call onFailure
                                activity.runOnUiThread(() -> {
                                    activity.runOnUiThread(() -> callback.onSuccess(unitModelList));
                                });
                            }
                        }
                    } else {
                        activity.runOnUiThread(() -> {
                            Log.e(message, "GET request failed:" + response);
                            callback.onFailure("Response code:" + response.code());
                        });
                    }
                }
            });

        }).exceptionally(ex -> {
//            Log.e("TokenError", "Failed to get user token", ex);
            return null;
        });
    }

    public static void modifyUnit(String unitId, String width, String height, String depth, String maxWeight, Activity activity, OperationCallback<Boolean> callback)
    {
        String json = "{\"unitid\":\""+Integer.parseInt(unitId)+"\", \"width\":\""+width+"\", \"height\":\""+height+"\", \"depth\":\""+depth+"\", \"maxweight\":\""+maxWeight+"\"}";

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.ModifyUnitDimension;
        RequestBody body = RequestBody.create(json, JSON);
        TokenManager.getToken().thenAccept(results->{
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
