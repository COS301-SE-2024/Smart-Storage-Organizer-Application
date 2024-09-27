package com.example.smartstorageorganizer.ui.Units;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.smartstorageorganizer.MyAmplifyApp;
import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.UnitActivity;
import com.example.smartstorageorganizer.ViewUnitsActivity;
import com.example.smartstorageorganizer.adapters.UnitsAdapter;
import com.example.smartstorageorganizer.model.unitModel;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.Utils;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

public class UnitsFragment extends Fragment {

    View root;
    private RecyclerView recyclerViewUnits;
    private UnitsAdapter unitsAdapter;
    private List<unitModel> unitList;
    MyAmplifyApp app;
    private ShimmerFrameLayout skeletonLoader;
    private LottieAnimationView addButton;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root =  inflater.inflate(R.layout.fragment_units, container, false);

        skeletonLoader = root.findViewById(R.id.skeletonLoader);
        addButton = root.findViewById(R.id.addButton);

        app = (MyAmplifyApp) requireActivity().getApplicationContext();

        recyclerViewUnits = root.findViewById(R.id.recyclerViewUnits);
        recyclerViewUnits.setLayoutManager(new LinearLayoutManager(requireActivity()));

        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh_layout);

        unitList = new ArrayList<>();

        unitsAdapter = new UnitsAdapter(requireContext(), unitList);
        recyclerViewUnits.setAdapter(unitsAdapter);

        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), UnitActivity.class);
            startActivity(intent);
        });

        loadUnits("");

        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Refresh the data when user swipes down
            loadUnits("");
        });

        return root;
    }

    private void loadUnits(String authorizationToken) {
        skeletonLoader.startShimmer();
        skeletonLoader.setVisibility(View.VISIBLE);
        recyclerViewUnits.setVisibility(View.GONE);

        Utils.FetchAllUnits(app.getOrganizationID(), requireActivity(), new OperationCallback<List<unitModel>>() {
            @Override
            public void onSuccess(List<unitModel> result) {
                skeletonLoader.setVisibility(View.GONE);
                unitList.clear();
                unitList.addAll(result);
                unitsAdapter.notifyDataSetChanged();

                if(result.isEmpty()){
                    addButton.setVisibility(View.VISIBLE);
                    skeletonLoader.setVisibility(View.GONE);
                    recyclerViewUnits.setVisibility(View.GONE);
                }
                else {
                    addButton.setVisibility(View.GONE);
                    skeletonLoader.setVisibility(View.GONE);
                    recyclerViewUnits.setVisibility(View.VISIBLE);
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(String error) {
                swipeRefreshLayout.setRefreshing(false);
                skeletonLoader.setVisibility(View.GONE);
                Toast.makeText(requireActivity(), "Failed to fetch units: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
