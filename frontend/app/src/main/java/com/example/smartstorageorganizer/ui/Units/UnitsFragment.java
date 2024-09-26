package com.example.smartstorageorganizer.ui.Units;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.smartstorageorganizer.MyAmplifyApp;
import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.ViewUnitsActivity;
import com.example.smartstorageorganizer.adapters.UnitsAdapter;
import com.example.smartstorageorganizer.model.unitModel;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class UnitsFragment extends Fragment {

    View root;
    private RecyclerView recyclerViewUnits;
    private UnitsAdapter unitsAdapter;
    private List<unitModel> unitList;
    MyAmplifyApp app;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root =  inflater.inflate(R.layout.fragment_units, container, false);

        app = (MyAmplifyApp) requireActivity().getApplicationContext();

        recyclerViewUnits = root.findViewById(R.id.recyclerViewUnits);
        recyclerViewUnits.setLayoutManager(new LinearLayoutManager(requireActivity()));

        unitList = new ArrayList<>();

        unitsAdapter = new UnitsAdapter(requireContext(), unitList);
        recyclerViewUnits.setAdapter(unitsAdapter);

        loadUnits("");

        return root;
    }

    private void loadUnits(String authorizationToken) {
//        shimmerFrameLayout.startShimmer();
//        shimmerFrameLayout.setVisibility(View.VISIBLE);
//        itemsLayout.setVisibility(View.GONE);
//        sortBySpinner.setVisibility(View.GONE);

        Utils.FetchAllUnits(app.getOrganizationID(), requireActivity(), new OperationCallback<List<unitModel>>() {
            @Override
            public void onSuccess(List<unitModel> result) {
                unitList.clear();
                unitList.addAll(result);
                unitsAdapter.notifyDataSetChanged();
//                if (!Objects.equals(currentSelectedOption, "Sort by")) {
//                    setupSort(currentSelectedOption);
//                } else {
//                    recentAdapter.notifyDataSetChanged();
//                }
//                notFoundText.setVisibility(result.isEmpty() ? View.VISIBLE : View.GONE);
//                Toast.makeText(UncategorizedItemsActivity.this, "Items fetched successfully", Toast.LENGTH_SHORT).show();
//                updatePaginationButtons(result.size());
//                shimmerFrameLayout.stopShimmer();
//                shimmerFrameLayout.setVisibility(View.GONE);
//                itemsLayout.setVisibility(View.VISIBLE);
//                sortBySpinner.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(requireActivity(), "Failed to fetch units: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCategoriesOfUnits(String unit_id) {
        Utils.FetchCategoriesOfUnits(unit_id, requireActivity(), new OperationCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> result) {
//                unitList.clear();
//                unitList.addAll(result);
//                unitsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(requireActivity(), "Failed to fetch units: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
