package com.example.smartstorageorganizer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.cognito.result.AWSCognitoAuthSignOutResult;
import com.amplifyframework.core.Amplify;
import com.bumptech.glide.Glide;
import com.example.smartstorageorganizer.model.ItemModel;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartstorageorganizer.databinding.ActivityHomeBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {
    public TextView fullName;
    public ShapeableImageView profileImage;
    public AppBarConfiguration mAppBarConfiguration;
    public ActivityHomeBinding binding;
    public String currentName, currentSurname, currentPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarHome.toolbar);

        getDetails().thenAccept(getDetails->{
            Log.i("AuthDemo", "User is signed in");
        });

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        ConstraintLayout logout = binding.logoutButton;
        LottieAnimationView buttonLoader = binding.buttonLoader;
        TextView logoutButtonText = binding.logOutButtonText;
        ImageView logoutButtonIcon = binding.logoutButtonIcon;

        View header = navigationView.getHeaderView(0);
        fullName = (TextView) header.findViewById(R.id.fullName);
        profileImage = header.findViewById(R.id.profileImage);
//        fullName.setText("Ezekiel Makau");

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonLoader.setVisibility(View.VISIBLE);
                buttonLoader.playAnimation();
                logoutButtonText.setVisibility(View.GONE);
                logoutButtonIcon.setVisibility(View.GONE);
                SignOut();
//                Snackbar.make(v, "Logging out", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null)
//                        .setAnchorView(R.id.fab).show();
            }
        });

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile_management, R.id.nav_notifications, R.id.nav_help)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public CompletableFuture<Boolean> getDetails() {
        CompletableFuture<Boolean> future=new CompletableFuture<>();

        Amplify.Auth.fetchUserAttributes(
                attributes -> {

                    for (AuthUserAttribute attribute : attributes) {
                        switch (attribute.getKey().getKeyString()) {
                            case "name":
                                currentName = attribute.getValue();
                                break;
                            case "family_name":
                                currentSurname = attribute.getValue();
                                break;
                            case "picture":
                                currentPicture = attribute.getValue();
                                break;
//                            case "address":
//                                currentAddress = attribute.getValue();
//                                break;
//                            case "custom:myCustomAttribute":
//                                customAttribute = attribute.getValue();
//                                break;
                        }




                    }
                    Log.i("progress","User attributes fetched successfully");
                    runOnUiThread(() -> {
                        Glide.with(this).load(currentPicture).placeholder(R.drawable.no_profile_image).error(R.drawable.no_profile_image).into(profileImage);
                        String username = currentName+" "+currentSurname;
                        fullName.setText(username);
                    });
                    future.complete(true);
                },
                error -> {Log.e("AuthDemo", "Failed to fetch user attributes.", error);  future.complete(false); }

        );
        return future;
    }

    public CompletableFuture<Boolean> SignOut()
    {
        CompletableFuture<Boolean> future=new CompletableFuture<>();
        Amplify.Auth.signOut(signOutResult -> {
            if (signOutResult instanceof AWSCognitoAuthSignOutResult.CompleteSignOut) {
                // Sign Out completed fully and without errors.
                Log.i("AuthQuickStart", "Signed out successfully");
                //move to a different page
                future.complete(true);
                runOnUiThread(() -> {
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                });
            } else if (signOutResult instanceof AWSCognitoAuthSignOutResult.PartialSignOut) {
                // Sign Out completed with some errors. User is signed out of the device.
                AWSCognitoAuthSignOutResult.PartialSignOut partialSignOutResult =
                        (AWSCognitoAuthSignOutResult.PartialSignOut) signOutResult;
                //move to the different page
                future.complete(true);
                runOnUiThread(() -> {
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                });

            } else if (signOutResult instanceof AWSCognitoAuthSignOutResult.FailedSignOut) {
                AWSCognitoAuthSignOutResult.FailedSignOut failedSignOutResult =
                        (AWSCognitoAuthSignOutResult.FailedSignOut) signOutResult;
                // Sign Out failed with an exception, leaving the user signed in.
                Log.e("AuthQuickStart", "Sign out Failed", failedSignOutResult.getException());
                //dont move to different page
                future.complete(false);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Sign Out Failed.", Toast.LENGTH_LONG).show();

                });
            }
        });
        return future;
    }

    public void DeleteCategory(int id, String email)
    {
        String json = "{\"useremail\":\""+email+"\", \"id\":\""+Integer.toString(id)+"\" }";


        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.DeleteCategoryEndPoint;
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();
    }
    public void CategoryToUncategorized(int id)
    {
        String json = "{\"parentcategoryid\":\""+Integer.toString(id)+"\" }";

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.CategoryToUncategorized;
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();
    }
    public void SubcategoryToUncategorized(int id)
    {
        String json = "{\"subcategoryid\":\""+Integer.toString(id)+"\" }";

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.SubcategoryToUncategorized;
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();
    }







}