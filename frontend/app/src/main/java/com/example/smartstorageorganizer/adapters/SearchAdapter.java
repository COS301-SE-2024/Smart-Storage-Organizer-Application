package com.example.smartstorageorganizer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.model.ItemModel;
import com.example.smartstorageorganizer.model.SearchResult;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private List<ItemModel> searchResults;

    public SearchAdapter(List<ItemModel> searchResults) {
        this.searchResults = searchResults;
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
