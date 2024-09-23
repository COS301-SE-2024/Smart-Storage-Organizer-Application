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
import com.example.smartstorageorganizer.model.UnitRequestModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;
import java.util.Objects;

public class RequestCardAdapter extends RecyclerView.Adapter<RequestCardAdapter.CardViewHolder> {

    private Context context;
    private List<UnitRequestModel> cardItemList;

    public RequestCardAdapter(Context context, List<UnitRequestModel> cardItemList) {
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
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.pending_popup, null);
        builder.setView(dialogView);

        Button closeButton = dialogView.findViewById(R.id.closeButton);

        AlertDialog dialog = builder.create();

        closeButton.setOnClickListener(v -> {

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

