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
import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.adapters.RequestCardAdapter;
import com.example.smartstorageorganizer.adapters.UsersAdapter;
import com.example.smartstorageorganizer.model.RequestModel;
import com.example.smartstorageorganizer.model.UserModel;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.UserUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RequestFragment extends Fragment {

    View root;
    List<UserModel> cardItemList;
    UsersAdapter requestAdapter;
    String currentEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        root =  inflater.inflate(R.layout.fragment_request, container, false);

        getDetails().thenAccept(getDetails-> Log.i("AuthDemo", "User is signed in"));

        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        cardItemList = new ArrayList<>();

        requestAdapter = new UsersAdapter(getContext(), cardItemList);
        recyclerView.setAdapter(requestAdapter);

        return root;
    }

    private CompletableFuture<Boolean> getDetails()
    {
        CompletableFuture<Boolean> future=new CompletableFuture<>();

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
                    Log.i("progress","User attributes fetched successfully");

                    future.complete(true);
                },
                error -> Log.e("AuthDemo", "Failed to fetch user attributes.", error)

        );
        return future;
    }

    private void getUsersInGroup(String username, String type, String authorizationToken) {
//        String email = getIntent().getStringExtra(EMAIL_KEY);
        UserUtils.getUsersInGroup(username, type, authorizationToken, requireActivity(), new OperationCallback<List<UserModel>>() {
            @Override
            public void onSuccess(List<UserModel> result) {
                cardItemList.addAll(result);
                requestAdapter.notifyDataSetChanged();

                Toast.makeText(requireActivity(), "user groups fetched successful: ", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(requireActivity(), "user groups fetched failed", Toast.LENGTH_LONG).show();
            }
        });
    }
}