package com.example.smartstorageorganizer.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstorageorganizer.EmailVerificationActivity;
import com.example.smartstorageorganizer.LoginActivity;
import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.model.UserModel;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.UserUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;
import java.util.Objects;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.CardViewHolder> {
    private Context context;
    private List<UserModel> usersList;

    public UsersAdapter(Context context, List<UserModel> usersList) {
        this.context = context;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_layout, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        UserModel cardItem = usersList.get(position);
        holder.name.setText(cardItem.getEmail() + " (" + cardItem.getName() + ")");
        if (Objects.equals(cardItem.getStatus(), "Active")) {
            holder.moreButton.setVisibility(View.VISIBLE);
            holder.approveButton.setVisibility(View.GONE);
            holder.declineButton.setText(cardItem.getStatus());
            int color = Color.parseColor("#00DC32");
            holder.declineButton.setTextColor(color);
            color = Color.parseColor("#D3F8D3");
            holder.declineButton.setBackgroundColor(color);
        }

        holder.itemView.setOnClickListener(v -> {
            // Handle item click if needed
        });

        holder.approveButton.setOnClickListener(v -> {
            ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Approving user...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            setUserRole(cardItem.getEmail(), "guestUser", "", holder.getAdapterPosition(), progressDialog);
        });

        holder.moreButton.setOnClickListener(v -> showBottomSheetDialog(cardItem));
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView date, name, description, status, approveButton, declineButton;
        ImageView moreButton;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            name = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);
            status = itemView.findViewById(R.id.status);
            approveButton = itemView.findViewById(R.id.approveButton);
            declineButton = itemView.findViewById(R.id.declineButton);
            moreButton = itemView.findViewById(R.id.moreButton);
        }
    }

    private void showBottomSheetDialog(UserModel cardItem) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_dialog_manager, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        TextView actionEdit = bottomSheetView.findViewById(R.id.action_edit);
        TextView actionDelete = bottomSheetView.findViewById(R.id.action_delete);
        Spinner roleSpinner = bottomSheetView.findViewById(R.id.role_spinner);
        Button confirmButton = bottomSheetView.findViewById(R.id.confirm_button);

        actionEdit.setOnClickListener(v -> {
            // Show the spinner and confirm button
            roleSpinner.setVisibility(View.VISIBLE);
            confirmButton.setVisibility(View.VISIBLE);
            actionEdit.setVisibility(View.GONE);
            actionDelete.setVisibility(View.GONE);
        });

        actionDelete.setOnClickListener(v -> {
            // Handle delete action
            Toast.makeText(context, "Delete clicked for " + cardItem.getName(), Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
        });

        confirmButton.setOnClickListener(v -> {
            // Get the selected role from the spinner
            String selectedRole = roleSpinner.getSelectedItem().toString();
            // Call setUserRole with the selected role
            ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Setting role...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            setUserRole(cardItem.getEmail(), selectedRole, "", -1, progressDialog);
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    private void setUserToVerified(String username, String authorization, int position, ProgressDialog progressDialog) {
        UserUtils.setUserToVerified(username, authorization, (Activity) context, new OperationCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                progressDialog.dismiss();
                if (Boolean.TRUE.equals(result) && position != -1){
                        ((Activity) context).runOnUiThread(() -> {
                            usersList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, usersList.size());
                        });
                    }
                Toast.makeText(context, "User approved successfully", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(String error) {
                progressDialog.dismiss();
                Toast.makeText(context, "User approval failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setUserToUnverified(String username, String authorization) {
        UserUtils.setUserToUnverified(username, authorization, (Activity) context, new OperationCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (Boolean.TRUE.equals(result)) {
                    Toast.makeText(context, "user unverified successful: ", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(String error) {
//                hideLoading();
                Toast.makeText(context, "user verification failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setUserRole(String username, String role, String authorization, int position, ProgressDialog progressDialog) {
        UserUtils.setUserRole(username, role, authorization, (Activity) context, new OperationCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                progressDialog.dismiss();
                if (Boolean.TRUE.equals(result)) {
                    ((Activity) context).runOnUiThread(() -> {
                        setUserToVerified(username, "", position, progressDialog);
                        Toast.makeText(context, "User role set successfully", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onFailure(String error) {
                progressDialog.dismiss();
                Toast.makeText(context, "Setting user role failed", Toast.LENGTH_LONG).show();
            }
        });
    }
}