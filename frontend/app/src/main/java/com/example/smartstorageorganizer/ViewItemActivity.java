package com.example.smartstorageorganizer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
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
import com.example.smartstorageorganizer.model.ItemModel;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.Utils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_item);

        initializeViews();
        setupBackButton();
        setupRecyclerView();
        loadInitialData();
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
//        ImageView deleteButton = findViewById(R.id.bottom_nav_delete);
//        ImageView colorButton = findViewById(R.id.bottom_nav_color);
//        ImageView shareButton = findViewById(R.id.bottom_nav_share);
//        TextView selectAllButton = findViewById(R.id.bottom_nav_select_all);
//
//        deleteButton.setOnClickListener(view -> {
//            // Handle delete action
//        });
//
//        colorButton.setOnClickListener(view -> {
//            // Handle color assign action
//        });
//
//        shareButton.setOnClickListener(view -> {
//            // Handle share action
//        });
//
//        selectAllButton.setOnClickListener(view -> {
//            // Handle select all action
//        });
    }

    public void updateBottomNavigationBar(boolean isVisible) {
        bottomNavigationView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }
}
