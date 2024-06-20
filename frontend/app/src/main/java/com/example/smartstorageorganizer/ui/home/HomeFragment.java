package com.example.smartstorageorganizer.ui.home;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.lottie.LottieAnimationView;
import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.core.Amplify;
import com.example.smartstorageorganizer.Adapters.ItemAdapter;
import com.example.smartstorageorganizer.AddCategoryActivity;
import com.example.smartstorageorganizer.BuildConfig;
import com.example.smartstorageorganizer.HomeActivity;
import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.databinding.FragmentHomeBinding;
import com.example.smartstorageorganizer.model.ItemModel;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
    private FragmentHomeBinding binding;
    private List<ItemModel> itemModelList;
    private ItemAdapter itemAdapter;
    private RecyclerView itemRecyclerView;
    private String currentEmail;
    AlertDialog alertDialog;

    private MaterialCardView addCategory;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        getDetails().thenAccept(getDetails->{
            Log.i("AuthDemo", "User is signed in");
        });

        FloatingActionButton addItemButton = root.findViewById(R.id.addItemButton);
        itemRecyclerView = root.findViewById(R.id.item_rec);
        fetchItemsLoader = root.findViewById(R.id.fetchItemsLoader);
        addCategory = root.findViewById(R.id.addCategory);
        itemRecyclerView.setHasFixedSize(true);
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        itemRecyclerView.setLayoutManager(layoutManager);

        addItemButton.setOnClickListener(v -> showAddItemPopup());

        addCategory.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddCategoryActivity.class);
            startActivity(intent);
        });

//        itemRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        itemModelList = new ArrayList<>();
        //flash sale

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

    private CompletableFuture<Boolean> getDetails() {
        CompletableFuture<Boolean> future=new CompletableFuture<>();

        Amplify.Auth.fetchUserAttributes(
                attributes -> {

                    for (AuthUserAttribute attribute : attributes) {
                        switch (attribute.getKey().getKeyString()) {
                            case "email":
                                currentEmail = attribute.getValue();
                                break;
                        }




                    }
                    Log.i("progress","User attributes fetched successfully");
                    requireActivity().runOnUiThread(() -> {
                        FetchByEmail(currentEmail);
                    });
                    future.complete(true);
                },
                error -> Log.e("AuthDemo", "Failed to fetch user attributes.", error)

        );
        return future;
    }

    private void showAddItemPopup() {
        // Create an AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Inflate the dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_item_popup, null);
        builder.setView(dialogView);

        // Get the EditTexts and Button from the dialog layout
        EditText itemName = dialogView.findViewById(R.id.item_name);
        EditText itemDescription = dialogView.findViewById(R.id.item_description);
        Spinner itemCategorySpinner = dialogView.findViewById(R.id.item_category_spinner);
        Button buttonNext = dialogView.findViewById(R.id.button_add_item);

        // Fetch data from the database to populate the spinner
        fetchCategories(itemCategorySpinner);

        // Create the AlertDialog
        alertDialog = builder.create();

        // Set the button click listener
        buttonNext.setOnClickListener(v -> {
            String name = itemName.getText().toString().trim();
            String description = itemDescription.getText().toString().trim();
            String category = itemCategorySpinner.getSelectedItem().toString();
            postAddItem(name, description, category, "Yellow", "asdffd",  "00111100", "1", "Centinary", currentEmail);
        });

        // Show the AlertDialog
        alertDialog.show();
    }


    private void fetchCategories(Spinner spinner) {

        // API call to fetch categories from the database
        String json = "{\"email\":\""+currentEmail+"\" }";
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
                requireActivity().runOnUiThread(() -> Log.e("Request Method", "GET request failed", e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    requireActivity().runOnUiThread(() -> {
                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            String bodyString = jsonObject.getString("body");
                            JSONArray bodyArray = new JSONArray(bodyString);

                            List<String> categories = new ArrayList<>();
                            for (int i = 0; i < bodyArray.length(); i++) {
                                JSONObject categoryObject = bodyArray.getJSONObject(i);
                                String category = categoryObject.getString("category_name");
                                categories.add(category);
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(),
                                    android.R.layout.simple_spinner_item, categories);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(adapter);

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

    private void postAddItem(String item_name, String description, String colourcoding, String barcode, String qrcode, String quantity, String location, String email, String currentEmail)  {
        String json = "{\"item_name\":\""+item_name+"\",\"description\":\""+description+"\" ,\"colourcoding\":\""+colourcoding+"\",\"barcode\":\""+barcode+"\",\"qrcode\":\""+qrcode+"\",\"quanity\":"+quantity+",\"location\":\""+location+"\",\"email\":\""+email+"\" }";
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.AddItemEndPoint;
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> Log.e("Request Method", "POST request failed", e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    requireActivity().runOnUiThread(() -> {
                        requireActivity().runOnUiThread(() -> Log.i("Request Method", "POST request succeeded: " + responseData));
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}