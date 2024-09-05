package com.example.smartstorageorganizer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.smartstorageorganizer.adapters.RecentAdapter;
import com.example.smartstorageorganizer.model.ItemModel;
import com.example.smartstorageorganizer.model.SearchResult;
import com.example.smartstorageorganizer.model.TokenManager;
import com.google.android.material.textfield.TextInputEditText;

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

public class AddItemActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    private RecentAdapter adapter;
    private List<ItemModel> searchResults;
    private TextInputEditText name, description;
    private Button noButton;
    AlertDialog alertDialog;
    ProgressDialog progressDialog;
    private LottieAnimationView loader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_item);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        description = findViewById(R.id.inputDescription);
        name = findViewById(R.id.inputName);

        findViewById(R.id.nextLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSimilarItemPopup();
            }
        });
    }

    private void showSimilarItemPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.suggested_items_popup, null);
        builder.setView(dialogView);

        loader = dialogView.findViewById(R.id.loader);
        recyclerView = dialogView.findViewById(R.id.similar_items_recycler_view);
        noButton = dialogView.findViewById(R.id.button_no);

        loader.setVisibility(View.VISIBLE);
        noButton.setVisibility(View.GONE);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchResults = new ArrayList<>();

        adapter = new RecentAdapter(this, searchResults);
        recyclerView.setAdapter(adapter);

        SearchForItem(name.getText().toString().trim(), "*", "*");

        alertDialog = builder.create();
        alertDialog.show();
    }

    public CompletableFuture<List<SearchResult>> SearchForItem(String target, String parentcategoryid, String subcategoryid) {
        CompletableFuture<List<SearchResult>> future = new CompletableFuture<>();
//        resultsNotFound.setVisibility(View.GONE);
//        loader.setVisibility(View.VISIBLE);
        searchResults.clear();
        adapter.notifyDataSetChanged();

        String json = "{\"target\":\"" + target + "\", \"parentcategoryid\":\"" + parentcategoryid + "\", \"subcategoryid\":\"" + subcategoryid + "\" }";
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.SearchEndPoint;
        RequestBody body = RequestBody.create(json, JSON);

        TokenManager.getToken().thenAccept(results-> {
            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", results)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                    future.completeExceptionally(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            String bodyString = jsonObject.getString("body");
                            JSONArray bodyArray = new JSONArray(bodyString);
                            runOnUiThread(() -> Log.e("Search Results Body Array", bodyArray.toString()));

                            for (int i = 0; i < 4; i++) {
                                JSONObject itemObject = bodyArray.getJSONObject(i);

                                JSONObject itemObj = new JSONObject(itemObject.getString("_source"));

                                ItemModel item = new ItemModel();
                                item.setItemId(itemObj.getString("item_id"));
                                item.setItemName(itemObj.getString("item_name"));
                                item.setDescription(itemObj.getString("description"));
                                item.setColourCoding(itemObj.getString("colourcoding"));
                                item.setBarcode(itemObj.getString("barcode"));
                                item.setQrcode(itemObj.getString("qrcode"));
                                item.setQuantity(itemObj.getString("quanity"));
                                item.setLocation(itemObj.getString("location"));
                                item.setEmail(itemObj.getString("email"));
                                item.setItemImage(itemObj.getString("item_image"));
                                item.setParentCategoryId(itemObj.getString("parentcategoryid"));
                                item.setSubCategoryId(itemObj.getString("subcategoryid"));
//                            item.setCreatedAt(itemObject.getString("created_at"));

                                searchResults.add(item);
                            }
                            runOnUiThread(() -> {
                                adapter.notifyDataSetChanged();
//                                progressDialog.dismiss();
                                loader.setVisibility(View.GONE);
                                noButton.setVisibility(View.VISIBLE);
//                                sortBySpinner.setVisibility(View.VISIBLE);
//                                loader.setVisibility(View.GONE);
                                if(searchResults.isEmpty()){
//                                    resultsNotFound.setVisibility(View.VISIBLE);
                                }
                            });

                        }catch (JSONException e) {
                            if(searchResults.isEmpty()){
                                loader.setVisibility(View.GONE);
                                noButton.setVisibility(View.VISIBLE);
//                                progressDialog.dismiss();
//                                resultsNotFound.setVisibility(View.VISIBLE);
                            }
                            throw new RuntimeException(e);
                        }

                    } else {
//                        loader.setVisibility(View.GONE);
                        future.completeExceptionally(new IOException("Search request failed: " + response.code()));
                    }
                }
            });

        }).exceptionally(ex -> {
//            loader.setVisibility(View.GONE);
            Log.e("TokenError", "Failed to get user token", ex);
            return null;
        });

        return future;
    }
}