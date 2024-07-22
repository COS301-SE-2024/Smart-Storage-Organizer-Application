package com.example.smartstorageorganizer.adapters;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.smartstorageorganizer.BuildConfig;
import com.example.smartstorageorganizer.ItemDetailsActivity;
import com.example.smartstorageorganizer.ItemInfoActivity;
import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.model.ItemModel;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.IOException;
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
import yuku.ambilwarna.AmbilWarnaDialog;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private final Context context;
    private final List<ItemModel> itemModelList;
    private final Set<Integer> selectedItems = new HashSet<>();
    private String currentEmail;

    public ItemAdapter(Context context, List<ItemModel> itemModelList) {
        this.context = context;
        this.itemModelList = itemModelList;
        new OkHttpClient();
    }

    @NonNull
    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ViewHolder holder, int position) {
        Log.i("Adapter", "Adapter function.");
        ItemModel item = itemModelList.get(position);

        holder.name.setText(item.getItemName());
        holder.description.setText(item.getDescription());

        if (!Objects.equals(item.getItemImage(), "empty")) {
            Glide.with(context).load(item.getItemImage()).placeholder(R.drawable.no_image).error(R.drawable.no_image).into(holder.itemImage);
        }

        // Set background color based on color coding
//        if (item.getColourCoding() != null && !item.getColourCoding().isEmpty()) {
//            holder.itemView.setBackgroundColor(Color.parseColor(item.getColourCoding()));
//        } else {
//            // Default color if no color coding is set
//            holder.itemView.setBackgroundColor(Color.WHITE);
//        }
//        updateColorCoding(item.getColourCoding());


//        to view on the next page whenever it clicks use onClick()
        holder.itemView.setOnClickListener(view -> {
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
        });

        // Handle item long click to select
        holder.itemView.setOnLongClickListener(view -> {
            toggleSelection(holder.getAdapterPosition());
            if (!selectedItems.isEmpty()) {
                showThreeDotMenu(view);
            }
            return true;
        });

        // Show/hide tick icon based on selection
        if (selectedItems.contains(position)) {
            holder.tickIcon.setVisibility(View.VISIBLE);
        } else {
            holder.tickIcon.setVisibility(View.GONE);
        }

        // Handle delete icon click
        holder.deleteIcon.setOnClickListener(view -> {
            showBottomSheetDialog(holder.getAdapterPosition(), itemModelList.get(holder.getAdapterPosition()).getItemId());
            // Show a confirmation dialog or directly delete the item
        });
    }

    private void showThreeDotMenu(View view) {
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.select_items_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_select_all) {
                selectAllItems();
                return true;
            } else if (itemId == R.id.action_assign_color) {
                assignColorToSelectedItems();
                return true;
            } else {
                return false;
            }
        });


        popup.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void selectAllItems() {
        // Add logic to select all items
        for (int i = 0; i < itemModelList.size(); i++) {
            selectedItems.add(i);
        }
        notifyDataSetChanged(); // Update the adapter to refresh the UI
    }

//    @SuppressLint("NotifyDataSetChanged")
//    private void assignColorToSelectedItems() {
//        // Add logic to assign color to selected items
//        int color = getColorFromUser(); // Implement this method to get the desired color
//        for (int position : selectedItems) {
//            itemModelList.get(position).setColor(color); // Assuming your items have a setColor method
//        }
//        notifyDataSetChanged(); // Update the adapter to refresh the UI
//    }

    public interface OnColorSelectedListener {
        void onColorSelected(int color);
    }

    private void assignColorToSelectedItems() {
        showColorPickerDialog(context, color -> {
            String colorCode = String.format("#%06X", (0xFFFFFF & color));
            for (int position : selectedItems) {
                 itemModelList.get(position);
                addNewColorCode(colorCode, itemModelList.get(position).getItemName(), itemModelList.get(position).getDescription(), itemModelList.get(position).getItemId());
            }
            notifyDataSetChanged(); // Update the adapter to refresh the UI
        });
    }


    private void showColorPickerDialog(Context context, OnColorSelectedListener listener) {
        int initialColor = Color.RED; // Default color

        AmbilWarnaDialog colorPickerDialog = new AmbilWarnaDialog(context, initialColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                // Color selected
                listener.onColorSelected(color);
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                // Action canceled
            }
        });

        colorPickerDialog.show();
    }

    private void addNewColorCode(String colorCode, String title, String description, String itemId) {
        Utils.addColourGroup(colorCode, title, description, currentEmail, (Activity) context, new OperationCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (Boolean.TRUE.equals(result)) {
                    // Assuming you might want to update the item on the server
                    updateItemColorCodingOnServer(itemId, colorCode);
//                    showToast("Color Code added successfully");
                    // Optionally navigate or refresh the view
                    // navigateToHome();
                }
            }

            @Override
            public void onFailure(String error) {
//                showToast("Failed to add color code: " + error);
            }
        });
    }
    private void toggleSelection(int position) {
        if (selectedItems.contains(position)) {
            selectedItems.remove(Integer.valueOf(position));
        } else {
            selectedItems.add(position);
        }
        notifyItemChanged(position);
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
        ImageView tickIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_image);
            name = itemView.findViewById(R.id.item_name);
            description = itemView.findViewById(R.id.item_description);
            itemImage = itemView.findViewById(R.id.itemImage);
            deleteIcon = itemView.findViewById(R.id.delete_icon);  // Initialize delete icon
            tickIcon = itemView.findViewById(R.id.tick_icon);

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
                        // Handle the delete action
//                        Log.i("1ItemId: ", itemModelList.get(holder.getAdapterPosition()).getItemId());
                        deleteItem(itemId, position);
//                        deleteItem(itemModelList.get(holder.getAdapterPosition()).getItemId());

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
            // Convert itemId to integer
            int itemIdInt = Integer.parseInt(itemId);
            String json = "{\"item_id\":\"" + itemIdInt + "\"}";
            Log.d("Delete Item Payload", "JSON Payload: " + json);  // Log the JSON payload
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
                    // Handle request failure
                    Log.e("Delete Item", "Error deleting item", e);
                    ((android.app.Activity) context).runOnUiThread(() -> Toast.makeText(context, "Failed to delete item. Please try again.", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        // Remove item from list and notify adapter

                        // Parse the response body
                        String responseBody = response.body().string();

                        // Log the successful response and its body
                        Log.d("Delete Item Response", "Response: " + response);
                        Log.d("Delete Item Response Body", "Response Body: " + responseBody);

                        ((android.app.Activity) context).runOnUiThread(() -> {
                            itemModelList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, itemModelList.size());
                            Toast.makeText(context, "Item deleted successfully", Toast.LENGTH_SHORT).show();

                        });
                    } else {
                        // Handle failure response
                        String errorBody = response.body().string();
                        Log.e("Delete Item", "Failed to delete item. Response code: " + response.code() + ", message: " + response.message());
                        Log.e("Delete Item", "Error body: " + errorBody);

                        ((android.app.Activity) context).runOnUiThread(() -> Toast.makeText(context, "Failed to delete item. Please try again.", Toast.LENGTH_SHORT).show());

                    }
                }
            });
        } catch (NumberFormatException e) {
            // Handle the exception if itemId is not a valid integer
            Log.e("Delete Item", "Invalid itemId: " + itemId, e);
            ((android.app.Activity) context).runOnUiThread(() -> Toast.makeText(context, "Invalid item ID. Please check and try again.", Toast.LENGTH_SHORT).show());
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private void updateColorCoding(String colorCode) {
        for (Integer position : selectedItems) {
            ItemModel item = itemModelList.get(position);
            item.setColourCoding(colorCode); // Update the color coding in the model

            // Optionally, update the server with the new color coding
            updateItemColorCodingOnServer(item.getItemId(), colorCode);
        }
        notifyDataSetChanged(); // Refresh the RecyclerView to reflect the changes
    }

    private void updateItemColorCodingOnServer(String itemId, String colorCode) {
        try {
            String json = "{\"item_id\":\"" + itemId + "\", \"color_code\":\"" + colorCode + "\"}";
            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            String API_URL = BuildConfig.AddColourEndPoint;
            RequestBody body = RequestBody.create(json, JSON);
            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .build();

            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    // Handle request failure
                    Log.e("Update Color Coding", "Error updating color coding", e);
                    ((android.app.Activity) context).runOnUiThread(() -> Toast.makeText(context, "Failed to update color coding. Please try again.", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        // Handle successful response
                        Log.d("Update Color Coding", "Color coding updated successfully");
                    } else {
                        // Handle failure response
                        Log.e("Update Color Coding", "Failed to update color coding. Response code: " + response.code());
                        ((android.app.Activity) context).runOnUiThread(() -> Toast.makeText(context, "Failed to update color coding. Please try again.", Toast.LENGTH_SHORT).show());
                    }
                }
            });
        } catch (Exception e) {
            Log.e("Update Color Coding", "Exception occurred", e);
        }
    }

}


