package com.example.smartstorageorganizer.ui.users;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.core.Amplify;
import com.example.smartstorageorganizer.MyAmplifyApp;
import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.adapters.UsersAdapter;
import com.example.smartstorageorganizer.model.UserModel;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.UserUtils;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RequestFragment extends Fragment {
    View root;
    List<UserModel> cardItemList;
    UsersAdapter requestAdapter;
    String currentEmail;
    private ShimmerFrameLayout shimmerFrameLayout;
    private RecyclerView recyclerView;
    private MyAmplifyApp app;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_request, container, false);

        app = (MyAmplifyApp) requireActivity().getApplicationContext();

        shimmerFrameLayout = root.findViewById(R.id.shimmer_view_container);
        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        cardItemList = new ArrayList<>();
        requestAdapter = new UsersAdapter(getContext(), cardItemList);
        recyclerView.setAdapter(requestAdapter);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmer();
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        refreshData();
    }

    private void refreshData() {
        cardItemList.clear();
        requestAdapter.notifyDataSetChanged();

        getDetails().thenAccept(getDetails -> Log.i("AuthDemo", "User is signed in"));
    }

    private CompletableFuture<Boolean> getDetails() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        Amplify.Auth.fetchUserAttributes(
                attributes -> {
                    for (AuthUserAttribute attribute : attributes) {
                        switch (attribute.getKey().getKeyString()) {
                            case "email":
                                currentEmail = attribute.getValue();
                                getUsersInGroup(currentEmail, "unVerifiedUsers", "");
                                break;
                            default:
                        }
                    }
                    Log.i("progress", "User attributes fetched successfully");
                    future.complete(true);
                },
                error -> {
                    Log.e("AuthDemo", "Failed to fetch user attributes.", error);
                    future.complete(false);
                }
        );
        return future;
    }

    private void getUsersInGroup(String username, String type, String authorizationToken) {
        UserUtils.getUsersInGroup(username, type, app.getOrganizationID(), requireActivity(), new OperationCallback<List<UserModel>>() {
            @Override
            public void onSuccess(List<UserModel> result) {
                cardItemList.addAll(result);
                requestAdapter.notifyDataSetChanged();

                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                Toast.makeText(requireActivity(), "User groups fetched successfully", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(requireActivity(), "Failed to fetch user groups", Toast.LENGTH_LONG).show();
            }
        });
    }
}
