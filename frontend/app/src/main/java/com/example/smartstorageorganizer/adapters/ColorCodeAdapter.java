package com.example.smartstorageorganizer.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.smartstorageorganizer.BuildConfig;
import com.example.smartstorageorganizer.GlideApp;
import com.example.smartstorageorganizer.ItemDetailsActivity;
import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.ViewItemActivity;
import com.example.smartstorageorganizer.model.ColorCodeModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
                Intent intent = new Intent(v.getContext(), ViewItemActivity.class);
                intent.putExtra("color_code_id", itemModelList.get(holder.getAdapterPosition()).getId());
                intent.putExtra("category", "");
                intent.putExtra("category_id", "");

                context.startActivity(intent);
            }
        });

        holder.cardView.setOnLongClickListener(v -> {
            selectionMode = true;
            toggleSelection(holder, position, model);
            return true;
        });
        holder.qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQRCodeDialog(model.getQrCode());
            }
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

    public void selectItem(ColorCodeModel model) {
        int position = itemModelList.indexOf(model);
        if (position >= 0 && !selectedItems.contains(position)) {
            selectedItems.add(position);
            notifyItemChanged(position);
            onSelectionChangedListener.onSelectionChanged(selectedItems);
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

    public List<ColorCodeModel> getSelectedItems() {
        List<ColorCodeModel> selectedColorCodes = new ArrayList<>();
        for (int position : selectedItems) {
            selectedColorCodes.add(itemModelList.get(position));
        }
        return selectedColorCodes;
    }

    public List<ColorCodeModel> getItemModelList() {
        return itemModelList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView description;
        ImageView qrcode;
        CardView cardView;
        ImageView deleteIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.color_code_name);
            description = itemView.findViewById(R.id.color_code_description);
            cardView = itemView.findViewById(R.id.color_card_view);
            deleteIcon = itemView.findViewById(R.id.delete_icon);
            qrcode = itemView.findViewById(R.id.qrcode);
        }
    }

    private void showQRCodeDialog(String qrCodeUrl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View qrView = LayoutInflater.from(context).inflate(R.layout.dialog_qr_code, null);
        builder.setView(qrView);

        ImageView qrCodeImage = qrView.findViewById(R.id.qr_code_image);
        Button shareButton = qrView.findViewById(R.id.share_button);
        Button downloadButton = qrView.findViewById(R.id.download_button);

        String imageUrl;
        imageUrl = qrCodeUrl;

        if (imageUrl.endsWith(".svg")) {
            // Use Glide to load SVG
            GlideApp.with(context)
                    .as(PictureDrawable.class)
                    .load(imageUrl)
                    .placeholder(R.drawable.no_image)
                    .error(R.drawable.no_image)
//                    .listener((RequestListener<PictureDrawable>) new SvgSoftwareLayerSetter(qrCodeImage))
                    .into(qrCodeImage);
        } else {
            // Fallback for non-SVG images
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.no_image)
                    .error(R.drawable.no_image)
                    .into(qrCodeImage);
        }

        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();

        shareButton.setOnClickListener(v -> {
                shareImage(qrCodeUrl, context);
        });

        downloadButton.setOnClickListener(v -> {
                downloadImage(qrCodeUrl, context);
        });
    }

    private void shareImage(String imageUrl, Context context) {
        Glide.with(context)
                .asBitmap()
                .load(imageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        try {
                            // Create directory in external files (scoped storage for API 29+)
                            File imagesDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "shared_images");

                            if (!imagesDir.exists()) {
                                imagesDir.mkdirs();  // Create the directory if it doesn't exist
                            }

                            // Save the image to a file
                            File file = new File(imagesDir, "shared_image.png");
                            FileOutputStream fOut = new FileOutputStream(file);
                            resource.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                            fOut.flush();
                            fOut.close();
                            file.setReadable(true, false);

                            // Get URI using FileProvider
                            Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);

                            // Share image intent
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra(Intent.EXTRA_STREAM, uri);
                            intent.setType("image/png");
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                            context.startActivity(Intent.createChooser(intent, "Share image via"));
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Failed to share image", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Handle cleanup if necessary
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void downloadImage(String imageUrl, Context context) {
        Glide.with(context)
                .asBitmap()
                .load(imageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        try {
                            // Create directory in external public storage (API 29+ must handle this with scoped storage)
                            File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "SmartStorage");
                            if (!directory.exists()) {
                                directory.mkdirs();
                            }

                            // Save the image to a file
                            File file = new File(directory, "qr_code_" + System.currentTimeMillis() + ".png");
                            FileOutputStream fOut = new FileOutputStream(file);
                            resource.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                            fOut.flush();
                            fOut.close();

                            Toast.makeText(context, "Image downloaded", Toast.LENGTH_SHORT).show();

                            // Refresh gallery to show the new image
                            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            Uri contentUri = Uri.fromFile(file);
                            mediaScanIntent.setData(contentUri);
                            context.sendBroadcast(mediaScanIntent);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Failed to download image", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Cleanup if necessary
                    }
                });
    }

}