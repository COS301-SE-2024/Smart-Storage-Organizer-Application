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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.smartstorageorganizer.BuildConfig;
import com.example.smartstorageorganizer.ItemInfoActivity;
import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.model.ItemModel;

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

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private final Context context;
    private final List<ItemModel> itemModelList;

    private final OkHttpClient client;

    public ItemAdapter(Context context, List<ItemModel> itemModelList) {
        this.context = context;
        this.itemModelList = itemModelList;
        this.client = new OkHttpClient();
    }

    @NonNull
    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false));
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
                        Log.i("1ItemId: ",itemModelList.get(holder.getAdapterPosition()).getItemId());
                        deleteItem(itemModelList.get(holder.getAdapterPosition()).getItemId(), holder.getAdapterPosition());
//                        deleteItem(itemModelList.get(holder.getAdapterPosition()).getItemId());

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

    public static class ViewHolder extends RecyclerView.ViewHolder {
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
//    @SuppressLint("NotifyDataSetChanged")
//    private void deleteItem(int position) {
//        // Implement your delete logic here
//        itemModelList.remove(position);
//        notifyItemRemoved(position);
//        // Optionally, notify the adapter that the data set has changed
//        notifyDataSetChanged();
//    }

    // Method to handle item deletion
    private void deleteItem(String itemId, int position)
    {
        try
        {
            // Convert itemId to integer
            int itemIdInt = Integer.parseInt(itemId);
            String json = "{\"id\":\"" + itemIdInt + "\"}";
            Log.d("Delete Item Payload", "JSON Payload: " + json);  // Log the JSON payload
            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            String API_URL = BuildConfig.DeleteItemEndPoint;
            Log.d("Delete Item Endpoint", "API URL: " + BuildConfig.DeleteItemEndPoint);
            RequestBody body = RequestBody.create(json, JSON);

            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback()
            {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    // Handle request failure
                    Log.e("Delete Item", "Error deleting item", e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    if (response.isSuccessful()) {
                        // Remove item from list and notify adapter
                        ((android.app.Activity) context).runOnUiThread(() -> {
                            itemModelList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, itemModelList.size());
                            Toast.makeText(context, "Item deleted successfully", Toast.LENGTH_SHORT).show();

                        });
                    } else {
                        // Handle failure response
                        Log.e("Delete Item", "Failed to delete item");
                    }
                }
            });
        } catch (NumberFormatException e) {
        // Handle the exception if itemId is not a valid integer
        Log.e("Delete Item", "Invalid itemId: " + itemId, e);
        }
    }

}


