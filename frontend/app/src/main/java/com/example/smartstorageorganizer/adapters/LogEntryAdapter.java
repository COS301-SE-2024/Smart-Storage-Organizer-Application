package com.example.smartstorageorganizer.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.model.LogEntry;

import java.util.List;
import java.util.Objects;

public class LogEntryAdapter extends RecyclerView.Adapter<LogEntryAdapter.ViewHolder> {

    private List<LogEntry> logEntries;

    public LogEntryAdapter(List<LogEntry> logEntries) {
        this.logEntries = logEntries;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_log_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LogEntry logEntry = logEntries.get(position);

        holder.userName.setText(logEntry.getUserName());
        holder.actionDone.setText(logEntry.getActionDone());
        holder.itemActedUpon.setText(logEntry.getItemActedUpon());
        holder.details.setText(logEntry.getDetails());

        int color = Color.parseColor("#4CAF50");
        if(Objects.equals(logEntry.getActionDone(), "Modified")){
            color = Color.parseColor("#2196F3");
        }
        else if(Objects.equals(logEntry.getActionDone(), "Deleted")){
            color = Color.parseColor("#F44336");
        }
        holder.actionDone.setTextColor(color);

        // Update expandable section if available
        if (logEntry.getPreviousDetails() != null && logEntry.getNewDetails() != null) {
            holder.previousDetails.setText("Previous: " + logEntry.getPreviousDetails());
            holder.newDetails.setText("New: " + logEntry.getNewDetails());
        }

        // Handle the expand/collapse toggle
        boolean isExpanded = logEntry.isExpanded();
        if(logEntry.isExpanded()){
            holder.expandCollapseText.setText("Hide Change History");
        }
        else {
            holder.expandCollapseText.setText("View Change History");
        }
        holder.expandableSection.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        holder.expandCollapseText.setOnClickListener(v -> {
            logEntry.setExpanded(!logEntry.isExpanded());
            if(!logEntry.isExpanded()){
                holder.expandCollapseText.setText("Hide Change History");
            }
            else {
                holder.expandCollapseText.setText("View Change History");
            }
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return logEntries.size();
    }

    // Method to add new data (for pagination)
    public void addData(List<LogEntry> newLogEntries) {
        int initialSize = logEntries.size();
        logEntries.addAll(newLogEntries);
        notifyDataSetChanged();
//        notifyItemRangeInserted(initialSize, newLogEntries.size());
    }

    public void clearData(){
        logEntries.clear();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName, actionDone, itemActedUpon, details, previousDetails, newDetails, expandCollapseText;
        LinearLayout expandableSection;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            actionDone = itemView.findViewById(R.id.actionDone);
            itemActedUpon = itemView.findViewById(R.id.itemActedUpon);
            details = itemView.findViewById(R.id.details);
            previousDetails = itemView.findViewById(R.id.previousDetails);
            newDetails = itemView.findViewById(R.id.newDetails);
            expandCollapseText = itemView.findViewById(R.id.expandCollapseText);
            expandableSection = itemView.findViewById(R.id.expandableSection);
        }
    }
}


