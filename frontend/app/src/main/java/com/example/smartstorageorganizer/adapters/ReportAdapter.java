package com.example.smartstorageorganizer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.model.ItemModel;
import com.example.smartstorageorganizer.model.ReportModel;

import java.util.List;
import java.util.Objects;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {

    private final Context context;
    private final List<ReportModel> reportModelList;
    private String organizationID;


    public ReportAdapter(Context context, List<ReportModel> reportModelList) {
        this.context = context;
        this.reportModelList = reportModelList;
    }

    @NonNull
    @Override
    public ReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.report_layout, parent, false));
    }

    public void onBindViewHolder(@NonNull ReportAdapter.ViewHolder holder, int position) {
        holder.name.setText(reportModelList.get(position).getName());
        holder.description.setText(reportModelList.get(position).getDescription());

        if (Objects.equals(reportModelList.get(position).getName(), "Items")) {
            holder.reportImage.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.item));

//            Glide.with(context).load(reportModelList.get(position).getItemImage()).placeholder(R.drawable.item).error(R.drawable.item).into(holder.itemImage);
        }
        else if (Objects.equals(reportModelList.get(position).getName(), "Organizational")) {
            holder.reportImage.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.organization));
//            Glide.with(context).load(reportModelList.get(position).getItemImage()).placeholder(R.drawable.organization).error(R.drawable.organization).into(holder.itemImage);
        }
        else if (Objects.equals(reportModelList.get(position).getName(), "App")) {
            holder.reportImage.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.app));
//            Glide.with(context).load(reportModelList.get(position).getItemImage()).placeholder(R.drawable.app).error(R.drawable.app).into(holder.itemImage);
        }
        else if (Objects.equals(reportModelList.get(position).getName(), "Security")) {
            holder.reportImage.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.security));
//            Glide.with(context).load(reportModelList.get(position).getItemImage()).placeholder(R.drawable.security).error(R.drawable.security).into(holder.itemImage);
        }
        else {
            holder.reportImage.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.custom));
//            Glide.with(context).load(reportModelList.get(position).getItemImage()).placeholder(R.drawable.custom).error(R.drawable.custom).into(holder.itemImage);
        }

        holder.itemView.setOnClickListener(view -> {

        });
    }

    @Override
    public int getItemCount() {
        return reportModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView description;
        ImageView reportImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_name);
            description = itemView.findViewById(R.id.item_description);
            reportImage = itemView.findViewById(R.id.itemImage);
        }
    }

    public void setOrganizationId(String organizationId) {
        this.organizationID = organizationId;
    }
}
