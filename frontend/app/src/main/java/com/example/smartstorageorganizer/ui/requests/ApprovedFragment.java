package com.example.smartstorageorganizer.ui.requests;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartstorageorganizer.BuildConfig;
import com.example.smartstorageorganizer.LoginActivity;
import com.example.smartstorageorganizer.MyAmplifyApp;
import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.adapters.CategoryRequestCardAdapter;
import com.example.smartstorageorganizer.adapters.RequestCardAdapter;
import com.example.smartstorageorganizer.databinding.FragmentPendingBinding;
import com.example.smartstorageorganizer.model.CategoryModel;
import com.example.smartstorageorganizer.model.CategoryRequestModel;
import com.example.smartstorageorganizer.model.ItemRequestModel;
import com.example.smartstorageorganizer.model.ModifyItemRequestModel;
import com.example.smartstorageorganizer.model.RequestModel;
import com.example.smartstorageorganizer.model.UnitRequestModel;
import com.example.smartstorageorganizer.model.UserModel;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.UserUtils;
import com.example.smartstorageorganizer.utils.Utils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApprovedFragment extends Fragment {
    View root;
    List<Object> mixedList;
    List<UnitRequestModel> cardItemList;
    List<CategoryRequestModel> cardCategoryList;
    List<CategoryRequestModel> cardDeleteCategoryList;
    List<ItemRequestModel> cardDeleteItemList;
    List<ModifyItemRequestModel> cardModifyItemList;
    RequestCardAdapter requestAdapter;
    RequestCardAdapter adapter;
    ShimmerFrameLayout skeletonLoader;
    private TextView noRequest;
    private MyAmplifyApp app;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        root =  inflater.inflate(R.layout.fragment_approved, container, false);
        app = (MyAmplifyApp) requireActivity().getApplicationContext();


        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        skeletonLoader = root.findViewById(R.id.skeletonLoader);
        noRequest = root.findViewById(R.id.text);

        mixedList = new ArrayList<>();
        cardItemList = new ArrayList<>();
        cardCategoryList = new ArrayList<>();
        cardDeleteCategoryList = new ArrayList<>();
        cardDeleteItemList = new ArrayList<>();
        cardModifyItemList = new ArrayList<>();

        adapter = new RequestCardAdapter(getContext(), mixedList, "approved");
        recyclerView.setAdapter(adapter);


//        fetchPendingRequests();

        return root;
    }

    // Fetch all pending unit requests from Firestore and add them to the list
    public void fetchPendingRequests() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("unit_requests")
                .whereEqualTo("status", "approved")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Extract data from Firestore document
                            String documentId = document.getString("documentId");
                            String unitName = document.getString("unitName");
                            String capacity = document.getString("capacity");
                            String constraints = document.getString("constraints");
                            String width = document.getString("width");
                            String height = document.getString("height");
                            String depth = document.getString("depth");
                            String maxWeight = document.getString("maxweight");
                            String userEmail = document.getString("userEmail");
                            String organizationId = document.getString("organizationId");
                            Timestamp requestDate = document.getTimestamp("requestDate");
                            String requestType = document.getString("requestType");
                            assert requestDate != null;
                            String formattedDate = convertTimestampToDate(requestDate);
                            String status = document.getString("status");

                            // Create a UnitRequestModel object
                            UnitRequestModel unitRequest = new UnitRequestModel(
                                    unitName, capacity, constraints, width, height, depth, maxWeight, userEmail, organizationId, status, formattedDate, documentId, requestType
                            );

                            // Add to cardItemList
                            cardItemList.add(unitRequest);
                        }
                        mixedList.addAll(cardItemList);  // Add all unit requests
                        adapter.notifyDataSetChanged();
                        requireActivity().runOnUiThread(() -> skeletonLoader.setVisibility(View.GONE));

                        // Now your cardItemList contains all pending unit requests
                        // You can now update your UI with the cardItemList
                        Log.i("Firestore", "Pending requests fetched: " + cardItemList.size());
                    } else {
                        Log.e("Firestore", "Error getting pending requests: ", task.getException());
                    }
                });
        checkForNoRequests();
    }

    public void fetchCategoryPendingRequests() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("category_requests")
                .whereEqualTo("status", "approved")
                .whereEqualTo("requestType", "Add Category")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Extract data from Firestore document
                            String documentId = document.getString("documentId");
                            String categoryName = document.getString("categoryName");
                            Object parentCategoryObj = document.get("parentCategory");

                            String parentCategory;
                            if (parentCategoryObj instanceof Number) {
                                parentCategory = String.valueOf(parentCategoryObj);
                            } else {
                                parentCategory = (String) parentCategoryObj;
                            }

                            String url = document.getString("url");
                            String userEmail = document.getString("userEmail");
                            String organizationId = document.getString("organizationId");
                            Timestamp requestDate = document.getTimestamp("requestDate");
                            String requestType = document.getString("requestType");
                            assert requestDate != null;
                            String formattedDate = convertTimestampToDate(requestDate);
                            String status = document.getString("status");

                            // Create a UnitRequestModel object
                            CategoryRequestModel categoryRequest = new CategoryRequestModel(categoryName, Integer.parseInt(parentCategory), formattedDate, status, userEmail, url, documentId, organizationId, requestType);

                            // Add to cardItemList
                            cardCategoryList.add(categoryRequest);
                        }
                        mixedList.addAll(cardCategoryList);  // Add all category requests
                        adapter.notifyDataSetChanged();
                        requireActivity().runOnUiThread(() -> skeletonLoader.setVisibility(View.GONE));

                        // Now your cardItemList contains all pending unit requests
                        // You can now update your UI with the cardItemList
                        Log.i("Firestore", "Pending requests fetched: " + cardItemList.size());
                    } else {
                        Log.e("Firestore", "Error getting pending requests: ", task.getException());
                    }
                });
        checkForNoRequests();
    }

    public void fetchDeleteCategoryPendingRequests() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("category_requests")
                .whereEqualTo("status", "approved")
                .whereEqualTo("requestType", "Delete Category")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Extract data from Firestore document
                            String documentId = document.getString("documentId");
                            String categoryName = document.getString("categoryName");
                            Object parentCategoryObj = document.get("id");

                            String parentCategory;
                            if (parentCategoryObj instanceof Number) {
                                parentCategory = String.valueOf(parentCategoryObj);
                            } else {
                                parentCategory = (String) parentCategoryObj;
                            }

                            String userEmail = document.getString("userEmail");
                            String organizationId = document.getString("organizationId");
                            Timestamp requestDate = document.getTimestamp("requestDate");
                            String requestType = document.getString("requestType");
                            assert requestDate != null;
                            String formattedDate = convertTimestampToDate(requestDate);
                            String status = document.getString("status");

                            // Create a UnitRequestModel object
                            CategoryRequestModel categoryRequest = new CategoryRequestModel(categoryName, Integer.parseInt(parentCategory), formattedDate, status, userEmail, "", documentId, organizationId, requestType);

                            // Add to cardItemList
                            cardDeleteCategoryList.add(categoryRequest);
                        }
                        mixedList.addAll(cardDeleteCategoryList);  // Add all category requests
                        adapter.notifyDataSetChanged();
                        requireActivity().runOnUiThread(() -> skeletonLoader.setVisibility(View.GONE));

                        // Now your cardItemList contains all pending unit requests
                        // You can now update your UI with the cardItemList
                        Log.i("Firestore", "Pending requests fetched: " + cardDeleteCategoryList.size());
                    } else {
                        Log.e("Firestore", "Error getting pending requests: ", task.getException());
                    }
                });
        checkForNoRequests();
    }

    public void fetchDeleteItemPendingRequests() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("item_requests")
                .whereEqualTo("status", "approved")
                .whereEqualTo("requestType", "Delete Item")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String documentId = document.getString("documentId");
                            String itemId = document.getString("itemId");
                            String itemName = document.getString("itemName");
                            String itemDescription = document.getString("itemDescription");
                            String location = document.getString("location");
                            String parentCategory = document.getString("parentCategory");
                            String colorCode = document.getString("colorCode");
                            String subcategory = document.getString("subcategory");
                            String userEmail = document.getString("userEmail");
                            String organizationId = document.getString("organizationId");
                            Timestamp requestDate = document.getTimestamp("requestDate");
                            String requestType = document.getString("requestType");
                            assert requestDate != null;
                            String formattedDate = convertTimestampToDate(requestDate);
                            String status = document.getString("status");

                            // Create a UnitRequestModel object
                            ItemRequestModel itemRequest = new ItemRequestModel(
                                    documentId, itemName, itemDescription, location, parentCategory, subcategory, colorCode, userEmail, organizationId, formattedDate, requestType, status, itemId
                            );
                            // Add to cardItemList
                            cardDeleteItemList.add(itemRequest);
                        }
                        mixedList.addAll(cardDeleteItemList);  // Add all category requests
                        adapter.notifyDataSetChanged();
                        requireActivity().runOnUiThread(() -> skeletonLoader.setVisibility(View.GONE));

                        // Now your cardItemList contains all pending unit requests
                        // You can now update your UI with the cardItemList
                        Log.i("Firestore", "Pending requests fetched: " + cardDeleteItemList.size());
                    } else {
                        Log.e("Firestore", "Error getting pending requests: ", task.getException());
                    }
                });
        checkForNoRequests();
    }

    public void fetchPendingModifyItemRequests() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("item_requests")
                .whereEqualTo("status", "approved")
                .whereEqualTo("requestType", "Modify Item")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Extract data from Firestore document
                            String documentId = document.getString("documentId");
                            String itemId = document.getString("itemId");
                            String itemName = document.getString("itemName");
                            String itemDescription = document.getString("itemDescription");
                            String location = document.getString("location");
                            String quantity = document.getString("quantity");
                            String qrcode = document.getString("qrcode");
                            String barcode = document.getString("barcode");
                            String parentCategory = document.getString("parentCategory");
                            String parentCategoryId = document.getString("parentCategoryId");
                            String colorCode = document.getString("colorCode");
                            String subcategory = document.getString("subcategory");
                            String subcategoryId = document.getString("subcategoryId");
                            String userEmail = document.getString("userEmail");
                            String image = document.getString("image");
                            String organizationId = document.getString("organizationId");
                            Timestamp requestDate = document.getTimestamp("requestDate");
                            String requestType = document.getString("requestType");
                            assert requestDate != null;
                            String formattedDate = convertTimestampToDate(requestDate);
                            String status = document.getString("status");

                            // Create a UnitRequestModel object
                            ModifyItemRequestModel itemRequest = new ModifyItemRequestModel(
                                    documentId, itemName, itemDescription, location, parentCategory, subcategory, colorCode, userEmail, organizationId, formattedDate, requestType, status, itemId
                            );

                            itemRequest.setParentCategoryId(parentCategoryId);
                            itemRequest.setSubCategoryId(subcategoryId);
                            itemRequest.setQrcode(qrcode);
                            itemRequest.setBarcode(barcode);
                            itemRequest.setQuantity(quantity);
                            itemRequest.setImage(image);

                            Map<String, Map<String, String>> changedFields = (Map<String, Map<String, String>>) document.get("changedFields");

                            // Now you can use the 'changedFields' map
                            if (changedFields != null) {
                                // Example: Fetch old and new values for Subcategory
                                if(changedFields.get("Subcategory") != null){
                                    String oldSubcategory = Objects.requireNonNull(changedFields.get("Subcategory")).get("oldValue");
                                    String newSubcategory = Objects.requireNonNull(changedFields.get("Subcategory")).get("newValue");
                                    itemRequest.setNewSubcategory(newSubcategory);
                                    itemRequest.setOldSubcategory(oldSubcategory);
                                }
                                if(changedFields.get("ItemName") != null){
                                    String oldItemName = Objects.requireNonNull(changedFields.get("ItemName")).get("oldValue");
                                    String newItemName = Objects.requireNonNull(changedFields.get("ItemName")).get("newValue");
                                    itemRequest.setNewItem(newItemName);
                                    itemRequest.setOldItem(oldItemName);
                                }
                                if(changedFields.get("Category") != null){
                                    String oldCategory = Objects.requireNonNull(changedFields.get("Category")).get("oldValue");
                                    String newCategory = Objects.requireNonNull(changedFields.get("Category")).get("newValue");
                                    itemRequest.setNewParentCategory(newCategory);
                                    itemRequest.setOldParentCategory(oldCategory);
                                }
                                if(changedFields.get("ItemDescription") != null){
                                    String oldItemDescription = Objects.requireNonNull(changedFields.get("ItemDescription")).get("oldValue");
                                    String newItemDescription = Objects.requireNonNull(changedFields.get("ItemDescription")).get("newValue");
                                    itemRequest.setNewDescription(newItemDescription);
                                    itemRequest.setOldDescription(oldItemDescription);
                                }
                                if(changedFields.get("ItemQuantity") != null){
                                    String oldItemQuantity = Objects.requireNonNull(changedFields.get("ItemQuantity")).get("oldValue");
                                    String newItemQuantity = Objects.requireNonNull(changedFields.get("ItemQuantity")).get("newValue");
                                    itemRequest.setNewQuantity(newItemQuantity);
                                    itemRequest.setOldQuantity(oldItemQuantity);
                                }
                            }

                            // Add to cardItemList
                            cardModifyItemList.add(itemRequest);
                        }
                        mixedList.addAll(cardModifyItemList);  // Add all unit requests
                        adapter.notifyDataSetChanged();
                        requireActivity().runOnUiThread(() -> skeletonLoader.setVisibility(View.GONE));

                        // Now your cardItemList contains all pending unit requests
                        // You can now update your UI with the cardItemList
                        Log.i("Firestore", "Pending requests fetched: " + cardModifyItemList.size());
                    } else {
                        Log.e("Firestore", "Error getting pending requests: ", task.getException());
                    }
                });
        checkForNoRequests();
    }

    public String convertTimestampToDate(Timestamp timestamp) {
        // Convert the Timestamp to a Date object
        Date date = timestamp.toDate();

        // Define the date format you want
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        // Format the date to the desired string
        return dateFormat.format(date);
    }

    private void checkForNoRequests() {
        // After all the fetch operations are complete
        if (mixedList.isEmpty()) {
            // If the list is empty, show the noRequest TextView
            noRequest.setVisibility(View.VISIBLE);
        } else {
            // Otherwise, hide the noRequest TextView
            noRequest.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData() {
        skeletonLoader = root.findViewById(R.id.skeletonLoader);
        skeletonLoader.setVisibility(View.VISIBLE);
        noRequest.setVisibility(View.GONE);
        mixedList.clear();
        cardItemList.clear();
        cardCategoryList.clear();
        cardDeleteCategoryList.clear();
        cardDeleteItemList.clear();
        cardModifyItemList.clear();
        adapter.notifyDataSetChanged();

        fetchPendingRequests();
        fetchCategoryPendingRequests();
        fetchDeleteCategoryPendingRequests();
        fetchDeleteItemPendingRequests();
        fetchPendingModifyItemRequests();

        checkForNoRequests();
    }
}