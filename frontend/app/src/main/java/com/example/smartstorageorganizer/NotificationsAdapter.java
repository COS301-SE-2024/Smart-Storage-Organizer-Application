package com.example.smartstorageorganizer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {
    private List<NotificationModel> notificationsList;
    private Context context;


    private OnItemClickListener onItemClickListener;

    // Interface for handling item clicks
    public interface OnItemClickListener {
        void onItemClick(NotificationModel notification);
    }

    // Constructor to initialize context and list
    public NotificationsAdapter(Context context, List<NotificationModel> notificationsList, OnItemClickListener listener) {
        this.context = context;
        this.notificationsList = notificationsList;
        this.onItemClickListener = listener;
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView messageTextView;
        public TextView dateTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textView_title);
            messageTextView = itemView.findViewById(R.id.textView_message);
            dateTextView = itemView.findViewById(R.id.textView_date);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the custom layout for each list item
        View view = LayoutInflater.from(context).inflate(R.layout.activity_notifications_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the notification data based on position
        NotificationModel notification = notificationsList.get(position);

        // Set data to the views
        holder.titleTextView.setText(notification.getTitle());
        holder.messageTextView.setText(notification.getMessage());
        holder.dateTextView.setText(notification.getDate());

        // Set up the click listener on the item view
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                // Pass NotificationModel object to the click listener
                onItemClickListener.onItemClick(notification);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationsList.size();
    }



}
