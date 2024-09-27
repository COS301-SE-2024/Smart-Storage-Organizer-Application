package com.example.smartstorageorganizer.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.smartstorageorganizer.BuildConfig;
import com.example.smartstorageorganizer.ItemDetailsActivity;
import com.example.smartstorageorganizer.MyAmplifyApp;
import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.UncategorizedItemsActivity;
import com.example.smartstorageorganizer.ViewItemActivity;
import com.example.smartstorageorganizer.ViewUnitItemsActivity;
import com.example.smartstorageorganizer.model.ItemModel;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

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
    private boolean selectAllFlag = true;
    private String organizationID;
    private Activity activity;
    private MyAmplifyApp app;

    public ItemAdapter(Context context, List<ItemModel> itemModelList, Activity activity) {
        this.context = context;
        this.itemModelList = itemModelList;
        this.activity = activity;
        new OkHttpClient();

        app = (MyAmplifyApp) context.getApplicationContext();

    }

    @NonNull
    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false));
    }

    public void onBindViewHolder(@NonNull ItemAdapter.ViewHolder holder, int position) {
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
                intent.putExtra("item_barcode", itemModelList.get(holder.getAdapterPosition()).getBarcode());
                intent.putExtra("quantity", itemModelList.get(holder.getAdapterPosition()).getQuantity());
                intent.putExtra("organization_id", organizationID);

                if (context instanceof ViewItemActivity) {
                    ((ViewItemActivity) context).logUserFlow("ItemDetailsActivity");
                }
                else if (context instanceof ViewUnitItemsActivity) {
                    ((ViewUnitItemsActivity) context).logUserFlow("ItemDetailsActivity");
                }

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
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.gray));
            holder.selectedOverlay.setVisibility(View.VISIBLE);
            holder.tickMark.setVisibility(View.VISIBLE);
        } else {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
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
        if (context instanceof ViewItemActivity) {
            ((ViewItemActivity) context).updateBottomNavigationBar(selectedItems.size() > 0);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;
        TextView description;
        ImageView itemImage;
        ImageView deleteIcon;
        View selectedOverlay;
        ImageView tickMark;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_image);
            name = itemView.findViewById(R.id.item_name);
            description = itemView.findViewById(R.id.item_description);
            itemImage = itemView.findViewById(R.id.itemImage);
            deleteIcon = itemView.findViewById(R.id.delete_icon);
            selectedOverlay = itemView.findViewById(R.id.selected_overlay);
            tickMark = itemView.findViewById(R.id.tick_mark);
        }
    }


    private void showBottomSheetDialog(int position, String itemId) {
        ItemModel item = itemModelList.get(position);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_dialog_item, null);

        TextView deleteItem = bottomSheetView.findViewById(R.id.delete);
        TextView groupItem = bottomSheetView.findViewById(R.id.group);

        deleteItem.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            new AlertDialog.Builder(context)
                    .setTitle("Delete Item")
                    .setMessage("Are you sure you want to delete this item?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        if(Objects.equals(app.getUserRole(), "Manager") || Objects.equals(app.getUserRole(), "Admin")){
                            deleteItem(itemId, position);
                        }
                        else if (Objects.equals(app.getUserRole(), "normalUser")){
                            sendRequestToDeleteItem(item.getItemId(), item.getItemName(), item.getDescription(), item.getLocation(), item.getParentCategoryName(), item.getColourCoding(), item.getSubcategoryName(), position);
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void deleteItem(String itemId, int position) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Deleting Item...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Utils.deleteItem(itemId, app.getOrganizationID(), (Activity) context, new OperationCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (Boolean.TRUE.equals(result)) {
                    ((android.app.Activity) context).runOnUiThread(() -> {
                        itemModelList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, itemModelList.size());
                        progressDialog.dismiss();
                        Toast.makeText(context, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onFailure(String error) {

            }
        });
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

    @SuppressLint("NotifyDataSetChanged")
    public void selectAllItems() {
        if(selectAllFlag){
            for (int i = 0; i < itemModelList.size(); i++) {
                selectedItems.add(i);
            }
            selectAllFlag = false;
        }
        else {
            selectedItems.clear();
            ((ViewItemActivity) context).updateBottomNavigationBar(selectedItems.size() > 0);
            selectAllFlag = true;
        }
        notifyDataSetChanged();
    }

    public List<String> getSelectedItemsIdsArray() {
        List<String> selectedIds = new ArrayList<>();
        for (int position : selectedItems) {
            selectedIds.add(itemModelList.get(position).getItemId());
        }
        return selectedIds;
    }

    public void unselect() {
        selectedItems.clear();
        ((ViewItemActivity) context).updateBottomNavigationBar(selectedItems.size() > 0);
        selectAllFlag = true;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationID = organizationId;
    }

    public void sendRequestToDeleteItem(String id, String itemName, String itemDescription, String location, String parentCategory, String colorCode, String subcategory, int position) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Sending Request To Delete an Item...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a map for the unit request
        Map<String, Object> unitRequest = new HashMap<>();
        unitRequest.put("itemId", id);
        unitRequest.put("itemName", itemName);
        unitRequest.put("itemDescription", itemDescription);
        unitRequest.put("location", location);
        unitRequest.put("parentCategory", parentCategory);
        unitRequest.put("colorCode", colorCode);
        unitRequest.put("subcategory", subcategory);
        unitRequest.put("userEmail", app.getEmail());
        unitRequest.put("requestType", "Delete Item");
        unitRequest.put("organizationId", app.getOrganizationID());
        unitRequest.put("status", "pending");  // Initially set to pending
        unitRequest.put("requestDate", FieldValue.serverTimestamp()); // Store request date and time

        // Store the request in Firestore
        db.collection("item_requests")
                .add(unitRequest)
                .addOnSuccessListener(documentReference -> {
                    // Get the unique document ID
                    String documentId = documentReference.getId();

                    // Update the document to include the document ID or use it as a unique ID
                    db.collection("item_requests").document(documentId)
                            .update("documentId", documentId) // Store documentId within the document itself
                            .addOnSuccessListener(aVoid -> {
                                ((android.app.Activity) context).runOnUiThread(() -> {
//                                    itemModelList.remove(position);
//                                    notifyItemRemoved(position);
//                                    notifyItemRangeChanged(position, itemModelList.size());
                                    showRequestDialog();
//                                    Toast.makeText(context, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                });
                                Log.i("Firestore", "Request stored successfully with documentId: " + documentId);
                                future.complete(true);
                            })
                            .addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                Log.e("Firestore", "Error updating documentId", e);
                                future.complete(false);
                            });
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Log.e("Firestore", "Error storing request", e);
                    future.complete(false);
                });

    }

    public void showRequestDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.send_request_popup, null);

        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        Button closeButton = dialogView.findViewById(R.id.finishButton);

        closeButton.setOnClickListener(v -> {
            alertDialog.dismiss();
//            Intent intent = new Intent(ViewItemActivity.this, HomeActivity.class);
//            startActivity(intent);
//            finish();
        });

        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }
}
