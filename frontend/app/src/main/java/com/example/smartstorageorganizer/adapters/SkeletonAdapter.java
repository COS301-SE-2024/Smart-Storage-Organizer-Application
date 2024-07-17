package com.example.smartstorageorganizer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstorageorganizer.R;

public class SkeletonAdapter extends RecyclerView.Adapter<SkeletonAdapter.SkeletonViewHolder> {

    private int itemCount;

    public SkeletonAdapter(int itemCount) {
        this.itemCount = itemCount;
    }

    @NonNull
    @Override
    public SkeletonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.skeleton_item, parent, false);
        return new SkeletonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SkeletonViewHolder holder, int position) {
        // No binding needed for skeletons
    }

    @Override
    public int getItemCount() {
        return itemCount;
    }

    static class SkeletonViewHolder extends RecyclerView.ViewHolder {
        SkeletonViewHolder(View itemView) {
            super(itemView);
        }
    }
}

