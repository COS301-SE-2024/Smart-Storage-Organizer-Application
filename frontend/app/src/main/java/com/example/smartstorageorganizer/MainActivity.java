package com.example.smartstorageorganizer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.amplifyframework.core.Amplify;

import java.util.concurrent.CompletableFuture;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "AmplifyQuickstart";
    private static final int SPLASH_DISPLAY_LENGTH = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        setupWindowInsets();
        navigateAfterDelay();
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void navigateAfterDelay() {
        new Handler().postDelayed(() -> isSignedIn().thenAccept(isSignedIn -> {
            Intent intent;
            if (Boolean.TRUE.equals(isSignedIn)) {
                intent = new Intent(MainActivity.this, HomeActivity.class);
            } else {
                intent = new Intent(MainActivity.this, LoginActivity.class);
            }
            startActivity(intent);
            finish();
        }), SPLASH_DISPLAY_LENGTH);
    }

    CompletableFuture<Boolean> isSignedIn() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Amplify.Auth.fetchAuthSession(
                result -> {
                    if (result.isSignedIn()) {
                        Log.i(TAG, "User is signed in");
                        future.complete(true);
                    } else {
                        Log.i(TAG, "User is not signed in");
                        future.complete(false);
                    }
                },
                error -> {
                    Log.e(TAG, error.toString());
                    future.completeExceptionally(error);
                }
        );
        return future;
    }
}
