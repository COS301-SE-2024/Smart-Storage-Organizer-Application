package com.example.smartstorageorganizer.adapters;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.smartstorageorganizer.ItemInfoActivity;
import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.model.ColorCodeModel;
import com.example.smartstorageorganizer.model.ItemModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ColorCodeAdapter extends RecyclerView.Adapter<ColorCodeAdapter.ViewHolder> {

    private final Context context;
    private final List<ColorCodeModel> itemModelList;
    private final Set<Integer> selectedItems;
    private final OnSelectionChangedListener onSelectionChangedListener;
    private boolean selectionMode = false;

    public interface OnSelectionChangedListener {
        void onSelectionChanged(Set<Integer> selectedItems);
    }

    public ColorCodeAdapter(Context context, List<ColorCodeModel> itemModelList, OnSelectionChangedListener onSelectionChangedListener) {
        this.context = context;
        this.itemModelList = itemModelList;
        this.selectedItems = new HashSet<>();
        this.onSelectionChangedListener = onSelectionChangedListener;
    }

    @NonNull
    @Override
    public ColorCodeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.color_code_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ColorCodeAdapter.ViewHolder holder, int position) {
        ColorCodeModel model = itemModelList.get(position);

        holder.name.setText(model.getName());
        holder.description.setText(model.getDescription());

        try {
            int color = Color.parseColor(model.getColor());
            GradientDrawable drawable = new GradientDrawable();
            drawable.setStroke(4, color);
            drawable.setCornerRadius(12);
            holder.cardView.setBackground(drawable);
        } catch (IllegalArgumentException e) {
            Log.e("Adapter", "Invalid color string: " + model.getColor());
        }

        if (selectedItems.contains(position)) {
            holder.cardView.setBackgroundColor(Color.LTGRAY);
            holder.deleteIcon.setVisibility(View.VISIBLE);
        } else {
            int color = Color.parseColor(model.getColor());
            GradientDrawable drawable = new GradientDrawable();
            drawable.setStroke(4, color);
            drawable.setCornerRadius(12);
            holder.cardView.setBackground(drawable);
            holder.deleteIcon.setVisibility(View.GONE);
        }

        holder.cardView.setOnClickListener(v -> {
            if (selectionMode) {
                toggleSelection(holder, position, model);
            } else {
                // Normal click action
            }
        });

        holder.cardView.setOnLongClickListener(v -> {
            selectionMode = true;
            toggleSelection(holder, position, model);
            return true;
        });
    }

    private void toggleSelection(ViewHolder holder, int position, ColorCodeModel model) {
        if (selectedItems.contains(position)) {
            selectedItems.remove(position);
            int color = Color.parseColor(model.getColor());
            GradientDrawable drawable = new GradientDrawable();
            drawable.setStroke(4, color);
            drawable.setCornerRadius(12);
            holder.cardView.setBackground(drawable);
            holder.deleteIcon.setVisibility(View.GONE);
        } else {
            selectedItems.add(position);
            holder.cardView.setBackgroundColor(Color.LTGRAY);
            holder.deleteIcon.setVisibility(View.VISIBLE);
        }

        onSelectionChangedListener.onSelectionChanged(selectedItems);

        if (selectedItems.isEmpty()) {
            selectionMode = false;
        }
    }

    @Override
    public int getItemCount() {
        return itemModelList.size();
    }

    public void deleteSelectedItems() {
        List<ColorCodeModel> itemsToDelete = new ArrayList<>();
        for (int position : selectedItems) {
            itemsToDelete.add(itemModelList.get(position));
        }
        itemModelList.removeAll(itemsToDelete);
        notifyDataSetChanged();
        selectedItems.clear();
        selectionMode = false;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView description;
        CardView cardView;
        ImageView deleteIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.color_code_name);
            description = itemView.findViewById(R.id.color_code_description);
            cardView = itemView.findViewById(R.id.color_card_view);
            deleteIcon = itemView.findViewById(R.id.delete_icon);
        }
    }
}
