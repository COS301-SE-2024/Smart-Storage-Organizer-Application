package com.example.smartstorageorganizer.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.example.smartstorageorganizer.HomeActivity;
import com.example.smartstorageorganizer.ItemDetailsActivity;
import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.UncategorizedItemsActivity;
import com.example.smartstorageorganizer.ViewItemActivity;
import com.example.smartstorageorganizer.model.ItemModel;
import com.example.smartstorageorganizer.ui.home.HomeFragment;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.ViewHolder> {

    private final Context context;
    private final List<ItemModel> itemModelList;
    private final Set<Integer> selectedItems = new HashSet<>();

    public RecentAdapter(Context context, List<ItemModel> itemModelList) {
        this.context = context;
        this.itemModelList = itemModelList;
        new OkHttpClient();
    }

    @NonNull
    @Override
    public RecentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_added_layout, parent, false));
    }

    public void onBindViewHolder(@NonNull RecentAdapter.ViewHolder holder, int position) {
        Log.i("Adapter", "Adapter function.");
        holder.name.setText(itemModelList.get(position).getItemName());
        holder.description.setText(itemModelList.get(position).getDescription());

        if (!Objects.equals(itemModelList.get(position).getItemImage(), "empty")) {
            Glide.with(context).load(itemModelList.get(position).getItemImage()).placeholder(R.drawable.no_image).error(R.drawable.no_image).into(holder.itemImage);
        }

        holder.itemView.setOnClickListener(view -> {
            if (selectedItems.isEmpty()) {
                Intent intent = new Intent(view.getContext(), ItemDetailsActivity.class);
                intent.putExtra("item_name", itemModelList.get(holder.getAdapterPosition()).getItemName());
                intent.putExtra("item_description", itemModelList.get(holder.getAdapterPosition()).getDescription());
                intent.putExtra("location", itemModelList.get(holder.getAdapterPosition()).getLocation());
                intent.putExtra("color_code", itemModelList.get(holder.getAdapterPosition()).getColourCoding());
                intent.putExtra("item_id", itemModelList.get(holder.getAdapterPosition()).getItemId());
                intent.putExtra("item_image", itemModelList.get(holder.getAdapterPosition()).getItemImage());
                intent.putExtra("subcategory_id", itemModelList.get(holder.getAdapterPosition()).getSubCategoryId());
                intent.putExtra("parentcategory_id", itemModelList.get(holder.getAdapterPosition()).getParentCategoryId());
                intent.putExtra("item_qrcode", itemModelList.get(holder.getAdapterPosition()).getQrcode());

                context.startActivity(intent);
            } else {
                toggleSelection(holder.getAdapterPosition());
            }
        });

        holder.itemView.setOnLongClickListener(view -> {
            toggleSelection(holder.getAdapterPosition());
            return true;
        });

        if (selectedItems.contains(position)) {
//            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.gray));
            holder.selectedOverlay.setVisibility(View.VISIBLE);
            holder.tickMark.setVisibility(View.VISIBLE);
        } else {
//            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.selectedOverlay.setVisibility(View.GONE);
            holder.tickMark.setVisibility(View.GONE);
        }

        // Handle delete icon click
        holder.deleteIcon.setOnClickListener(view -> {
            showBottomSheetDialog(holder.getAdapterPosition(), itemModelList.get(holder.getAdapterPosition()).getItemId());
        });
    }

    @Override
    public int getItemCount() {
        return itemModelList.size();
    }

    private void toggleSelection(int position) {
        if (selectedItems.contains(position)) {
            selectedItems.remove(position);
        } else {
            selectedItems.add(position);
        }
        notifyItemChanged(position);
        if (context instanceof UncategorizedItemsActivity) {
            ((UncategorizedItemsActivity) context).updateBottomNavigationBar(selectedItems.size() > 0);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;
        TextView description;
        ShapeableImageView itemImage;
        ImageView deleteIcon;
        View selectedOverlay;
        ImageView tickMark;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);
            itemImage = itemView.findViewById(R.id.image);
            deleteIcon = itemView.findViewById(R.id.delete_icon);
            selectedOverlay = itemView.findViewById(R.id.selected_overlay);
            tickMark = itemView.findViewById(R.id.tick_mark);
        }
    }


    private void showBottomSheetDialog(int position, String itemId) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_dialog_item, null);

        TextView deleteItem = bottomSheetView.findViewById(R.id.delete);

        deleteItem.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            new AlertDialog.Builder(context)
                    .setTitle("Delete Item")
                    .setMessage("Are you sure you want to delete this item?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        deleteItem(itemId, position);
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void deleteItem(String itemId, int position) {
        try {
            int itemIdInt = Integer.parseInt(itemId);
            String json = "{\"item_id\":\"" + itemIdInt + "\"}";
            Log.d("Delete Item Payload", "JSON Payload: " + json);
            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            String API_URL = BuildConfig.DeleteItemEndPoint;
            Log.d("Delete Item Endpoint", "API URL: " + BuildConfig.DeleteItemEndPoint);
            RequestBody body = RequestBody.create(json, JSON);
            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .build();

            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e("Delete Item", "Error deleting item", e);
                    ((android.app.Activity) context).runOnUiThread(() -> Toast.makeText(context, "Failed to delete item. Please try again.", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        Log.d("Delete Item Response", "Response: " + response);
                        Log.d("Delete Item Response Body", "Response Body: " + responseBody);

                        ((android.app.Activity) context).runOnUiThread(() -> {
                            itemModelList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, itemModelList.size());
                            Toast.makeText(context, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        String errorBody = response.body().string();
                        Log.e("Delete Item", "Failed to delete item. Response code: " + response.code() + ", message: " + response.message());
                        Log.e("Delete Item", "Error body: " + errorBody);

                        ((android.app.Activity) context).runOnUiThread(() -> Toast.makeText(context, "Failed to delete item. Please try again.", Toast.LENGTH_SHORT).show());
                    }
                }
            });
        } catch (NumberFormatException e) {
            Log.e("Delete Item", "Invalid itemId: " + itemId, e);
            ((android.app.Activity) context).runOnUiThread(() -> Toast.makeText(context, "Invalid item ID. Please check and try again.", Toast.LENGTH_SHORT).show());
        }
    }

    public String getSelectedItemsIds() {
        StringBuilder selectedIds = new StringBuilder();
        for (int position : selectedItems) {
            if (selectedIds.length() > 0) {
                selectedIds.append(",");
            }
            selectedIds.append(itemModelList.get(position).getItemId());
        }
        return selectedIds.toString();
    }


}

















