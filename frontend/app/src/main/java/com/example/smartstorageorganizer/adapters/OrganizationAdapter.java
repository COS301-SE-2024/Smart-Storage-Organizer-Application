package com.example.smartstorageorganizer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.model.OrganizationModel;

import java.util.List;

public class OrganizationAdapter extends RecyclerView.Adapter<OrganizationAdapter.CardViewHolder> {
    private Context context;
    private List<OrganizationModel> organizationList;

    public OrganizationAdapter(Context context, List<OrganizationModel> usersList) {
        this.context = context;
        this.organizationList = usersList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.organization_layout, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        OrganizationModel cardItem = organizationList.get(position);
        holder.name.setText(cardItem.getOrganizationName());
        holder.organizationId.setText(cardItem.getOrganizationId());
        holder.date.setText(cardItem.getCreatedAt());

        holder.itemView.setOnClickListener(v -> {
            // Handle item click if needed
        });

    }

    @Override
    public int getItemCount() {
        return organizationList.size();
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView date, name, organizationId, approveButton;
        ImageView moreButton;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            name = itemView.findViewById(R.id.name);
            organizationId = itemView.findViewById(R.id.id);
            approveButton = itemView.findViewById(R.id.approveButton);
            moreButton = itemView.findViewById(R.id.moreButton);
        }
    }


}