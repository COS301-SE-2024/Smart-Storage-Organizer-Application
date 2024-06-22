package com.example.smartstorageorganizer.Adapters;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.smartstorageorganizer.ItemInfoActivity;
import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.ViewItemActivity;
import com.example.smartstorageorganizer.model.CategoryModel;

import java.util.List;
import java.util.Objects;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private Context context;
    private List<CategoryModel> categoryModelList;

    public CategoryAdapter(Context context, List<CategoryModel> categoryModelList) {
        this.context = context;
        this.categoryModelList = categoryModelList;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.category_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        Log.i("Adapter", "Adapter function.");
//        Glide.with(context).load(ItemModelList.get(position).getItem_image()).into(holder.image);
        holder.name.setText(categoryModelList.get(position).getCategoryName());
//        holder.description.setText(ParentCategoryModelList.get(position).getDescription());
//        holder.price_before.setPaintFlags(holder.price_before.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        if(!Objects.equals(categoryModelList.get(position).getImageUrl(), "empty")){
            Glide.with(context).load(categoryModelList.get(position).getImageUrl()).placeholder(R.drawable.no_image).error(R.drawable.no_image).into(holder.image);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ViewItemActivity.class);
                intent.putExtra("category", categoryModelList.get(holder.getAdapterPosition()).getCategoryName());
                intent.putExtra("category_id", categoryModelList.get(holder.getAdapterPosition()).getCategoryID());
//                intent.putExtra("location", ItemModelList.get(holder.getAdapterPosition()).getLocation());
//                intent.putExtra("color_code", ItemModelList.get(holder.getAdapterPosition()).getColourcoding());
//                intent.putExtra("item_id", ItemModelList.get(holder.getAdapterPosition()).getItem_id());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.category_image);
            name = itemView.findViewById(R.id.category_name);
        }
    }

}


