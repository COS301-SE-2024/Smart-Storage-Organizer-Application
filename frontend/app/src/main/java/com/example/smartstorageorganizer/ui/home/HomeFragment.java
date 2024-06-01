package com.example.smartstorageorganizer.ui.home;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstorageorganizer.Adapters.ItemAdapter;
import com.example.smartstorageorganizer.EditProfileActivity;
import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.databinding.FragmentHomeBinding;
import com.example.smartstorageorganizer.model.ItemModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private List<ItemModel> itemModelList;
    private ItemAdapter itemAdapter;
    private RecyclerView itemRecyclerView;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        FloatingActionButton addItemButton = root.findViewById(R.id.addItemButton);
        itemRecyclerView = root.findViewById(R.id.item_rec);

        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddItemPopup();
            }
        });

        itemRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        itemModelList = new ArrayList<>();
        //flash sale

        itemAdapter = new ItemAdapter(requireActivity(), itemModelList);
        itemRecyclerView.setAdapter(itemAdapter);

//        db.collection("FlashSaleProducts")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                FlashSaleModel popularModel = document.toObject(FlashSaleModel.class);
//                                flashSaleModelListModelList.add(popularModel);
//                                flashSaleAdapter.notifyDataSetChanged();
//                            }
//                        } else {
//                            Toast.makeText(HomeActivity.this, "Error"+task.getException(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });

        return root;
    }

    private void showAddItemPopup() {
        // Create an AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Inflate the dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_item_popup, null);
        builder.setView(dialogView);

        // Get the EditTexts and Button from the dialog layout
        EditText itemName = dialogView.findViewById(R.id.item_name);
        EditText itemDescription = dialogView.findViewById(R.id.item_description);
        Button buttonNext = dialogView.findViewById(R.id.button_add_item);

        // Create the AlertDialog
        AlertDialog alertDialog = builder.create();

        // Set the button click listener
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = itemName.getText().toString().trim();
                String description = itemDescription.getText().toString().trim();
                postAddItem(name, description, "Yellow", "asdffd",  "00111100", "1", "Centinary", "gayol59229@fincainc.com");
                alertDialog.dismiss();
            }
        });

        // Show the AlertDialog
        alertDialog.show();
    }

    private void postAddItem(String item_name, String description, String colourcoding, String barcode, String qrcode, String quantity, String location, String email )  {
        String json = "{\"item_name\":\""+item_name+"\",\"description\":\""+description+"\" ,\"colourcoding\":\""+colourcoding+"\",\"barcode\":\""+barcode+"\",\"qrcode\":\""+qrcode+"\",\"quanity\":"+quantity+",\"location\":\""+location+"\",\"email\":\""+email+"\" }";
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = "https://m1bavqqu90.execute-api.eu-north-1.amazonaws.com/deployment/ssrest/AddItem";
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> Log.e("Request Method", "POST request failed", e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    requireActivity().runOnUiThread(() -> Log.i("Request Method", "POST request succeeded: " + responseData));
                } else {
                    requireActivity().runOnUiThread(() -> Log.e("Request Method", "POST request failed: " + response.code()));
                }
            }
        });
    }

//    private void PostEditItem(String item_name, String description, String colourcoding, String barcode, String qrcode, String quanity, String location, String item_id ) {
//        String json = "{\"item_name\":\""+item_name+"\",\"description\":\""+description+"\" ,\"colourcoding\":\""+colourcoding+"\",\"barcode\":\""+barcode+"\",\"qrcode\":\""+qrcode+"\",\"quanity\":"+quanity+",\"location\":\""+location+"\", \"item_id\":\""+item_id+"\" }";
//        MediaType JSON = MediaType.get("application/json; charset=utf-8");
//        OkHttpClient client = new OkHttpClient();
//        String API_URL = "https://m1bavqqu90.execute-api.eu-north-1.amazonaws.com/deployment/ssrest/EditItem";
//        RequestBody body = RequestBody.create(json, JSON);
//
//        Request request = new Request.Builder()
//                .url(API_URL)
//                .post(body)
//                .build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//                requireActivity().runOnUiThread(() -> Log.e("Request Method", "POST request failed", e));
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    final String responseData = response.body().string();
//                    requireActivity().runOnUiThread(() -> Log.i("Request Method", "POST request succeeded: " + responseData));
//                } else {
//                    requireActivity().runOnUiThread(() -> Log.e("Request Method", "POST request failed: " + response.code()));
//                }
//            }
//        });
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}