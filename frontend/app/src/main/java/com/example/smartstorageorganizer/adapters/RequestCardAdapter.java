package com.example.smartstorageorganizer.adapters;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstorageorganizer.BuildConfig;
import com.example.smartstorageorganizer.MyAmplifyApp;
import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.model.CategoryModel;
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

public class RequestCardAdapter extends RecyclerView.Adapter<RequestCardAdapter.CardViewHolder> {

    private Context context;
    private List<UnitRequestModel> cardItemList;
    private MyAmplifyApp app;

    public RequestCardAdapter(Context context, List<UnitRequestModel> cardItemList) {
        this.context = context;
        this.cardItemList = cardItemList;
        app = (MyAmplifyApp) context.getApplicationContext();

    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_layout, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        UnitRequestModel cardItem = cardItemList.get(position);
        holder.date.setText((cardItem.getRequestDate()).toString());
        holder.name.setText(cardItem.getUnitName());
        holder.description.setText(cardItem.getUserEmail());
        holder.status.setText(cardItem.getStatus());
        int color;
        if(Objects.equals(cardItem.getStatus(), "pending")) {
            color = Color.parseColor("#CC0000");
        }
        else {
            color = Color.parseColor("#00DC32");
            holder.status.setBackgroundColor(Color.parseColor("#D3F8D3"));
        }
        holder.status.setTextColor(color);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRequestSentDialog(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return cardItemList.size();
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView date, name, description, status;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            name = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);
            status = itemView.findViewById(R.id.status);
        }
    }

    private void showRequestSentDialog(int position) {
        UnitRequestModel cardItem = cardItemList.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.pending_popup, null);
        builder.setView(dialogView);

        Button closeButton = dialogView.findViewById(R.id.closeButton);

        AlertDialog dialog = builder.create();

        closeButton.setOnClickListener(v -> {
            approveRequest(cardItem.getRequestId(), position);
            dialog.dismiss();
        });
//
//        rejectButton.setOnClickListener(v -> {
//            // Handle reject action
//            // Change the status to Rejected
//            // Example: requestList.get(position).setStatus("Rejected");
//            // notifyDataSetChanged();
//            dialog.dismiss();
//        });

        dialog.show();
    }

    // Function to approve the unit request
    public void approveRequest(String documentId, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        UnitRequestModel cardItem = cardItemList.get(position);

        // Update the request's status to "approved"
        db.collection("unit_requests")
                .document(documentId)
                .update("status", "approved")
                .addOnSuccessListener(aVoid -> {
                    Log.i("Firestore", "Request approved successfully.");
                    // Now you can call your API to create the unit
                    // Optionally trigger the unit creation logic here
                    createUnitAPI(cardItem.getUnitName(), cardItem.getCapacity(), cardItem.getConstraints(), cardItem.getWidth(), cardItem.getHeight(), cardItem.getDepth(), cardItem.getMaxWeight());
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

