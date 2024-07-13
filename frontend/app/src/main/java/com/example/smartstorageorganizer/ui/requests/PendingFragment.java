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
import com.example.smartstorageorganizer.databinding.FragmentPendingBinding;
import com.example.smartstorageorganizer.model.RequestModel;

import java.util.ArrayList;
import java.util.List;

public class PendingFragment extends Fragment {
    View root;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        root =  inflater.inflate(R.layout.fragment_pending, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        List<RequestModel> cardItemList = new ArrayList<>();
        cardItemList.add(new RequestModel("12/07/2024", "Discontinued Product", "Request to delete the discontinued product \"Vintage Lamps\" from the inventory.", "Pending"));
        cardItemList.add(new RequestModel("12/07/2024", "Edit Product Name", "Request to change the product name from \"Red Apples\" to \"Crimson Apples.\"", "Pending"));
        cardItemList.add(new RequestModel("12/07/2024", "Update Product Description", "Request to update the description of \"Handcrafted Wooden Table\" to include new dimensions and materials.", "Pending"));
        cardItemList.add(new RequestModel("12/07/2024", "Change Product Category", "Request to move \"Ceramic Vases\" from the \"Home Decor\" category to the \"Gifts\" category.", "Pending"));
        cardItemList.add(new RequestModel("12/07/2024", "Change Color Code", "Request to update the color code of \"Blue Paint\" to #1E90FF.", "Pending"));
        cardItemList.add(new RequestModel("12/07/2024", "Remove Outdated Item", "Request to remove the outdated item \"2019 Calendars\" from the inventory.", "Pending"));
        cardItemList.add(new RequestModel("12/07/2024", "Request Title 7", "Request Description 7", "Pending"));
        cardItemList.add(new RequestModel("12/07/2024", "Request Title 8", "Request Description 8", "Pending"));

        RequestCardAdapter requestAdapter = new RequestCardAdapter(getContext(), cardItemList);
        recyclerView.setAdapter(requestAdapter);

        return root;
    }
}
