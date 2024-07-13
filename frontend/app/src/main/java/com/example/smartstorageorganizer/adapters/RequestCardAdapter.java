package com.example.smartstorageorganizer.adapters;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.model.CategoryModel;
import com.example.smartstorageorganizer.model.RequestModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;
import java.util.Objects;

public class RequestCardAdapter extends RecyclerView.Adapter<RequestCardAdapter.CardViewHolder> {

    private Context context;
    private List<RequestModel> cardItemList;

    public RequestCardAdapter(Context context, List<RequestModel> cardItemList) {
        this.context = context;
        this.cardItemList = cardItemList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_layout, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        RequestModel cardItem = cardItemList.get(position);
        holder.date.setText(cardItem.getDate());
        holder.name.setText(cardItem.getName());
        holder.description.setText(cardItem.getDescription());
        holder.status.setText(cardItem.getStatus());
        int color;
        if(Objects.equals(cardItem.getStatus(), "Pending")) {
            color = Color.parseColor("#CC0000");
        }
        else {
            color = Color.parseColor("#00DC32");
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
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.pending_popup, null);
        builder.setView(dialogView);

        // Example data, replace this with actual data from your request list
//        String requestType = "Edit Item Name and Description";
//        String date = "12/07/2024";
//        String currentItemName = "Old Item Name"; // Replace with actual data
//        String currentDescription = "Old Description"; // Replace with actual data
//        String newName = "Eco-Friendly Notebooks";
//        String newDescription = "Notebooks made from recycled materials.";
//        String status = "Pending";

        // Initialize dialog views
//        TextView requestTypeTextView = dialogView.findViewById(R.id.requestType);
//        TextView dateTextView = dialogView.findViewById(R.id.date);
//        TextView currentItemNameTextView = dialogView.findViewById(R.id.currentOne);
//        TextView currentDescriptionTextView = dialogView.findViewById(R.id.currentTwo);
//        TextView newNameTextView = dialogView.findViewById(R.id.changeOne);
//        TextView newDescriptionTextView = dialogView.findViewById(R.id.changeTwo);
//        TextView statusTextView = dialogView.findViewById(R.id.status);

        Button closeButton = dialogView.findViewById(R.id.closeButton);
//        Button rejectButton = dialogView.findViewById(R.id.reject_button);

        AlertDialog dialog = builder.create();

        // Set data to dialog views
//        requestTypeTextView.setText(requestType);
//        dateTextView.setText(date);
//        currentItemNameTextView.setText(currentItemName);
//        currentDescriptionTextView.setText(currentDescription);
//        newNameTextView.setText(newName);
//        newDescriptionTextView.setText(newDescription);
//        statusTextView.setText(status);

        // Set button click listeners
        closeButton.setOnClickListener(v -> {
            // Handle approve action
            // Update the item details and change the status to Approved
            // Example: requestList.get(position).setStatus("Approved");
            // notifyDataSetChanged();
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

}

