package com.example.smartstorageorganizer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.smartstorageorganizer.Adapters.ItemAdapter;
import com.example.smartstorageorganizer.model.CategoryModel;
import com.example.smartstorageorganizer.model.ItemModel;

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

public class ViewItemActivity extends AppCompatActivity {
    private TextView Category;
    private Spinner mySpinner;
    private String categoryID, currentSelectedCategory;
    private List<ItemModel> itemModelList;
    private ItemAdapter itemAdapter;
    RecyclerView.LayoutManager layoutManager;
    private boolean flag = true;
    public ArrayList<CategoryModel> categoryModelList = new ArrayList<>();
    private List<String> parentCategories = new ArrayList<>();
    private RecyclerView itemRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_item);

        mySpinner = findViewById(R.id.category_filter);
        Category = findViewById(R.id.category_text);
        itemRecyclerView = findViewById(R.id.view_all_rec);

        itemRecyclerView.setHasFixedSize(true);
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        itemRecyclerView.setLayoutManager(layoutManager);

        itemModelList = new ArrayList<>();

        itemAdapter = new ItemAdapter(ViewItemActivity.this, itemModelList);
        itemRecyclerView.setAdapter(itemAdapter);

        Category.setText(getIntent().getStringExtra("category"));
        categoryID = getIntent().getStringExtra("category_id");

        if(flag) {
            parentCategories.add("Filter");
            FilterByCategory(Integer.parseInt(categoryID));
//            FilterBySubCategory(Integer.parseInt(categoryID), 36);
            FetchCategory(Integer.parseInt(getIntent().getStringExtra("category_id")), getIntent().getStringExtra("email"));
            currentSelectedCategory = "";
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get selected item
                currentSelectedCategory = parentView.getItemAtPosition(position).toString();

                // Do something with the selected item
                if(!currentSelectedCategory.equals("Filter")) {
                    CategoryModel subCategory = findCategoryByName(currentSelectedCategory);
                    assert subCategory != null;
                    FilterBySubCategory(Integer.parseInt(categoryID), Integer.parseInt(subCategory.getCategoryID()));
                }
                Toast.makeText(ViewItemActivity.this, "Selected: " + currentSelectedCategory, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do something here if nothing is selected
            }
        });
    }

    private CategoryModel findCategoryByName(String categoryName) {
        runOnUiThread(() -> {
            Log.e("Spinner", categoryModelList.get(0).getCategoryName());
        });
        for (CategoryModel category : categoryModelList) {
            if (category.getCategoryName().equalsIgnoreCase(categoryName)) {
                return category;
            }
        }
        return null; // Return null if no category with the given name is found
    }

    public void FetchCategory(int ParentCategory, String email)
    {
        if(flag) {
            String json = "{\"useremail\":\""+email+"\", \"parentcategory\":\""+Integer.toString(ParentCategory)+"\" }";


            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            String API_URL = BuildConfig.FetchCategoryEndPoint;
            RequestBody body = RequestBody.create(json, JSON);

            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .build();


            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Log.e("Category Request Method", "GET request failed", e));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String responseData = response.body().string();
                        runOnUiThread(() -> {
                            runOnUiThread(() -> Log.e("Category Response Results", responseData));

                            try {
                                JSONObject jsonObject = new JSONObject(responseData);

                                int statusCode = jsonObject.getInt("statusCode");
                                String status = jsonObject.getString("status");

                                String bodyString = jsonObject.getString("body");
                                JSONArray bodyArray = new JSONArray(bodyString);

                                List<ItemModel> items = new ArrayList<>();

                                runOnUiThread(() -> Log.e("Category Details Array", bodyArray.toString()));
                                for (int i = 0; i < bodyArray.length(); i++) {
                                    JSONObject itemObject = bodyArray.getJSONObject(i);

                                    CategoryModel parentCategory = new CategoryModel();
                                    parentCategory.setCategoryID(itemObject.getString("id"));
                                    parentCategory.setCategoryName(itemObject.getString("categoryname"));
//                                    parentCategory.setImageUrl(itemObject.getString("icon"));

                                    categoryModelList.add(parentCategory);
                                    parentCategories.add(itemObject.getString("categoryname"));
//                                    String temp = itemObject.getString("icon");
//                                    runOnUiThread(() -> Log.e("Image Url", temp));
                                }
                                runOnUiThread(() -> {
                                    ArrayAdapter<String> adapter = new ArrayAdapter<>(ViewItemActivity.this, android.R.layout.simple_spinner_item, parentCategories);
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    mySpinner.setAdapter(adapter);
                                });


//                            requireActivity().runOnUiThread(() -> {
//                                fetchItemsLoader.setVisibility(View.GONE);
//                                itemRecyclerView.setVisibility(View.VISIBLE);
//                            });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });

                    } else {
                        runOnUiThread(() -> Log.e("Request Method", "GET request failed: " + response));
                    }
                }
            });
            flag = false;
        }
    }

    public void FilterByCategory(int parentcategory )
    {
        String json = "{\"parentcategory\":\""+parentcategory+"\" }";

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.CategoryFilterEndPoint;
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Log.e("View Request Method", "GET request failed", e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    runOnUiThread(() -> {
                        runOnUiThread(() -> Log.e("View Response Results", responseData));

//                        String jsonString = "{\"statusCode\": 200, \"status\": \"Fetch Query Successful\", \"body\": \"[{\\\"item_id\\\": 25, \\\"item_name\\\": \\\"Joystick\\\", \\\"description\\\": \\\"Gaming\\\", \\\"colourcoding\\\": \\\"Yellow\\\", \\\"barcode\\\": \\\"asdffd\\\", \\\"qrcode\\\": \\\"00111100\\\", \\\"quanity\\\": 1, \\\"location\\\": \\\"Centinary\\\", \\\"email\\\": \\\"gayol59229@fincainc.com\\\"}, {\\\"item_id\\\": 26, \\\"item_name\\\": \\\"Book\\\", \\\"description\\\": \\\"Textbook\\\", \\\"colourcoding\\\": \\\"Yellow\\\", \\\"barcode\\\": \\\"asdffd\\\", \\\"qrcode\\\": \\\"00111100\\\", \\\"quanity\\\": 1, \\\"location\\\": \\\"Centinary\\\", \\\"email\\\": \\\"gayol59229@fincainc.com\\\"}]\"}";

                        try {
                            JSONObject jsonObject = new JSONObject(responseData);

                            // Parse the main fields
                            int statusCode = jsonObject.getInt("statusCode");
//                            String status = jsonObject.getString("status");

                            // Parse the body field (which is a string containing a JSON array)
                            String bodyString = jsonObject.getString("body");
                            JSONArray bodyArray = new JSONArray(bodyString);

                            // Create a list to hold the items
                            List<ItemModel> items = new ArrayList<>();

                            runOnUiThread(() -> Log.e("View Response Results Body Array", bodyArray.toString()));

                            // Iterate through the array and populate the items list
                            for (int i = 0; i < bodyArray.length(); i++) {
                                JSONObject itemObject = bodyArray.getJSONObject(i);

                                ItemModel item = new ItemModel();
                                item.setItem_id(itemObject.getString("item_id"));
                                item.setItem_name(itemObject.getString("item_name"));
                                item.setDescription(itemObject.getString("description"));
                                item.setColourcoding(itemObject.getString("colourcoding"));
                                item.setBarcode(itemObject.getString("barcode"));
                                item.setQrcode(itemObject.getString("qrcode"));
                                item.setQuanity(itemObject.getString("quanity"));
                                item.setLocation(itemObject.getString("location"));
                                item.setEmail(itemObject.getString("email"));
                                item.setItem_image(itemObject.getString("item_image"));

                                itemModelList.add(item);
                                itemAdapter.notifyDataSetChanged();

                                runOnUiThread(() -> Log.e("View Item Details", item.getItem_name()));
                                runOnUiThread(() -> Log.e("View Item Details", item.getDescription()));
//                                runOnUiThread(() -> Log.e("Item Details", item.getItem_image()));
                            }

//                            runOnUiThread(() -> {
//                                fetchItemsLoader.setVisibility(View.GONE);
//                                itemRecyclerView.setVisibility(View.VISIBLE);
//                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                            runOnUiThread(() -> Log.e("View Response Results", e.toString()));
                        }
                    });

                } else {
                    runOnUiThread(() -> Log.e("Request Method", "GET request failed: " + response));
                }
            }
        });
    }

    public void FilterBySubCategory(int parentcategory, int subcategory )
    {
        String json = "{\"parentcategory\":\""+parentcategory+"\", \"subcategory\":\""+subcategory+"\" }";

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.SubCategoryFilterEndPoint;
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Log.e("View Request Method", "GET request failed", e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    runOnUiThread(() -> {
                        runOnUiThread(() -> Log.e("View Response Results", responseData));

//                        String jsonString = "{\"statusCode\": 200, \"status\": \"Fetch Query Successful\", \"body\": \"[{\\\"item_id\\\": 25, \\\"item_name\\\": \\\"Joystick\\\", \\\"description\\\": \\\"Gaming\\\", \\\"colourcoding\\\": \\\"Yellow\\\", \\\"barcode\\\": \\\"asdffd\\\", \\\"qrcode\\\": \\\"00111100\\\", \\\"quanity\\\": 1, \\\"location\\\": \\\"Centinary\\\", \\\"email\\\": \\\"gayol59229@fincainc.com\\\"}, {\\\"item_id\\\": 26, \\\"item_name\\\": \\\"Book\\\", \\\"description\\\": \\\"Textbook\\\", \\\"colourcoding\\\": \\\"Yellow\\\", \\\"barcode\\\": \\\"asdffd\\\", \\\"qrcode\\\": \\\"00111100\\\", \\\"quanity\\\": 1, \\\"location\\\": \\\"Centinary\\\", \\\"email\\\": \\\"gayol59229@fincainc.com\\\"}]\"}";

                        try {
                            JSONObject jsonObject = new JSONObject(responseData);

                            // Parse the main fields
                            int statusCode = jsonObject.getInt("statusCode");
//                            String status = jsonObject.getString("status");

                            // Parse the body field (which is a string containing a JSON array)
                            String bodyString = jsonObject.getString("body");
                            JSONArray bodyArray = new JSONArray(bodyString);

                            // Create a list to hold the items
                            List<ItemModel> items = new ArrayList<>();

                            runOnUiThread(() -> Log.e("View Response Results Body Array", bodyArray.toString()));

                            itemModelList.clear();
                            // Iterate through the array and populate the items list
                            for (int i = 0; i < bodyArray.length(); i++) {
                                JSONObject itemObject = bodyArray.getJSONObject(i);

                                ItemModel item = new ItemModel();
                                item.setItem_id(itemObject.getString("item_id"));
                                item.setItem_name(itemObject.getString("item_name"));
                                item.setDescription(itemObject.getString("description"));
                                item.setColourcoding(itemObject.getString("colourcoding"));
                                item.setBarcode(itemObject.getString("barcode"));
                                item.setQrcode(itemObject.getString("qrcode"));
                                item.setQuanity(itemObject.getString("quanity"));
                                item.setLocation(itemObject.getString("location"));
                                item.setEmail(itemObject.getString("email"));
                                item.setItem_image(itemObject.getString("item_image"));

                                itemModelList.add(item);
                                itemAdapter.notifyDataSetChanged();

                                runOnUiThread(() -> Log.e("View Item Details", item.getItem_name()));
                                runOnUiThread(() -> Log.e("View Item Details", item.getDescription()));
//                                runOnUiThread(() -> Log.e("Item Details", item.getItem_image()));
                            }

//                            runOnUiThread(() -> {
//                                fetchItemsLoader.setVisibility(View.GONE);
//                                itemRecyclerView.setVisibility(View.VISIBLE);
//                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                            runOnUiThread(() -> Log.e("View Response Results", e.toString()));
                        }
                    });

                } else {
                    runOnUiThread(() -> Log.e("Request Method", "GET request failed: " + response));
                }
            }
        });
    }
}