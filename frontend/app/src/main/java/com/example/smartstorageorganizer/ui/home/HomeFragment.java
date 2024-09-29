package com.example.smartstorageorganizer.ui.home;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;


import androidx.annotation.NonNull;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.StoragePath;
import com.amplifyframework.storage.options.StorageUploadFileOptions;
import com.example.smartstorageorganizer.AddColorCodeActivity;
import com.example.smartstorageorganizer.AddItemActivity;
import com.example.smartstorageorganizer.LoginActivity;
import com.example.smartstorageorganizer.MyAmplifyApp;
import com.example.smartstorageorganizer.adapters.CategoryAdapter;
import com.example.smartstorageorganizer.AddCategoryActivity;
import com.example.smartstorageorganizer.HomeActivity;
import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.UnitActivity;
import com.example.smartstorageorganizer.adapters.ItemAdapter;
import com.example.smartstorageorganizer.adapters.RecentAdapter;
import com.example.smartstorageorganizer.adapters.SkeletonAdapter;
import com.example.smartstorageorganizer.databinding.FragmentHomeBinding;
import com.example.smartstorageorganizer.model.ItemModel;
import com.example.smartstorageorganizer.model.CategoryModel;
import com.example.smartstorageorganizer.model.unitModel;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.UserUtils;
import com.example.smartstorageorganizer.utils.Utils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class HomeFragment extends Fragment {

    Spinner suggestionSpinner, colorSpinner;
    List<CategoryModel> suggestedCategory = new ArrayList<>();
    int PICK_IMAGE_MULTIPLE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int GALLERY_CODE = 1;
    private String currentSelectedCategory, currentSelectedSubcategory;
    Uri ImageUri;
    File file;
    List<String> imagesEncodedList;
    private Spinner parentSpinner, subcategorySpinner;

    LottieAnimationView fetchItemsLoader, addButton;
    RecyclerView.LayoutManager layoutManager;
    private TextView name;
    private FragmentHomeBinding binding;
    private List<ItemModel> itemModelList;
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> subAdapter;
    private RecentAdapter recentAdapter;
    private RecyclerView itemRecyclerView, category_RecyclerView;
    private String currentEmail, currentName, currentSurname, organizationID;
    AlertDialog alertDialog;
    private List<CategoryModel> categoryModelList;
    private List<CategoryModel> subcategoryModelList;
    private CategoryAdapter categoryAdapter;
    private String parentCategoryId, subcategoryId;
    Button buttonNext;
    Button buttonTakePhoto;
    EditText itemDescription, itemName;
    ImageView itemImage;
    private FloatingActionButton addItemButton;
    private List<String> parentCategories = new ArrayList<>();
    private List<String> subCategories = new ArrayList<>();
    private ShimmerFrameLayout shimmerFrameLayoutName;
    private ShimmerFrameLayout shimmerFrameLayoutCategory;
    private ShimmerFrameLayout shimmerFrameLayoutRecent;
    private TextView recentText;
//    private RecyclerView recyclerViewRecent;
    ProgressDialog progressDialogAddingItem;
    private LinearLayout noInternet;
    private String imageFilePath;
    private AppCompatButton tryAgainButton;
    MyAmplifyApp app;
    private long startTime;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SwipeRefreshLayout swipeRefreshRecentLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        app = (MyAmplifyApp) requireActivity().getApplicationContext();

        addItemButton = root.findViewById(R.id.addItemButton);
        addButton = root.findViewById(R.id.addButton);
        itemRecyclerView = root.findViewById(R.id.item_rec);
        fetchItemsLoader = root.findViewById(R.id.fetchItemsLoader);
        tryAgainButton = root.findViewById(R.id.tryAgainButton);
        category_RecyclerView = root.findViewById(R.id.category_rec);
        shimmerFrameLayoutName = root.findViewById(R.id.shimmer_view_container);
        shimmerFrameLayoutCategory = root.findViewById(R.id.shimmer_view_container_category);
        noInternet = root.findViewById(R.id.noInternet);
        shimmerFrameLayoutRecent = root.findViewById(R.id.shimmer_view_container_recent);
        recentText = root.findViewById(R.id.recentText);
        name = root.findViewById(R.id.name);
        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshRecentLayout = root.findViewById(R.id.swipe_refresh_recent_layout);

        getDetails().thenAccept(getDetails-> Log.i("AuthDemo", "User is signed in"));


        category_RecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        categoryModelList = new ArrayList<>();
        subcategoryModelList = new ArrayList<>();


        categoryAdapter = new CategoryAdapter(requireActivity(), categoryModelList, this);
        category_RecyclerView.setAdapter(categoryAdapter);

        itemRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(requireActivity());
        itemRecyclerView.setLayoutManager(layoutManager);

        addItemButton.setOnClickListener(v -> showBottomDialog());
        tryAgainButton.setOnClickListener(v -> {
            recentText.setVisibility(View.GONE);
            shimmerFrameLayoutCategory.startShimmer();
            shimmerFrameLayoutCategory.setVisibility(View.VISIBLE);
            shimmerFrameLayoutRecent.setVisibility(View.VISIBLE);
            shimmerFrameLayoutName.setVisibility(View.VISIBLE);
            itemRecyclerView.setVisibility(View.VISIBLE);
            noInternet.setVisibility(View.GONE);
            getDetails().thenAccept(getDetails-> Log.i("AuthDemo", "User is signed in"));
        });

        itemModelList = new ArrayList<>();
        recentAdapter = new RecentAdapter(requireActivity(), itemModelList, "");
        itemRecyclerView.setAdapter(recentAdapter);

        addButton.setOnClickListener(v -> showBottomDialog());

        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Refresh the data when user swipes down
            recentText.setVisibility(View.GONE);
            shimmerFrameLayoutCategory.startShimmer();
            shimmerFrameLayoutCategory.setVisibility(View.VISIBLE);
            shimmerFrameLayoutRecent.setVisibility(View.VISIBLE);
            shimmerFrameLayoutName.setVisibility(View.VISIBLE);
            itemRecyclerView.setVisibility(View.GONE);
            category_RecyclerView.setVisibility(View.GONE);
            noInternet.setVisibility(View.GONE);
            getDetails().thenAccept(getDetails-> Log.i("AuthDemo", "User is signed in"));
        });

        swipeRefreshRecentLayout.setOnRefreshListener(() -> {
            // Refresh the data when user swipes down
            recentText.setVisibility(View.GONE);
            shimmerFrameLayoutCategory.startShimmer();
            shimmerFrameLayoutCategory.setVisibility(View.VISIBLE);
            shimmerFrameLayoutRecent.setVisibility(View.VISIBLE);
            shimmerFrameLayoutName.setVisibility(View.VISIBLE);
            itemRecyclerView.setVisibility(View.GONE);
            category_RecyclerView.setVisibility(View.GONE);
            noInternet.setVisibility(View.GONE);
            getDetails().thenAccept(getDetails-> Log.i("AuthDemo", "User is signed in"));
        });

        return root;
    }

    private void loadRecentItems(String email, String organizationID) {
        Utils.fetchRecentItems(email, organizationID,requireActivity(), new OperationCallback<List<ItemModel>>() {
            @Override
            public void onSuccess(List<ItemModel> result) {
                itemModelList.clear();
                if(!result.isEmpty()){
                    itemModelList.addAll(result);
                }
                recentAdapter.notifyDataSetChanged();

                recentText.setVisibility(View.VISIBLE);
                shimmerFrameLayoutCategory.stopShimmer();
                shimmerFrameLayoutCategory.setVisibility(View.GONE);
                shimmerFrameLayoutRecent.stopShimmer();
                shimmerFrameLayoutRecent.setVisibility(View.GONE);

                if(result.isEmpty()){
                    addButton.setVisibility(View.VISIBLE);
                    addItemButton.setVisibility(View.GONE);
                    swipeRefreshRecentLayout.setVisibility(View.GONE);
                }
                else {
                    addButton.setVisibility(View.GONE);
                    addItemButton.setVisibility(View.VISIBLE);
                    swipeRefreshRecentLayout.setVisibility(View.VISIBLE);
                }

                itemRecyclerView.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshRecentLayout.setRefreshing(false);            }

            @Override
            public void onFailure(String error) {
//                recentText.setVisibility(View.GONE);
                shimmerFrameLayoutCategory.startShimmer();
                shimmerFrameLayoutCategory.setVisibility(View.VISIBLE);
                shimmerFrameLayoutRecent.startShimmer();
                shimmerFrameLayoutRecent.setVisibility(View.VISIBLE);
                itemRecyclerView.setVisibility(View.GONE);
                noInternet.setVisibility(View.VISIBLE);
                shimmerFrameLayoutCategory.stopShimmer();
                shimmerFrameLayoutCategory.setVisibility(View.GONE);
                shimmerFrameLayoutRecent.stopShimmer();
                shimmerFrameLayoutRecent.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshRecentLayout.setRefreshing(false);
//                if(itemModelList.isEmpty()){
//                    addButton.setVisibility(View.VISIBLE);
//                    addItemButton.setVisibility(View.GONE);
//                }
//                else {
//                    addButton.setVisibility(View.GONE);
//                    addItemButton.setVisibility(View.VISIBLE);
//                }
                Toast.makeText(requireActivity(), "Failed to fetch items: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private CompletableFuture<Boolean> getDetails()
    {
        CompletableFuture<Boolean> future=new CompletableFuture<>();

        Amplify.Auth.fetchUserAttributes(
                attributes -> {

                    for (AuthUserAttribute attribute : attributes) {
                        switch (attribute.getKey().getKeyString()) {
                            case "email":
                                currentEmail = attribute.getValue();
                                break;
                            case "name":
                                currentName = attribute.getValue();
                                break;
                            case "family_name":
                                currentSurname = attribute.getValue();
                                break;
                            case "address":
                                organizationID = attribute.getValue();
                                break;
                            default:
                        }
                    }
                    Log.i("progress","User attributes fetched successfully");
                    requireActivity().runOnUiThread(() -> {
                        app.setSurname(currentSurname);
                        app.setName(currentName);
                        if(app.isLoggedIn()){
                            Date currentDate = new Date();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String formattedDate = dateFormat.format(currentDate);
                            loginActivities(currentEmail, currentName, currentSurname, "sign_in", organizationID, formattedDate);
                        }
                        name.setText("Hi "+currentName);
                        categoryAdapter.setOrganizationId(organizationID);
                        recentAdapter.setOrganizationId(organizationID);
                        loadRecentItems(currentEmail, organizationID);
                        shimmerFrameLayoutName.startShimmer();
                        shimmerFrameLayoutName.setVisibility(View.GONE);
                        fetchParentCategories(0, currentEmail, organizationID);
                    });
                    future.complete(true);
                },
                error -> {
                    Log.e("AuthDemo", "Failed to fetch user attributes.", error);
                    requireActivity().runOnUiThread(() -> {
                        recentText.setVisibility(View.GONE);
                        shimmerFrameLayoutCategory.startShimmer();
                        shimmerFrameLayoutCategory.setVisibility(View.GONE);
                        shimmerFrameLayoutRecent.setVisibility(View.GONE);
                        shimmerFrameLayoutName.setVisibility(View.GONE);
                        itemRecyclerView.setVisibility(View.GONE);
                        noInternet.setVisibility(View.VISIBLE);

                        // If you want to return false in case of an error
                        future.complete(false);
                    });
                }

        );
        return future;
    }

    private void fetchParentCategories(int categoryId, String email, String organizationID) {
        Utils.fetchParentCategories(categoryId, email, organizationID, requireActivity(), new OperationCallback<List<CategoryModel>>() {
            @Override
            public void onSuccess(List<CategoryModel> result) {
                if(categoryId != 0) {
                    subCategories.clear();
                    subcategoryModelList.clear();
                    subcategoryModelList.addAll(result);
                    for (CategoryModel category : result) {
                        subCategories.add(category.getCategoryName());
                    }
                    subAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, subCategories);
                    subAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    subcategorySpinner.setAdapter(subAdapter);
                }
                else {
                    categoryModelList.clear();
                    CategoryModel allCategory = new CategoryModel("All", "all", "all");
                    CategoryModel uncategorizedCategory = new CategoryModel("Uncategorized", "uncategorized", "uncategorized");
                    categoryModelList.add(allCategory);
                    categoryModelList.add(uncategorizedCategory);
                    categoryModelList.addAll(result);
                    categoryAdapter.notifyDataSetChanged();
                    for (CategoryModel category : categoryModelList) {
                        parentCategories.add(category.getCategoryName());
                    }
                    adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, parentCategories);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                }
                category_RecyclerView.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshRecentLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(String error) {
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshRecentLayout.setRefreshing(false);
                Toast.makeText(requireActivity(), "Category Fetching failed... ", Toast.LENGTH_LONG).show();

            }
        });
    }

    private void showBottomDialog() {

        final Dialog dialog = new Dialog(requireActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheet_layout);

        LinearLayout itemLayout = dialog.findViewById(R.id.layoutItem);
        LinearLayout categoryLayout = dialog.findViewById(R.id.layoutCategory);
        LinearLayout unitLayout = dialog.findViewById(R.id.layoutUnit);
        LinearLayout groupingLayout = dialog.findViewById(R.id.layoutGrouping);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

        itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddItemActivity.class);
                logUserFlow("HomeFragment", "AddItemActivity");
                intent.putExtra("email", currentEmail);
                startActivity(intent);
            }
        });

        categoryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddCategoryActivity.class);
                logUserFlow("HomeFragment", "AddCategoryActivity");
                intent.putExtra("email", currentEmail);
                startActivity(intent);
            }
        });

        unitLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UnitActivity.class);
                logUserFlow("HomeFragment", "UnitActivity");
                startActivity(intent);
            }
        });

        groupingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddColorCodeActivity.class);
                logUserFlow("HomeFragment", "AddColorCodeActivity");
                intent.putExtra("email", currentEmail);
                startActivity(intent);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void loginActivities(String email, String name, String surname, String type, String organization_id, String time) {
        UserUtils.loginActivities(email, name, surname, type, organization_id, time, requireActivity(), new OperationCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                app.setLoggedIn(false);
//                Toast.makeText(requireActivity(), "Login Activities Successfully Saved"+ result, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(String error) {
//                loginActivities(email, name, surname, "sign_in", organization_id, time);
//                Toast.makeText(requireActivity(), "Login Activities Failed to Save", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void logActivityView(String activityName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = requireActivity().getIntent().getStringExtra("email");

        Map<String, Object> activityView = new HashMap<>();
        activityView.put("user_id", userId);
        activityView.put("activity_name", activityName);
        activityView.put("view_time", new Timestamp(new Date()));

        db.collection("activity_views")
                .add(activityView)
                .addOnSuccessListener(documentReference -> Log.d("Firestore", "Activity view logged."))
                .addOnFailureListener(e -> Log.w("Firestore", "Error logging activity view", e));
    }

    private void logSessionDuration(String activityName, long sessionDuration) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = requireActivity().getIntent().getStringExtra("email");

        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("user_id", userId);
        sessionData.put("activity_name", activityName);
        sessionData.put("session_duration", sessionDuration); // Duration in milliseconds

        db.collection("activity_sessions")
                .add(sessionData)
                .addOnSuccessListener(documentReference -> Log.d("Firestore", "Session duration logged."))
                .addOnFailureListener(e -> Log.w("Firestore", "Error logging session duration", e));
    }

    public void logUserFlow(String fromActivity, String toActivity) {
        long sessionDuration = System.currentTimeMillis() - startTime;
        logSessionDuration("HomeFragment", (sessionDuration));
        long transitionTime = System.currentTimeMillis();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = requireActivity().getIntent().getStringExtra("email");

        Map<String, Object> userFlowData = new HashMap<>();
        userFlowData.put("user_id", userId);
        userFlowData.put("previous_activity", fromActivity);
        userFlowData.put("next_activity", toActivity);
        userFlowData.put("transition_time", new Timestamp(new Date(transitionTime)));

        db.collection("user_flow")
                .add(userFlowData)
                .addOnSuccessListener(documentReference -> Log.d("Firestore", "User flow logged."))
                .addOnFailureListener(e -> Log.w("Firestore", "Error logging user flow", e));
    }

    @Override
    public void onStart() {
        super.onStart();
        logActivityView("HomeFragment");
    }

    @Override
    public void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}