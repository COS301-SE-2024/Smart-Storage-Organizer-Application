package com.example.smartstorageorganizer.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.ViewItemActivity;
import com.example.smartstorageorganizer.model.CategoryModel;

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

            context.startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(view -> {
            new AlertDialog.Builder(context)
                    .setTitle("Confirmation")
                    .setMessage("Do you want to delete the "+categoryModelList.get(holder.getAdapterPosition()).getCategoryName()+" category?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Log.i("Adapter", "Yes clicked.");
                        Log.i("Delete", "Yes clicked.");
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        // Dismiss the dialog
                        Log.i("Adapter", "No clicked.");
                        dialog.dismiss();
                    })
                    .show();
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
}
