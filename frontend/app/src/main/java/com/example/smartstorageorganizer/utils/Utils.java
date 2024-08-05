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
import com.example.smartstorageorganizer.model.SuggestedCategoryModel;
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

public class Utils
{
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
        Log.d("Delete Item Payload Cc", "JSON Payload: " + json);  // Log the JSON payload

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
    }

    public static void addColourGroup(String colourcode, String title, String description, String email, Activity activity, OperationCallback<Boolean> callback)
    {
        String json = "{\"colourcode\":\""+colourcode+"\", \"description\":\""+description+"\", \"title\":\""+title+"\", \"createremail\":\""+email+"\"}";


        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.AddColourEndPoint;
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

    public static void fetchAllColour(Activity activity, OperationCallback<List<ColorCodeModel>> callback)
    {
        String json = "{}";

        List<ColorCodeModel> colorCodeModelList = new ArrayList<>();

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.FetchByColourEndPoint;
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(API_URL)
                .get()
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

    public static void deleteColour(int colourId, Activity activity, OperationCallback<Boolean> callback)
    {
        String json = "{\"id\":\""+Integer.toString(colourId)+"\"}";

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.DeleteColour;
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

    public static void fetchByColour(int colourId, Activity activity, OperationCallback<List<ItemModel>> callback)
    {
        String json = "{\"colourid\":\""+Integer.toString(colourId)+"\"}";
        List<ItemModel> itemModelList = new ArrayList<>();



        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.FetchByColourEndPoint;
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
    }

    public static void fetchRecentItems(String email, Activity activity, OperationCallback<List<ItemModel>> callback)
    {
        String json = "{\"email\":\""+email+"\" }";

        List<ItemModel> itemModelList = new ArrayList<>();

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.FetchByEmailEndPoint;
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
    }

    public static void fetchCategorySuggestions(String name, String description, String email, Activity activity, OperationCallback<List<CategoryModel>> callback)
    {
        // API endpoint that can return category suggestions based on the item
        String API_URL = BuildConfig.RecommendCategoryEndPoint;
        OkHttpClient client = new OkHttpClient();

        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put("itemname", name);
            jsonObject.put("itemdescription", description);
            jsonObject.put("useremail", email);
        } catch (JSONException e)
        {
            e.printStackTrace();
            return;
        }

        RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback()
        {
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
                if (response.isSuccessful())
                {
                    // response body converted to string
                    final String responseData = response.body().string();
                    Log.i("1Response Data", responseData);
                    activity.runOnUiThread(() ->
                    {
                        try
                        {
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
                                Log.e(message, "JSON parsing error: " + e.getMessage());
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
    }

    public static void postAddItem(String item_image, String item_name, String description, int category, int parentCategory, String userEmail, Activity activity, OperationCallback<Boolean> callback) {
        // Provide default values for the remaining attributes
        int subCategory = 0;
        String colourcoding = "default";
        String barcode = "default";
        String qrcode = "default";
        int quantity = 1;
        String location = "default";
        String email = userEmail;
        String json = "{\"item_name\":\""+item_name+"\",\"description\":\""+description+"\" ,\"colourcoding\":\""+colourcoding+"\",\"barcode\":\""+barcode+"\",\"qrcode\":\""+qrcode+"\",\"quanity\":"+quantity+",\"location\":\""+location+"\",\"email\":\""+email+"\", \"category\":\""+3+"\", \"sub_category\":\""+5+"\" }";
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.AddItemEndPoint;

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
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.get("application/json; charset=utf-8"));

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

    public static void fetchByID(int id, Activity activity, OperationCallback<List<ItemModel>> callback)
    {
        String json = "{\"item_id\":\""+Integer.toString(id)+"\"}";

        List<ItemModel> itemModelList = new ArrayList<>();

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.FetchByIDEndPoint;
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
    }

    public static void FetchUncategorizedItems(int howMany, int pageNumber, Activity activity, OperationCallback<List<ItemModel>> callback)
    {
        String json = "{\"limit\":\""+Integer.toString(howMany)+"\", \"offset\":\""+Integer.toString(pageNumber)+"\" }";

        List<ItemModel> itemModelList = new ArrayList<>();

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.FetchUncategorizedEndPoint;
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
    }

    public static void RecommendMultipleCategories(String id, Activity activity, OperationCallback<List<SuggestedCategoryModel>> callback)
    {
        String json = "{\"id\":\""+id+"\"}";

        List<SuggestedCategoryModel> categoryModelList = new ArrayList<>();

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.RecommendMultipleEndPoint;
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
                    Log.e(message, "GET Suggested request failed", e);
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
                        String bodyString = jsonObject.getString("response");
                        JSONArray bodyArray = new JSONArray(bodyString);
                        activity.runOnUiThread(() -> Log.e("View Suggested Response Results Body Array", bodyArray.toString()));

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

                            activity.runOnUiThread(() -> Log.e("View Suggested Response Results Body Array", categoryString.toString()));
                            activity.runOnUiThread(() -> Log.e("View Suggested Response Results Body Array", subcategoryString.toString()));

//                            ItemModel item = new ItemModel();
//                            item.setItemId(itemObject.getString("item_id"));
//                            item.setItemName(itemObject.getString("item_name"));
//                            item.setDescription(itemObject.getString("description"));
//                            item.setColourCoding(itemObject.getString("colourcoding"));
//                            item.setBarcode(itemObject.getString("barcode"));
//                            item.setQrcode(itemObject.getString("qrcode"));
//                            item.setQuantity(itemObject.getString("quanity"));
//                            item.setLocation(itemObject.getString("location"));
//                            item.setEmail(itemObject.getString("email"));
//                            item.setItemImage(itemObject.getString("item_image"));
//                            item.setParentCategoryId(itemObject.getString("parentcategoryid"));
//                            item.setSubCategoryId(itemObject.getString("subcategoryid"));
//                            item.setCreatedAt(itemObject.getString("created_at"));

//                            itemModelList.add(item);
                            categoryModelList.add(suggestedCategory);
                        }

                        activity.runOnUiThread(() -> callback.onSuccess(categoryModelList));
                    } catch (JSONException e) {
                        activity.runOnUiThread(() -> {
                            Log.e(message, "JSON Suggested parsing error: " + e.getMessage());
                            callback.onFailure(e.getMessage());
                        });
                    }
                } else {
                    activity.runOnUiThread(() -> {
                        Log.e(message, "GET Suggested request failed:" + response);
                        callback.onFailure("Response code:" + response.code());
                    });
                }
            }
        });
    }
}
