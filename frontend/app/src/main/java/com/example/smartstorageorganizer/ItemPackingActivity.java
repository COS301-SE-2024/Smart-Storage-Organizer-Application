package com.example.smartstorageorganizer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class ItemPackingActivity extends AppCompatActivity {

    private EditText widthInput, heightInput, depthInput, weightInput;
    private TextView packedItemsOutput;
    private List<Item3D> items;
    private BoxPacking3D boxPacking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packed_item);

        widthInput = findViewById(R.id.itemWidthInput);
        heightInput = findViewById(R.id.itemHeightInput);
        depthInput = findViewById(R.id.itemDepthInput);
        weightInput = findViewById(R.id.itemWeightInput);
        packedItemsOutput = findViewById(R.id.packedItemsOutput);

        items = new ArrayList<>();
        boxPacking = new BoxPacking3D(20, 20, 20);  // Example box size

        Button packItemsButton = findViewById(R.id.packItemsButton);
        packItemsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemAndPack();
            }
        });
    }

    private void addItemAndPack() {
        int width = Integer.parseInt(widthInput.getText().toString());
        int height = Integer.parseInt(heightInput.getText().toString());
        int depth = Integer.parseInt(depthInput.getText().toString());
        int weight = Integer.parseInt(weightInput.getText().toString());

        items.add(new Item3D(width, height, depth, weight));
        boxPacking.packItems(items);

        StringBuilder result = new StringBuilder();
        for (Item3D item : boxPacking.getBox().getItems()) {
            result.append("Item Volume: ").append(item.getWidth()).append("x")
                    .append(item.getHeight()).append("x")
                    .append(item.getDepth()).append(" Weight: ")
                    .append(item.getWeight()).append("\n");
        }
        packedItemsOutput.setText(result.toString());
    }
}
