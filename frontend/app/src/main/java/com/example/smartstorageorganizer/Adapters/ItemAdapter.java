package com.example.smartstorageorganizer.Adapters;


import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        //Glide.with(context).load(FlashSaleModelList.get(position).getImage()).into(holder.image);
        holder.name.setText(ItemModelList.get(position).getItem_name());
        holder.description.setText(ItemModelList.get(position).getDescription());
//        holder.price_before.setPaintFlags(holder.price_before.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

//        if(!Objects.equals(ItemModelList.get(position).getImg_url_one(), "empty")){
//            Glide.with(context).load(ItemModelList.get(position).getImg_url_one()).placeholder(R.drawable.app_name_text).error(R.drawable.app_name_text).into(holder.image);
//        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(view.getContext(), Main.class);
//                intent.putExtra("flash_sale_name", FlashSaleModelList.get(holder.getAdapterPosition()).getName());
//                intent.putExtra("category", FlashSaleModelList.get(holder.getAdapterPosition()).getType());
//                intent.putExtra("price_now", FlashSaleModelList.get(holder.getAdapterPosition()).getPrice_now());
//                intent.putExtra("price_before", FlashSaleModelList.get(holder.getAdapterPosition()).getPrice_before());
//                intent.putExtra("img_url_one", FlashSaleModelList.get(holder.getAdapterPosition()).getImg_url_one());

//                context.startActivity(intent);
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_image);
            name = itemView.findViewById(R.id.item_name);
            description = itemView.findViewById(R.id.item_description);
        }
    }

}


