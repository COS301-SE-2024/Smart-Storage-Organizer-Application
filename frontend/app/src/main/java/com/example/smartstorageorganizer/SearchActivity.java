package com.example.smartstorageorganizer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstorageorganizer.adapters.RecentAdapter;
import com.example.smartstorageorganizer.adapters.SearchAdapter;
import com.example.smartstorageorganizer.model.ItemModel;
import com.example.smartstorageorganizer.model.SearchResult;
import com.example.smartstorageorganizer.model.TokenManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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

    private RecentAdapter adapter;
    private String currentSelectedOption;
    private Spinner sortBySpinner;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        searchView = findViewById(R.id.searchView);
        searchResultsRecyclerView = findViewById(R.id.search_results_recycler_view);
        sortBySpinner = findViewById(R.id.sort_by_filter);

        setupSortByListener();
        // Set up the RecyclerView (adapter, layout manager, etc.)
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchResults = new ArrayList<>();

        adapter = new RecentAdapter(this, searchResults);
        searchResultsRecyclerView.setAdapter(adapter);

        findViewById(R.id.scanButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, CodeScannerActivity.class);
                startActivity(intent);
            }
        });

        searchView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int leftDrawableWidth = searchView.getCompoundDrawables()[0] != null ?
                            searchView.getCompoundDrawables()[0].getBounds().width() : 0;

                    int rightDrawableWidth = searchView.getCompoundDrawables()[2] != null ?
                            searchView.getCompoundDrawables()[2].getBounds().width() : 0;

                    if (event.getX() <= (searchView.getPaddingLeft() + leftDrawableWidth)) {
                        // Mic icon touched
                        startVoiceInput();
                        return true;
                    } else if (event.getX() >= (searchView.getWidth() - searchView.getPaddingRight() - rightDrawableWidth)) {
                        // Search icon touched
                        String query = searchView.getText().toString();
                        if (!query.isEmpty()) {
                            Toast.makeText(SearchActivity.this, "Search query: " + query, Toast.LENGTH_SHORT).show();
                            SearchForItem(query, "*", "*");
                            searchResults.clear();
                        } else {
                            Toast.makeText(SearchActivity.this, "Please enter a search query", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                }
                return false;
            }
        });


//        searchView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    if (event.getRawX() <= (searchView.getLeft() + searchView.getCompoundDrawables()[0].getBounds().width())) {
//                        startVoiceInput();
//                        return true;
//                    }
//
//                    else if (event.getRawX() >= (searchView.getRight() - searchView.getCompoundDrawables()[2].getBounds().width())) {
//                        String query = searchView.getText().toString();
//                        if (!query.isEmpty()) {
//                            Toast.makeText(SearchActivity.this, "Search query: " + query, Toast.LENGTH_SHORT).show();
//                            SearchForItem(query, "*", "*");
//                            searchResults.clear();
//                        } else {
//                            Toast.makeText(SearchActivity.this, "Please enter a search query", Toast.LENGTH_SHORT).show();
//                        }
//                        return true;
//                    }
//                }
//                return false;
//            }
//        });
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak something...");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            Toast.makeText(this, "Your device doesn't support Speech Input", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                String spokenText = result.get(0);
                handleVoiceInput(spokenText);
            }
        }
    }

    private void handleVoiceInput(String spokenText) {
        if (!spokenText.isEmpty()) {
            Toast.makeText(SearchActivity.this, "Search query: " + spokenText, Toast.LENGTH_SHORT).show();
            SearchForItem(spokenText, "*", "*");
            searchResults.clear();
        } else {
            Toast.makeText(SearchActivity.this, "Please enter a search query", Toast.LENGTH_SHORT).show();
        }
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
                SearchForItem(query, "", "");
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
                            runOnUiThread(() -> {
                                adapter.notifyDataSetChanged();
                                sortBySpinner.setVisibility(View.VISIBLE);
                            });

                        }catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    } else {
                        future.completeExceptionally(new IOException("Search request failed: " + response.code()));
                    }
                }
            });

                }).exceptionally(ex -> {
            Log.e("TokenError", "Failed to get user token", ex);
            return null;
        });

        return future;
    }

    private void setupSortByListener(){
        String[] dropdownItems = {"Sort by", "Newest to Oldest", "Oldest to Newest", "A to Z", "Z to A"};
        currentSelectedOption = "Sort by";
        ArrayAdapter<String> adapter = new ArrayAdapter<>(SearchActivity.this, android.R.layout.simple_spinner_item, dropdownItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortBySpinner.setAdapter(adapter);

        sortBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                currentSelectedOption = parentView.getItemAtPosition(position).toString();

                if (currentSelectedOption.equals("Sort by")) {
                    return;
                }

                switch (currentSelectedOption) {
                    case "A to Z":
                        sortItemModelsAscending(searchResults);
                        adapter.notifyDataSetChanged();
                        break;
                    case "Z to A":
                        sortItemModelsDescending(searchResults);
                        adapter.notifyDataSetChanged();
                        break;
                    case "Newest to Oldest":
                        sortItemModelsByNewest(searchResults);
                        adapter.notifyDataSetChanged();
                        break;
                    case "Oldest to Newest":
                        sortItemModelsByOldest(searchResults);
                        adapter.notifyDataSetChanged();
                        break;
                    default:
                }

                Toast.makeText(SearchActivity.this, "Selected: " + currentSelectedOption, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do something here if nothing is selected
            }
        });
    }

    public static void sortItemModelsAscending(List<ItemModel> itemModels) {
        itemModels.sort((o1, o2) -> o1.getItemName().compareToIgnoreCase(o2.getItemName()));
    }

    public static void sortItemModelsDescending(List<ItemModel> itemModels) {
        itemModels.sort((o1, o2) -> o2.getItemName().compareToIgnoreCase(o1.getItemName()));
    }
    public static void sortItemModelsByNewest(List<ItemModel> itemModels) {
        itemModels.sort((o1, o2) -> {
            LocalDateTime date1 = LocalDateTime.parse(o1.getCreatedAt(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSXXX"));
            LocalDateTime date2 = LocalDateTime.parse(o2.getCreatedAt(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSXXX"));
            return date2.compareTo(date1);
        });
    }

    public static void sortItemModelsByOldest(List<ItemModel> itemModels) {
        itemModels.sort((o1, o2) -> {
            LocalDateTime date1 = LocalDateTime.parse(o1.getCreatedAt(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSXXX"));
            LocalDateTime date2 = LocalDateTime.parse(o2.getCreatedAt(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSXXX"));
            return date1.compareTo(date2);
        });
    }

}