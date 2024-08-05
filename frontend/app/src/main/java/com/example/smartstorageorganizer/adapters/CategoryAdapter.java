package com.example.smartstorageorganizer.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.smartstorageorganizer.HomeActivity;
import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.ViewItemActivity;
import com.example.smartstorageorganizer.model.CategoryModel;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;
import java.util.Objects;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private Context context;
    private List<CategoryModel> categoryModelList;

    public CategoryAdapter(Context context, List<CategoryModel> categoryModelList) {
        this.context = context;
        this.categoryModelList = categoryModelList;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.category_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        holder.name.setText(categoryModelList.get(position).getCategoryName());

        if (!Objects.equals(categoryModelList.get(position).getImageUrl(), "empty")) {
            Glide.with(context).load(categoryModelList.get(position).getImageUrl()).placeholder(R.drawable.no_image).error(R.drawable.no_image).into(holder.image);
        }

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), ViewItemActivity.class);
            intent.putExtra("category", categoryModelList.get(holder.getAdapterPosition()).getCategoryName());
            intent.putExtra("category_id", categoryModelList.get(holder.getAdapterPosition()).getCategoryID());
            intent.putExtra("color_code_id", "");

            context.startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(view -> {
            showBottomSheetDialog(holder.getAdapterPosition());
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.category_image);
            name = itemView.findViewById(R.id.category_name);
        }
    }

    private void showBottomSheetDialog(int position) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_dialog, null);

        TextView editCategory = bottomSheetView.findViewById(R.id.edit_category);
        TextView deleteCategory = bottomSheetView.findViewById(R.id.delete_category);

        editCategory.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            showEditCategoryDialog(position);
        });

        deleteCategory.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            showDeleteConfirmationDialog(position);
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void showEditCategoryDialog(int position) {
        CategoryModel category = categoryModelList.get(position);
        View editCategoryView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_category, null);
        EditText editCategoryName = editCategoryView.findViewById(R.id.edit_category_name);
        Button updateButton = editCategoryView.findViewById(R.id.update_button);

        editCategoryName.setText(category.getCategoryName());

        AlertDialog editDialog = new AlertDialog.Builder(context)
                .setTitle("Edit Category Name")
                .setView(editCategoryView)
                .setPositiveButton("Update", null)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create();

        editDialog.setOnShowListener(dialog -> {
            updateButton.setOnClickListener(v -> {
                String newName = editCategoryName.getText().toString().trim();
                if (!newName.isEmpty() && !newName.equals(category.getCategoryName())) {
                    // Update the category name (you need to implement the updateCategoryName method)
                    updateCategoryName(Integer.parseInt(category.getCategoryID()), newName);
                    editDialog.dismiss();
                }
            });

            editCategoryName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    //We not performing anything before text changes
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String newName = s.toString().trim();
                    updateButton.setEnabled(!newName.isEmpty() && !newName.equals(category.getCategoryName()));
                }

                @Override
                public void afterTextChanged(Editable s) {
                    //We not performing anything after text changes
                }
            });
        });

        editDialog.show();
    }

    private void showDeleteConfirmationDialog(int position) {
        CategoryModel category = categoryModelList.get(position);
        new AlertDialog.Builder(context)
                .setTitle("Confirmation")
                .setMessage("Do you want to delete the " + category.getCategoryName() + " category?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Log.i("Adapter", "Yes clicked.");
                    deleteCategory(Integer.parseInt(category.getCategoryID()));
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // Dismiss the dialog
                    Log.i("Adapter", "No clicked.");
                    dialog.dismiss();
                })
                .show();
    }

    private void deleteCategory(int id) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Deleting category...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Utils.deleteCategory(id, "NULL", (Activity) context, new OperationCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (Boolean.TRUE.equals(result)) {
                    moveItemsUnderTheDeletedCategory(id);
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

    private void moveItemsUnderTheDeletedCategory(int id) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Deleting category...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Utils.categoryToUncategorized(id, (Activity) context, new OperationCallback<Boolean>(){
            @Override
            public void onSuccess(Boolean result) {
                progressDialog.dismiss();
                if (Boolean.TRUE.equals(result)) {
                    Toast.makeText(context, "Category Deleted Successfully.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, HomeActivity.class);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }
            }
            @Override
            public void onFailure(String error) {
                progressDialog.dismiss();
                Toast.makeText(context, "Failed to Delete Category.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCategoryName(int categoryId, String newName) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Updating category name...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Utils.modifyCategoryName(categoryId, newName, (Activity) context, new OperationCallback<Boolean>(){
            @Override
            public void onSuccess(Boolean result) {
                progressDialog.dismiss();
                if (Boolean.TRUE.equals(result)) {
                    Toast.makeText(context, "Category Name Changed Successfully.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, HomeActivity.class);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }
            }
            @Override
            public void onFailure(String error) {
                progressDialog.dismiss();
                Toast.makeText(context, "Failed to Modify Category Name.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToHome() {
        Intent intent = new Intent(context, ViewItemActivity.class);
        context.startActivity(intent);
    }
}
