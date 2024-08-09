package com.example.smartstorageorganizer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.model.unitModel;

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
        holder.capacity.setText(unit.getCapacityAsString());
        holder.capacityUsed.setText(unit.getCapacityUsedAsString());


        boolean isExpanded = unit.isExpanded();
        holder.capacity.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.arrow.setRotation(isExpanded ? 180 : 0);

        holder.cardViewDescription.setOnClickListener(v -> {
            unit.setExpanded(!unit.isExpanded());
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return unitList.size();
    }

    public static class UnitViewHolder extends RecyclerView.ViewHolder {
        TextView unitName, capacity, capacityUsed;
        ImageView arrow;
        CardView cardViewDescription;

        public UnitViewHolder(@NonNull View itemView) {
            super(itemView);
            unitName = itemView.findViewById(R.id.unitName);
            capacity = itemView.findViewById(R.id.capacity);
            capacityUsed = itemView.findViewById(R.id.capacityUsed);
            arrow = itemView.findViewById(R.id.arrow);
            cardViewDescription = itemView.findViewById(R.id.cardViewDescription);
        }
    }
}
