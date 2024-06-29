package com.example.smartstorageorganizer.adapters;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.smartstorageorganizer.ItemInfoActivity;
import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.model.ItemModel;

import java.util.List;
import java.util.Objects;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private Context context;
    private List<ItemModel> itemModelList;

    public ItemAdapter(Context context, List<ItemModel> itemModelList) {
        this.context = context;
        this.itemModelList = itemModelList;
    }

    @NonNull
    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ViewHolder holder, int position) {
        Log.i("Adapter", "Adapter function.");
        holder.name.setText(itemModelList.get(position).getItemName());
        holder.description.setText(itemModelList.get(position).getDescription());

        if(!Objects.equals(itemModelList.get(position).getItemImage(), "empty")){
            Glide.with(context).load(itemModelList.get(position).getItemImage()).placeholder(R.drawable.no_image).error(R.drawable.no_image).into(holder.itemImage);
        }

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), ItemInfoActivity.class);
            intent.putExtra("item_name", itemModelList.get(holder.getAdapterPosition()).getItemName());
            intent.putExtra("item_description", itemModelList.get(holder.getAdapterPosition()).getDescription());
            intent.putExtra("location", itemModelList.get(holder.getAdapterPosition()).getLocation());
            intent.putExtra("color_code", itemModelList.get(holder.getAdapterPosition()).getColourCoding());
            intent.putExtra("item_id", itemModelList.get(holder.getAdapterPosition()).getItemId());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return itemModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;
        TextView description;
        ImageView itemImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_image);
            name = itemView.findViewById(R.id.item_name);
            description = itemView.findViewById(R.id.item_description);
            itemImage = itemView.findViewById(R.id.itemImage);
        }
    }

}


