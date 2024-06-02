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
        //Glide.with(context).load(FlashSaleModelList.get(position).getImage()).into(holder.image);
        holder.name.setText(ItemModelList.get(position).getItem_name());
        holder.description.setText(ItemModelList.get(position).getDescription());

//        if(!Objects.equals(ItemModelList.get(position).getImg_url_one(), "empty")){
//            Glide.with(context).load(ItemModelList.get(position).getImg_url_one()).placeholder(R.drawable.app_name_text).error(R.drawable.app_name_text).into(holder.image);
//        }

        holder.moreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ItemInfoActivity.class);
                intent.putExtra("item_name", ItemModelList.get(holder.getAdapterPosition()).getItem_name());
                intent.putExtra("item_description", ItemModelList.get(holder.getAdapterPosition()).getDescription());
                intent.putExtra("item_id", ItemModelList.get(holder.getAdapterPosition()).getItem_id());
                intent.putExtra("barcode", ItemModelList.get(holder.getAdapterPosition()).getBarcode());
                intent.putExtra("email", ItemModelList.get(holder.getAdapterPosition()).getEmail());
                intent.putExtra("color_code", ItemModelList.get(holder.getAdapterPosition()).getColourcoding());
                intent.putExtra("quantity", ItemModelList.get(holder.getAdapterPosition()).getQuanity());
                intent.putExtra("location", ItemModelList.get(holder.getAdapterPosition()).getLocation());

                context.startActivity(intent);
            }
        });
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(view.getContext(), ItemInfoActivity.class);
//                intent.putExtra("item_name", ItemModelList.get(holder.getAdapterPosition()).getItem_name());
//                intent.putExtra("item_description", ItemModelList.get(holder.getAdapterPosition()).getDescription());
//                intent.putExtra("item_id", ItemModelList.get(holder.getAdapterPosition()).getItem_id());
//                intent.putExtra("barcode", ItemModelList.get(holder.getAdapterPosition()).getBarcode());
//                intent.putExtra("email", ItemModelList.get(holder.getAdapterPosition()).getEmail());
//                intent.putExtra("color_code", ItemModelList.get(holder.getAdapterPosition()).getColourcoding());
//                intent.putExtra("quantity", ItemModelList.get(holder.getAdapterPosition()).getQuanity());
//                intent.putExtra("location", ItemModelList.get(holder.getAdapterPosition()).getLocation());
//
//                context.startActivity(intent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return ItemModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView image;
        TextView name;
        TextView description;
        Button moreInfo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_image);
            name = itemView.findViewById(R.id.item_name);
            description = itemView.findViewById(R.id.item_description);
            moreInfo = itemView.findViewById(R.id.more_info_button);
        }
    }

}


