package com.example.smartstorageorganizer;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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
        public Button markAsReadButton;
        public Button markAsUnreadButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textView_title);
            messageTextView = itemView.findViewById(R.id.textView_message);
            dateTextView = itemView.findViewById(R.id.textView_date);
            markAsReadButton = itemView.findViewById(R.id.button_mark_as_read);
            markAsUnreadButton = itemView.findViewById(R.id.button_mark_as_unread);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the custom layout for each list item
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
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

        // Show the correct button based on the read status
        if (notification.isRead()) {
            holder.markAsReadButton.setVisibility(View.GONE);
            holder.markAsUnreadButton.setVisibility(View.VISIBLE);
        } else {
            holder.markAsReadButton.setVisibility(View.VISIBLE);
            holder.markAsUnreadButton.setVisibility(View.GONE);
        }

        // Mark as Read button
        holder.markAsReadButton.setOnClickListener(v -> {
            notification.setRead(true);
            notifyItemChanged(position); // Update the current item
        });

        // Mark as Unread button
        holder.markAsUnreadButton.setOnClickListener(v -> {
            notification.setRead(false);
            notifyItemChanged(position); // Update the current item
        });

        // Set up the click listener on the item view
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {

                // Mark the message as read when clicked
//                notification.setRead(true);
//                notifyItemChanged(position); // Update UI to reflect the change
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
