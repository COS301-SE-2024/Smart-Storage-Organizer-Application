package com.example.smartstorageorganizer;

import android.os.Bundle;
import android.text.Layout;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.smartstorageorganizer.adapters.RecentAdapter;
import com.example.smartstorageorganizer.adapters.SkeletonAdapter;
import com.example.smartstorageorganizer.model.ItemModel;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.Utils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UncategorizedItemsActivity extends AppCompatActivity {
    private static final int PAGE_SIZE = 6;
    private int currentPage = 1;
    private TextView notFoundText;
    private ImageView backButton;
    private Spinner sortBySpinner;
    private List<ItemModel> itemModelList;
    private RecentAdapter recentAdapter;
    private LottieAnimationView loadingScreen;
    private boolean firstTime = true;
    private RecyclerView itemRecyclerView;
    private Button prevButton;
    private Button nextButton;
    private TextView pageNumber;
    private String currentSelectedOption;
    private ShimmerFrameLayout shimmerFrameLayout;
    private RecyclerView recyclerView;
    private NestedScrollView itemsLayout;
    private LinearLayout bottomNavigationView;
    List<String> selectedItemsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_uncategorized_items);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupBackButton();
        setupRecyclerView();
        loadInitialData();
        setupSortByListener();
        setupPaginationButtons();
        setupBottomNavigationBar();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        SkeletonAdapter skeletonAdapter = new SkeletonAdapter(10);
        recyclerView.setAdapter(skeletonAdapter);
    }

    private void initializeViews() {
        sortBySpinner = findViewById(R.id.sort_by_filter);
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
        TextView category = findViewById(R.id.category_text);
        category.setText(getIntent().getStringExtra("category"));
    }

    private void setupBackButton() {
        backButton.setOnClickListener(v -> finish());
    }

    private void setupPaginationButtons() {
        prevButton.setOnClickListener(v -> {
            if (currentPage > 1) {
                itemModelList.clear();
                recentAdapter.notifyDataSetChanged();
                currentPage--;
                firstTime = false;
                loadInitialData();
                pageNumber.setText("Page " + currentPage);
            }
        });

        nextButton.setOnClickListener(v -> {
            itemModelList.clear();
            recentAdapter.notifyDataSetChanged();
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
        itemRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemModelList = new ArrayList<>();
        selectedItemsList = new ArrayList<>();
        recentAdapter = new RecentAdapter(this, itemModelList);
        itemRecyclerView.setAdapter(recentAdapter);
    }

    private void loadInitialData() {
        loadUncategorizedItems();
    }

    private void loadUncategorizedItems() {
        shimmerFrameLayout.startShimmer();
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        itemsLayout.setVisibility(View.GONE);
        sortBySpinner.setVisibility(View.GONE);

        Utils.FetchUncategorizedItems(PAGE_SIZE, currentPage, this, new OperationCallback<List<ItemModel>>() {
            @Override
            public void onSuccess(List<ItemModel> result) {
                itemModelList.clear();
                itemModelList.addAll(result);
                if (!Objects.equals(currentSelectedOption, "Sort by")) {
                    setupSort(currentSelectedOption);
                } else {
                    recentAdapter.notifyDataSetChanged();
                }
                notFoundText.setVisibility(result.isEmpty() ? View.VISIBLE : View.GONE);
                Toast.makeText(UncategorizedItemsActivity.this, "Items fetched successfully", Toast.LENGTH_SHORT).show();
                updatePaginationButtons(result.size());
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                itemsLayout.setVisibility(View.VISIBLE);
                sortBySpinner.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(UncategorizedItemsActivity.this, "Failed to fetch items: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSortByListener() {
        String[] dropdownItems = {"Sort by", "Newest to Oldest", "Oldest to Newest", "A to Z", "Z to A"};
        currentSelectedOption = "Sort by";
        ArrayAdapter<String> adapter = new ArrayAdapter<>(UncategorizedItemsActivity.this, android.R.layout.simple_spinner_item, dropdownItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortBySpinner.setAdapter(adapter);

        sortBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                currentSelectedOption = parentView.getItemAtPosition(position).toString();

                if (currentSelectedOption.equals("Sort by")) {
                    return;
                }

                setupSort(currentSelectedOption);
                Toast.makeText(UncategorizedItemsActivity.this, "Selected: " + currentSelectedOption, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do something here if nothing is selected
            }
        });
    }

    private void setupSort(String sortType) {
        switch (sortType) {
            case "A to Z":
                sortItemModelsAscending(itemModelList);
                recentAdapter.notifyDataSetChanged();
                break;
            case "Z to A":
                sortItemModelsDescending(itemModelList);
                recentAdapter.notifyDataSetChanged();
                break;
            case "Newest to Oldest":
                sortItemModelsByNewest(itemModelList);
                recentAdapter.notifyDataSetChanged();
                break;
            case "Oldest to Newest":
                sortItemModelsByOldest(itemModelList);
                recentAdapter.notifyDataSetChanged();
                break;
            default:
        }
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

    public void suggestCategory(String selectedIds) {
        //call api to suggest
        Toast.makeText(UncategorizedItemsActivity.this, "Clicked:"+selectedIds, Toast.LENGTH_LONG).show();

    }

    private void setupBottomNavigationBar() {
        LinearLayout deleteButton = findViewById(R.id.delete);
        LinearLayout colorButton = findViewById(R.id.color);
        LinearLayout shareButton = findViewById(R.id.share);
        LinearLayout selectAllButton = findViewById(R.id.select_all);
        LinearLayout categorizeButton = findViewById(R.id.categorize);

        deleteButton.setOnClickListener(view -> {
            // Handle delete action
        });

        colorButton.setOnClickListener(view -> {
            // Handle color assign action
        });

        shareButton.setOnClickListener(view -> {
            // Handle share action
        });

        selectAllButton.setOnClickListener(view -> {
            // Handle select all action
        });
        categorizeButton.setOnClickListener(view -> {
            // Handle select all action
            String selectedIds = recentAdapter.getSelectedItemsIds();
            suggestCategory(selectedIds);
        });
    }

    public void updateBottomNavigationBar(boolean isVisible) {
        LinearLayout paginationLayout = findViewById(R.id.paginationLayout);
        paginationLayout.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        bottomNavigationView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }
}
