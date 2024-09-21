package com.example.smartstorageorganizer.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstorageorganizer.AddCategoryActivity;
import com.example.smartstorageorganizer.AddColorCodeActivity;
import com.example.smartstorageorganizer.AddItemActivity;
import com.example.smartstorageorganizer.ItemDetailsActivity;
import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.UnitActivity;
import com.example.smartstorageorganizer.ViewUnitItemsActivity;
import com.example.smartstorageorganizer.model.ArrangementModel;
import com.example.smartstorageorganizer.model.unitModel;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.Utils;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import kotlin.Unit;

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
        holder.categories.setText(String.join(", ", unit.getCategories()));

        boolean isExpanded = unit.isExpanded();
        holder.capacity.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.capacityUsed.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.categories.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.viewItemsButton.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.modifyText.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.viewText.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.arrow.setRotation(isExpanded ? 180 : 0);

//        if (isExpanded && !unit.hasCategories()) {
//            loadCategoriesOfUnits(unit.getId(), holder, unit);
//        } else if (unit.hasCategories()) {
//            holder.categories.setText(String.join(", ", unit.getCategories()));
//        }

        holder.cardViewDescription.setOnClickListener(v -> {
            unit.setExpanded(!unit.isExpanded());
            notifyItemChanged(position);
        });

        holder.viewItemsButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ViewUnitItemsActivity.class);
            intent.putExtra("unit_name", unitList.get(holder.getAdapterPosition()).getUnitName());
            intent.putExtra("unit_id", unitList.get(holder.getAdapterPosition()).getId());

            context.startActivity(intent);
        });

        holder.modifyText.setOnClickListener(v -> showBottomDialog(unit.getUnitName(), unit.getCapacity()));
        holder.viewText.setOnClickListener(v -> {
            generateProcess(unit.getId(), unit.getUnitName(), holder);
        });
    }

    @Override
    public int getItemCount() {
        return unitList.size();
    }

    public static class UnitViewHolder extends RecyclerView.ViewHolder {
        TextView unitName, capacity, capacityUsed, categories, modifyText, viewText, viewItemsButton;
        ImageView arrow;
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
            modifyText = itemView.findViewById(R.id.modifyText);
            viewText = itemView.findViewById(R.id.viewText);
        }
    }

    private void generateProcess(String unit_id, String unit_name, UnitViewHolder holder) {
        Utils.generateProcess(unit_id, unit_name, (Activity) context, new OperationCallback<ArrangementModel>() {
            @Override
            public void onSuccess(ArrangementModel result) {
                Toast.makeText(context, result.getImageUrl(), Toast.LENGTH_LONG).show();
                Intent sceneViewerIntent = new Intent(Intent.ACTION_VIEW);
                sceneViewerIntent.setData(Uri.parse("https://arvr.google.com/scene-viewer/1.0?file="+result.getImageUrl()));
                sceneViewerIntent.setPackage("com.google.android.googlequicksearchbox");
                context.startActivity(sceneViewerIntent);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(context, "Failed to generate Image: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showBottomDialog(String name, int unitCapacity) {
//        unitModel unit = unitList.get(position);

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheet_layout_unit);

        TextInputEditText unitName = dialog.findViewById(R.id.unitName);
        TextInputEditText capacity = dialog.findViewById(R.id.capacity);
////        LinearLayout unitLayout = dialog.findViewById(R.id.layoutUnit);
////        LinearLayout groupingLayout = dialog.findViewById(R.id.layoutGrouping);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);
//
        unitName.setText(name);
        capacity.setText(String.valueOf(unitCapacity));

//        itemLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), AddItemActivity.class);
//                logUserFlow("HomeFragment", "AddItemActivity");
//                intent.putExtra("email", currentEmail);
//                startActivity(intent);
//            }
//        });
//
//        categoryLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), AddCategoryActivity.class);
//                logUserFlow("HomeFragment", "AddCategoryActivity");
//                intent.putExtra("email", currentEmail);
//                startActivity(intent);
//            }
//        });
//
//        unitLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), UnitActivity.class);
//                logUserFlow("HomeFragment", "UnitActivity");
//                startActivity(intent);
//            }
//        });
//
//        groupingLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), AddColorCodeActivity.class);
//                logUserFlow("HomeFragment", "AddColorCodeActivity");
//                intent.putExtra("email", currentEmail);
//                startActivity(intent);
//            }
//        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

}
