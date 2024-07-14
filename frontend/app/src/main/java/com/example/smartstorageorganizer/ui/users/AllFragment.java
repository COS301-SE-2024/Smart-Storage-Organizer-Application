package com.example.smartstorageorganizer.ui.users;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.adapters.RequestCardAdapter;
import com.example.smartstorageorganizer.adapters.UsersAdapter;
import com.example.smartstorageorganizer.model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class AllFragment extends Fragment {

    View root;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_all, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        List<UserModel> cardItemList = new ArrayList<>();
        cardItemList.add(new UserModel("12/07/2024", "ezekiel@gmail.com", "Active"));
        cardItemList.add(new UserModel("12/07/2024", "victor@gmail.com", "Active"));
        cardItemList.add(new UserModel("12/07/2024", "tshegofatso@gmail.com", "Active"));
        cardItemList.add(new UserModel("12/07/2024", "siyabonga@gmail.com", "Active"));
        cardItemList.add(new UserModel("12/07/2024", "mosa@gmail.com", "Active"));

        UsersAdapter requestAdapter = new UsersAdapter(getContext(), cardItemList);
        recyclerView.setAdapter(requestAdapter);

        return root;
    }
}