package com.example.smartstorageorganizer.ui.home;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
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
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.TextView;


import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.lottie.LottieAnimationView;
import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.StoragePath;
import com.amplifyframework.storage.options.StorageUploadFileOptions;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
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

public class HomeFragment extends Fragment {
    int PICK_IMAGE_MULTIPLE = 1;
    private static final int GALLERY_CODE = 1;
    private String currentSelectedCategory, currentSelectedSubcategory;
    Uri ImageUri;
    File file;
    List<String> imagesEncodedList;
    ArrayList<Uri> ChooseImageList;
    private Spinner parentSpinner, subcategorySpinner;

    LottieAnimationView fetchItemsLoader;
    RecyclerView.LayoutManager layoutManager;
    private TextView name;
    private FragmentHomeBinding binding;
    private List<ItemModel> itemModelList;
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> subAdapter;
    private ItemAdapter itemAdapter;
    private RecyclerView itemRecyclerView, category_RecyclerView;
    private String currentEmail, currentName;
    AlertDialog alertDialog;
    private List<CategoryModel> categoryModelList, subcategoryModelList;
    private CategoryAdapter categoryAdapter, subcategoryAdapter;
    private String parentCategoryId, subcategoryId;
    Button buttonNext;
    Spinner itemCategorySpinner;
    EditText itemDescription, itemName;
    ImageView itemImage;
    private boolean flag = true;
    private List<String> parentCategories = new ArrayList<>();
    private List<String> subCategories = new ArrayList<>();
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        getDetails().thenAccept(getDetails->{
            Log.i("AuthDemo", "User is signed in");
        });

        if(flag) {
//            FetchCategory(0, "ezemakau@gmail.com");
        }

        FloatingActionButton addItemButton = root.findViewById(R.id.addItemButton);
        itemRecyclerView = root.findViewById(R.id.item_rec);
        fetchItemsLoader = root.findViewById(R.id.fetchItemsLoader);
        category_RecyclerView = root.findViewById(R.id.category_rec);
        name = root.findViewById(R.id.name);

        category_RecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        categoryModelList = new ArrayList<>();
        subcategoryModelList = new ArrayList<>();

        categoryAdapter = new CategoryAdapter(requireActivity(), categoryModelList);
        subcategoryAdapter = new CategoryAdapter(requireActivity(), subcategoryModelList);
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

                                itemModelList.add(item);
                                itemAdapter.notifyDataSetChanged();

                                requireActivity().runOnUiThread(() -> Log.e("Item Details", item.getItemImage()));
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
                        FetchCategory(0, currentEmail);
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
        itemImage  = dialogView.findViewById(R.id.item_image);
        itemName = dialogView.findViewById(R.id.item_name);
        itemDescription = dialogView.findViewById(R.id.item_description);
        itemCategorySpinner = dialogView.findViewById(R.id.item_category_spinner);
        buttonNext = dialogView.findViewById(R.id.button_add_item);

        // Create the AlertDialog
        alertDialog = builder.create();

        // Set click listener to open image picker
        itemImage.setOnClickListener(v -> OpenGallery());

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
                UploadItemImage(file);
//                postAddItem(itemImage1, name, description, 0);

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
//                    fetchCategorySuggestions(name, description, currentEmail, itemCategorySpinner);

                    // Post the item
                    UploadItemImage(file);

                } catch (Exception e) {
                    // Log the exception
                    Log.e("AddItem", "Error in onClick", e);
                }
            });
        } else {
            Log.e("AddItem", "One or more UI elements are not initialized");
        }

        // Show the AlertDialog
        alertDialog.show();
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
                            parentCategoryId = categoryObject.getString("id");

                            // Extract the subcategory name
                            JSONObject subcategoryObject = jsonObject.getJSONObject("subcategory");
                            String subcategory = subcategoryObject.getString("categoryname");
                            subcategoryId = subcategoryObject.getString("id");

                            // Create a list of categories (with only one category in this case)
                            List<String> categories = new ArrayList<>();
                            categories.add(category+ " - "+subcategory);
                            categories.add("Add Custom Category");  //Add other

                            // Populate the Spinner
                            ArrayAdapter<String> adapter2 = new ArrayAdapter<>(requireActivity(),
                                    android.R.layout.simple_spinner_item, categories);
                            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            itemCategorySpinner.setAdapter(adapter2);

                            // Set the item selected listener
                            itemCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                            {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                                {
                                    if (categories.get(position).equals("Add Custom Category"))
                                    {
                                        // Show dialog to input custom category
                                        showCustomCategoryDialog(categories, adapter2);
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

            private void showCustomCategoryDialog(List<String> categories, ArrayAdapter<String> adapter2) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

//                builder.setView(R.layout.add_custom_category_popup);

                LayoutInflater inflater = requireActivity().getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.add_custom_category_popup, null);

                parentSpinner = dialogView.findViewById(R.id.ParentSpinner);
                subcategorySpinner = dialogView.findViewById(R.id.subcategorySpinner);

                builder.setView(dialogView);
                String[] categoriesArray = {"Category 1", "Category 2", "Category 3", "Category 4", "Category 1", "Category 2", "Category 3", "Category 4"};
                adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, parentCategories);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                parentSpinner.setAdapter(adapter);

                parentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        // Get selected item
                        flag = true;
                        currentSelectedCategory = parentView.getItemAtPosition(position).toString();
                        String parentCategory = "";
                        parentCategory = findCategoryByName(currentSelectedCategory);

                        if(!Objects.equals(parentCategory, "")){
                            parentCategoryId = parentCategory;
                            FetchSubcategory(Integer.parseInt(parentCategory), currentEmail);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // Do something here if nothing is selected
                    }
                });
                subcategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        currentSelectedSubcategory = parentView.getItemAtPosition(position).toString();
                        String subCategory = findCategoryByName(currentSelectedSubcategory);
                        if(!Objects.equals(subCategory, "")) {
                            subcategoryId = subCategory;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // Do something here if nothing is selected
                    }
                });
                builder.setPositiveButton("Add", (dialog, which) ->
                {
                    categories.add(categories.size() - 1, currentSelectedCategory+" - "+currentSelectedSubcategory); // Add before "Add Custom Category"
                    adapter2.notifyDataSetChanged();
                    itemCategorySpinner.setSelection(categories.indexOf(currentSelectedCategory+" - "+currentSelectedSubcategory));

                    Toast.makeText(requireActivity(), "Selected: " + currentSelectedCategory+" - "+currentSelectedSubcategory, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

                builder.show();
            }
        });


    }

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

    private void postAddItem(String item_image, String item_name, String description, int category, int parentCategory) {
        // Provide default values for the remaining attributes
        int subCategory = 0;
        String colourcoding = "default";
        String barcode = "default";
        String qrcode = "default";
        int quantity = 1;
        String location = "default";
        String email = currentEmail;
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

    private String findCategoryByName(String categoryName) {
        for (CategoryModel category : categoryModelList) {
            if (category.getCategoryName().equalsIgnoreCase(categoryName)) {
                requireActivity().runOnUiThread(() -> {
                    Log.e("Filtering: ", category.getCategoryID());
                });
                return category.getCategoryID();
            }
        }
        return ""; // Return null if no category with the given name is found
    }

    public void FetchCategory(int ParentCategory, String email)
    {
//        if(flag) {
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
                                    subCategories.add(itemObject.getString("categoryname"));


                                }
                                requireActivity().runOnUiThread(() -> {
                                    adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, parentCategories);
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                                    parentSpinner.setAdapter(adapter);
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
//        }

    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        //startActivityForResult(galleryIntent, GalleryPick);
        startActivityForResult(Intent.createChooser(galleryIntent,"Select Picture"), PICK_IMAGE_MULTIPLE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK  && data != null) {

            imagesEncodedList = new ArrayList<>();

            if (data.getData() != null) {
                ImageUri = data.getData();
                itemImage.setImageURI(ImageUri);
                BitmapDrawable drawable = (BitmapDrawable) itemImage.getDrawable();
                Bitmap bitmap = drawable.getBitmap();

                // Create a file to save the image
                file = new File(requireActivity().getCacheDir(), "image.jpeg");
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            else if (data.getClipData() != null) {
                ImageUri = data.getClipData().getItemAt(0).getUri();
                itemImage.setImageURI(ImageUri);
                BitmapDrawable drawable = (BitmapDrawable) itemImage.getDrawable();
                Bitmap bitmap = drawable.getBitmap();

                // Create a file to save the image
                file = new File(requireActivity().getCacheDir(), "image.jpeg");
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void UploadItemImage(File ParentCategoryImage)
    {
        StorageUploadFileOptions options = StorageUploadFileOptions.builder()
                .contentType("image/png") // Adjust based on file type
                .build();
        long Time = System.nanoTime();
        String key= String.valueOf(Time);
        String Path="public/ItemImages/"+key+".png";
        Amplify.Storage.uploadFile(
                StoragePath.fromString(Path),
                ParentCategoryImage,
                options,
                result -> Log.i("MyAmplifyApp", "Successfully uploaded: " + GetObjectUrl(key)),
                storageFailure -> {Log.e("MyAmplifyApp", "Upload failed", storageFailure);}
        );
    }
    public String GetObjectUrl(String key)
    {
        String url = "https://smart-storage-f0629f0176059-staging.s3.eu-north-1.amazonaws.com/public/ItemImages/"+key+".png";
        postAddItem(url, itemName.getText().toString().trim(), itemDescription.getText().toString().trim(), Integer.parseInt(subcategoryId), Integer.parseInt(parentCategoryId));
        Toast.makeText(requireActivity(), "Suggested: "+itemCategorySpinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();

        return "https://smart-storage-f0629f0176059-staging.s3.eu-north-1.amazonaws.com/public/ItemImages/"+key+".png";
    }

    public void FetchSubcategory(int ParentCategory, String email)
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
//                                subCategories.add("");
                                subCategories.clear();
                                for (int i = 0; i < bodyArray.length(); i++) {
                                    JSONObject itemObject = bodyArray.getJSONObject(i);

                                    CategoryModel parentCategory = new CategoryModel();
                                    parentCategory.setCategoryID(itemObject.getString("id"));
                                    parentCategory.setCategoryName(itemObject.getString("categoryname"));
                                    parentCategory.setImageUrl(itemObject.getString("icon"));

                                    categoryModelList.add(parentCategory);
                                    subcategoryAdapter.notifyDataSetChanged();

                                    Log.i("Category Image Url", itemObject.getString("categoryname"));
                                    subCategories.add(itemObject.getString("categoryname"));


                                }
                                requireActivity().runOnUiThread(() -> {
                                    subAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, subCategories);
                                    subAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    subcategorySpinner.setAdapter(subAdapter);

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