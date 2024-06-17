package com.example.smartstorageorganizer.Adapters;


import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.smartstorageorganizer.ItemInfoActivity;
import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.model.ItemModel;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;
import java.util.Objects;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private Context context;
    private List<ItemModel> ItemModelList;

    public ItemAdapter(Context context, List<ItemModel> ItemModelList) {
        this.context = context;
        this.ItemModelList = ItemModelList;
    }

    @NonNull
    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ViewHolder holder, int position) {
        Log.i("Adapter", "Adapter function.");
//        Glide.with(context).load(ItemModelList.get(position).getItem_image()).into(holder.image);
        holder.name.setText(ItemModelList.get(position).getItem_name());
        holder.description.setText(ItemModelList.get(position).getDescription());
//        holder.price_before.setPaintFlags(holder.price_before.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        if(!Objects.equals(ItemModelList.get(position).getItem_image(), "empty")){
            Glide.with(context).load(ItemModelList.get(position).getItem_image()).placeholder(R.drawable.appliance).error(R.drawable.appliance).into(holder.itemImage);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ItemInfoActivity.class);
                intent.putExtra("item_name", ItemModelList.get(holder.getAdapterPosition()).getItem_name());
                intent.putExtra("item_description", ItemModelList.get(holder.getAdapterPosition()).getDescription());
                intent.putExtra("location", ItemModelList.get(holder.getAdapterPosition()).getLocation());
                intent.putExtra("color_code", ItemModelList.get(holder.getAdapterPosition()).getColourcoding());
                intent.putExtra("item_id", ItemModelList.get(holder.getAdapterPosition()).getItem_id());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ItemModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView image;
        TextView name;
        TextView description;
        ImageView itemImage;
        Button more_info_button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_image);
            name = itemView.findViewById(R.id.item_name);
            description = itemView.findViewById(R.id.item_description);
            itemImage = itemView.findViewById(R.id.itemImage);

//            more_info_button = itemView.findViewById(R.id.more_info_button);
        }
    }

}


