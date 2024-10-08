package com.example.smartstorageorganizer.adapters;
import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstorageorganizer.BuildConfig;
import com.example.smartstorageorganizer.HomeActivity;
import com.example.smartstorageorganizer.MyAmplifyApp;
import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.model.CategoryModel;
import com.example.smartstorageorganizer.model.CategoryRequestModel;
import com.example.smartstorageorganizer.model.ItemRequestModel;
import com.example.smartstorageorganizer.model.ModifyItemRequestModel;
import com.example.smartstorageorganizer.model.RequestModel;
import com.example.smartstorageorganizer.model.TokenManager;
import com.example.smartstorageorganizer.model.UnitRequestModel;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RequestCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Object> mixedList;
    private static final int UNIT_REQUEST = 0;
    private static final int CATEGORY_REQUEST = 1;
    private static final int ITEM_REQUEST = 2;
    private static final int MODIFY_ITEM_REQUEST = 3;
    private String type;
    private MyAmplifyApp app;

    public RequestCardAdapter(Context context, List<Object> mixedList, String type) {
        this.context = context;
        this.mixedList = mixedList;
        this.type = type;
        app = (MyAmplifyApp) context.getApplicationContext();
    }

    @Override
    public int getItemViewType(int position) {
        if (mixedList.get(position) instanceof UnitRequestModel) {
            return UNIT_REQUEST;
        } else if (mixedList.get(position) instanceof CategoryRequestModel) {
            return CATEGORY_REQUEST;
        }
        else if (mixedList.get(position) instanceof ItemRequestModel) {
            return ITEM_REQUEST;
        }
        else if (mixedList.get(position) instanceof ModifyItemRequestModel) {
            return MODIFY_ITEM_REQUEST;
        }
        return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == UNIT_REQUEST) {
            View view = LayoutInflater.from(context).inflate(R.layout.unit_request_card, parent, false);
            return new UnitRequestViewHolder(view);
        } else if (viewType == CATEGORY_REQUEST) {
            View view = LayoutInflater.from(context).inflate(R.layout.category_request_card, parent, false);
            return new CategoryRequestViewHolder(view);
        } else if (viewType == ITEM_REQUEST) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_request_card, parent, false);
            return new ItemRequestViewHolder(view);
        }
        else if (viewType == MODIFY_ITEM_REQUEST) {
            View view = LayoutInflater.from(context).inflate(R.layout.modify_item_request_card, parent, false);
            return new ModifyItemRequestViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == UNIT_REQUEST) {
            UnitRequestModel request = (UnitRequestModel) mixedList.get(position);
            UnitRequestViewHolder unitHolder = (UnitRequestViewHolder) holder;

            // Set visible fields
            unitHolder.unitName.setText("Unit Name: " + request.getUnitName());
            unitHolder.requestType.setText("Request Type: " + request.getRequestType());
            unitHolder.capacity.setText("Capacity: " + request.getCapacity());
            unitHolder.userEmail.setText("Requested by: " + request.getUserEmail());

            // Set hidden fields
            unitHolder.constraints.setText("Constraints: " + request.getConstraints());
            unitHolder.dimensions.setText("Dimensions: " + request.getWidth() + " x " + request.getHeight() + " x " + request.getDepth());
            unitHolder.maxWeight.setText("Max Weight: " + request.getMaxWeight());
            unitHolder.organizationId.setText("Organization ID: " + request.getOrganizationId());
            unitHolder.requestDate.setText("Request Date: " + request.getRequestDate());
            unitHolder.status.setText("Status: " + request.getStatus());

            if(Objects.equals(type, "approved")){
                unitHolder.approveButton.setVisibility(View.GONE);
                unitHolder.rejectButton.setVisibility(View.GONE);
            }
            else {
                unitHolder.approveButton.setVisibility(View.VISIBLE);
                unitHolder.rejectButton.setVisibility(View.VISIBLE);
            }

            setStatusBackgroundColor(unitHolder.status, request.getStatus());

            toggleButtonVisibility(unitHolder.buttonsLayout, type);

            // Initially hide details
            unitHolder.detailsLayout.setVisibility(View.GONE);

            // Toggle "View More Details"
            unitHolder.viewMoreLink.setOnClickListener(v -> toggleDetailsVisibility(unitHolder.detailsLayout, unitHolder.viewMoreLink));

            unitHolder.approveButton.setOnClickListener(v -> {
                // Handle Approve action
                approveRequest(request.getRequestId(), holder.getAdapterPosition());
            });

            unitHolder.rejectButton.setOnClickListener(v -> {
                // Handle Reject action
            });

        } else if (holder.getItemViewType() == CATEGORY_REQUEST) {
            CategoryRequestModel request = (CategoryRequestModel) mixedList.get(position);
            CategoryRequestViewHolder categoryHolder = (CategoryRequestViewHolder) holder;

            // Set visible fields
            if(Objects.equals(request.getRequestType(), "Modify Category")){
                categoryHolder.categoryName.setText("Current Category Name: " + request.getCategoryName());
                categoryHolder.newCategoryName.setVisibility(View.VISIBLE);
                categoryHolder.newCategoryName.setText("New Category Name: "+ request.getUrl());
            }
            else {
                categoryHolder.categoryName.setText("Category Name: " + request.getCategoryName());
                categoryHolder.newCategoryName.setVisibility(View.GONE);
            }
            categoryHolder.requestType.setText("Request Type: " + request.getRequestType());
            categoryHolder.categoryType.setText("Category Type: Parent Category");
            categoryHolder.userEmail.setText("Requested by: " + request.getUserEmail());
            categoryHolder.organizationId.setText("Organization ID: " + request.getOrganizationId());
            categoryHolder.requestDate.setText("Request Date: " + request.getRequestDate());
            categoryHolder.status.setText("Status: " + request.getStatus());

            setStatusBackgroundColor(categoryHolder.status, request.getStatus());

            toggleButtonVisibility(categoryHolder.buttonsLayout, type);
            if(Objects.equals(type, "approved")){
                categoryHolder.approveButton.setVisibility(View.GONE);
                categoryHolder.rejectButton.setVisibility(View.GONE);
            }
            else {
                categoryHolder.approveButton.setVisibility(View.VISIBLE);
                categoryHolder.rejectButton.setVisibility(View.VISIBLE);
            }

            // Initially hide details
            categoryHolder.detailsLayout.setVisibility(View.GONE);

            // Toggle "View More Details"
            categoryHolder.viewMoreLink.setOnClickListener(v -> toggleDetailsVisibility(categoryHolder.detailsLayout, categoryHolder.viewMoreLink));

            categoryHolder.approveButton.setOnClickListener(v -> {
                approveCategoryRequest(request.getRequestId(), holder.getAdapterPosition(), request.getRequestType());
                // Handle Approve action
            });

            categoryHolder.rejectButton.setOnClickListener(v -> {
                // Handle Reject action
            });
        } else if (holder.getItemViewType() == ITEM_REQUEST) {
            ItemRequestModel request = (ItemRequestModel) mixedList.get(position);
            ItemRequestViewHolder categoryHolder = (ItemRequestViewHolder) holder;

            // Set visible fields
            if(Objects.equals(request.getRequestType(), "Modify Item")){
//                categoryHolder.categoryName.setText("Current Category Name: " + request.getCategoryName());
//                categoryHolder.newCategoryName.setVisibility(View.VISIBLE);
//                categoryHolder.newCategoryName.setText("New Category Name: "+ request.getUrl());
            }
            else {
                categoryHolder.itemName.setText("Item Name: " + request.getItemName());
            }
            categoryHolder.requestType.setText("Request Type: " + request.getRequestType());
            categoryHolder.itemDescription.setText("Description: "+request.getItemDescription());
            categoryHolder.location.setText("Location: " + request.getLocation());
            categoryHolder.colorGroup.setText("Color Group: " + request.getColorCode());
            categoryHolder.userEmail.setText("Requested by: " + request.getUserEmail());
            categoryHolder.organizationId.setText("Organization ID: " + request.getOrganizationId());
            categoryHolder.requestDate.setText("Request Date: " + request.getRequestDate());
            categoryHolder.status.setText("Status: " + request.getStatus());

            setStatusBackgroundColor(categoryHolder.status, request.getStatus());

            toggleButtonVisibility(categoryHolder.buttonsLayout, type);
            if(Objects.equals(type, "approved")){
                categoryHolder.approveButton.setVisibility(View.GONE);
                categoryHolder.rejectButton.setVisibility(View.GONE);
            }
            else {
                categoryHolder.approveButton.setVisibility(View.VISIBLE);
                categoryHolder.rejectButton.setVisibility(View.VISIBLE);
            }

            // Initially hide details
            categoryHolder.detailsLayout.setVisibility(View.GONE);

            // Toggle "View More Details"
            categoryHolder.viewMoreLink.setOnClickListener(v -> toggleDetailsVisibility(categoryHolder.detailsLayout, categoryHolder.viewMoreLink));

            categoryHolder.approveButton.setOnClickListener(v -> {
                approveItemRequest(request.getRequestId(), holder.getAdapterPosition());
                // Handle Approve action
            });

            categoryHolder.rejectButton.setOnClickListener(v -> {
                // Handle Reject action
            });
        }
        else if (holder.getItemViewType() == MODIFY_ITEM_REQUEST) {
            ModifyItemRequestModel request = (ModifyItemRequestModel) mixedList.get(position);
            ModifyItemRequestViewHolder categoryHolder = (ModifyItemRequestViewHolder) holder;

//            String itemname, String description, String colourcoding, String barcode, String qrcode, int quantity, String location, String itemimage,int itemId,
//            int parentcategory, int subcategory

            String itemName = request.getItemName();
            String description = request.getItemDescription();
            String colorCode = request.getColorCode();
            String barcode = request.getBarcode();
            String qrcode = request.getQrcode();
            int quantity = Integer.parseInt(request.getQuantity());
            String location = request.getLocation();
            String image = request.getImage();
            String itemId = request.getItemId();
            int parentCategoryId = Integer.parseInt(request.getParentCategoryId());
            int subcategoryId = Integer.parseInt(request.getSubCategoryId());


            categoryHolder.itemName.setText("Item Name: " + request.getItemName());
            categoryHolder.requestType.setText("Request Type: " + request.getRequestType());
            categoryHolder.itemDescription.setText("Description: "+request.getItemDescription());
            categoryHolder.location.setText("Location: " + request.getLocation());
            categoryHolder.colorGroup.setText("Color Group: " + request.getColorCode());
            categoryHolder.userEmail.setText("Requested by: " + request.getUserEmail());
            categoryHolder.organizationId.setText("Organization ID: " + request.getOrganizationId());
            categoryHolder.requestDate.setText("Request Date: " + request.getRequestDate());
            categoryHolder.status.setText("Status: " + request.getStatus());

            setStatusBackgroundColor(categoryHolder.status, request.getStatus());

            if(!Objects.equals(request.getOldItem(), "")){
                categoryHolder.oldItemName.setVisibility(View.VISIBLE);
                categoryHolder.newItemName.setVisibility(View.VISIBLE);
                categoryHolder.oldItemName.setText("Old Item Name: "+request.getOldItem());
                categoryHolder.newItemName.setText("New Item Name: "+request.getNewItem());
                itemName = request.getNewItem();
            }
            if(!Objects.equals(request.getOldDescription(), "")){
                categoryHolder.oldDescription.setVisibility(View.VISIBLE);
                categoryHolder.newDescription.setVisibility(View.VISIBLE);
                categoryHolder.oldDescription.setText("Old Description: "+request.getOldDescription());
                categoryHolder.newDescription.setText("New Description: "+request.getNewDescription());
                description = request.getNewDescription();
            }
            if(!Objects.equals(request.getParentCategory(), "")){
                categoryHolder.oldCategory.setVisibility(View.VISIBLE);
                categoryHolder.newCategory.setVisibility(View.VISIBLE);
                categoryHolder.oldCategory.setText("Old Category: "+request.getParentCategory()+" - "+request.getSubcategory());
                String newCategory = "";
                String newSubcategory = "";
                if(!Objects.equals(request.getNewParentCategory(), "")){
                    String word = "";
                    String number = "";

// Regular expression to match a word followed by a number
                    Pattern pattern = Pattern.compile("(\\w+),\\s*(\\d+)");
                    Matcher matcher = pattern.matcher(request.getNewParentCategory());

                    if (matcher.find()) {
                        word = matcher.group(1);    // Extract the word (e.g., "Device")
                        number = matcher.group(2);  // Extract the number (e.g., "45")
                    }

                    newCategory += word;
                }
                else {
                    newCategory += request.getParentCategory();
                }
                if(!Objects.equals(request.getNewSubcategory(), "")) {
                    String word = "";
                    String number = "";

// Regular expression to match a word followed by a number
                    Pattern pattern = Pattern.compile("(\\w+),\\s*(\\d+)");
                    Matcher matcher = pattern.matcher(request.getNewSubcategory());

                    if (matcher.find()) {
                        word = matcher.group(1);    // Extract the word (e.g., "Device")
                        number = matcher.group(2);  // Extract the number (e.g., "45")

                    }

                    newCategory +=  " - " + word;
                    subcategoryId = Integer.parseInt(number);
                }
                else {
                    newCategory += " - " + request.getSubcategory();
                }
                categoryHolder.newCategory.setText("New Category: "+newCategory);
            }
            if(!Objects.equals(request.getOldQuantity(), "")){
                categoryHolder.oldQuantity.setVisibility(View.VISIBLE);
                categoryHolder.newQuantity.setVisibility(View.VISIBLE);
                categoryHolder.oldQuantity.setText("Old Quantity: "+request.getOldQuantity());
                categoryHolder.newQuantity.setText("New Quantity: "+request.getNewQuantity());
                quantity = Integer.parseInt(request.getNewQuantity());
            }

            toggleButtonVisibility(categoryHolder.buttonsLayout, type);
            if(Objects.equals(type, "approved")){
                categoryHolder.approveButton.setVisibility(View.GONE);
                categoryHolder.rejectButton.setVisibility(View.GONE);
            }
            else {
                categoryHolder.approveButton.setVisibility(View.VISIBLE);
                categoryHolder.rejectButton.setVisibility(View.VISIBLE);
            }

            // Initially hide details
            categoryHolder.detailsLayout.setVisibility(View.GONE);

            // Toggle "View More Details"
            categoryHolder.viewMoreLink.setOnClickListener(v -> toggleDetailsVisibility(categoryHolder.detailsLayout, categoryHolder.viewMoreLink));

            String finalItemName = itemName;
            String finalDescription = description;
            int finalQuantity = quantity;
            int finalSubcategoryId = subcategoryId;
            categoryHolder.approveButton.setOnClickListener(v -> {
                approveModifyItemRequest(request.getRequestId(), holder.getAdapterPosition(), finalItemName, finalDescription, colorCode, qrcode, barcode, finalQuantity, location, image, Integer.parseInt(itemId), parentCategoryId, finalSubcategoryId);
                // Handle Approve action
            });

            categoryHolder.rejectButton.setOnClickListener(v -> {
                // Handle Reject action
            });
        }
    }

    @Override
    public int getItemCount() {
        return mixedList.size();
    }

    // Helper to toggle button visibility based on the request type (approved/pending)
    private void toggleButtonVisibility(LinearLayout buttonsLayout, String type) {
        if ("approved".equals(type)) {
            buttonsLayout.setVisibility(View.GONE);
        } else {
            buttonsLayout.setVisibility(View.VISIBLE);
        }
    }

    private void toggleDetailsVisibility(View detailsLayout, TextView viewMoreLink) {
        if (detailsLayout.getVisibility() == View.GONE) {
            detailsLayout.setVisibility(View.VISIBLE);
            viewMoreLink.setText("View Less Details");
        } else {
            detailsLayout.setVisibility(View.GONE);
            viewMoreLink.setText("View More Details");
        }
    }

    // Helper to set status background color
    private void setStatusBackgroundColor(TextView statusView, String status) {
        int color;
        if ("pending".equals(status)) {
            color = Color.parseColor("#CC0000");
        } else {
            color = Color.parseColor("#00DC32");
            statusView.setBackgroundColor(Color.parseColor("#D3F8D3"));
        }
        statusView.setTextColor(color);
    }

    // ViewHolder for Unit Requests
    static class UnitRequestViewHolder extends RecyclerView.ViewHolder {
        TextView unitName, requestType, capacity, userEmail, constraints, dimensions, maxWeight, organizationId, requestDate, status, viewMoreLink;
        LinearLayout detailsLayout, buttonsLayout;
        Button approveButton, rejectButton;

        UnitRequestViewHolder(View itemView) {
            super(itemView);
            unitName = itemView.findViewById(R.id.unitName);
            capacity = itemView.findViewById(R.id.capacity);
            userEmail = itemView.findViewById(R.id.userEmail);
            constraints = itemView.findViewById(R.id.constraints);
            dimensions = itemView.findViewById(R.id.dimensions);
            maxWeight = itemView.findViewById(R.id.maxWeight);
            organizationId = itemView.findViewById(R.id.organizationId);
            requestDate = itemView.findViewById(R.id.requestDate);
            requestType = itemView.findViewById(R.id.requestType);
            status = itemView.findViewById(R.id.status);
            viewMoreLink = itemView.findViewById(R.id.viewMoreLink);
            detailsLayout = itemView.findViewById(R.id.detailsLayout);
            approveButton = itemView.findViewById(R.id.approveButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);
            buttonsLayout = itemView.findViewById(R.id.buttonsLayout);
        }
    }

    // ViewHolder for Category Requests
    static class CategoryRequestViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName, categoryType, userEmail, organizationId, requestDate, status, viewMoreLink, requestType, newCategoryName;
        LinearLayout detailsLayout, buttonsLayout;
        Button approveButton, rejectButton;

        CategoryRequestViewHolder(View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryName);
            categoryType = itemView.findViewById(R.id.categoryType);
            userEmail = itemView.findViewById(R.id.userEmail);
            organizationId = itemView.findViewById(R.id.organizationId);
            requestDate = itemView.findViewById(R.id.requestDate);
            requestType = itemView.findViewById(R.id.requestType);
            status = itemView.findViewById(R.id.status);
            viewMoreLink = itemView.findViewById(R.id.viewMoreLink);
            detailsLayout = itemView.findViewById(R.id.detailsLayout);
            approveButton = itemView.findViewById(R.id.approveButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);
            buttonsLayout = itemView.findViewById(R.id.buttonsLayout);
            newCategoryName = itemView.findViewById(R.id.newCategoryName);
        }
    }

    static class ItemRequestViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemDescription, location, colorGroup, userEmail, organizationId, requestDate, status, viewMoreLink, requestType;
        LinearLayout detailsLayout, buttonsLayout;
        Button approveButton, rejectButton;

        ItemRequestViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            itemDescription = itemView.findViewById(R.id.description);
            userEmail = itemView.findViewById(R.id.userEmail);
            organizationId = itemView.findViewById(R.id.organizationId);
            requestDate = itemView.findViewById(R.id.requestDate);
            requestType = itemView.findViewById(R.id.requestType);
            status = itemView.findViewById(R.id.status);
            viewMoreLink = itemView.findViewById(R.id.viewMoreLink);
            detailsLayout = itemView.findViewById(R.id.detailsLayout);
            approveButton = itemView.findViewById(R.id.approveButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);
            buttonsLayout = itemView.findViewById(R.id.buttonsLayout);
            location = itemView.findViewById(R.id.location);
            colorGroup = itemView.findViewById(R.id.colorGroup);
        }
    }

    static class ModifyItemRequestViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemDescription, location, colorGroup, userEmail, organizationId, requestDate, status, viewMoreLink, requestType;
        TextView newItemName, oldItemName, newDescription, oldDescription, newCategory, oldCategory, newQuantity, oldQuantity;
        LinearLayout detailsLayout, buttonsLayout;
        Button approveButton, rejectButton;

        ModifyItemRequestViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            itemDescription = itemView.findViewById(R.id.description);
            userEmail = itemView.findViewById(R.id.userEmail);
            organizationId = itemView.findViewById(R.id.organizationId);
            requestDate = itemView.findViewById(R.id.requestDate);
            requestType = itemView.findViewById(R.id.requestType);
            status = itemView.findViewById(R.id.status);
            viewMoreLink = itemView.findViewById(R.id.viewMoreLink);
            detailsLayout = itemView.findViewById(R.id.detailsLayout);
            approveButton = itemView.findViewById(R.id.approveButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);
            buttonsLayout = itemView.findViewById(R.id.buttonsLayout);
            location = itemView.findViewById(R.id.location);
            colorGroup = itemView.findViewById(R.id.colorGroup);
            newItemName = itemView.findViewById(R.id.newItemName);
            oldItemName = itemView.findViewById(R.id.oldItemName);
            newDescription = itemView.findViewById(R.id.newDescription);
            oldDescription = itemView.findViewById(R.id.oldDescription);
            newCategory = itemView.findViewById(R.id.newCategory);
            oldCategory = itemView.findViewById(R.id.oldCategory);
            newQuantity = itemView.findViewById(R.id.newQuantity);
            oldQuantity = itemView.findViewById(R.id.oldQuantity);
        }
    }

    // Function to approve the unit request
    public void approveRequest(String documentId, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        UnitRequestModel cardItem = (UnitRequestModel) mixedList.get(position);

        // Show the progress dialog
        Dialog progressDialog = new Dialog(context);
        progressDialog.setContentView(R.layout.progress_dialog); // Inflate the custom layout
        progressDialog.setCancelable(false); // Make dialog non-cancellable
        progressDialog.show();

        // Update the request's status to "approved"
        db.collection("unit_requests")
                .document(documentId)
                .update("status", "approved")
                .addOnSuccessListener(aVoid -> {
                    Log.i("Firestore", "Request approved successfully.");
                    // Now dismiss the progress dialog
//                    progressDialog.dismiss();

                    createUnit(cardItem.getUnitName(), cardItem.getCapacity(), cardItem.getConstraints(),
                            cardItem.getWidth(), cardItem.getHeight(), cardItem.getDepth(), cardItem.getMaxWeight(), progressDialog, position);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error approving request", e);
                    // Dismiss the progress dialog on failure too
                    progressDialog.dismiss();
                });
    }


    public void approveCategoryRequest(String documentId, int position, String requestType) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CategoryRequestModel request = (CategoryRequestModel) mixedList.get(position);

        // Show the progress dialog
        Dialog progressDialog = new Dialog(context);
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Update the request's status to "approved"
        db.collection("category_requests")
                .document(documentId)
                .update("status", "approved")
                .addOnSuccessListener(aVoid -> {
                    Log.i("Firestore", "Request approved successfully.");

                    if(Objects.equals(requestType, "Add Category")){
                        addNewCategory(request.getParentCategory(), request.getCategoryName(),
                                request.getUrl(), app.getEmail(), request.getOrganizationId(), progressDialog, position);
                    }
                    else if(Objects.equals(requestType, "Delete Category")){
                        progressDialog.dismiss();
                        deleteCategory(request.getParentCategory(), position);
                    }
                    else {
                        progressDialog.dismiss();
                        updateCategoryName(request.getParentCategory(), request.getUrl(), position);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error approving request", e);
                    progressDialog.dismiss();
                });
    }

    public void approveItemRequest(String documentId, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ItemRequestModel cardItem = (ItemRequestModel) mixedList.get(position);

        // Show the progress dialog
        Dialog progressDialog = new Dialog(context);
        progressDialog.setContentView(R.layout.progress_dialog); // Inflate the custom layout
        progressDialog.setCancelable(false); // Make dialog non-cancellable
        progressDialog.show();

        // Update the request's status to "approved"
        db.collection("item_requests")
                .document(documentId)
                .update("status", "approved")
                .addOnSuccessListener(aVoid -> {
                    Log.i("Firestore", "Request approved successfully.");
                    deleteItem(cardItem.getItemId(), progressDialog, position);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error approving request", e);
                    // Dismiss the progress dialog on failure too
                    progressDialog.dismiss();
                });
    }

    public void approveModifyItemRequest(String documentId, int position, String finalItemName, String finalDescription, String colorCode, String qrcode, String barcode, int finalQuantity, String location, String image, int itemId, int parentCategoryId, int finalSubcategoryId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        ModifyItemRequestModel cardItem = (ModifyItemRequestModel) mixedList.get(position);

        // Show the progress dialog
        Dialog progressDialog = new Dialog(context);
        progressDialog.setContentView(R.layout.progress_dialog); // Inflate the custom layout
        progressDialog.setCancelable(false); // Make dialog non-cancellable
        progressDialog.show();

        // Update the request's status to "approved"
        db.collection("item_requests")
                .document(documentId)
                .update("status", "approved")
                .addOnSuccessListener(aVoid -> {
                    Log.i("Firestore", "Request approved successfully.");
                    postEditItem(finalItemName, finalDescription, colorCode, qrcode, barcode, finalQuantity, location, image, itemId, parentCategoryId, finalSubcategoryId, progressDialog, position);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error approving request", e);
                    // Dismiss the progress dialog on failure too
                    progressDialog.dismiss();
                });
    }

    public void createUnit(String unitName, String capacity, String constraints, String width, String height, String depth, String maxweight, Dialog progressDialog, int position) {
        Utils.createUnitAPI(unitName, capacity, constraints, width, height, depth, maxweight, app.getEmail(), app.getOrganizationID(), (Activity) context, new OperationCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (Boolean.TRUE.equals(result)) {
                    ((Activity) context).runOnUiThread(() -> {
                        mixedList.remove(position);
                        notifyDataSetChanged();
                        progressDialog.dismiss();
                    });

                }
            }

            @Override
            public void onFailure(String error) {
                progressDialog.dismiss();
            }
        });
    }
    public void addNewCategory(int parentCategory, String categoryName, String url, String email, String organizationId, Dialog progressDialog, int position) {
        Utils.addCategory(parentCategory, categoryName, email, url, organizationId, (Activity) context, new OperationCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (Boolean.TRUE.equals(result)) {
                    mixedList.remove(position);
                    notifyDataSetChanged();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(String error) {
                progressDialog.dismiss();
            }
        });
    }

    private void deleteCategory(int id, int position) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Deleting category...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Utils.deleteCategory(id, "NULL", app.getEmail(), (Activity) context, new OperationCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (Boolean.TRUE.equals(result)) {
                    moveItemsUnderTheDeletedCategory(id, position);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(context, "Failed to Delete Category.", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void moveItemsUnderTheDeletedCategory(int id, int position) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Deleting category...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Utils.categoryToUncategorized(id, (Activity) context, new OperationCallback<Boolean>(){
            @Override
            public void onSuccess(Boolean result) {
                progressDialog.dismiss();
                if (Boolean.TRUE.equals(result)) {
                    mixedList.remove(position);
                    notifyDataSetChanged();
                    showSuccessDialog("Category Deleted Successfully!!!");
//                    Toast.makeText(context, "Category Deleted Successfully.", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(context, HomeActivity.class);
//                    context.startActivity(intent);
//                    ((Activity) context).finish();
                }
            }
            @Override
            public void onFailure(String error) {
                progressDialog.dismiss();
                Toast.makeText(context, "Failed to Delete Category.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCategoryName(int categoryId, String newName, int position) {
        ProgressDialog progressDialogModify = new ProgressDialog(context);
        progressDialogModify.setMessage("Updating category name...");
        progressDialogModify.setCancelable(false);
        progressDialogModify.show();
        Utils.modifyCategoryName(categoryId, newName, app.getEmail(), app.getOrganizationID(), (Activity) context, new OperationCallback<Boolean>(){
            @Override
            public void onSuccess(Boolean result) {
                progressDialogModify.dismiss();
                if (Boolean.TRUE.equals(result)) {
                    mixedList.remove(position);
                    notifyDataSetChanged();
                    showSuccessDialog("Category Name Changed Successfully!!!");
//                    Toast.makeText(context, "Category Name Changed Successfully.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(String error) {
                progressDialogModify.dismiss();
                Toast.makeText(context, "Failed to Modify Category Name.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void deleteItem(String itemId, Dialog progressDialog, int position) {
        Utils.deleteItem(itemId, app.getOrganizationID(), (Activity) context, new OperationCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                progressDialog.dismiss();
                if (Boolean.TRUE.equals(result)) {
                    mixedList.remove(position);
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(String error) {

            }
        });
    }

    private void postEditItem(String itemname, String description, String colourcoding, String barcode, String qrcode, int quantity, String location, String itemimage,int itemId, int parentcategory, int subcategory, Dialog progressDialog, int position) {

        String json = "{\"item_name\":\"" + itemname + "\",\"description\":\"" + description + "\" ,\"colourcoding\":\"" + colourcoding + "\",\"barcode\":\"" + barcode + "\",\"qrcode\":\"" + qrcode + "\",\"quanity\":" + quantity + ",\"location\":\"" + location + "\", \"item_id\":\"" + itemId + "\", \"item_image\": \""+itemimage+"\", \"parentcategoryid\": \""+parentcategory+"\", \"subcategoryid\": \""+subcategory+"\", \"username\": \""+app.getEmail()+"\", \"organizationid\":\""+app.getOrganizationID()+"\" }";
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.EditItemEndPoint;
        RequestBody body = RequestBody.create(json, JSON);

        TokenManager.getToken().thenAccept(results-> {

            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", results)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Log.e("Request Method", "POST request failed", e));
                    progressDialog.dismiss();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String responseData = response.body().string();
                        runOnUiThread(() -> {
                            Log.i("Request Method", "POST request succeeded: " + responseData);
                            mixedList.remove(position);
                            notifyDataSetChanged();
                            progressDialog.dismiss();
//                            showUpdateSuccessMessage();
                        });
                    } else {
                        runOnUiThread(() -> Log.e("Request Method", "POST request failed: " + response));
                        progressDialog.dismiss();
                    }
                }
            });

        }).exceptionally(ex -> {
            Log.e("TokenError", "Failed to get user token", ex);
            return null;
        });
    }

    public void showSuccessDialog(String message) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.send_request_popup, null);

        builder.setView(dialogView);
        android.app.AlertDialog alertDialog = builder.create();
        Button closeButton = dialogView.findViewById(R.id.finishButton);
        TextView textView = dialogView.findViewById(R.id.textView);
        TextView textView3 = dialogView.findViewById(R.id.textView3);

        textView.setText("Sucess");
        textView3.setText(message);

        closeButton.setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }
}

