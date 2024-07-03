package com.example.smartstorageorganizer.adapters;


import android.annotation.SuppressLint;
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

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.smartstorageorganizer.ItemInfoActivity;
import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.model.ColorCodeModel;
import com.example.smartstorageorganizer.model.ItemModel;

import java.util.List;
import java.util.Objects;

public class ColorCodeAdapter extends RecyclerView.Adapter<ColorCodeAdapter.ViewHolder> {

    private final Context context;
    private final List<ColorCodeModel> itemModelList;

    public ColorCodeAdapter(Context context, List<ColorCodeModel> itemModelList) {
        this.context = context;
        this.itemModelList = itemModelList;
    }

    @NonNull
    @Override
    public ColorCodeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.color_code_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ColorCodeAdapter.ViewHolder holder, int position) {
        Log.i("Adapter", "Adapter function.");
        ColorCodeModel model = itemModelList.get(position);

        holder.name.setText(model.getName());
        holder.description.setText(model.getDescription());

        // Parse the color string and set the card background color
        try {
            int color = Color.parseColor(model.getColor());
            holder.cardView.setCardBackgroundColor(color);
        } catch (IllegalArgumentException e) {
            Log.e("Adapter", "Invalid color string: " + model.getColor());
        }
    }

    @Override
    public int getItemCount() {
        return itemModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView description;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.color_code_name);
            description = itemView.findViewById(R.id.color_code_description);
            cardView = itemView.findViewById(R.id.color_card_view);
        }
    }
}
