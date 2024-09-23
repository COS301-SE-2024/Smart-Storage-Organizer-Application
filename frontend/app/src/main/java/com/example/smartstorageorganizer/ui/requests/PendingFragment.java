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

import com.example.smartstorageorganizer.LoginActivity;
import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.adapters.RequestCardAdapter;
import com.example.smartstorageorganizer.databinding.FragmentPendingBinding;
import com.example.smartstorageorganizer.model.CategoryModel;
import com.example.smartstorageorganizer.model.RequestModel;
import com.example.smartstorageorganizer.model.UnitRequestModel;
import com.example.smartstorageorganizer.model.UserModel;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.UserUtils;
import com.example.smartstorageorganizer.utils.Utils;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PendingFragment extends Fragment {
    View root;
    List<UnitRequestModel> cardItemList;
    RequestCardAdapter requestAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        root =  inflater.inflate(R.layout.fragment_pending, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        cardItemList = new ArrayList<>();

        requestAdapter = new RequestCardAdapter(getContext(), cardItemList);
        recyclerView.setAdapter(requestAdapter);

        fetchPendingRequests();

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
                            assert requestDate != null;
                            String formattedDate = convertTimestampToDate(requestDate);
                            String status = document.getString("status");

                            // Create a UnitRequestModel object
                            UnitRequestModel unitRequest = new UnitRequestModel(
                                    unitName, capacity, constraints, width, height, depth, maxWeight, userEmail, organizationId, status, formattedDate, documentId
                            );

                            // Add to cardItemList
                            cardItemList.add(unitRequest);
                        }
                        requestAdapter.notifyDataSetChanged();

                        // Now your cardItemList contains all pending unit requests
                        // You can now update your UI with the cardItemList
                        Log.i("Firestore", "Pending requests fetched: " + cardItemList.size());
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

}
