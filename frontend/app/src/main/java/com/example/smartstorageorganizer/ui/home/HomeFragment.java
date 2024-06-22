package com.example.smartstorageorganizer.ui.home;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.TextView;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.lottie.LottieAnimationView;
import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.core.Amplify;
import com.bumptech.glide.Glide;
import com.example.smartstorageorganizer.Adapters.CategoryAdapter;
import com.example.smartstorageorganizer.Adapters.ItemAdapter;
import com.example.smartstorageorganizer.AddCategoryActivity;
import com.example.smartstorageorganizer.BuildConfig;
import com.example.smartstorageorganizer.HomeActivity;
import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.UnitActivity;
import com.example.smartstorageorganizer.databinding.FragmentHomeBinding;
import com.example.smartstorageorganizer.model.ItemModel;
import com.example.smartstorageorganizer.model.CategoryModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;

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

public class HomeFragment extends Fragment {

    LottieAnimationView fetchItemsLoader;
    RecyclerView.LayoutManager layoutManager;
    private TextView name;
    private FragmentHomeBinding binding;
    private List<ItemModel> itemModelList;
    private ItemAdapter itemAdapter;
    private RecyclerView itemRecyclerView;
    private String currentEmail, currentName;
    AlertDialog alertDialog;
    private RecyclerView category_RecyclerView;
    private List<CategoryModel> categoryModelList;
    private CategoryAdapter categoryAdapter;

    private ShapeableImageView itemImageView;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView itemImage; // Declare as a class member
    private Uri imageUri;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private MaterialCardView addCategory;
//    private MaterialCardView addCategory;
    private boolean flag = true;
    private List<String> parentCategories = new ArrayList<>();
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        getDetails().thenAccept(getDetails->{
            Log.i("AuthDemo", "User is signed in");
        });

        if(flag) {
//                parentCategoryModelList = new ArrayList<>();
            FetchCategory(0, "ezemakau@gmail.com");
        }

        FloatingActionButton addItemButton = root.findViewById(R.id.addItemButton);
        itemRecyclerView = root.findViewById(R.id.item_rec);
        fetchItemsLoader = root.findViewById(R.id.fetchItemsLoader);
        category_RecyclerView = root.findViewById(R.id.category_rec);
        name = root.findViewById(R.id.name);
//        addCategory = root.findViewById(R.id.addCategory);

        category_RecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        categoryModelList = new ArrayList<>();
        //flash sale

        categoryAdapter = new CategoryAdapter(requireActivity(), categoryModelList);
        category_RecyclerView.setAdapter(categoryAdapter);

        itemRecyclerView.setHasFixedSize(true);
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        itemRecyclerView.setLayoutManager(layoutManager);

        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddButtonPopup();
            }
        });

//        itemRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        itemModelList = new ArrayList<>();
        itemAdapter = new ItemAdapter(requireActivity(), itemModelList);
        itemRecyclerView.setAdapter(itemAdapter);

        return root;
    }

    public void FetchByEmail(String email)
    {
        String json = "{\"email\":\""+email+"\" }";

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
                requireActivity().runOnUiThread(() -> Log.e("Request Method", "GET request failed", e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    requireActivity().runOnUiThread(() -> {
                        requireActivity().runOnUiThread(() -> Log.e("Response Results", responseData));

//                        String jsonString = "{\"statusCode\": 200, \"status\": \"Fetch Query Successful\", \"body\": \"[{\\\"item_id\\\": 25, \\\"item_name\\\": \\\"Joystick\\\", \\\"description\\\": \\\"Gaming\\\", \\\"colourcoding\\\": \\\"Yellow\\\", \\\"barcode\\\": \\\"asdffd\\\", \\\"qrcode\\\": \\\"00111100\\\", \\\"quanity\\\": 1, \\\"location\\\": \\\"Centinary\\\", \\\"email\\\": \\\"gayol59229@fincainc.com\\\"}, {\\\"item_id\\\": 26, \\\"item_name\\\": \\\"Book\\\", \\\"description\\\": \\\"Textbook\\\", \\\"colourcoding\\\": \\\"Yellow\\\", \\\"barcode\\\": \\\"asdffd\\\", \\\"qrcode\\\": \\\"00111100\\\", \\\"quanity\\\": 1, \\\"location\\\": \\\"Centinary\\\", \\\"email\\\": \\\"gayol59229@fincainc.com\\\"}]\"}";

                        try {
                            JSONObject jsonObject = new JSONObject(responseData);

                            // Parse the main fields
                            int statusCode = jsonObject.getInt("statusCode");
                            String status = jsonObject.getString("status");

                            // Parse the body field (which is a string containing a JSON array)
                            String bodyString = jsonObject.getString("body");
                            JSONArray bodyArray = new JSONArray(bodyString);

                            // Create a list to hold the items
                            List<ItemModel> items = new ArrayList<>();

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

                                requireActivity().runOnUiThread(() -> Log.e("Item Details", item.getItem_image()));
                            }

                            requireActivity().runOnUiThread(() -> {
                                fetchItemsLoader.setVisibility(View.GONE);
                                itemRecyclerView.setVisibility(View.VISIBLE);
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });

                } else {
                    requireActivity().runOnUiThread(() -> Log.e("Request Method", "GET request failed: " + response));
                }
            }
        });
    }

    private CompletableFuture<Boolean> getDetails()
    {
        CompletableFuture<Boolean> future=new CompletableFuture<>();

        Amplify.Auth.fetchUserAttributes(
                attributes -> {

                    for (AuthUserAttribute attribute : attributes) {
                        switch (attribute.getKey().getKeyString()) {
                            case "email":
                                currentEmail = attribute.getValue();
                                break;
                            case "name":
                                currentName = attribute.getValue();
                                break;
                        }
                    }
                    Log.i("progress","User attributes fetched successfully");
                    requireActivity().runOnUiThread(() -> {
                        name.setText("Hi "+currentName);
                        FetchByEmail(currentEmail);
                    });
                    future.complete(true);
                },
                error -> Log.e("AuthDemo", "Failed to fetch user attributes.", error)

        );
        return future;
    }




    private void showAddItemPopup()
    {
        // Create an AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Inflate the dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_item_popup, null);
        builder.setView(dialogView);

        // Get the EditTexts and Button from the dialog layout
        ImageView itemImage  = dialogView.findViewById(R.id.item_image);
        EditText itemName = dialogView.findViewById(R.id.item_name);
        EditText itemDescription = dialogView.findViewById(R.id.item_description);
        Spinner itemCategorySpinner = dialogView.findViewById(R.id.item_category_spinner);
        Button buttonNext = dialogView.findViewById(R.id.button_add_item);

        // Create the AlertDialog
        alertDialog = builder.create();

        // Set click listener to open image picker
        itemImage.setOnClickListener(v -> openImagePicker());

        // Set the button click listener
        buttonNext.setOnClickListener(v -> {
            String itemImage1 =
                    "https://assets.goal.com/images/v3/blt31a85dcdf5b9e95f/MOFeng.jpg?auto=webp&format=pjpg&width=3840&quality=60"; // default image url for now
            String name = itemName.getText().toString().trim();
            String description = itemDescription.getText().toString().trim();
//            int category = itemCategorySpinner.getSelectedItem().toString();

            // Check if image URI, name, description, and category are not empty
            if (!name.isEmpty() && !description.isEmpty()) {
                // Assuming postAddItem is a method in your activity or fragment
                postAddItem(itemImage1, name, description, 0);

            } else {
                // Handle empty fields or missing image
                Toast.makeText(requireActivity(), "Filled can't be empty", Toast.LENGTH_SHORT).show();
            }

        });

        // Add TextWatchers to EditText fields
        TextWatcher textWatcher = new TextWatcher()
        {
            private Handler handler = new Handler();
            private Runnable runnable;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                // Cancel the previous request if the user continues typing
                if (runnable != null)
                {
                    handler.removeCallbacks(runnable);
                }

                // Delay the request to fetch category suggestions
                runnable = () ->
                {
                    String name = itemName.getText().toString().trim();
                    String description = itemDescription.getText().toString().trim();

                    // Check if both name and description are filled
                    if (!name.isEmpty() && !description.isEmpty())
                    {
                        Spinner itemCategorySpinner1 = dialogView.findViewById(R.id.item_category_spinner);
                        fetchCategorySuggestions(name, description, currentEmail, itemCategorySpinner1);
                    }
                };

                // Execute the runnable after a delay (e.g., 1000 milliseconds or 1 second)
                handler.postDelayed(runnable, 1000);
            }

            @Override
            public void afterTextChanged(Editable s) {
//                handler.postDelayed(fetchRunnable, 500); // Delay of 500 milliseconds
            }
        };

        itemName.addTextChangedListener(textWatcher);
        itemDescription.addTextChangedListener(textWatcher);

        // Ensure all UI elements are initialized
        if (buttonNext != null && itemName != null && itemDescription != null && itemCategorySpinner != null) {
            // Set the button click listener
            buttonNext.setOnClickListener(v -> {
                try {
                    // Get the text from EditText and Spinner
                    String itemImageS = "https://assets.goal.com/images/v3/blt31a85dcdf5b9e95f/MOFeng.jpg?auto=webp&format=pjpg&width=3840&quality=60"; // Replace with your default image URI or get from user input

                    String name = itemName.getText().toString().trim();
                    String description = itemDescription.getText().toString().trim();
                    String category = itemCategorySpinner.getSelectedItem().toString();

                    // Log the values for debugging
                    Log.d("AddItem", "Name: " + name);
                    Log.d("AddItem", "Description: " + description);
                    Log.d("AddItem", "Category: " + category);

                    // Ensure none of the values are empty
                    if (name.isEmpty() || description.isEmpty() || category.isEmpty()) {
                        Log.e("AddItem", "One or more fields are empty");
//                        Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Fetch category suggestions
                    fetchCategorySuggestions(name, description, currentEmail, itemCategorySpinner);

                    // Post the item
                    postAddItem(itemImageS, name, description, 0);

                } catch (Exception e) {
                    // Log the exception
                    Log.e("AddItem", "Error in onClick", e);
//                    Toast.makeText(getApplicationContext(), "An error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e("AddItem", "One or more UI elements are not initialized");
        }

        // Show the AlertDialog
        alertDialog.show();
    }

    private void openImagePicker()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void fetchCategorySuggestions(String name, String description, String email, Spinner itemCategorySpinner)
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

                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireActivity(), "Failed-03 to fetch category suggestions", Toast.LENGTH_SHORT).show()
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
                    requireActivity().runOnUiThread(() ->
                    {
                        try
                        {
                            if (responseData.isEmpty()) {
                                throw new JSONException("Empty or null response data");
                            }

                            // Parse the response as a JSON object
                            JSONObject jsonObject = new JSONObject(responseData);

                            // Extract the category name
                            JSONObject categoryObject = jsonObject.getJSONObject("category");
                            String category = categoryObject.getString("categoryname");

                            // Extract the subcategory name
                            JSONObject subcategoryObject = jsonObject.getJSONObject("subcategory");
                            String subcategory = subcategoryObject.getString("categoryname");


                            // Create a list of categories (with only one category in this case)
                            List<String> categories = new ArrayList<>();
                            categories.add(category+ " - "+subcategory);
                            categories.add("Add Custom Category");  //Add other

                            // Populate the Spinner
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(),
                                    android.R.layout.simple_spinner_item, categories);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            itemCategorySpinner.setAdapter(adapter);

                            // Set the item selected listener
                            itemCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                            {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                                {
                                    if (categories.get(position).equals("Add Custom Category"))
                                    {
                                        // Show dialog to input custom category
                                        showCustomCategoryDialog(categories, adapter);
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("JSON Exception", e.toString());
                            Toast.makeText(requireActivity(), "Failed-01 to parse category suggestions", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireActivity(), "Failed-02 to fetch category suggestions", Toast.LENGTH_SHORT).show()
                    );
                }
            }

            private void showCustomCategoryDialog(List<String> categories, ArrayAdapter<String> adapter) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setTitle("Add Custom Category and Subcategory");

//                final EditText input = new EditText(requireActivity());
//                input.setInputType(InputType.TYPE_CLASS_TEXT);

                // Set up the input fields
                LinearLayout layout = new LinearLayout(requireActivity());
                layout.setOrientation(LinearLayout.VERTICAL);

                // Input field for category
                final EditText inputCategory = new EditText(requireActivity());
                inputCategory.setHint("Category Name");
                layout.addView(inputCategory);

                // Input field for subcategory
                final EditText inputSubCategory = new EditText(requireActivity());
                inputSubCategory.setHint("Subcategory Name");
                layout.addView(inputSubCategory);

                builder.setView(layout);

                builder.setPositiveButton("Add", (dialog, which) ->
                {
                    String category = inputCategory.getText().toString().trim();
                    String subcategory = inputSubCategory.getText().toString().trim();

                    if (!category.isEmpty() && !subcategory.isEmpty()) {
                        String combinedCategory = category + " - " + subcategory;
                        categories.add(categories.size() - 1, combinedCategory); // Add before "Add Custom Category"
                        adapter.notifyDataSetChanged();
                        itemCategorySpinner.setSelection(categories.indexOf(combinedCategory));
                    } else {
                        Toast.makeText(requireActivity(), "Please enter both category and subcategory", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

                builder.show();
            }
        });


    }

    private void postAddItem(String item_image, String item_name, String description, int category) {
        // Provide default values for the remaining attributes
        int subCategory = 0;
        String colourcoding = "default";
        String barcode = "default";
        String qrcode = "default";
        int quantity = 1;
        String location = "default";
        String email = currentEmail;


    private void showAddButtonPopup() {
        // Create an AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Inflate the dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_button_popup, null);
        builder.setView(dialogView);

        // Get the EditTexts and Button from the dialog layout
        Button addItemButton = dialogView.findViewById(R.id.addItemButton);
        Button addCategoryButton = dialogView.findViewById(R.id.addCategoryButton);
        Button addUnitButton = dialogView.findViewById(R.id.addUnitButton);

        // Create the AlertDialog
        alertDialog = builder.create();

        // Set the button click listener
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddItemPopup();
            }
        });
        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddCategoryActivity.class);
                intent.putExtra("email", currentEmail);
                startActivity(intent);
            }
        });
        addUnitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UnitActivity.class);
                startActivity(intent);
            }
        });

        // Show the AlertDialog
        alertDialog.show();
    }

    private void postAddItem(String item_name, String description, String colourcoding, String barcode, String qrcode, String quantity, String location, String email )  {
        String json = "{\"item_name\":\""+item_name+"\",\"description\":\""+description+"\" ,\"colourcoding\":\""+colourcoding+"\",\"barcode\":\""+barcode+"\",\"qrcode\":\""+qrcode+"\",\"quanity\":"+quantity+",\"location\":\""+location+"\",\"email\":\""+email+"\" }";
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.AddItemEndPoint;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("item_image", item_image);
            jsonObject.put("item_name", item_name);
            jsonObject.put("description", description);
            jsonObject.put("category", category);
            jsonObject.put("sub_category", subCategory);
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
                requireActivity().runOnUiThread(() -> Log.e("2Request Method", "POST request failed", e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    Log.i("Post Data", responseData);
                    requireActivity().runOnUiThread(() -> {
                        Log.i("Request Method", "POST request succeeded: " + responseData);
                        Intent intent = new Intent(getActivity(), HomeActivity.class);
                        startActivity(intent);
                        requireActivity().finish();
                    });
                } else {
                    requireActivity().runOnUiThread(() -> Log.e("Request Method", "POST request failed: " + response.code()));
                }
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
                    requireActivity().runOnUiThread(() -> Log.e("Category Request Method", "GET request failed", e));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String responseData = response.body().string();
                        requireActivity().runOnUiThread(() -> {
                            requireActivity().runOnUiThread(() -> Log.e("Category Response Results", responseData));

                            try {
                                JSONObject jsonObject = new JSONObject(responseData);

                                int statusCode = jsonObject.getInt("statusCode");
                                String status = jsonObject.getString("status");

                                String bodyString = jsonObject.getString("body");
                                JSONArray bodyArray = new JSONArray(bodyString);

                                List<ItemModel> items = new ArrayList<>();

                                requireActivity().runOnUiThread(() -> Log.e("Category Details Array", bodyArray.toString()));
                                parentCategories.add("");
                                for (int i = 0; i < bodyArray.length(); i++) {
                                    JSONObject itemObject = bodyArray.getJSONObject(i);

                                    CategoryModel parentCategory = new CategoryModel();
                                    parentCategory.setCategoryID(itemObject.getString("id"));
                                    parentCategory.setCategoryName(itemObject.getString("categoryname"));
                                    parentCategory.setImageUrl(itemObject.getString("icon"));

                                    categoryModelList.add(parentCategory);
                                    categoryAdapter.notifyDataSetChanged();

                                    Log.i("Category Image Url", itemObject.getString("icon"));
                                    parentCategories.add(itemObject.getString("categoryname"));


                                }
                                requireActivity().runOnUiThread(() -> {
                                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, parentCategories);
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                                    mySpinner.setAdapter(adapter);
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
                        requireActivity().runOnUiThread(() -> Log.e("Request Method", "GET request failed: " + response));
                    }
                }
            });
            flag = false;
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}