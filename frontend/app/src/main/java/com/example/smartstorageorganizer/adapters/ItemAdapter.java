package com.example.smartstorageorganizer.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.example.smartstorageorganizer.ItemDetailsActivity;
import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.model.ColorCodeModel;
import com.example.smartstorageorganizer.model.ItemModel;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.Utils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;

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

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private final Context context;
    private final List<ItemModel> itemModelList;
    private final Set<Integer> selectedItems = new HashSet<>();
    private boolean isSelectionMode = false;

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

//        updated onClick for selecting items
        // Handle item long click to enter selection mode
        holder.itemView.setOnLongClickListener(view -> {
            isSelectionMode = true; // Enable selection mode
            toggleSelection(holder.getAdapterPosition());
            return true;
        });

// Handle item click to toggle selection if in selection mode
        holder.itemView.setOnClickListener(view -> {
            if (isSelectionMode) {
                toggleSelection(holder.getAdapterPosition());
            }
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

    @SuppressLint("NotifyDataSetChanged")
    private void selectAllItems() {
        // Add logic to select all items
        for (int i = 0; i < itemModelList.size(); i++) {
            selectedItems.add(i);
        }
        notifyDataSetChanged(); // Update the adapter to refresh the UI
    }

//    updated assignItemToColor load the list of the available colors
    public void assignItemToColor(Activity activity, String itemId) {
        // Fetch all available color codes
        Utils.fetchAllColour(activity, new OperationCallback<List<ColorCodeModel>>() {
            @Override
            public void onSuccess(List<ColorCodeModel> colorCodeModelList) {
                // Create an array of color names to display to the user
                String[] colorNames = new String[colorCodeModelList.size()];
                for (int i = 0; i < colorCodeModelList.size(); i++) {
                    colorNames[i] = colorCodeModelList.get(i).getName();
                }

                // Show the color options to the user (e.g., in a dialog)
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Choose a color for the item");
                builder.setItems(colorNames, (dialog, which) -> {
                    // Get the selected color code
                    ColorCodeModel selectedColor = colorCodeModelList.get(which);
                    String colourId = selectedColor.getId();
                    Log.d("Selected Color", "Color ID: " + colourId);
                    Log.d("Selected Item", "Item ID: " + itemId);

                    // Assign the selected color to the item
                    assignColorToItem(itemId, selectedColor);

                    //assigned to color db
                    AssignColour(colourId,itemId);
                });
                builder.show();
            }

            @Override
            public void onFailure(String errorMessage) {
                // Handle the error (e.g., show a toast or log the error)
                Toast.makeText(activity, "Failed to fetch colors: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void assignColorToItem(String itemId, ColorCodeModel colorCode) {
        // Your logic to assign the colorCode to the item with the given itemId
        // This might involve updating a database, sending a request to a server, etc.
        Log.d("AssignColor", "Item ID: " + itemId + " assigned to color: " + colorCode.getName());
    }

    public void AssignColour(String colourid, String itemid) {
        String json = "{\"colourid\":\"" + colourid + "\", \"itemid\":\"" + itemid + "\"}";

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.AddItemToColourEndPoint;
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e("AssignColour", "Request failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("AssignColour", "Successfully assigned color to items");
                } else {
                    Log.e("AssignColour", "Failed to assign color: " + response.message());
                }
            }
        });
    }


    private void toggleSelection(int position) {
        if (selectedItems.contains(position)) {
            selectedItems.remove(position);
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

//    updated showBottomSheetDialog
private void showBottomSheetDialog(int position, String itemId) {
    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
    View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_dialog_item, null);

    TextView deleteItem = bottomSheetView.findViewById(R.id.delete);
    TextView selectAllItems = bottomSheetView.findViewById(R.id.select_all);
    TextView assignColor = bottomSheetView.findViewById(R.id.assign_color);
    Log.d("AssignItem", "Item ID: " + itemId);
    deleteItem.setOnClickListener(view -> {
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

    selectAllItems.setOnClickListener(view -> {
        bottomSheetDialog.dismiss();
        selectAllItems(); // Implement your method to select all items
    });

    assignColor.setOnClickListener(view -> {
        bottomSheetDialog.dismiss();
        // Assuming context is an instance of Activity
        if (context instanceof Activity) {
            // Replace with the actual list of selected item IDs
            assignItemToColor((Activity) context, itemId);
        } else {
            Toast.makeText(context, "Unable to assign color. Context is not an activity.", Toast.LENGTH_SHORT).show();
        }
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
}


