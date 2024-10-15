package com.example.smartstorageorganizer.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import com.example.smartstorageorganizer.model.BinItemModel;
import com.example.smartstorageorganizer.model.unitModel;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.Utils;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;
import java.util.Objects;

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
//        holder.capacity.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
//        holder.capacityUsed.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
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
            ProgressDialog progressDialog;
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Generating 3D Arrangement...");
            progressDialog.setCancelable(false);

            progressDialog.show();
            generateProcess(unit.getId(), unit.getUnitName(), holder, progressDialog);
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

//    private void generateProcess(String unit_id, String unit_name, UnitViewHolder holder) {
//        Utils.generateProcess(unit_id, unit_name, (Activity) context, new OperationCallback<ArrangementModel>() {
//            @Override
//            public void onSuccess(ArrangementModel result) {
//                Toast.makeText(context, result.getImageUrl(), Toast.LENGTH_LONG).show();
//                Intent sceneViewerIntent = new Intent(Intent.ACTION_VIEW);
//                sceneViewerIntent.setData(Uri.parse("https://arvr.google.com/scene-viewer/1.0?file="+result.getImageUrl()));
//                sceneViewerIntent.setPackage("com.google.android.googlequicksearchbox");
//                context.startActivity(sceneViewerIntent);
//            }
//
//            @Override
//            public void onFailure(String error) {
//                Toast.makeText(context, "Failed to generate Image: " + error, Toast.LENGTH_LONG).show();
//            }
//        });
//    }

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

    public void showArrangementDialog(String text, String unit_id, String unit_name, UnitViewHolder holder, ProgressDialog progressDialog) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.arrangement_popup, null);

        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        Button closeButton = dialogView.findViewById(R.id.finishButton);
        TextView message = dialogView.findViewById(R.id.textView3);
        if(!Objects.equals(text, "")){
            message.setText(text);
            closeButton.setText("Close");
        }
        else {
            message.setText("The arrangement could not be generated because one or more items are missing essential dimensions (width, depth, height, or weight). All items must have complete dimensions in order for the system to create an optimized arrangement. Please ensure that each item has the necessary dimensions and try again.");
            closeButton.setText("Close");
        }
        closeButton.setOnClickListener(v -> {
            alertDialog.dismiss();
//            if(!Objects.equals(text, "")){
//                progressDialog.dismiss();
//                generateProcess(unit_id, unit_name, holder, progressDialog);
//            }
        });

//        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void generateProcess(String unit_id, String unit_name, UnitViewHolder holder, ProgressDialog progressDialog) {
        Utils.generateProcess(unit_id, unit_name, (Activity) context, new OperationCallback<ArrangementModel>() {
            @Override
            public void onSuccess(ArrangementModel result) {
                if (Objects.equals(result.getImageUrl(), "400")) {
                    progressDialog.dismiss();
                } else if (Objects.equals(result.getImageUrl(), "500")) {
                    progressDialog.dismiss();
//                    mainLayout.setVisibility(View.VISIBLE);
//                    arrangementLoader.setVisibility(View.GONE);
                    showArrangementDialog("There was an error generating the arrangement. Please ensure the unit is not empty and contains items before trying again.", unit_id, unit_name, holder, progressDialog);
//                    generateProcess(unit_id, unit_name, holder, progressDialog);
                } else {
//                    progressDialog.dismiss();
//                    mainLayout.setVisibility(View.VISIBLE);
//                    arrangementLoader.setVisibility(View.GONE);
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

                    LayoutInflater inflater = LayoutInflater.from(context);
                    View dialogView = inflater.inflate(R.layout.dialog_items_list, null);
                    dialogBuilder.setView(dialogView);

                    TableLayout tableLayout = dialogView.findViewById(R.id.items_table);

                    TableRow headerRow = new TableRow(context);
                    TextView headerName = new TextView(context);
                    TextView headerColor = new TextView(context);

                    headerName.setText("Name");
                    headerName.setPadding(8, 8, 8, 8);
                    headerName.setTypeface(null, Typeface.BOLD);
                    headerColor.setText("Color");
                    headerColor.setPadding(8, 8, 8, 8);
                    headerColor.setTypeface(null, Typeface.BOLD);

                    headerRow.addView(headerName);
                    headerRow.addView(headerColor);

                    tableLayout.addView(headerRow);

                    List<BinItemModel> items = result.getItems();

                    for (BinItemModel item : items) {
                        TableRow row = new TableRow(context);

                        TextView nameView = new TextView(context);
                        nameView.setText(item.getName());
                        nameView.setPadding(8, 8, 8, 8);

                        View colorView = new View(context);
                        colorView.setBackgroundColor(parseColor(item.getColor()));

                        TableRow.LayoutParams params = new TableRow.LayoutParams(50, 50);
                        params.setMargins(8, 8, 8, 8);
                        colorView.setLayoutParams(params);

                        row.addView(nameView);
                        row.addView(colorView);

                        tableLayout.addView(row);
                    }

                    if (!result.getUnfittedItems().isEmpty()) {
                        // Header for unfitted items
                        TableRow unfittedHeaderRow = new TableRow(context);
                        TextView unfittedHeaderName = new TextView(context);
                        TextView unfittedHeaderColor = new TextView(context);

                        unfittedHeaderName.setText("Unfitted Name");
                        unfittedHeaderName.setPadding(8, 8, 8, 8);
                        unfittedHeaderName.setTypeface(null, Typeface.BOLD);

                        unfittedHeaderRow.addView(unfittedHeaderName);

                        tableLayout.addView(unfittedHeaderRow);

                        // Rows for unfitted items
                        List<BinItemModel> unfittedItems = result.getUnfittedItems();
                        for (BinItemModel unfittedItem : unfittedItems) {
                            TableRow row = new TableRow(context);

                            TextView unfittedNameView = new TextView(context);
                            unfittedNameView.setText(unfittedItem.getName());
                            unfittedNameView.setPadding(8, 8, 8, 8);

                            row.addView(unfittedNameView);

                            tableLayout.addView(row);
                        }
                    }

                    Button view3DButton = dialogView.findViewById(R.id.view_3d_button);
                    view3DButton.setOnClickListener(v -> {
                        Intent sceneViewerIntent = new Intent(Intent.ACTION_VIEW);
                        sceneViewerIntent.setData(Uri.parse("https://arvr.google.com/scene-viewer/1.0?file=" + result.getImageUrl()));
                        sceneViewerIntent.setPackage("com.google.android.googlequicksearchbox");
                        context.startActivity(sceneViewerIntent);
                    });

                    // Show the dialog
                    AlertDialog dialog = dialogBuilder.create();
                    dialog.show();
                    progressDialog.dismiss();
                }

            }



            @Override
            public void onFailure(String error) {
                if(Objects.equals(error.toLowerCase(), "timeout")){
                    generateProcess(unit_id, unit_name, holder, progressDialog);
                }
//                Toast.makeText(context, "Failed to generate Image: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    public int parseColor(String colorString) {
        // Remove the square brackets and split the string by commas
        String[] rgbValues = colorString.replaceAll("[\\[\\]]", "").split(",");

        // Parse the RGB values and alpha channel
        int red = Integer.parseInt(rgbValues[0].trim());
        int green = Integer.parseInt(rgbValues[1].trim());
        int blue = Integer.parseInt(rgbValues[2].trim());
        int alpha = Integer.parseInt(rgbValues[3].trim());

        // Return the color as an int
        return Color.argb(alpha, red, green, blue);
    }

    private void modifyUnit(String unitId, String width, String height, String depth, String maxWeight) {
//        skeletonLoader.startShimmer();
//        skeletonLoader.setVisibility(View.VISIBLE);
//        recyclerViewUnits.setVisibility(View.GONE);

        Utils.modifyUnit(unitId, width, height, depth, maxWeight, (Activity) context, new OperationCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if(result) {
                    Toast.makeText((Activity) context, "Modified Successful", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String error) {
//                swipeRefreshLayout.setRefreshing(false);
//                skeletonLoader.setVisibility(View.GONE);
                Toast.makeText((Activity) context, "Failed to fetch units: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
