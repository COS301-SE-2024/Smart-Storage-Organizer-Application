package com.example.smartstorageorganizer.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstorageorganizer.ItemDetailsActivity;
import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.model.ItemModel;
import com.example.smartstorageorganizer.model.SearchResult;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private List<ItemModel> searchResults;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(ItemModel item);
    }

    public SearchAdapter(List<ItemModel> searchResults, Context context) {
        this.searchResults = searchResults;
        this.context = context;
//        this.onItemClickListener = onItemClickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemModel searchResult = searchResults.get(position);
        holder.titleTextView.setText(searchResult.getItemName());
        holder.descriptionTextView.setText(searchResult.getDescription());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ItemDetailsActivity.class);
            intent.putExtra("item_id", searchResult.getItemId());
            intent.putExtra("item_name", searchResult.getItemName());
            intent.putExtra("item_description", searchResult.getDescription());
            intent.putExtra("location", searchResult.getLocation());
            intent.putExtra("color_code", searchResult.getColourCoding());
            intent.putExtra("item_qrcode", searchResult.getQrcode());
            intent.putExtra("item_image", searchResult.getItemImage());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    public void updateData(List<ItemModel> newSearchResults) {
        searchResults = newSearchResults;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView descriptionTextView;

        public ViewHolder(View view) {
            super(view);
            titleTextView = view.findViewById(R.id.title);
            descriptionTextView = view.findViewById(R.id.description);
        }
    }
}