package com.example.smartstorageorganizer.adapters;


import android.app.AlertDialog;
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

//        to view on the next page whenever it clicks use onClick()
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), ItemInfoActivity.class);
            intent.putExtra("item_name", itemModelList.get(holder.getAdapterPosition()).getItemName());
            intent.putExtra("item_description", itemModelList.get(holder.getAdapterPosition()).getDescription());
            intent.putExtra("location", itemModelList.get(holder.getAdapterPosition()).getLocation());
            intent.putExtra("color_code", itemModelList.get(holder.getAdapterPosition()).getColourCoding());
            intent.putExtra("item_id", itemModelList.get(holder.getAdapterPosition()).getItemId());

            context.startActivity(intent);
        });

        // Handle delete icon click
        holder.deleteIcon.setOnClickListener(view -> {
            // Show a confirmation dialog or directly delete the item
            new AlertDialog.Builder(context)
                    .setTitle("Delete Item")
                    .setMessage("Are you sure you want to delete this item?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        // Handle the delete action
                        deleteItem(position);
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
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
        ImageView deleteIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_image);
            name = itemView.findViewById(R.id.item_name);
            description = itemView.findViewById(R.id.item_description);
            itemImage = itemView.findViewById(R.id.itemImage);
            deleteIcon = itemView.findViewById(R.id.delete_icon);  // Initialize delete icon

        }
    }
    // Method to handle item deletion
    private void deleteItem(int position) {
        // Implement your delete logic here
        itemModelList.remove(position);
        notifyItemRemoved(position);
        // Optionally, notify the adapter that the data set has changed
        notifyDataSetChanged();
    }
}


