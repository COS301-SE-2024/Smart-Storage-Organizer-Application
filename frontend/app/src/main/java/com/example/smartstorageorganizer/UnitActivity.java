package com.example.smartstorageorganizer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smartstorageorganizer.model.CategoryModel;

import com.example.smartstorageorganizer.utils.Utils;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UnitActivity extends AppCompatActivity {

    boolean flag = true;
    private ArrayList<CategoryModel> categoryModelList= new ArrayList<>();
    private ConstraintLayout addButton;
    private final List<String> parentCategories = new ArrayList<>();
    private LinearLayout checkboxContainer;
    TextInputEditText unitName;
    TextInputEditText unitCap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_unit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {

            checkboxContainer = findViewById(R.id.checkbox_container);
            addButton = findViewById(R.id.addButton);
            unitName = findViewById(R.id.unitName);
            unitCap = findViewById(R.id.capacity);

            if (flag) {
                fetchCategory(0, "ezemakau@gmail.com");

            }

            addButton.setOnClickListener(v1 -> {
                StringBuilder constraints = new StringBuilder();
                int j = 2;
                for (; j < checkboxContainer.getChildCount(); j++) {
                    CheckBox cb = (CheckBox) checkboxContainer.getChildAt(j);
                    if (cb.isChecked()) {


                        Log.i("ConstraintsName", cb.getText().toString());

                        if (constraints.toString().isEmpty()) {

                            constraints.append(getCategoriesId(cb.getText().toString()));

                        }else {
                            constraints.append(","+getCategoriesId(cb.getText().toString()));

                        }



                    }
                }
                Log.i("Constraints", constraints.toString());
                Log.i("Checkbox count", String.valueOf(checkboxContainer.getChildCount()));
                String unit;
                unit = Objects.requireNonNull(unitName.getText()).toString();

                String capacity;
                capacity = unitCap.getText().toString();
                Log.i("Constraints", categoryModelList.toString());
                Log.i("unit", unit);
                createUnit(unit, capacity, constraints.toString()).thenAccept(result -> {
                    Log.i("Response", "Unit created successfully");
                    if (result) {
                        Log.i("Unit Creation", "Unit created successfully " + unit + " " + capacity);
                        runOnUiThread(() -> {
                            unitName.setText("");
                            unitCap.setText("");
                            Intent intent = new Intent(UnitActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        });
                   }
              });
//                Utils.getAllUnitsForCategory(2).thenAccept(unitModels -> {
//                    if (unitModels.isEmpty()) {
//                        Log.i("Unit", "No units found");
//                    }
//                    for (int i = 0; i < unitModels.size(); i++) {
//                        Log.i("Unit", unitModels.get(i).getUnitName());
//                    }
//                }).exceptionally(throwable -> {
//                    Log.e("Unit", "Error fetching units", throwable);
//                    return null;
//                });
//
          });

            // Create checkboxes dynamically and add them to the LinearLayout

            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


    }

    public void fetchCategory(int parentCategory, String email) {
        if (flag) {
            String json = "{\"useremail\":\"" + email + "\", \"parentcategory\":\"" + parentCategory + "\" }";


            MediaType jsonObject = MediaType.get("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            String apiUrl = BuildConfig.FetchCategoryEndPoint;
            RequestBody body = RequestBody.create(json, jsonObject);

            Request request = new Request.Builder()
                    .url(apiUrl)
                    .post(body)
                    .build();


            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Log.e("Category Request Method", "GET request failed", e));
                }

                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String responseData = response.body().string();
                        runOnUiThread(() -> {
                            runOnUiThread(() -> Log.e("Category Response Results", responseData));

                            try {
                                JSONObject jsonObject = new JSONObject(responseData);

                                String bodyString = jsonObject.getString("body");
                                JSONArray bodyArray = new JSONArray(bodyString);


                                runOnUiThread(() -> Log.e("Category Details Array", bodyArray.toString()));
                                parentCategories.add("");
                                for (int i = 0; i < bodyArray.length(); i++) {
                                    JSONObject itemObject = bodyArray.getJSONObject(i);

                                    CategoryModel parentCategory = new CategoryModel();
                                    parentCategory.setCategoryID(itemObject.getString("id"));
                                    parentCategory.setCategoryName(itemObject.getString("categoryname"));
                                    parentCategory.setImageUrl(itemObject.getString("icon"));

                                    categoryModelList.add(parentCategory);


                                    Log.i("Category Image Url", itemObject.getString("icon"));
                                    parentCategories.add(itemObject.getString("categoryname"));


                                }
                                runOnUiThread(() -> {
                                    if (!categoryModelList.isEmpty()) {
                                        CheckBox checkBox = new CheckBox(UnitActivity.this);
                                        checkBox.setText("Select All");
                                        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                                            if (!isChecked) {
                                                CheckBox cb = (CheckBox) checkboxContainer.getChildAt(0);
                                                cb.setChecked(false);
                                            } else {
                                                for (int i = 1; i < checkboxContainer.getChildCount(); i++) {
                                                    CheckBox cb = (CheckBox) checkboxContainer.getChildAt(i);
                                                    cb.setChecked(true);
                                                }
                                            }

                                        });
                                        checkboxContainer.addView(checkBox);
                                        CheckBox unselect = new CheckBox(UnitActivity.this);
                                        unselect.setText("Unselect All");
                                        unselect.setOnCheckedChangeListener((buttonView, isChecked) -> {
                                            if (!isChecked) {
                                                CheckBox cb = (CheckBox) checkboxContainer.getChildAt(0);
                                                cb.setChecked(true);
                                            } else {
                                                for (int i = 1; i < checkboxContainer.getChildCount(); i++) {
                                                    CheckBox cb = (CheckBox) checkboxContainer.getChildAt(i);
                                                    cb.setChecked(false);
                                                }
                                            }

                                        });
                                        checkboxContainer.addView(unselect);
                                    }
                                    for (CategoryModel category : categoryModelList) {
                                        CheckBox checkBox = new CheckBox(UnitActivity.this);
                                        checkBox.setText(category.getCategoryName());
                                        checkBox.setOnCheckedChangeListener(
                                                (buttonView, isChecked) -> {
                                                    if (!isChecked) {
                                                        CheckBox cb = (CheckBox) checkboxContainer.getChildAt(0);
                                                        cb.setChecked(false);
                                                        checkBox.setChecked(false);

                                                    }
                                                }
                                        );
                                        checkboxContainer.addView(checkBox);

                                    }

                                });


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


    public CompletableFuture<Boolean> createUnit(String unitName, String capacity, String constraints) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        String json = "{\"Unit_Name\":\"" + unitName + "\", \"Unit_Capacity\":\"" + capacity + "\", \"constraints\":\"" + constraints + "\",\"Unit_QR\":\"1\",\"unit_capacity_used\":\"0\" }";
        MediaType jsonObject = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String apiUrl = BuildConfig.AddUnitEndpoint;
        RequestBody body = RequestBody.create(json, jsonObject);
        Log.i("hell", "createUnit: " + json);
        Request request = new Request.Builder()
                .url(apiUrl)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Log.e("Unit Request Method", "GET request failed", e));
                future.complete(false);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    runOnUiThread(() -> {
                        runOnUiThread(() -> Log.e("Unit Response Results", responseData));
                        future.complete(true);
                    });
                } else {
                    runOnUiThread(() -> Log.e("Request Method", "GET request failed: " + response));
                    future.complete(false);
                }
            }
        });
        return future;
    }

    public String getCategoriesId(String name) {
        String categoryID = "";
        Log.i("getCategoriesId", "Searching for category: " + name);
        Log.i("getCategoriesId", "Number of categories: " + categoryModelList.size());
        for (int i = 0; i < categoryModelList.size(); i++) {

            if (categoryModelList.get(i).getCategoryName().equals(name)) {
                categoryID = categoryModelList.get(i).getCategoryID();


            }
        }
        Log.i("ShouldNot", "1Lets se");
        return categoryID;
    }
}


