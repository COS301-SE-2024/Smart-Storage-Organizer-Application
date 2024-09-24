package com.example.smartstorageorganizer.adapters;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstorageorganizer.BuildConfig;
import com.example.smartstorageorganizer.MyAmplifyApp;
import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.model.CategoryModel;
import com.example.smartstorageorganizer.model.CategoryRequestModel;
import com.example.smartstorageorganizer.model.RequestModel;
import com.example.smartstorageorganizer.model.UnitRequestModel;
import com.example.smartstorageorganizer.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RequestCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Object> mixedList;
    private static final int UNIT_REQUEST = 0;
    private static final int CATEGORY_REQUEST = 1;
    private String type;
    private MyAmplifyApp app;

    public RequestCardAdapter(Context context, List<Object> mixedList, String type) {
        this.context = context;
        this.mixedList = mixedList;
        this.type = type;
        app = (MyAmplifyApp) context.getApplicationContext();
    }

    @Override
    public int getItemViewType(int position) {
        if (mixedList.get(position) instanceof UnitRequestModel) {
            return UNIT_REQUEST;
        } else if (mixedList.get(position) instanceof CategoryRequestModel) {
            return CATEGORY_REQUEST;
        }
        return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == UNIT_REQUEST) {
            View view = LayoutInflater.from(context).inflate(R.layout.unit_request_card, parent, false);
            return new UnitRequestViewHolder(view);
        } else if (viewType == CATEGORY_REQUEST) {
            View view = LayoutInflater.from(context).inflate(R.layout.category_request_card, parent, false);
            return new CategoryRequestViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == UNIT_REQUEST) {
            UnitRequestModel request = (UnitRequestModel) mixedList.get(position);
            UnitRequestViewHolder unitHolder = (UnitRequestViewHolder) holder;

            // Set visible fields
            unitHolder.unitName.setText("Unit Name: " + request.getUnitName());
            unitHolder.requestType.setText("Request Type: " + request.getRequestType());
            unitHolder.capacity.setText("Capacity: " + request.getCapacity());
            unitHolder.userEmail.setText("Requested by: " + request.getUserEmail());

            // Set hidden fields
            unitHolder.constraints.setText("Constraints: " + request.getConstraints());
            unitHolder.dimensions.setText("Dimensions: " + request.getWidth() + " x " + request.getHeight() + " x " + request.getDepth());
            unitHolder.maxWeight.setText("Max Weight: " + request.getMaxWeight());
            unitHolder.organizationId.setText("Organization ID: " + request.getOrganizationId());
            unitHolder.requestDate.setText("Request Date: " + request.getRequestDate());
            unitHolder.status.setText("Status: " + request.getStatus());

            setStatusBackgroundColor(unitHolder.status, request.getStatus());

            toggleButtonVisibility(unitHolder.buttonsLayout, type);

            // Initially hide details
            unitHolder.detailsLayout.setVisibility(View.GONE);

            // Toggle "View More Details"
            unitHolder.viewMoreLink.setOnClickListener(v -> toggleDetailsVisibility(unitHolder.detailsLayout, unitHolder.viewMoreLink));

            unitHolder.approveButton.setOnClickListener(v -> {
                // Handle Approve action
            });

            unitHolder.rejectButton.setOnClickListener(v -> {
                // Handle Reject action
            });

        } else if (holder.getItemViewType() == CATEGORY_REQUEST) {
            CategoryRequestModel request = (CategoryRequestModel) mixedList.get(position);
            CategoryRequestViewHolder categoryHolder = (CategoryRequestViewHolder) holder;

            // Set visible fields
            categoryHolder.categoryName.setText("Category Name: " + request.getCategoryName());
            categoryHolder.requestType.setText("Request Type: " + request.getRequestType());
            categoryHolder.categoryType.setText("Category Type: Parent Category");
            categoryHolder.userEmail.setText("Requested by: " + request.getUserEmail());
            categoryHolder.organizationId.setText("Organization ID: " + request.getOrganizationId());
            categoryHolder.requestDate.setText("Request Date: " + request.getRequestDate());
            categoryHolder.status.setText("Status: " + request.getStatus());

            setStatusBackgroundColor(categoryHolder.status, request.getStatus());

            toggleButtonVisibility(categoryHolder.buttonsLayout, type);

            // Initially hide details
            categoryHolder.detailsLayout.setVisibility(View.GONE);

            // Toggle "View More Details"
            categoryHolder.viewMoreLink.setOnClickListener(v -> toggleDetailsVisibility(categoryHolder.detailsLayout, categoryHolder.viewMoreLink));

            categoryHolder.approveButton.setOnClickListener(v -> {
                // Handle Approve action
            });

            categoryHolder.rejectButton.setOnClickListener(v -> {
                // Handle Reject action
            });
        }
    }

    @Override
    public int getItemCount() {
        return mixedList.size();
    }

    // Helper to toggle button visibility based on the request type (approved/pending)
    private void toggleButtonVisibility(LinearLayout buttonsLayout, String type) {
        if ("approved".equals(type)) {
            buttonsLayout.setVisibility(View.GONE);
        } else {
            buttonsLayout.setVisibility(View.VISIBLE);
        }
    }

    // Helper to toggle details visibility
    private void toggleDetailsVisibility(View detailsLayout, TextView viewMoreLink) {
        if (detailsLayout.getVisibility() == View.GONE) {
            detailsLayout.setVisibility(View.VISIBLE);
            viewMoreLink.setText("View Less Details");
        } else {
            detailsLayout.setVisibility(View.GONE);
            viewMoreLink.setText("View More Details");
        }
    }

    // Helper to set status background color
    private void setStatusBackgroundColor(TextView statusView, String status) {
        int color;
        if ("pending".equals(status)) {
            color = Color.parseColor("#CC0000");
        } else {
            color = Color.parseColor("#00DC32");
            statusView.setBackgroundColor(Color.parseColor("#D3F8D3"));
        }
        statusView.setTextColor(color);
    }

    // ViewHolder for Unit Requests
    static class UnitRequestViewHolder extends RecyclerView.ViewHolder {
        TextView unitName, requestType, capacity, userEmail, constraints, dimensions, maxWeight, organizationId, requestDate, status, viewMoreLink;
        LinearLayout detailsLayout, buttonsLayout;
        Button approveButton, rejectButton;

        UnitRequestViewHolder(View itemView) {
            super(itemView);
            unitName = itemView.findViewById(R.id.unitName);
            capacity = itemView.findViewById(R.id.capacity);
            userEmail = itemView.findViewById(R.id.userEmail);
            constraints = itemView.findViewById(R.id.constraints);
            dimensions = itemView.findViewById(R.id.dimensions);
            maxWeight = itemView.findViewById(R.id.maxWeight);
            organizationId = itemView.findViewById(R.id.organizationId);
            requestDate = itemView.findViewById(R.id.requestDate);
            requestType = itemView.findViewById(R.id.requestType);
            status = itemView.findViewById(R.id.status);
            viewMoreLink = itemView.findViewById(R.id.viewMoreLink);
            detailsLayout = itemView.findViewById(R.id.detailsLayout);
            approveButton = itemView.findViewById(R.id.approveButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);
            buttonsLayout = itemView.findViewById(R.id.buttonsLayout);
        }
    }

    // ViewHolder for Category Requests
    static class CategoryRequestViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName, categoryType, userEmail, organizationId, requestDate, status, viewMoreLink, requestType;
        LinearLayout detailsLayout, buttonsLayout;
        Button approveButton, rejectButton;

        CategoryRequestViewHolder(View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryName);
            categoryType = itemView.findViewById(R.id.categoryType);
            userEmail = itemView.findViewById(R.id.userEmail);
            organizationId = itemView.findViewById(R.id.organizationId);
            requestDate = itemView.findViewById(R.id.requestDate);
            requestType = itemView.findViewById(R.id.requestType);
            status = itemView.findViewById(R.id.status);
            viewMoreLink = itemView.findViewById(R.id.viewMoreLink);
            detailsLayout = itemView.findViewById(R.id.detailsLayout);
            approveButton = itemView.findViewById(R.id.approveButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);
            buttonsLayout = itemView.findViewById(R.id.buttonsLayout);
        }
    }


//    private void showRequestSentDialog(int position) {
//        UnitRequestModel cardItem = cardItemList.get(position);
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        LayoutInflater inflater = LayoutInflater.from(context);
//        View dialogView = inflater.inflate(R.layout.pending_popup, null);
//        builder.setView(dialogView);
//
//        Button closeButton = dialogView.findViewById(R.id.closeButton);
//
//        AlertDialog dialog = builder.create();
//
//        closeButton.setOnClickListener(v -> {
//            approveRequest(cardItem.getRequestId(), position);
//            dialog.dismiss();
//        });
////
////        rejectButton.setOnClickListener(v -> {
////            // Handle reject action
////            // Change the status to Rejected
////            // Example: requestList.get(position).setStatus("Rejected");
////            // notifyDataSetChanged();
////            dialog.dismiss();
////        });
//
//        dialog.show();
//    }

    // Function to approve the unit request
    public void approveRequest(String documentId, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        UnitRequestModel cardItem = cardItemList.get(position);

        // Update the request's status to "approved"
        db.collection("unit_requests")
                .document(documentId)
                .update("status", "approved")
                .addOnSuccessListener(aVoid -> {
                    Log.i("Firestore", "Request approved successfully.");
                    // Now you can call your API to create the unit
                    // Optionally trigger the unit creation logic here
//                    createUnitAPI(cardItem.getUnitName(), cardItem.getCapacity(), cardItem.getConstraints(), cardItem.getWidth(), cardItem.getHeight(), cardItem.getDepth(), cardItem.getMaxWeight());
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error approving request", e);
                });
    }

    public void createUnitAPI(String unitName, String capacity, String constraints, String width, String height, String depth, String maxweight) {
        String json = "{\"Unit_Name\":\"" + unitName + "\", \"Unit_Capacity\":\"" + capacity + "\", \"constraints\":\"" + constraints + "\",\"Unit_QR\":\"1\",\"unit_capacity_used\":\"0\", \"width\":\"" + width + "\", \"height\":\"" + height + "\", \"depth\":\"" + depth + "\", \"maxweight\":\"" + maxweight + "\", \"username\":\"" + app.getEmail() + "\", \"organization_id\":\"" + app.getOrganizationID() + "\", \"Unit_QR\":\"" + "QR1" + "\"}";

        MediaType jsonObject = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String apiUrl = BuildConfig.AddUnitEndpoint;
        RequestBody body = RequestBody.create(json, jsonObject);

        Utils.getUserToken().thenAccept(token -> {
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .header("Authorization", token)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                    Log.e("Unit Request Method", "POST request failed", e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        Log.i("Unit Response", "Unit created successfully");
                    } else {
                        Log.e("Unit Request Method", "POST request failed: " + response);
                    }
                }
            });
        }).exceptionally(ex -> {
            Log.e("TokenError", "Failed to get user token", ex);
            return null;
        });
    }

}

