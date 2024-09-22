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
//                        Log.e("Category Request Method", "GET request failed", e);
                        callback.onFailure(e.getMessage());
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String responseData = response.body().string();
//                        activity.runOnUiThread(() -> Log.e("Category Response Results", responseData));
                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            String bodyString = jsonObject.getString("body");
                            JSONArray bodyArray = new JSONArray(bodyString);
//                            activity.runOnUiThread(() -> Log.e("Category Details Array", bodyArray.toString()));

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
//                                Log.e(message, "GET request failed: " + response);
                                callback.onFailure(e.getMessage());
                            });
                        }
                    } else {
                        activity.runOnUiThread(() -> {
//                            Log.e(message, "GET request failed: " + response);
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
//        String json = "{\"parentcategory\":\"" + parentCategory + "\", \"subcategory\":\"" + subcategory + "\" }";
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
//    public static CompletableFuture<Boolean> getAllUnits (String categoriesId)  {
//        CompletableFuture<Boolean> future = new CompletableFuture<>();
//        JSONObject jsonObject=new JSONObject();
//        try {
//            jsonObject.put("categories_id", Integer.parseInt(categoriesId));
//            jsonObject.put("type", "GetCategoryConstraints");
//        }
//        catch (JSONException e){
//            future.completeExceptionally(e);
//            return future;
//        }
//
//        OkHttpClient client = new OkHttpClient();
//
//
//        RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.get(type));
//        TokenManager.getToken().thenAccept(results->{
//            Request request = new Request.Builder()
//                    .url(BuildConfig.AddUnitEndPoint)
//                    .addHeader("Authorization", results)
//                    .post(body)
//                    .build();
//            client.newCall(request).enqueue(new Callback() {
//                @Override
//                public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                    future.completeExceptionally(e);
////                    Log.i("Error","Error in fetching data");
//
//                }
//
//                @Override
//                public void onResponse(@NonNull Call call, @NonNull Response response)  {
//                    if(response.isSuccessful()){
//                        try {
//
//                            final String jsonBody = response.body().toString();
//                            JSONObject jsonObject = new JSONObject(jsonBody);
//                            final String body= jsonObject.getString("body");
//                            JSONArray bodyArray = new JSONArray(body);
//                            List<unitModel> unitList=new ArrayList<>();
//                            for(int i=0;i<bodyArray.length();i++){
//                                JSONObject unitObject=bodyArray.getJSONObject(i);
//                                String unitName=unitObject.getString("unit_name");
//                                String unitId=unitObject.getString("unit_id");
//                                int capacity=Integer.parseInt(unitObject.getString("capacity"));
//                                int currentCapacity=Integer.parseInt(unitObject.getString("current_capacity"));
//                                unitModel unit=new unitModel(unitName,unitId,capacity,currentCapacity);
//                                unitList.add(unit);
//                            }
//                        }
//                        catch (Exception e){
//                            future.completeExceptionally(e);
////                            Log.i("Error: ","Error in parsing json");
//                        }
//
//                    }
//                    else{
//
////                        Log.i("Error","Error in fetching data");
//                        future.complete(false);
//                    }
//                }
//            });
//        }).exceptionally(ex -> {
////            Log.e("TokenError", "Failed to get user token", ex);
//            return null;
//        });
//        return future;
//    }
//    public unitModel getUnitAvailable(List<unitModel> unitList){
//        unitModel units=new unitModel("","",0,0);
//        for(unitModel unit:unitList){
//            if(units.getFreeCapacity()>unit.getFreeCapacity()){
//                units=unit;
//            }
//        }
//        return units;
//
//    }

    public static void deleteCategory(int id, String email, Activity activity, OperationCallback<Boolean> callback)
    {
        String json = "{\"useremail\":\""+email+"\", \"id\":\""+Integer.toString(id)+"\" }";
//        Log.d("Delete Item Payload Cc", "JSON Payload: " + json);  // Log the JSON payload

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

    public static void modifyCategoryName(int id, String newCategoryName, Activity activity, OperationCallback<Boolean> callback)
    {
        String json = "{\"id\":\""+Integer.toString(id)+"\", \"categoryname\":\""+newCategoryName+"\" }";

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
        String json = "{\"colourcode\":\""+colourcode+"\", \"description\":\""+description+"\", \"title\":\""+title+"\", \"createremail\":\""+email+"\", \"organizationid\":\""+Integer.parseInt(organizationId)+"\"}";


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

    public static void deleteColour(int colourId, String organizationId, Activity activity, OperationCallback<Boolean> callback) {
        String json = "{\"id\":\"" + Integer.toString(colourId) + "\", \"organizationid\":\""+Integer.parseInt(organizationId)+"\"}";

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

    public static void fetchByColour(int colourId, String organizationId, Activity activity, OperationCallback<List<ItemModel>> callback) {
        String json = "{\"colourid\":\"" + Integer.toString(colourId) + "\", \"organizationid\":\"" + Integer.parseInt(organizationId)+"\"}";
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

    public static void fetchByID(int id, String organizationId, Activity activity, OperationCallback<List<ItemModel>> callback)
    {
        String json = "{\"item_id\":\""+Integer.toString(id)+"\", \"organizationid\":\""+Integer.parseInt(organizationId)+"\"}";

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
                            JSONArray bodyArray = new JSONArray(bodyString);
                            activity.runOnUiThread(() -> Log.e("QR Code", bodyArray.toString()));

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

    public static void FetchUncategorizedItems(int howMany, int pageNumber, String organizationID, Activity activity, OperationCallback<List<ItemModel>> callback)
    {
        String json = "{\"limit\":\""+Integer.toString(howMany)+"\", \"offset\":\""+Integer.toString(pageNumber)+"\", \"organizationid\":\"" + Integer.parseInt(organizationID) + "\" }";

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
//    static public CompletableFuture<ArrayList<unitModel>> getAllUnitsForCategory(int categoryId) {
//        CompletableFuture<ArrayList<unitModel>> future = new CompletableFuture<>();
//        String json = "{\"category_id\":\""+categoryId+"\"}";
//        MediaType JSON = MediaType.get("application/json; charset=utf-8");
//        String API_URL = BuildConfig.GetUnitConstraints;
//        OkHttpClient client=new OkHttpClient();
//        TokenManager.getToken().thenAccept(results-> {
//            Request request = new Request.Builder()
//                    .url(API_URL)
//                    .addHeader("Authorization", results)
//                    .post(RequestBody.create(json, JSON))
//                    .build();
//            client.newCall(request).enqueue(new Callback() {
//                @Override
//                public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                    future.completeExceptionally(e);
//                }
//
//                @Override
//                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                    if (response.isSuccessful()) {
//                        try {
//                            final String responseData = response.body().string();
//                            JSONObject jsonObject = new JSONObject(responseData);
//                            String bodyString = jsonObject.getString("body");
//                            JSONArray bodyArray = new JSONArray(bodyString);
//                            ArrayList<unitModel> unitList = new ArrayList<>();
//                            for (int i = 0; i < bodyArray.length(); i++) {
//                                JSONObject unitObject = bodyArray.getJSONObject(i);
//                                String unitName = unitObject.getString("name");
//                                String unitId = unitObject.getString("id");
//                                int capacity = Integer.parseInt(unitObject.getString("capacity"));
//                                int currentCapacity = Integer.parseInt(unitObject.getString("capacity_used"));
//                                unitModel unit = new unitModel(unitName, unitId, capacity, currentCapacity);
//                                unitList.add(unit);
//                            }
//                            future.complete(unitList);
//                        } catch (Exception e) {
//                            future.completeExceptionally(e);
//                        }
//                    } else {
//                        future.completeExceptionally(new Exception("Error in fetching data"));
//                    }
//                }
//            });
//        }).exceptionally(ex -> {
////            Log.e("TokenError", "Failed to get user token", ex);
//            return null;
//        });
//        return future;
//    }

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



    public static String AllocateUnitToItem(ArrayList<unitModel> units) {
        int id = -1;
        String name = "";
        int capacity = -1;
        for (unitModel unit : units) {
            if (unit.getFreeCapacity() > capacity) {
                id = Integer.parseInt(unit.getId());
                capacity = unit.getFreeCapacity();
                name = unit.getUnitName();
            }
        }

        return name;
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

    public static void FetchCategoriesOfUnits(String unit_id,  Activity activity, OperationCallback<List<String>> callback)
    {
        String json = "{"
                + "\"unit_id\": \"" + Integer.parseInt(unit_id) + "\""
                + "}";
        List<String> categoriesList = new ArrayList<>();

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.GetCategoriesOfUnits;
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
//                            if(!bodyString.equals("No category found for the given unit")){
                                JSONArray bodyArray = new JSONArray(bodyString);
                                activity.runOnUiThread(() -> Log.e("View Response Results Body Array", bodyArray.toString()));

                                for (int i = 0; i < bodyArray.length(); i++) {
                                    JSONObject itemObject = bodyArray.getJSONObject(i);

                                    String categoryName = (itemObject.getString("categoryname"));
//                            String capacity = (itemObject.getString("capacity"));
//                            String unitId = (itemObject.getString("id"));
//                            String capacityUsed = (itemObject.getString("capacity_used"));

//                            unitModel item = new unitModel(unitName, unitId, Integer.parseInt(capacity), Integer.parseInt(capacityUsed));

                                    categoriesList.add(categoryName);
                                }
//                            }

                            activity.runOnUiThread(() -> callback.onSuccess(categoriesList));
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
                        activity.runOnUiThread(() -> Log.e(message, responseData));

                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            String bodyString = jsonObject.getString("body");
                            JSONArray bodyArray = new JSONArray(bodyString);
//                            activity.runOnUiThread(() -> Log.e("View Response Results Body Array", bodyArray.toString()));

                            for (int i = 0; i < bodyArray.length(); i++) {
                                JSONObject itemObject = bodyArray.getJSONObject(i);

                                String unitName = (itemObject.getString("name"));
                                String capacity = (itemObject.getString("capacity"));
                                String unitId = (itemObject.getString("id"));
                                String capacityUsed = (itemObject.getString("capacity_used"));
                                String categories = (itemObject.getString("category_name"));

                                categories = categories.replaceAll("[\\[\\]\"]", "");

                                // Split the string by commas and store it in a list
                                List<String> list = new ArrayList<>(Arrays.asList(categories.split(", ")));
//                                JSONArray categoriesArray = new JSONArray(categories);

                                unitModel item = new unitModel(unitName, unitId, Integer.parseInt(capacity), Integer.parseInt(capacityUsed), categories);
                                item.setCategories(list);
//                                item.setCategories((List<String>) categoriesArray);

                                unitModelList.add(item);
                            }

                            activity.runOnUiThread(() -> callback.onSuccess(unitModelList));
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

    public static void fetchItemsUnderUnit(String unitName, Activity activity, OperationCallback<List<ItemModel>> callback)
    {
        String json = "{"
                + "\"body\": {"
                + "\"unit_name\": \"" + unitName + "\""
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

    public static void incrementQuantity(String item_id, int quantity, Activity activity, OperationCallback<Boolean> callback) {
        String json = "{"
                + "\"body\": {"
                + "\"item_id\": \"" + Integer.parseInt(item_id) + "\","
                + "\"quantity\": \"" + quantity + "\""
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
                        String bodyString = jsonObject.getString("response");
//                        JSONArray bodyArray = new JSONArray(bodyString);
                        activity.runOnUiThread(() -> Log.e("View Response Results Body Array", bodyString));
                        JSONObject jsonObject1 = new JSONObject(bodyString);
                        String imageUrl = jsonObject1.getString("path");
                        String items = jsonObject1.getString("items");
                        activity.runOnUiThread(() -> Log.e("View Response Results Body Array", imageUrl));

                        JSONArray itemsArray = new JSONArray(items);
                        List<BinItemModel> itemsList = new ArrayList<>();

                        for (int i = 0; i < itemsArray.length(); i++) {
                            JSONObject itemObject = itemsArray.getJSONObject(i);

                            String name = itemObject.getString("name");
                            String color = itemObject.getString("color");

                            BinItemModel item = new BinItemModel(name, color);
                            itemsList.add(item);
                        }

                        ArrangementModel obj = new ArrangementModel(imageUrl, itemsList);

                        activity.runOnUiThread(() -> callback.onSuccess(obj));
                    } catch (JSONException e) {
                        activity.runOnUiThread(() -> {
                            Log.e("View Response Results Body Array", "JSON parsing error: " + e.getMessage());
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
}
