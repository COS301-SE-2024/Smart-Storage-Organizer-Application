package com.example.smartstorageorganizer;

import android.content.Intent;
import android.media.RouteListingPreference;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstorageorganizer.adapters.SearchResultsAdapter;

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

public class SearchActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SearchResultsAdapter adapter;
    private List<Item> searchResults;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        recyclerView = findViewById(R.id.search_results_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchResults = new ArrayList<>();
        adapter = new SearchResultsAdapter(searchResults, new SearchResultsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RouteListingPreference.Item item) {

            }

            @Override
            public void onItemClick(Item item) {
                Intent intent = new Intent(SearchActivity.this, ItemInfoActivity.class);
                intent.putExtra("item_id", String.valueOf(item.getId()));
                intent.putExtra("item_name", item.getName());
                intent.putExtra("item_description", item.getDescription());
                intent.putExtra("location", item.getLocation());
                intent.putExtra("color_code", item.getColorCode());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        // Inflate the custom search view layout
        searchItem.setActionView(R.layout.search_view_with_icon);

        // Get references to the SearchView and the search icon
        SearchView searchView = searchItem.getActionView().findViewById(R.id.search_view);
        ImageView searchIcon = searchItem.getActionView().findViewById(R.id.search_icon);
        ImageButton voiceSearchButton = searchItem.getActionView().findViewById(R.id.voice_search_button);

        // Set up the search icon click listener
        searchIcon.setOnClickListener(v -> {
            String query = searchView.getQuery().toString();
            performSearch(query);
        });

        voiceSearchButton.setOnClickListener(v -> startVoiceSearch());

        // Set up the query text listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    private void startVoiceSearch() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now");
        voiceSearchLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> voiceSearchLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    ArrayList<String> matches = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (matches != null && !matches.isEmpty()) {
                        String query = matches.get(0);
                        searchView.setQuery(query, true);
                    }
                }
            }
    );

    private void performSearch(String query) {
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.SearchItem;
        String json = "{\"query\":\"" + query + "\"}";
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            private void run() {
                adapter.updateData(searchResults);
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Log.e("SearchActivity", "Search request failed", e));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    // Assuming responseData is a JSON array of items
                    searchResults = parseSearchResults(responseData);
                    runOnUiThread(this::run);
                } else {
                    runOnUiThread(() -> Log.e("SearchActivity", "Search request failed: " + response.code()));
                }
            }
        });
    }

    private List<Item> parseSearchResults(String responseData) {
        // Parse the JSON response data and return a list of Item objects
        // This is a placeholder, you will need to implement this method based on your API response
        return new ArrayList<>();
    }
}
