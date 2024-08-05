package com.example.smartstorageorganizer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.lottie.LottieAnimationView;
import com.example.smartstorageorganizer.adapters.ItemAdapter;
import com.example.smartstorageorganizer.adapters.SkeletonAdapter;
import com.example.smartstorageorganizer.model.CategoryModel;
import com.example.smartstorageorganizer.model.ColorCodeModel;
import com.example.smartstorageorganizer.model.ItemModel;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.Utils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ViewItemActivity extends AppCompatActivity {
    private static final int PAGE_SIZE = 6;
    private int currentPage = 1;
    private TextView notFoundText;
    private ImageView backButton;
    private Spinner mySpinner;
    private Spinner sortBySpinner;
    private String categoryID;
    private String colorCodeID;
    private String currentSelectedCategory;
    private List<ItemModel> itemModelList;
    private ItemAdapter itemAdapter;
    private LottieAnimationView loadingScreen;
    private boolean firstTime = true;
    private List<CategoryModel> categoryModelList = new ArrayList<>();
    private RecyclerView itemRecyclerView;
    private Button prevButton;
    private Button nextButton;
    private TextView pageNumber;
    private String currentSelectedOption;
    private ShimmerFrameLayout shimmerFrameLayout;
    private RecyclerView recyclerView;
    private NestedScrollView itemsLayout;
    private LinearLayout bottomNavigationView;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_item);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Assigning Item(s) to Color Code...");
        progressDialog.setCancelable(false);

        initializeViews();
        setupBackButton();
        setupRecyclerView();
        loadInitialData();
        setupBottomNavigationBar();
        if(!Objects.equals(getIntent().getStringExtra("category"), "")) {
            setupSpinnerListener();
        }
        setupSortByListener();
        setupPaginationButtons();
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        SkeletonAdapter skeletonAdapter = new SkeletonAdapter(10);
        recyclerView.setAdapter(skeletonAdapter);
    }

    private void initializeViews() {
        mySpinner = findViewById(R.id.category_filter);
        sortBySpinner = findViewById(R.id.sort_by_filter);
        TextView category = findViewById(R.id.category_text);
        itemRecyclerView = findViewById(R.id.view_all_rec);
        loadingScreen = findViewById(R.id.loadingScreen);
        notFoundText = findViewById(R.id.notFoundText);
        backButton = findViewById(R.id.back_home_button);
        prevButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);
        pageNumber = findViewById(R.id.pageNumber);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
        recyclerView = findViewById(R.id.recycler_view);
        itemsLayout = findViewById(R.id.newstedScrollview);

        if(!Objects.equals(getIntent().getStringExtra("category"), "")) {
            category.setText(getIntent().getStringExtra("category"));
            categoryID = getIntent().getStringExtra("category_id");
        }
        else {
            colorCodeID = getIntent().getStringExtra("color_code_id");
        }
    }

    private void setupBackButton() {
        backButton.setOnClickListener(v -> finish());
    }

    private void setupPaginationButtons() {
        prevButton.setOnClickListener(v -> {
//            firstTime = true;
            if (currentPage > 1) {
                itemModelList.clear();
                itemAdapter.notifyDataSetChanged();
                currentPage--;
                firstTime = false;
                loadInitialData();
                pageNumber.setText("Page " + currentPage);
            }
        });

        nextButton.setOnClickListener(v -> {
            itemModelList.clear();
            itemAdapter.notifyDataSetChanged();
            currentPage++;
            firstTime = false;
            loadInitialData();
            pageNumber.setText("Page " + currentPage);
        });
    }
    private void updatePaginationButtons(int resultSize) {
        prevButton.setEnabled(currentPage > 1);
        nextButton.setEnabled(resultSize == PAGE_SIZE);
    }

    private void setupRecyclerView() {
        itemRecyclerView.setHasFixedSize(true);
        itemRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        itemModelList = new ArrayList<>();
        itemAdapter = new ItemAdapter(this, itemModelList);
        itemRecyclerView.setAdapter(itemAdapter);
    }

    private void loadInitialData() {
        if(!Objects.equals(getIntent().getStringExtra("category"), "")) {
            if (firstTime) {
                currentSelectedCategory = "All";
            }
            if (Objects.equals(getIntent().getStringExtra("category"), "All")){
                loadAllItems();
            }
            else {
                if (!currentSelectedCategory.equals("All")) {
                    CategoryModel subCategory = findCategoryByName(currentSelectedCategory);
                    if (subCategory != null) {
                        loadItemsBySubCategory(Integer.parseInt(subCategory.getCategoryID()));
                    }
                } else {
                    loadItemsByCategory(Integer.parseInt(categoryID));
                    fetchAndSetupCategories();            }
            }
        }
        else {
            colorCodeID = getIntent().getStringExtra("color_code_id");
            fetchByColorCode(Integer.parseInt(colorCodeID));
        }
    }

    private void loadAllItems() {
//        loadingScreen.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        itemsLayout.setVisibility(View.GONE);
        sortBySpinner.setVisibility(View.GONE);
        mySpinner.setVisibility(View.GONE);
        Utils.fetchAllItems(PAGE_SIZE, currentPage,this, new OperationCallback<List<ItemModel>>() {
            @Override
            public void onSuccess(List<ItemModel> result) {
                itemModelList.clear();
                itemModelList.addAll(result);
                if(!Objects.equals(currentSelectedOption, "Sort by")){
                    setupSort(currentSelectedOption);
                }
                else {
                    itemAdapter.notifyDataSetChanged();
                }
//                loadingScreen.setVisibility(View.GONE);
                notFoundText.setVisibility(result.isEmpty() ? View.VISIBLE : View.GONE);
                Toast.makeText(ViewItemActivity.this, "Items fetched successfully", Toast.LENGTH_SHORT).show();
                updatePaginationButtons(result.size());
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                itemsLayout.setVisibility(View.VISIBLE);
                sortBySpinner.setVisibility(View.VISIBLE);
                mySpinner.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(String error) {
//                loadingScreen.setVisibility(View.GONE);
                Toast.makeText(ViewItemActivity.this, "Failed to fetch items: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchByColorCode(int colorCodeId) {
//        loadingScreen.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        itemsLayout.setVisibility(View.GONE);
        sortBySpinner.setVisibility(View.GONE);
        mySpinner.setVisibility(View.GONE);
        Utils.fetchByColour(colorCodeId,this, new OperationCallback<List<ItemModel>>() {
            @Override
            public void onSuccess(List<ItemModel> result) {
                itemModelList.clear();
                itemModelList.addAll(result);
                if(!Objects.equals(currentSelectedOption, "Sort by")){
                    setupSort(currentSelectedOption);
                }
                else {
                    itemAdapter.notifyDataSetChanged();
                }
//                loadingScreen.setVisibility(View.GONE);
                notFoundText.setVisibility(result.isEmpty() ? View.VISIBLE : View.GONE);
                Toast.makeText(ViewItemActivity.this, "Items fetched successfully", Toast.LENGTH_SHORT).show();
                updatePaginationButtons(result.size());
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                itemsLayout.setVisibility(View.VISIBLE);
                sortBySpinner.setVisibility(View.VISIBLE);
                mySpinner.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(String error) {
//                loadingScreen.setVisibility(View.GONE);
                Toast.makeText(ViewItemActivity.this, "Failed to fetch items: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadItemsByCategory(int categoryId) {
//        loadingScreen.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        itemsLayout.setVisibility(View.GONE);
        sortBySpinner.setVisibility(View.GONE);
        mySpinner.setVisibility(View.GONE);
        Utils.filterByCategory(categoryId,PAGE_SIZE, currentPage, this, new OperationCallback<List<ItemModel>>() {
            @Override
            public void onSuccess(List<ItemModel> result) {
                itemModelList.clear();
                itemModelList.addAll(result);
                if(!Objects.equals(currentSelectedOption, "Sort by")){
                    setupSort(currentSelectedOption);
                }
                else {
                    itemAdapter.notifyDataSetChanged();
                }
//                loadingScreen.setVisibility(View.GONE);
                notFoundText.setVisibility(result.isEmpty() ? View.VISIBLE : View.GONE);
                Toast.makeText(ViewItemActivity.this, "Items fetched successfully", Toast.LENGTH_SHORT).show();
                updatePaginationButtons(result.size());
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                itemsLayout.setVisibility(View.VISIBLE);
                sortBySpinner.setVisibility(View.VISIBLE);
                mySpinner.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(String error) {
//                loadingScreen.setVisibility(View.GONE);
                Toast.makeText(ViewItemActivity.this, "Failed to fetch items: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchAndSetupCategories() {
        Utils.fetchParentCategories(Integer.parseInt(categoryID), getIntent().getStringExtra("email"), this, new OperationCallback<List<CategoryModel>>() {
            @Override
            public void onSuccess(List<CategoryModel> result) {
                categoryModelList = result;
                List<String> categories = new ArrayList<>();
                categories.add("All");
                for (CategoryModel category : categoryModelList) {
                    categories.add(category.getCategoryName());
                }

                if(firstTime){
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(ViewItemActivity.this, android.R.layout.simple_spinner_item, categories);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mySpinner.setAdapter(adapter);
                }
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                itemsLayout.setVisibility(View.VISIBLE);
                sortBySpinner.setVisibility(View.VISIBLE);
                mySpinner.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(ViewItemActivity.this, "Failed to fetch categories: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSortByListener(){
        String[] dropdownItems = {"Sort by", "Newest to Oldest", "Oldest to Newest", "A to Z", "Z to A"};
        currentSelectedOption = "Sort by";
        ArrayAdapter<String> adapter = new ArrayAdapter<>(ViewItemActivity.this, android.R.layout.simple_spinner_item, dropdownItems);
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
                        sortItemModelsAscending(itemModelList);
                        itemAdapter.notifyDataSetChanged();
                        break;
                    case "Z to A":
                        sortItemModelsDescending(itemModelList);
                        itemAdapter.notifyDataSetChanged();
                        break;
                    case "Newest to Oldest":
                        sortItemModelsByNewest(itemModelList);
                        itemAdapter.notifyDataSetChanged();
                        break;
                    case "Oldest to Newest":
                        sortItemModelsByOldest(itemModelList);
                        itemAdapter.notifyDataSetChanged();
                        break;
                    default:
                }

                Toast.makeText(ViewItemActivity.this, "Selected: " + currentSelectedOption, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do something here if nothing is selected
            }
        });
    }

    private void setupSpinnerListener() {
        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                currentPage = 1;
                pageNumber.setText("Page " + currentPage);
                if (firstTime) {
                    firstTime = false;
                    return;
                }

                currentSelectedCategory = parentView.getItemAtPosition(position).toString();
                if (!currentSelectedCategory.equals("All")) {
                    CategoryModel subCategory = findCategoryByName(currentSelectedCategory);
                    if (subCategory != null) {
                        loadItemsBySubCategory(Integer.parseInt(subCategory.getCategoryID()));
                    }
                } else {
                    loadItemsByCategory(Integer.parseInt(categoryID));
                }

                Toast.makeText(ViewItemActivity.this, "Selected: " + currentSelectedCategory, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do something here if nothing is selected
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadItemsBySubCategory(int subcategoryId) {
//        loadingScreen.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        itemsLayout.setVisibility(View.GONE);
        sortBySpinner.setVisibility(View.GONE);
        mySpinner.setVisibility(View.GONE);
        itemModelList.clear();
        itemAdapter.notifyDataSetChanged();
        Utils.filterBySubCategory(Integer.parseInt(categoryID), subcategoryId, PAGE_SIZE, currentPage, this, new OperationCallback<List<ItemModel>>() {
            @Override
            public void onSuccess(List<ItemModel> result) {
                itemModelList.clear();
                itemModelList.addAll(result);
                if(!Objects.equals(currentSelectedOption, "Sort by")){
                    setupSort(currentSelectedOption);
                }
                else {
                    itemAdapter.notifyDataSetChanged();
                }
//                loadingScreen.setVisibility(View.GONE);
                notFoundText.setVisibility(result.isEmpty() ? View.VISIBLE : View.GONE);
                updatePaginationButtons(result.size());
                Toast.makeText(ViewItemActivity.this, "Items fetched successfully", Toast.LENGTH_SHORT).show();
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                itemsLayout.setVisibility(View.VISIBLE);
                sortBySpinner.setVisibility(View.VISIBLE);
                mySpinner.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(String error) {
//                loadingScreen.setVisibility(View.GONE);
                Toast.makeText(ViewItemActivity.this, "Failed to fetch items: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private CategoryModel findCategoryByName(String categoryName) {
        for (CategoryModel category : categoryModelList) {
            if (category.getCategoryName().equalsIgnoreCase(categoryName)) {
                return category;
            }
        }
        return null;
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

    public void setupSort(String sortType) {
        switch (sortType) {
            case "A to Z":
                sortItemModelsAscending(itemModelList);
                itemAdapter.notifyDataSetChanged();
                break;
            case "Z to A":
                sortItemModelsDescending(itemModelList);
                itemAdapter.notifyDataSetChanged();
                break;
            case "Newest to Oldest":
                sortItemModelsByNewest(itemModelList);
                itemAdapter.notifyDataSetChanged();
                break;
            case "Oldest to Newest":
                sortItemModelsByOldest(itemModelList);
                itemAdapter.notifyDataSetChanged();
                break;
            default:
        }
    }

    private void setupBottomNavigationBar() {
        LinearLayout deleteButton = findViewById(R.id.delete);
        LinearLayout colorButton = findViewById(R.id.color);
        LinearLayout shareButton = findViewById(R.id.share);
        LinearLayout selectAllButton = findViewById(R.id.select_all);

        deleteButton.setOnClickListener(view -> {
            // Handle delete action
        });

        colorButton.setOnClickListener(view -> {
            // Handle color assign action
            String selectedIds = itemAdapter.getSelectedItemsIds();
            progressDialog.setMessage("Fetching Color Codes...");
            progressDialog.show();
            assignItemToColor(selectedIds);
        });

        shareButton.setOnClickListener(view -> {
            // Handle share action
        });

        selectAllButton.setOnClickListener(view -> {
            // Handle select all action
            itemAdapter.selectAllItems();
        });
    }

    public void updateBottomNavigationBar(boolean isVisible) {
        LinearLayout paginationLayout = findViewById(R.id.paginationLayout);
        paginationLayout.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        bottomNavigationView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public void assignItemToColor(String itemId) {
        // Fetch all available color codes
        Utils.fetchAllColour(this, new OperationCallback<List<ColorCodeModel>>() {
            @Override
            public void onSuccess(List<ColorCodeModel> colorCodeModelList) {
                // Create an array of color names to display to the user
                String[] colorNames = new String[colorCodeModelList.size()];
                for (int i = 0; i < colorCodeModelList.size(); i++) {
                    colorNames[i] = colorCodeModelList.get(i).getName();
                }

                // Show the color options to the user (e.g., in a dialog)
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewItemActivity.this);
                progressDialog.dismiss();
                builder.setTitle("Choose a color for the item");
                builder.setItems(colorNames, (dialog, which) -> {
                    // Get the selected color code
                    ColorCodeModel selectedColor = colorCodeModelList.get(which);
                    String colourId = selectedColor.getId();
                    Log.d("Selected Color", "Color ID: " + colourId);
                    Log.d("Selected Item", "Item ID: " + itemId);
                    progressDialog.setMessage("Assigning Item(s) to Color Code...");
                    progressDialog.show();

                    // Assign the selected color to the item
                    assignColorToItem(itemId, selectedColor);

                    //assigned to color db
                    AssignColour(colourId,itemId);
                });
                builder.show();
            }

            @Override
            public void onFailure(String errorMessage) {
                // Handle the error (e.g., show a toast or log the error)
                Toast.makeText(ViewItemActivity.this, "Failed to fetch colors: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void assignColorToItem(String itemId, ColorCodeModel colorCode) {
        // Your logic to assign the colorCode to the item with the given itemId
        // This might involve updating a database, sending a request to a server, etc.
        Log.d("AssignColor", "Item ID: " + itemId + " assigned to color: " + colorCode.getName());
    }

    public void AssignColour(String colourid, String itemid) {
        String json = "{\"colourid\":\"" + colourid + "\", \"itemid\":\"" + itemid + "\"}";

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.AddItemToColourEndPoint;
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(ViewItemActivity.this, "Failed to Assign Color Code to item(s)", Toast.LENGTH_SHORT).show());
                Log.e("AssignColour", "Request failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    runOnUiThread(() -> Toast.makeText(ViewItemActivity.this, "Color Code Assigned Successfully.", Toast.LENGTH_SHORT).show());
                } else {
                    progressDialog.dismiss();
                    runOnUiThread(() -> Toast.makeText(ViewItemActivity.this, "Failed to Assign Color Code to item(s)", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
