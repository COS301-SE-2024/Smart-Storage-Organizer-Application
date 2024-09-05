package com.example.smartstorageorganizer.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstorageorganizer.ItemDetailsActivity;
import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.ViewUnitItemsActivity;
import com.example.smartstorageorganizer.model.unitModel;
import com.example.smartstorageorganizer.ui.home.HomeViewModel;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.Utils;

import java.util.List;

public class UnitsAdapter extends RecyclerView.Adapter<UnitsAdapter.UnitViewHolder> {

    private Context context;
    private List<unitModel> unitList;

    public UnitsAdapter(Context context, List<unitModel> unitList) {
        this.context = context;
        this.unitList = unitList;
    }

    @NonNull
    @Override
    public UnitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_unit, parent, false);
        return new UnitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UnitViewHolder holder, int position) {
        unitModel unit = unitList.get(position);
        holder.unitName.setText(unit.getUnitName());
//        holder.capacity.setText(unit.getCapacityAsString());
//        holder.capacityUsed.setText(unit.getCapacityUsedAsString());

        // Handle save button click
        holder.saveButton.setOnClickListener(v -> {
            try {
                // Fetch user inputs
                float unitWidth = Float.parseFloat(holder.unitWidthInput.getText().toString());
                float unitHeight = Float.parseFloat(holder.unitHeightInput.getText().toString());
                float unitDepth = Float.parseFloat(holder.unitDepthInput.getText().toString());

                // Update the unit model with the new configuration
                unit.setUnitWidth(unitWidth);
                unit.setUnitHeight(unitHeight);
                unit.setUnitDepth(unitDepth);

                // Notify the user
                Toast.makeText(context, "Unit configuration saved for: " + unit.getUnitName(), Toast.LENGTH_SHORT).show();

                // Optionally, refresh the view or trigger any additional logic
                notifyItemChanged(position);

            } catch (NumberFormatException e) {
                Toast.makeText(context, "Please enter valid dimensions.", Toast.LENGTH_SHORT).show();
            }
        });

        boolean isExpanded = unit.isExpanded();
        holder.capacity.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.capacityUsed.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.categories.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.viewItemsButton.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.arrow.setRotation(isExpanded ? 180 : 0);

        if (isExpanded && !unit.hasCategories()) {
            loadCategoriesOfUnits(unit.getId(), holder, unit);
        } else if (unit.hasCategories()) {
            holder.categories.setText(String.join(", ", unit.getCategories()));
        }

        holder.cardViewDescription.setOnClickListener(v -> {
            unit.setExpanded(!unit.isExpanded());
            notifyItemChanged(position);
        });

        holder.viewItemsButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ViewUnitItemsActivity.class);
            intent.putExtra("unit_name", unitList.get(holder.getAdapterPosition()).getUnitName());
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return unitList.size();
    }

    public static class UnitViewHolder extends RecyclerView.ViewHolder {
        public HomeViewModel unitWidthInput;
        TextView unitName, capacity, capacityUsed, categories;
        ImageView arrow, viewItemsButton;
        CardView cardViewDescription;

        public UnitViewHolder(@NonNull View itemView) {
            super(itemView);
            unitName = itemView.findViewById(R.id.unitName);
            capacity = itemView.findViewById(R.id.capacity);
            capacityUsed = itemView.findViewById(R.id.capacityUsed);
            categories = itemView.findViewById(R.id.categories);
            arrow = itemView.findViewById(R.id.arrow);
            viewItemsButton = itemView.findViewById(R.id.viewItemsButton);
            cardViewDescription = itemView.findViewById(R.id.cardViewDescription);
        }
    }

    private void loadCategoriesOfUnits(String unit_id, UnitViewHolder holder, unitModel unit) {
        Utils.FetchCategoriesOfUnits(unit_id, (Activity) context, new OperationCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> result) {
                unit.setCategories(result);
                holder.categories.setText(String.join(", ", result));
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(context, "Failed to fetch categories: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
