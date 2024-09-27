package com.example.smartstorageorganizer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.model.NotificationModel;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {
    private List<NotificationModel> notificationsList;
    private Context context;

    // Constructor to initialize context and list
    public NotificationsAdapter(Context context, List<NotificationModel> notificationsList) {
        this.context = context;
        this.notificationsList = notificationsList;
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
    }

    @Override
    public int getItemCount() {
        return notificationsList.size();
    }
}
