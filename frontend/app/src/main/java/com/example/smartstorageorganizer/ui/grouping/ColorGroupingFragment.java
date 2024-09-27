package com.example.smartstorageorganizer.ui.grouping;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.smartstorageorganizer.AddColorCodeActivity;
import com.example.smartstorageorganizer.HomeActivity;
import com.example.smartstorageorganizer.MyAmplifyApp;
import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.UnitActivity;
import com.example.smartstorageorganizer.ViewColorCodesActivity;
import com.example.smartstorageorganizer.adapters.ColorCodeAdapter;
import com.example.smartstorageorganizer.adapters.SkeletonAdapter;
import com.example.smartstorageorganizer.model.ColorCodeModel;
import com.example.smartstorageorganizer.utils.DeletionCallback;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.Utils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ColorGroupingFragment extends Fragment {

    View root;
    private RecyclerView colorCodeRecyclerView;
    private List<ColorCodeModel> colorCodeModelList;
    private ColorCodeAdapter colorCodeAdapter;
    private FloatingActionButton deleteFab;
    private ShimmerFrameLayout shimmerFrameLayout;
    private RecyclerView recyclerView;
    private LottieAnimationView addButton;
    MyAmplifyApp app;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root =  inflater.inflate(R.layout.fragment_color_grouping, container, false);

        app = (MyAmplifyApp) requireActivity().getApplicationContext();

        String id = app.getOrganizationID();
        Log.i("AuthDemo", "organization id: "+id);

//        getDetails().thenAccept(getDetails-> Log.i("AuthDemo", "User is signed in"));

        colorCodeRecyclerView = root.findViewById(R.id.color_code_rec);
        deleteFab = root.findViewById(R.id.delete_fab);
        deleteFab.setVisibility(View.GONE);
        shimmerFrameLayout = root.findViewById(R.id.shimmer_view_container);
        recyclerView = root.findViewById(R.id.recycler_view);
        addButton = root.findViewById(R.id.addButton);

        loadAllColorCodes();

        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), AddColorCodeActivity.class);
            startActivity(intent);
        });

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        SkeletonAdapter skeletonAdapter = new SkeletonAdapter(10);
        recyclerView.setAdapter(skeletonAdapter);

        colorCodeRecyclerView.setHasFixedSize(true);
        colorCodeRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        colorCodeModelList = new ArrayList<>();
        colorCodeAdapter = new ColorCodeAdapter(requireActivity(), colorCodeModelList, selectedItems -> {
            if (selectedItems.isEmpty()) {
                deleteFab.setVisibility(View.GONE);
            } else {
                deleteFab.setVisibility(View.VISIBLE);
            }
        });
        colorCodeRecyclerView.setAdapter(colorCodeAdapter);

        deleteFab.setOnClickListener(v -> new AlertDialog.Builder(requireActivity())
                .setTitle("Delete Color Codes")
                .setMessage("Are you sure you want to delete the selected color codes?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    ProgressDialog progressDialog;
                    progressDialog = new ProgressDialog(requireActivity());
                    progressDialog.setMessage("Deleting Color Group(s)...");
                    progressDialog.setCancelable(false);  // Set to false to prevent dismissal until done
                    progressDialog.show();

                    List<ColorCodeModel> selectedItems = colorCodeAdapter.getSelectedItems();
                    int totalItems = selectedItems.size();
                    final int[] deletedCount = {0};  // Track the number of completed deletions

                    // Loop through selected items
                    for (ColorCodeModel item : selectedItems) {
                        deleteGroup(item.getId(), new DeletionCallback() {
                            @Override
                            public void onDeleteSuccess() {
                                deletedCount[0]++;  // Increment count after successful deletion
                                if (deletedCount[0] == totalItems) {
                                    // All deletions are done, dismiss the progress dialog
                                    progressDialog.dismiss();
                                    shimmerFrameLayout.stopShimmer();
                                    shimmerFrameLayout.setVisibility(View.GONE);
                                    colorCodeRecyclerView.setVisibility(View.VISIBLE);
                                    colorCodeAdapter.deleteSelectedItems();
                                    deleteFab.setVisibility(View.GONE);
                                    if(colorCodeModelList.isEmpty()){
                                        addButton.setVisibility(View.VISIBLE);
                                        shimmerFrameLayout.stopShimmer();
                                        shimmerFrameLayout.setVisibility(View.GONE);
                                        colorCodeRecyclerView.setVisibility(View.GONE);
                                    }
                                    else {
                                        addButton.setVisibility(View.GONE);
                                        shimmerFrameLayout.stopShimmer();
                                        shimmerFrameLayout.setVisibility(View.GONE);
                                        colorCodeRecyclerView.setVisibility(View.VISIBLE);
                                    }
                                }
                            }

                            @Override
                            public void onDeleteFailure() {
                                // Handle failure case if needed
                                deletedCount[0]++;
                                if (deletedCount[0] == totalItems) {
                                    progressDialog.dismiss();
                                    shimmerFrameLayout.stopShimmer();
                                    shimmerFrameLayout.setVisibility(View.GONE);
                                    colorCodeRecyclerView.setVisibility(View.VISIBLE);
                                    colorCodeAdapter.deleteSelectedItems();
                                    deleteFab.setVisibility(View.GONE);
                                    if(colorCodeModelList.isEmpty()){
                                        addButton.setVisibility(View.VISIBLE);
                                        shimmerFrameLayout.stopShimmer();
                                        shimmerFrameLayout.setVisibility(View.GONE);
                                        colorCodeRecyclerView.setVisibility(View.GONE);
                                    }
                                    else {
                                        addButton.setVisibility(View.GONE);
                                        shimmerFrameLayout.stopShimmer();
                                        shimmerFrameLayout.setVisibility(View.GONE);
                                        colorCodeRecyclerView.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        });
                    }

                    // If no items are selected, dismiss the dialog immediately
                    if (selectedItems.isEmpty()) {
                        progressDialog.dismiss();
                    }
                })
                .setNegativeButton("No", null)
                .show());


        return root;
    }

    private void loadAllColorCodes() {
//        loadingScreen.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        colorCodeRecyclerView.setVisibility(View.GONE);
        Utils.fetchAllColour(app.getOrganizationID(), requireActivity(), new OperationCallback<List<ColorCodeModel>>() {
            @Override
            public void onSuccess(List<ColorCodeModel> result) {
                colorCodeModelList.clear();
                colorCodeModelList.addAll(result);
                colorCodeAdapter.notifyDataSetChanged();

                if(result.isEmpty()){
                    addButton.setVisibility(View.VISIBLE);
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    colorCodeRecyclerView.setVisibility(View.GONE);
                }
                else {
                    addButton.setVisibility(View.GONE);
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    colorCodeRecyclerView.setVisibility(View.VISIBLE);
                }

                Toast.makeText(requireActivity(), "Items fetched successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
//                loadingScreen.setVisibility(View.GONE);
                Toast.makeText(requireActivity(), "Failed to fetch items: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteGroup(String colorCodeId, DeletionCallback callback) {
//        shimmerFrameLayout.startShimmer();
//        shimmerFrameLayout.setVisibility(View.VISIBLE);
//        colorCodeRecyclerView.setVisibility(View.GONE);
        Utils.deleteColour(Integer.parseInt(colorCodeId), app.getOrganizationID(), app.getEmail(),requireActivity(), new OperationCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (Boolean.TRUE.equals(result)) {
//                    showToast("Category added successfully");
                    callback.onDeleteSuccess();
                }
            }

            @Override
            public void onFailure(String error) {
                showToast("Failed to add category: " + error);
                callback.onDeleteFailure();
//                loadingScreen.setVisibility(View.GONE);
//                addCategoryLayout.setVisibility(View.VISIBLE);
            }
        });
    }


//    private void deleteGroup(String colorCodeId) {
////        loadingScreen.setVisibility(View.VISIBLE);
//        shimmerFrameLayout.startShimmer();
//        shimmerFrameLayout.setVisibility(View.VISIBLE);
//        colorCodeRecyclerView.setVisibility(View.GONE);
//        Utils.deleteColour(Integer.parseInt(colorCodeId), app.getOrganizationID(), app.getEmail(),requireActivity(), new OperationCallback<Boolean>() {
//            @Override
//            public void onSuccess(Boolean result) {
//                if (Boolean.TRUE.equals(result)) {
//                    showToast("Category added successfully");
//                }
//            }
//
//            @Override
//            public void onFailure(String error) {
//                showToast("Failed to add category: " + error);
////                loadingScreen.setVisibility(View.GONE);
////                addCategoryLayout.setVisibility(View.VISIBLE);
//            }
//        });
//    }

    private void navigateToHome() {
        Intent intent = new Intent(requireActivity(), HomeActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    private void showToast(String message) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
