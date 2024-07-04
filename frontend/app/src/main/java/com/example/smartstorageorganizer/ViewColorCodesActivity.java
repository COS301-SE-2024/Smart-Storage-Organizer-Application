package com.example.smartstorageorganizer;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.smartstorageorganizer.adapters.ColorCodeAdapter;
import com.example.smartstorageorganizer.adapters.ItemAdapter;
import com.example.smartstorageorganizer.model.ColorCodeModel;
import com.example.smartstorageorganizer.model.ItemModel;

import java.util.ArrayList;
import java.util.List;

public class ViewColorCodesActivity extends AppCompatActivity {
    private RecyclerView colorCodeRecyclerView;
    private List<ColorCodeModel> colorCodeModelList;
    private ColorCodeAdapter colorCodeAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_color_codes);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        colorCodeRecyclerView = findViewById(R.id.color_code_rec);

        colorCodeRecyclerView.setHasFixedSize(true);
        colorCodeRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        colorCodeModelList = new ArrayList<>();
        colorCodeAdapter = new ColorCodeAdapter(this, colorCodeModelList);
        colorCodeRecyclerView.setAdapter(colorCodeAdapter);

        setupRecyclerView();
    }

    private void setupRecyclerView() {

        ColorCodeModel itemOne = new ColorCodeModel("Low Stock", "Items that are about to run out of stock.", "#FF0000");
        ColorCodeModel itemTwo = new ColorCodeModel("In Stock", "Items that are fully stocked and available.", "#026943");
        ColorCodeModel itemThree = new ColorCodeModel("Monitor Stock", "Items that are at a medium stock level and should be monitored.", "#000000");
        ColorCodeModel itemFour = new ColorCodeModel("New Arrival", "Items that are newly added to the inventory.", "#0000FF");
        ColorCodeModel itemFive = new ColorCodeModel("On Hold", "Items that are on hold or reserved for customers.", "#00FFFF");
        ColorCodeModel itemSix = new ColorCodeModel("On Sale", "Items that are on sale or have a discount.", "#FF00FF");
        ColorCodeModel itemSeven = new ColorCodeModel("Restocking", "Items that are being restocked or on order.", "#FFA500");
        ColorCodeModel itemEight = new ColorCodeModel("Discontinued", "Items that are discontinued or no longer available.", "#800080");
        ColorCodeModel itemNine = new ColorCodeModel("Returned", "Items that are returned or being processed for return.", "#A52A2A");
        ColorCodeModel itemTen = new ColorCodeModel("Inactive", "Items that are inactive or not currently for sale.", "#808080");

        colorCodeModelList.add(itemOne);
        colorCodeModelList.add(itemTwo);
        colorCodeModelList.add(itemThree);
        colorCodeModelList.add(itemFour);
        colorCodeModelList.add(itemFive);
        colorCodeModelList.add(itemSix);
        colorCodeModelList.add(itemSeven);
        colorCodeModelList.add(itemEight);
        colorCodeModelList.add(itemNine);
        colorCodeModelList.add(itemTen);

        colorCodeAdapter.notifyDataSetChanged();
//        colorCodeAdapter.notifyAll();
    }
}