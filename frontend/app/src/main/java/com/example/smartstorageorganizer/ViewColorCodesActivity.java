package com.example.smartstorageorganizer;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.lottie.LottieAnimationView;
import com.example.OnColorFetchListener;
import com.example.smartstorageorganizer.adapters.ColorCodeAdapter;
import com.example.smartstorageorganizer.adapters.ItemAdapter;
import com.example.smartstorageorganizer.adapters.SkeletonAdapter;
import com.example.smartstorageorganizer.model.ColorCodeModel;
import com.example.smartstorageorganizer.model.ItemModel;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.Utils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ViewColorCodesActivity extends AppCompatActivity implements OnColorFetchListener {
    private RecyclerView colorCodeRecyclerView;
    private List<ColorCodeModel> colorCodeModelList;
    private ColorCodeAdapter colorCodeAdapter;
    private FloatingActionButton deleteFab;
    private LottieAnimationView loadingScreen;
    private ShimmerFrameLayout shimmerFrameLayout;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_color_codes);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadingScreen = findViewById(R.id.loadingScreen);
        colorCodeRecyclerView = findViewById(R.id.color_code_rec);
        deleteFab = findViewById(R.id.delete_fab);
        deleteFab.setVisibility(View.GONE); // Initially hidden
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
        recyclerView = findViewById(R.id.recycler_view);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        SkeletonAdapter skeletonAdapter = new SkeletonAdapter(10);
        recyclerView.setAdapter(skeletonAdapter);

        colorCodeRecyclerView.setHasFixedSize(true);
        colorCodeRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        colorCodeModelList = new ArrayList<>();
        colorCodeAdapter = new ColorCodeAdapter(this, colorCodeModelList, selectedItems -> {
            if (selectedItems.isEmpty()) {
                deleteFab.setVisibility(View.GONE);
            } else {
                deleteFab.setVisibility(View.VISIBLE);
            }
        });
        colorCodeRecyclerView.setAdapter(colorCodeAdapter);

        deleteFab.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Delete Color Codes")
                .setMessage("Are you sure you want to delete the selected color codes?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    List<ColorCodeModel> selectedItems = colorCodeAdapter.getSelectedItems();
                    for (ColorCodeModel item : selectedItems) {
                        deleteCategory(item.getId());
                    }
//                    navigateToHome();
//                    loadingScreen.setVisibility(View.GONE);
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    colorCodeRecyclerView.setVisibility(View.VISIBLE);
                    colorCodeAdapter.deleteSelectedItems();
                    deleteFab.setVisibility(View.GONE);
                })
                .setNegativeButton("No", null)
                .show());

        loadAllColorCodes();
    }

    private void loadAllColorCodes() {
//        loadingScreen.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        colorCodeRecyclerView.setVisibility(View.GONE);
        Utils.fetchAllColour(this, new OperationCallback<List<ColorCodeModel>>() {
            @Override
            public void onSuccess(List<ColorCodeModel> result) {
                colorCodeModelList.clear();
                colorCodeModelList.addAll(result);
                colorCodeAdapter.notifyDataSetChanged();
//                loadingScreen.setVisibility(View.GONE);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                colorCodeRecyclerView.setVisibility(View.VISIBLE);
                Toast.makeText(ViewColorCodesActivity.this, "Items fetched successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
//                loadingScreen.setVisibility(View.GONE);
                Toast.makeText(ViewColorCodesActivity.this, "Failed to fetch items: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteCategory(String colorCodeId) {
//        loadingScreen.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        colorCodeRecyclerView.setVisibility(View.GONE);
        Utils.deleteColour(Integer.parseInt(colorCodeId), this, new OperationCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (Boolean.TRUE.equals(result)) {
                    showToast("Category added successfully");
                }
            }

            @Override
            public void onFailure(String error) {
                showToast("Failed to add category: " + error);
//                loadingScreen.setVisibility(View.GONE);
//                addCategoryLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void navigateToHome() {
        Intent intent = new Intent(ViewColorCodesActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(ViewColorCodesActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void fetchByColorCode() {
        loadAllColorCodes();
    }
}
