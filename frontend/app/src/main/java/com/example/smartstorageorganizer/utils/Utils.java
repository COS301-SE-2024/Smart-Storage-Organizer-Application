package com.example.smartstorageorganizer.utils;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.smartstorageorganizer.BuildConfig;
import com.example.smartstorageorganizer.model.CategoryModel;
import com.example.smartstorageorganizer.model.ItemModel;
import com.example.smartstorageorganizer.model.unitModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Utils {
    private static String type = "application/json; charset=utf-8";
    private static String message = "Request Method";

    private Utils() {}

    public static void fetchParentCategories(int categoryId, String email, Activity activity, OperationCallback<List<CategoryModel>> callback) {
        String json = "{\"useremail\":\"" + email + "\", \"parentcategory\":\"" + Integer.toString(categoryId) + "\" }";

        List<CategoryModel> categoryModelList = new ArrayList<>();

        MediaType mediaType = MediaType.get(type);
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

        String message = "Add Category";
        MediaType mediaType = MediaType.get(type);
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
                        callback.onFailure("Response code" + response.code());
                    });
                }
            }
        });
    }

    public static void filterByCategory(int parentCategory, int howMany, int pageNumber, Activity activity, OperationCallback<List<ItemModel>> callback) {
//        String json = "{\"parentcategory\":\"" + parentCategory + "\" }";
        String json = "{\"parentcategory\":\"" + parentCategory + "\", \"limit\":\""+Integer.toString(howMany)+"\", \"offset\":\""+Integer.toString(pageNumber)+"\" }";
        String message = "View Response Results";

        List<ItemModel> itemModelList = new ArrayList<>();
        MediaType mediaType = MediaType.get(type);
        OkHttpClient client = new OkHttpClient();
        String apiUrl = BuildConfig.CategoryFilterEndPoint;
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
                    Log.e(message, "GET request failed One", e);
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
                        callback.onFailure("Response code" + response.code());
                    });
                }
            }
        });
    }

    public static void filterBySubCategory(int parentCategory, int subcategory, int howMany, int pageNumber, Activity activity, OperationCallback<List<ItemModel>> callback) {
//        String json = "{\"parentcategory\":\"" + parentCategory + "\", \"subcategory\":\"" + subcategory + "\" }";
        String json = "{\"parentcategory\":\"" + parentCategory + "\", \"subcategory\":\"" + subcategory + "\", \"limit\":\""+Integer.toString(howMany)+"\", \"offset\":\""+Integer.toString(pageNumber)+"\" }";

        String message = "View Request Method";

        List<ItemModel> itemModelList = new ArrayList<>();
        MediaType mediaType = MediaType.get(type);
        OkHttpClient client = new OkHttpClient();
        String apiUrl = BuildConfig.SubCategoryFilterEndPoint;
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
                        String numPagesString = jsonObject.getString("totalpages");
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
    }
    public static CompletableFuture<Boolean> getAllUnits (String categoriesId)  {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("categories_id", Integer.parseInt(categoriesId));
            jsonObject.put("type", "GetCategoryConstraints");
        }
        catch (JSONException e){
            future.completeExceptionally(e);
            return future;
        }

        OkHttpClient client = new OkHttpClient();


        RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.get(type));

        Request request = new Request.Builder()
                .url(BuildConfig.AddUnitEndPoint)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                future.completeExceptionally(e);
                Log.i("Error","Error in fetching data");

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response)  {
                if(response.isSuccessful()){
                    try {

                        final String jsonBody = response.body().toString();
                        JSONObject jsonObject = new JSONObject(jsonBody);
                        final String body= jsonObject.getString("body");
                        JSONArray bodyArray = new JSONArray(body);
                        List<unitModel> unitList=new ArrayList<>();
                        for(int i=0;i<bodyArray.length();i++){
                            JSONObject unitObject=bodyArray.getJSONObject(i);
                            String unitName=unitObject.getString("unit_name");
                            String unitId=unitObject.getString("unit_id");
                            int capacity=Integer.parseInt(unitObject.getString("capacity"));
                            int currentCapacity=Integer.parseInt(unitObject.getString("current_capacity"));
                            unitModel unit=new unitModel(unitName,unitId,capacity,currentCapacity);
                            unitList.add(unit);
                        }
                    }
                    catch (Exception e){
                        future.completeExceptionally(e);
                        Log.i("Error: ","Error in parsing json");
                    }

                }
                else{

                    Log.i("Error","Error in fetching data");
                    future.complete(false);
                }
            }
        });
        return future;
    }
    public unitModel getUnitAvailable(List<unitModel> unitList){
        unitModel units=new unitModel("","",0,0);
        for(unitModel unit:unitList){
            if(units.getFreeCapacity()>unit.getFreeCapacity()){
               units=unit;
            }
        }
        return units;

    }

    public static void deleteCategory(int id, String email, Activity activity, OperationCallback<Boolean> callback)
    {
        String json = "{\"useremail\":\""+email+"\", \"id\":\""+Integer.toString(id)+"\" }";

        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String apiUrl = BuildConfig.DeleteCategoryEndPoint;
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

    public static void modifyCategoryName(int id, String newCategoryName, Activity activity, OperationCallback<Boolean> callback)
    {
        String json = "{\"id\":\""+Integer.toString(id)+"\", \"categoryname\":\""+newCategoryName+"\" }";

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.ModifyCategoryName;
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

    public static void categoryToUncategorized(int id, Activity activity, OperationCallback<Boolean> callback)
    {
        String json = "{\"parentcategoryid\":\""+Integer.toString(id)+"\" }";

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.CategoryToUncategorized;
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

    public static void fetchAllItems(int howMany, int pageNumber, Activity activity, OperationCallback<List<ItemModel>> callback)
    {
        String json = "{\"limit\":\""+Integer.toString(howMany)+"\", \"offset\":\""+Integer.toString(pageNumber)+"\" }";

        List<ItemModel> itemModelList = new ArrayList<>();

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.FetchAllEndPoint;
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
    }
}
