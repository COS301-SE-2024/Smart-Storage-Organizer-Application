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
import com.example.smartstorageorganizer.model.AppReportModel;
import com.example.smartstorageorganizer.model.LogEntry;

import java.util.List;
import java.util.Objects;

public class AppReportAdapter extends RecyclerView.Adapter<AppReportAdapter.ViewHolder> {

    private List<AppReportModel> appReports;

    public AppReportAdapter(List<AppReportModel> appReports) {
        this.appReports = appReports;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_reports_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppReportModel appReport = appReports.get(position);

        holder.pageName.setText(appReport.getPageTitle());
        holder.views.setText(appReport.getViews());
        holder.activeUsers.setText(appReport.getActiveUsers());
        holder.viewsPerUser.setText(appReport.getViewsPerActiveUser());
        holder.averageEngagement.setText(appReport.getAverageEngagementTimePerActiveUser());

//        int color = Color.parseColor("#4CAF50");
//        if(Objects.equals(appReports.getActionDone(), "Modified")){
//            color = Color.parseColor("#2196F3");
//        }
//        else if(Objects.equals(appReports.getActionDone(), "Deleted")){
//            color = Color.parseColor("#F44336");
//        }
//        holder.actionDone.setTextColor(color);

//        // Update expandable section if available
//        if (appReports.getPreviousDetails() != null && appReports.getNewDetails() != null) {
//            holder.previousDetails.setText("Previous: " + logEntry.getPreviousDetails());
//            holder.newDetails.setText("New: " + logEntry.getNewDetails());
//        }

        // Handle the expand/collapse toggle
//        boolean isExpanded = logEntry.isExpanded();
//        if(logEntry.isExpanded()){
//            holder.expandCollapseText.setText("Hide Change History");
//        }
//        else {
//            holder.expandCollapseText.setText("View Change History");
//        }
//        holder.expandableSection.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
//
//        holder.expandCollapseText.setOnClickListener(v -> {
//            logEntry.setExpanded(!logEntry.isExpanded());
//            if(!logEntry.isExpanded()){
//                holder.expandCollapseText.setText("Hide Change History");
//            }
//            else {
//                holder.expandCollapseText.setText("View Change History");
//            }
//            notifyItemChanged(position);
//        });
    }

    @Override
    public int getItemCount() {
        return appReports.size();
    }

    // Method to add new data (for pagination)
    public void addData(List<AppReportModel> newAppReports) {
        int initialSize = appReports.size();
        appReports.addAll(newAppReports);
        notifyDataSetChanged();
//        notifyItemRangeInserted(initialSize, newLogEntries.size());
    }

    public void clearData(){
        appReports.clear();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView pageName, views, activeUsers, viewsPerUser, averageEngagement;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pageName = itemView.findViewById(R.id.page);
            views = itemView.findViewById(R.id.views);
            activeUsers = itemView.findViewById(R.id.activeUsers);
            viewsPerUser = itemView.findViewById(R.id.viewsPerUser);
            averageEngagement = itemView.findViewById(R.id.averageEngagement);
        }
    }
}


