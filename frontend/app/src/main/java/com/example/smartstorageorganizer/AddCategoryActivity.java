package com.example.smartstorageorganizer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smartstorageorganizer.model.ItemModel;
import com.example.smartstorageorganizer.model.CategoryModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddCategoryActivity extends AppCompatActivity {
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private TextInputLayout subcategoryInput;
    private TextInputLayout parentCategoryInput;
    TextView spinnerHeaderText;
    public ArrayList<CategoryModel> categoryModelList = new ArrayList<>();
    private String currentSelectedParent;
    private String currentEmail;

    private List<String> parentCategories = new ArrayList<>();
    private List<String> parentCategoriesIcons = new ArrayList<>();
    private Spinner mySpinner;
    private ConstraintLayout addButton;
    private TextInputEditText parentCategoryEditText, subCategory;
    private boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            mySpinner = findViewById(R.id.mySpinner);
            radioGroup = findViewById(R.id.radioGroup);
            subcategoryInput = findViewById(R.id.subcategoryInput);
            parentCategoryInput = findViewById(R.id.parentcategoryInput);
            parentCategoryEditText = findViewById(R.id.parentcategory);
            spinnerHeaderText = findViewById(R.id.spinnerHeaderText);
            addButton = findViewById(R.id.addButton);
            subCategory = findViewById(R.id.subcategory);
            //flash sale

            if(flag) {
                FetchCategory(0, getIntent().getStringExtra("email"));
                currentSelectedParent = "";
            }

            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            addButton.setOnClickListener(v1 -> {
                Log.i("Spinner",radioButton.getText().toString());
                if(radioButton.getText().toString().equals("Parent Category")){
                    Log.i("Spinner","Inside the Parent category if statement");
                    if(validateParentForm()) {
                        Log.i("Spinner","Inside the Parent category if statement");
                        AddCategory(0, Objects.requireNonNull(parentCategoryEditText.getText()).toString(), "ezemakau@gmail.com");
                    }
                }
                else if(radioButton.getText().toString().equals("Sub Category")){
                    Log.i("Spinner","Inside the Sub category if statement");
                    if(validateSubCategoryForm()) {
                        Log.i("Spinner","Inside the Sub category validate "+currentSelectedParent);
//                            Log.i("Spinner",parentCategoryModelList.get(0).getCategoryName());

                            CategoryModel parent = findCategoryByName(categoryModelList, currentSelectedParent);
                            Log.i("Spinner", parent.getCategoryID() + " : " + parent.getCategoryName());
                            AddCategory(Integer.parseInt(parent.getCategoryID()), Objects.requireNonNull(subCategory.getText()).toString(), "ezemakau@gmail.com");
                        }
                    }
//                    AddCategory(0, "Gaming", "ezemakau@gmail.com");
            });
            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                radioButton = findViewById(checkedId);
                if(radioButton.getText().toString().equals("Sub Category")){
                    mySpinner.setVisibility(View.VISIBLE);
                    spinnerHeaderText.setVisibility(View.VISIBLE);
                    subcategoryInput.setVisibility(View.VISIBLE);
                    parentCategoryInput.setVisibility(View.GONE);
                    Toast.makeText(AddCategoryActivity.this, "Selected: " + radioButton.getText(), Toast.LENGTH_SHORT).show();
                }
                else if(radioButton.getText().toString().equals("Parent Category")){
                    mySpinner.setVisibility(View.GONE);
                    spinnerHeaderText.setVisibility(View.GONE);
                    subcategoryInput.setVisibility(View.GONE);
                    parentCategoryInput.setVisibility(View.VISIBLE);

                    Toast.makeText(AddCategoryActivity.this, "Selected: " + radioButton.getText(), Toast.LENGTH_SHORT).show();
                }
            });
            mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    // Get selected item
                    currentSelectedParent = parentView.getItemAtPosition(position).toString();

                    // Do something with the selected item
                    Log.i("Spinner","CurrentParent: "+currentSelectedParent);
                    Toast.makeText(AddCategoryActivity.this, "Selected: " + currentSelectedParent, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // Do something here if nothing is selected
                }
            });

            return insets;
        });
    }

    private CategoryModel findCategoryByName(ArrayList<CategoryModel> categories, String categoryName) {
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


    private boolean validateParentForm() {
        String parentCategoryText = Objects.requireNonNull(parentCategoryEditText.getText()).toString().trim();

        if (TextUtils.isEmpty(parentCategoryText)) {
            parentCategoryEditText.setError("Parent Category is required.");
            parentCategoryEditText.requestFocus();
            return false;
        }

        return true;
    }

    private boolean validateSubCategoryForm() {
        String subCategoryText = Objects.requireNonNull(subCategory.getText()).toString().trim();

        if (Objects.equals(currentSelectedParent, "")) {
            Toast.makeText(AddCategoryActivity.this, currentSelectedParent, Toast.LENGTH_LONG).show();
            return false;
        }
        if(TextUtils.isEmpty(subCategoryText)){
            subCategory.setError("Sub Category is required.");
            subCategory.requestFocus();
            return false;
        }

        return true;
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
                                parentCategories.add("");
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
                                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AddCategoryActivity.this, android.R.layout.simple_spinner_item, parentCategories);
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

    public void AddCategory(int ParentCategory, String CategoryName, String email)
    {
        String json = "{\"useremail\":\""+email+"\", \"parentcategory\":\""+Integer.toString(ParentCategory)+"\", \"categoryname\":\""+CategoryName+"\" }";

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.AddCategoryEndPoint;
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Log.e("Request Method", "POST request failed", e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    runOnUiThread(() -> {
                        runOnUiThread(() -> Log.i("Request Method", "POST request succeeded: " + responseData));
                        Intent intent = new Intent(AddCategoryActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(AddCategoryActivity.this, "New Category Added Successfully.", Toast.LENGTH_LONG).show();
                    });
                } else {
                    runOnUiThread(() -> Log.e("Request Method", "POST request failed: " + response.code()));
                }
            }
        });

    }
}