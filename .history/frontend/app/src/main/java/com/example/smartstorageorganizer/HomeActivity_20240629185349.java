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
    public final TextView fullName;
    public final ShapeableImageView profileImage;
    public final AppBarConfiguration mAppBarConfiguration;
    public final ActivityHomeBinding binding;
    public final String currentName;
    public final String currentSurname;
    public final String currentPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarHome.toolbar);

        Utils.getDetails().thenAccept(getDetails->{
         if (getDetails)
            Log.i("AuthDemo", "User is signed in");
        else
            Log.i("AuthDemo", "User not is signed in");
         } );

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        ConstraintLayout logout = binding.logoutButton;
        LottieAnimationView buttonLoader = binding.buttonLoader;
        TextView logoutButtonText = binding.logOutButtonText;
        ImageView logoutButtonIcon = binding.logoutButtonIcon;

        View header = navigationView.getHeaderView(0);
        fullName = (TextView) header.findViewById(R.id.fullName);
        profileImage = header.findViewById(R.id.profileImage);


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonLoader.setVisibility(View.VISIBLE);
                buttonLoader.playAnimation();
                logoutButtonText.setVisibility(View.GONE);
                logoutButtonIcon.setVisibility(View.GONE);
                Utils.signOut().thenAccept(result->{
                    if(result)
                        Log.i("Sign Out","User is signed out");
                    else
                        Log.i("Sign Out","User not signed out");
                });

            }
        });

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile_management, R.id.nav_notifications, R.id.nav_edit_profile)
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





}