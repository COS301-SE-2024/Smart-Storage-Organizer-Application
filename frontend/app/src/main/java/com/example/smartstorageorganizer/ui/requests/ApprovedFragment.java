package com.example.smartstorageorganizer.ui.requests;

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
import com.example.smartstorageorganizer.model.RequestModel;

import java.util.ArrayList;
import java.util.List;

public class ApprovedFragment extends Fragment {

    View root;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root =  inflater.inflate(R.layout.fragment_approved, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        List<RequestModel> cardItemList = new ArrayList<>();
        cardItemList.add(new RequestModel("12/07/2024", "Asus Vivobook", "Ryzen 5 5500U Processor 8GB GDDR 6 RAM.", "Approved"));
        cardItemList.add(new RequestModel("12/07/2024", "Asus Vivobook", "Ryzen 5 5500U Processor 8GB GDDR 6 RAM.", "Approved"));
        cardItemList.add(new RequestModel("12/07/2024", "Asus Vivobook", "Ryzen 5 5500U Processor 8GB GDDR 6 RAM.", "Approved"));
        cardItemList.add(new RequestModel("12/07/2024", "Asus Vivobook", "Ryzen 5 5500U Processor 8GB GDDR 6 RAM.", "Approved"));
        cardItemList.add(new RequestModel("12/07/2024", "Asus Vivobook", "Ryzen 5 5500U Processor 8GB GDDR 6 RAM.", "Approved"));

        RequestCardAdapter requestAdapter = new RequestCardAdapter(getContext(), cardItemList);
        recyclerView.setAdapter(requestAdapter);

        return root;
    }
}
