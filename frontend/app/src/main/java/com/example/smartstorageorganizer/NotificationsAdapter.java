package com.example.smartstorageorganizer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {
    private List<NotificationModel> notificationsList;
    private Context context;
    private MyAmplifyApp app;

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
        app = (MyAmplifyApp) context.getApplicationContext();
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView messageTextView;
        public TextView dateTextView;
        public TextView markAsReadButton;
        public TextView markAsUnreadButton;
        public LinearLayout notificationLayout;
        public LottieAnimationView lottieAnimationView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textView_title);
            messageTextView = itemView.findViewById(R.id.textView_message);
            dateTextView = itemView.findViewById(R.id.textView_date);
            markAsReadButton = itemView.findViewById(R.id.button_mark_as_read);
            markAsUnreadButton = itemView.findViewById(R.id.button_mark_as_unread);
            notificationLayout = itemView.findViewById(R.id.notificationLayout);
            lottieAnimationView = itemView.findViewById(R.id.unread);

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
            holder.lottieAnimationView.setVisibility(View.GONE);
//            holder.notificationLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.background));
            holder.markAsReadButton.setVisibility(View.GONE);
            holder.markAsUnreadButton.setVisibility(View.VISIBLE);
        } else {
            holder.lottieAnimationView.setVisibility(View.VISIBLE);
//            holder.notificationLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.card));
            holder.markAsReadButton.setVisibility(View.VISIBLE);
            holder.markAsUnreadButton.setVisibility(View.GONE);
        }

        // Mark as Read button
        holder.markAsReadButton.setOnClickListener(v -> {
            notification.setRead(true);
            SharedPreferences sharedPreferences = context.getSharedPreferences("Notifications", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String key =  app.getEmail() + "_" + notification.getDate(); // Use username + notification ID as key
            editor.putBoolean(key, true); // Mark as read for the specific user
            editor.apply();
            notifyItemChanged(position); // Update the current item
        });

        // Mark as Unread button
        holder.markAsUnreadButton.setOnClickListener(v -> {
            notification.setRead(false);
            SharedPreferences sharedPreferences = context.getSharedPreferences("Notifications", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String key =  app.getEmail() + "_" + notification.getDate(); // Use username + notification ID as key
            editor.putBoolean(key, false); // Mark as read for the specific user
            editor.apply();
            notifyItemChanged(position); // Update the current item
        });

        // Set up the click listener on the item view
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                notification.setRead(true);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationsList.size();
    }



}
