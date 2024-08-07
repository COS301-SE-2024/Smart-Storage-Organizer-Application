package com.example.smartstorageorganizer;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.core.Amplify;
import com.example.smartstorageorganizer.adapters.SearchAdapter;
import com.example.smartstorageorganizer.model.ItemModel;
import com.example.smartstorageorganizer.model.SearchResult;
import com.example.smartstorageorganizer.utils.Utils;

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

public class SearchActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<ItemModel> searchResults;
    private EditText searchView;
    private ImageButton searchButton;
    private RecyclerView searchResultsRecyclerView;

    private SearchAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchView = findViewById(R.id.searchView);
        searchButton = findViewById(R.id.searchButton);
        searchResultsRecyclerView = findViewById(R.id.search_results_recycler_view);

        // Set up the RecyclerView (adapter, layout manager, etc.)
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchResults = new ArrayList<>();

        adapter = new SearchAdapter(searchResults, this);
        searchResultsRecyclerView.setAdapter(adapter);
//
        searchButton.setOnClickListener(v -> {
            String query = searchView.getText().toString();
            if (!query.isEmpty()) {
                Toast.makeText(SearchActivity.this, "Search query: " + query, Toast.LENGTH_SHORT).show();
                SearchForItem(query, "*", "*");
                searchResults.clear();
//                searchResults.add(new ItemModel());
//                adapter.notifyDataSetChanged();
//                SearchForItem(query, "", ""); // Adjust parameters as needed
            } else {
                Toast.makeText(SearchActivity.this, "Please enter a search query", Toast.LENGTH_SHORT).show();
            }
        });

//        searchView.setOnClickListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                // Handle the search query submission
//                Toast.makeText(SearchActivity.this, "Search query: " + query, Toast.LENGTH_SHORT).show();
////                searchResults.add(new SearchResult("Title", "Description"));
//                searchResults.clear();
//                adapter.notifyDataSetChanged();
//                SearchForItem(query, "9", "*"); // Adjust parameters as needed
//
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                // Handle text change event
//                return false;
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        assert searchView != null;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle the search query submission
                Toast.makeText(SearchActivity.this, "Search query: " + query, Toast.LENGTH_SHORT).show();
                searchResults.clear();
                SearchForItem(query, "", ""); // Adjust parameters as needed
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle text change event
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            // Handle the search action
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public CompletableFuture<List<SearchResult>> SearchForItem(String target, String parentcategoryid, String subcategoryid) {
        CompletableFuture<List<SearchResult>> future = new CompletableFuture<>();

        String json = "{\"target\":\"" + target + "\", \"parentcategoryid\":\"" + parentcategoryid + "\", \"subcategoryid\":\"" + subcategoryid + "\" }";
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.SearchEndPoint;
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(API_URL)
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
//                    List<Ite> searchResults = parseSearchResults(responseData);
                    Log.i("Search Result", responseData);
//                    future.complete(searchResults);
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        String bodyString = jsonObject.getString("body");
                        JSONArray bodyArray = new JSONArray(bodyString);
                        runOnUiThread(() -> Log.e("Search Results Body Array", bodyArray.toString()));

                        for (int i = 0; i < bodyArray.length(); i++) {
                            JSONObject itemObject = bodyArray.getJSONObject(i);

                            JSONObject itemObj = new JSONObject(itemObject.getString("_source"));


                            runOnUiThread(() -> {
                                try {
                                    Log.i("Search Results convert", itemObj.getString("item_id"));
                                    Log.i("Search Results convert", itemObj.getString("item_name"));
                                    Log.i("Search Results convert", itemObj.getString("description"));
                                    Log.i("Search Results convert", itemObj.getString("colourcoding"));
                                    Log.i("Search Results convert", itemObj.getString("barcode"));
                                    Log.i("Search Results convert", itemObj.getString("qrcode"));
                                    Log.i("Search Results convert", itemObj.getString("quanity"));
                                    Log.i("Search Results convert", itemObj.getString("email"));
                                    Log.i("Search Results convert", itemObj.getString("item_image"));
                                    Log.i("Search Results convert", itemObj.getString("subcategoryid"));
                                    Log.i("Search Results convert", itemObj.getString("parentcategoryid"));
//                                    Log.e("Search Results Body Array", itemObject.getString("_source") );
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            });

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
//                            adapter.notifyDataSetChanged();
                        }
                        runOnUiThread(() -> adapter.notifyDataSetChanged());

                    }catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                } else {
                    future.completeExceptionally(new IOException("Search request failed: " + response.code()));
                }
            }
        });

        return future;
    }



}