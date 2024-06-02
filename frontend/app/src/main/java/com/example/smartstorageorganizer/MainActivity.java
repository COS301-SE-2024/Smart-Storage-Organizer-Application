package com.example.smartstorageorganizer;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.smartstorageorganizer.model.ItemModel;
import com.google.gson.Gson;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.amplifyframework.core.Amplify;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                    FetchByEmail("gayol59229@fincainc.com");
                isSignedIn().thenAccept(isSignedIn -> {
                    Intent intent;
                    if (isSignedIn) {
                        intent = new Intent(MainActivity.this, HomeActivity.class);

                    } else {
                        intent = new Intent(MainActivity.this, LoginActivity.class);
                    }
                    startActivity(intent);
                    finish();
                });
            }
        }, 3000);



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private CompletableFuture<Boolean> isSignedIn(){
        CompletableFuture<Boolean> future=new CompletableFuture<>();

        Amplify.Auth.fetchAuthSession(

                result->{
                    if(result.isSignedIn()){
                        Log.i("AmplifyQuickstart", "User is signed in");
                        future.complete(true);
                    }
                    else {
                        Log.i("AmplifyQuickstart", "User is not signed in");
                        future.complete(false);
                    }},
                error -> {
                    Log.e("AmplifyQuickstart", error.toString());
                    future.completeExceptionally(error);
                }

        );
        return future;
    }

    public void FetchByEmail(String email)
     {
        String json = "{\"email\":\""+email+"\" }";

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = "https://m1bavqqu90.execute-api.eu-north-1.amazonaws.com/deployment/ssrest/SearchByEmail";
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Log.e("Request Method", "GET request failed", e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    runOnUiThread(() -> {
                        String json1 = "{\"item_id\": 24, \"item_name\": \"samsung\", \"description\": \"A75025\", \"colourcoding\": \"blue\", \"barcode\": \"[plkjihgv]\", \"qrcode\": \"opijyut\", \"quanity\": 50, \"location\": \"booth\", \"email\": \"ezetest@gmail.com\"}";
                        Log.i("Request Method", "GET request succeeded: " + responseData);
                        Gson gson = new Gson();
                    ItemModel item = gson.fromJson(json1, ItemModel.class);
//                    int itemId = item.getItemId();

                    String itemName = item.getItem_name();
                    Log.i("itemName", itemName);

                        Gson gson1 = new Gson();
                        Log.i("countingg1", "Integer.toString(itemList.size())");
                        // Define the type of the collection
                        Type collectionType = new TypeToken<List<ItemModel>>(){}.getType();
                        Log.i("countingg2", "Integer.toString(itemList.size())");
                        // Convert JSON string to List<Item>
                        List<ItemModel> itemList = gson1.fromJson(responseData, collectionType);



//                    String description = item.getDescription();
//                    Log.i("description", description);
                    });
//                    runOnUiThread(() -> Log.i("Request Method", "GET request succeeded: " + responseData));
//                    String json = "{\"item_id\": 24, \"item_name\": \"samsung\", \"description\": \"A75025\", \"colourcoding\": \"blue\", \"barcode\": \"[plkjihgv]\", \"qrcode\": \"opijyut\", \"quanity\": 50, \"location\": \"booth\", \"email\": \"ezetest@gmail.com\"}";
//                    Log.i("itemName", "itemName");
//                    Gson gson = new Gson();
//                    ItemModel item = gson.fromJson(json, ItemModel.class);
//                    int itemId = item.getItemId();
//
//                    String itemName = item.getItemName();
//                    Log.i("itemName", itemName);
//                    String description = item.getDescription();
//                    Log.i("description", description);
// Access other properties as needed...

                } else {
                    runOnUiThread(() -> Log.e("Request Method", "GET request failed: " + response));
                }
            }
        });
    }
}