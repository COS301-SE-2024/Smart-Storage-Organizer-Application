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
    private boolean flag = true;
    public ArrayList<CategoryModel> categoryModelList = new ArrayList<>();
    private List<String> parentCategories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_item);

        mySpinner = findViewById(R.id.category_filter);
        Category = findViewById(R.id.category_text);

        Category.setText(getIntent().getStringExtra("category"));
        categoryID = getIntent().getStringExtra("category_id");

        if(flag) {
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
                Toast.makeText(ViewItemActivity.this, "Selected: " + currentSelectedCategory, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do something here if nothing is selected
            }
        });
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
                                parentCategories.add("Filter");
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
}