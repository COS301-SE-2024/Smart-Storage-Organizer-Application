package com.example.smartstorageorganizer.adapters;

import android.app.Notification;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.model.AppNotification;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private final List<AppNotification> notifications;
    private Context context;


    public NotificationAdapter(List<AppNotification> notifications) {
        this.notifications = notifications;
    }




    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        AppNotification notification = notifications.get(position);
        holder.contentTextView.setText(notification.getContent());

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
        String formattedDate = sdf.format(notification.getDate());
        holder.dateTextView.setText(formattedDate);

        // Set the notification as read/unread based on its state
        holder.itemView.setAlpha(notification.isRead() ? 0.5f : 1.0f);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView contentTextView;
        TextView dateTextView;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            contentTextView = itemView.findViewById(R.id.textView_message);
            dateTextView = itemView.findViewById(R.id.textView_date);
        }
    }
}
