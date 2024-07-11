package com.example.smartstorageorganizer.adapters;

import android.media.RouteListingPreference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstorageorganizer.Item;
import com.example.smartstorageorganizer.R;

import java.util.List;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {

    private List<Item> items;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(RouteListingPreference.Item item);

        void onItemClick(Item item);
    }

    public SearchResultsAdapter(List<Item> items, OnItemClickListener onItemClickListener) {
        this.items = items;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = items.get(position);
        holder.bind(item, onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateData(List<Item> newItems) {
        items = newItems;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView itemName;
        private TextView itemDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            itemDescription = itemView.findViewById(R.id.item_description);
        }

        public void bind(final Item item, final OnItemClickListener onItemClickListener) {
            itemName.setText(item.getName());
            itemDescription.setText(item.getDescription());
            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(item));
        }
    }
}
