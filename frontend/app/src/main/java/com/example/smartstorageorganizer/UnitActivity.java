package com.example.smartstorageorganizer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smartstorageorganizer.model.CategoryModel;
import com.example.smartstorageorganizer.model.ItemModel;
import com.google.android.material.textfield.TextInputEditText;

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

public class UnitActivity extends AppCompatActivity {

    boolean flag = true;
    private ArrayList<CategoryModel> categoryModelList ;
    private ConstraintLayout addButton;
    private List<String> parentCategories = new ArrayList<>();
    private LinearLayout checkboxContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_unit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {

            checkboxContainer = findViewById(R.id.checkbox_container);
            addButton = findViewById(R.id.addButton);

            if (flag) {
                fetchCategory(0, "ezemakau@gmail.com");

            }
            categoryModelList = new ArrayList<>();
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String constraints = "";
                    int i;
                    for (i = 2; i < checkboxContainer.getChildCount(); i++) {
                        CheckBox cb;
                        cb = (CheckBox) checkboxContainer.getChildAt(i);
                        if (cb.isChecked()) {
                            Log.e("Selected Category", cb.getText().toString());
                            CategoryModel category;
                            category = findCategoryByName( cb.getText().toString());
                            if (category != null) {
                                constraints += category.getCategoryID();
                            }
                        }
                    }
                    TextInputEditText unitName;
                    unitName = findViewById(R.id.unitName);
                    String unit;
                    unit = unitName.getText().toString();
                    TextInputEditText unitCap;
                    unitCap = findViewById(R.id.capacity);
                    String capacity;
                    capacity = unitCap.getText().toString();

                }
            });

            // Create checkboxes dynamically and add them to the LinearLayout

            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


    }

    public void fetchCategory(int parentCategory, String email) {
        if (flag) {
            String json = "{\"useremail\":\"" + email + "\", \"parentcategory\":\"" + Integer.toString(parentCategory) + "\" }";


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
                                                    cb.setChecked(isChecked);
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

    private CategoryModel findCategoryByName( String categoryName) {
        runOnUiThread(() ->
            Log.e("Spinner", categoryModelList.get(0).getCategoryName())
        );
        for (CategoryModel category : categoryModelList) {
            if (category.getCategoryName().equalsIgnoreCase(categoryName)) {
                return category;
            }
        }
        return null; // Return null if no category with the given name is found
    }


}