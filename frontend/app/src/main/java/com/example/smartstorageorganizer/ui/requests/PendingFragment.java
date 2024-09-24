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
import com.example.smartstorageorganizer.model.RequestModel;
import com.example.smartstorageorganizer.model.UnitRequestModel;
import com.example.smartstorageorganizer.model.UserModel;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.UserUtils;
import com.example.smartstorageorganizer.utils.Utils;
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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PendingFragment extends Fragment {
    View root;
    List<Object> mixedList;
    List<UnitRequestModel> cardItemList;
    List<CategoryRequestModel> cardCategoryList;
    List<CategoryRequestModel> cardDeleteCategoryList;
    List<CategoryRequestModel> cardModifyCategoryList;
    RequestCardAdapter requestAdapter;
    RequestCardAdapter adapter;
    private MyAmplifyApp app;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        root =  inflater.inflate(R.layout.fragment_pending, container, false);
        app = (MyAmplifyApp) requireActivity().getApplicationContext();


        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        mixedList = new ArrayList<>();
        cardItemList = new ArrayList<>();
        cardCategoryList = new ArrayList<>();
        cardDeleteCategoryList = new ArrayList<>();
        cardModifyCategoryList = new ArrayList<>();

        adapter = new RequestCardAdapter(getContext(), mixedList, "pending");
        recyclerView.setAdapter(adapter);


//        fetchPendingRequests();

        return root;
    }

    // Fetch all pending unit requests from Firestore and add them to the list
    public void fetchPendingRequests() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("unit_requests")
                .whereEqualTo("status", "pending")
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

                        // Now your cardItemList contains all pending unit requests
                        // You can now update your UI with the cardItemList
                        Log.i("Firestore", "Pending requests fetched: " + cardItemList.size());
                    } else {
                        Log.e("Firestore", "Error getting pending requests: ", task.getException());
                    }
                });
    }

    public void fetchCategoryPendingRequests() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("category_requests")
                .whereEqualTo("status", "pending")
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

                        // Now your cardItemList contains all pending unit requests
                        // You can now update your UI with the cardItemList
                        Log.i("Firestore", "Pending requests fetched: " + cardItemList.size());
                    } else {
                        Log.e("Firestore", "Error getting pending requests: ", task.getException());
                    }
                });
    }

    public void fetchDeleteCategoryPendingRequests() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("category_requests")
                .whereEqualTo("status", "pending")
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

                        // Now your cardItemList contains all pending unit requests
                        // You can now update your UI with the cardItemList
                        Log.i("Firestore", "Pending requests fetched: " + cardItemList.size());
                    } else {
                        Log.e("Firestore", "Error getting pending requests: ", task.getException());
                    }
                });
    }

    public void fetchModifyCategoryPendingRequests() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("category_requests")
                .whereEqualTo("status", "pending")
                .whereEqualTo("requestType", "Modify Category")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Extract data from Firestore document
                            String documentId = document.getString("documentId");
                            String categoryName = document.getString("currentCategoryName");
                            String newCategoryName = document.getString("newCategoryName");
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
                            CategoryRequestModel categoryRequest = new CategoryRequestModel(categoryName, Integer.parseInt(parentCategory), formattedDate, status, userEmail, newCategoryName, documentId, organizationId, requestType);

                            // Add to cardItemList
                            cardModifyCategoryList.add(categoryRequest);
                        }
                        mixedList.addAll(cardModifyCategoryList);  // Add all category requests
                        adapter.notifyDataSetChanged();

                        // Now your cardItemList contains all pending unit requests
                        // You can now update your UI with the cardItemList
                        Log.i("Firestore", "Pending requests fetched: " + cardModifyCategoryList.size());
                    } else {
                        Log.e("Firestore", "Error getting pending requests: ", task.getException());
                    }
                });
    }

    public String convertTimestampToDate(Timestamp timestamp) {
        // Convert the Timestamp to a Date object
        Date date = timestamp.toDate();

        // Define the date format you want
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        // Format the date to the desired string
        return dateFormat.format(date);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData() {
        mixedList.clear();
        cardItemList.clear();
        cardCategoryList.clear();
        cardDeleteCategoryList.clear();
        cardModifyCategoryList.clear();
        adapter.notifyDataSetChanged();

        fetchPendingRequests();
        fetchCategoryPendingRequests();
        fetchDeleteCategoryPendingRequests();
        fetchModifyCategoryPendingRequests();
    }

}