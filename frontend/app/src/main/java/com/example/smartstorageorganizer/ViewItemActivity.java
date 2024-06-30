package com.example.smartstorageorganizer;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.lottie.LottieAnimationView;
import com.example.smartstorageorganizer.adapters.ItemAdapter;
import com.example.smartstorageorganizer.model.CategoryModel;
import com.example.smartstorageorganizer.model.ItemModel;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ViewItemActivity extends AppCompatActivity {
    private TextView notFoundText;
    private Spinner mySpinner;
    private String categoryID;
    private String currentSelectedCategory;
    private List<ItemModel> itemModelList;
    private ItemAdapter itemAdapter;
    private LottieAnimationView loadingScreen;
    private boolean firstTime = true;
    private List<CategoryModel> categoryModelList = new ArrayList<>();
    private RecyclerView itemRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_item);

        initializeViews();
        setupRecyclerView();
        loadInitialData();
        setupSpinnerListener();
    }

    private void initializeViews() {
        mySpinner = findViewById(R.id.category_filter);
        TextView category = findViewById(R.id.category_text);
        itemRecyclerView = findViewById(R.id.view_all_rec);
        loadingScreen = findViewById(R.id.loadingScreen);
        notFoundText = findViewById(R.id.notFoundText);
        category.setText(getIntent().getStringExtra("category"));
        categoryID = getIntent().getStringExtra("category_id");
    }

    private void setupRecyclerView() {
        itemRecyclerView.setHasFixedSize(true);
        itemRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        itemModelList = new ArrayList<>();
        itemAdapter = new ItemAdapter(this, itemModelList);
        itemRecyclerView.setAdapter(itemAdapter);
    }

    private void loadInitialData() {
        loadItemsByCategory(Integer.parseInt(categoryID));
        fetchAndSetupCategories();
    }

    private void loadItemsByCategory(int categoryId) {
        loadingScreen.setVisibility(View.VISIBLE);
        Utils.filterByCategory(categoryId, this, new OperationCallback<List<ItemModel>>() {
            @Override
            public void onSuccess(List<ItemModel> result) {
                itemModelList.clear();
                itemModelList.addAll(result);
                itemAdapter.notifyDataSetChanged();
                loadingScreen.setVisibility(View.GONE);
                notFoundText.setVisibility(result.isEmpty() ? View.VISIBLE : View.GONE);
                Toast.makeText(ViewItemActivity.this, "Items fetched successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                loadingScreen.setVisibility(View.GONE);
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

                ArrayAdapter<String> adapter = new ArrayAdapter<>(ViewItemActivity.this, android.R.layout.simple_spinner_item, categories);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mySpinner.setAdapter(adapter);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(ViewItemActivity.this, "Failed to fetch categories: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSpinnerListener() {
        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
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
        loadingScreen.setVisibility(View.VISIBLE);
        itemModelList.clear();
        itemAdapter.notifyDataSetChanged();
        Utils.filterBySubCategory(Integer.parseInt(categoryID), subcategoryId, this, new OperationCallback<List<ItemModel>>() {
            @Override
            public void onSuccess(List<ItemModel> result) {
                itemModelList.clear();
                itemModelList.addAll(result);
                itemAdapter.notifyDataSetChanged();
                loadingScreen.setVisibility(View.GONE);
                notFoundText.setVisibility(result.isEmpty() ? View.VISIBLE : View.GONE);
                Toast.makeText(ViewItemActivity.this, "Items fetched successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                loadingScreen.setVisibility(View.GONE);
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
}
