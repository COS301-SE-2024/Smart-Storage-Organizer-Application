package com.example.smartstorageorganizer.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.model.UserModel;

import java.util.List;
import java.util.Objects;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.CardViewHolder>{
    private Context context;
    private List<UserModel> usersList;

    public UsersAdapter(Context context, List<UserModel> usersList) {
        this.context = context;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_layout, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        UserModel cardItem = usersList.get(position);
//        holder.date.setText(cardItem.getDate());
        holder.name.setText(cardItem.getName());
        if (Objects.equals(cardItem.getStatus(), "Active")) {
            holder.declineButton.setVisibility(View.GONE);
            holder.declineButton.setPadding(16, 8, 16, 8);
            holder.approveButton.setText(cardItem.getStatus());
        }
//        holder.description.setText(cardItem.getDescription());
//        holder.status.setText(cardItem.getStatus());
//        int color;
//        if(Objects.equals(cardItem.getStatus(), "Pending")) {
//            color = Color.parseColor("#CC0000");
//        }
//        else {
//            color = Color.parseColor("#00DC32");
//        }
//        holder.status.setTextColor(color);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showRequestSentDialog(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView date, name, description, status, approveButton, declineButton;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            name = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);
            status = itemView.findViewById(R.id.status);
            approveButton = itemView.findViewById(R.id.approveButton);
            declineButton = itemView.findViewById(R.id.declineButton);
        }
    }
}
