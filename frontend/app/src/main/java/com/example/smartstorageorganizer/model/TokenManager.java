package com.example.smartstorageorganizer.model;

import android.util.Base64;
import android.util.Log;

import com.example.smartstorageorganizer.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.CompletableFuture;

public class TokenManager {
    private static String token;

    public static CompletableFuture<String> getToken() {
        CompletableFuture<String> future=new CompletableFuture<>();
        if(token == null) {
            Utils.getUserToken().thenAccept(token->{
                setToken(token);
                future.complete(token);
            }).exceptionally(e->{
                e.printStackTrace();
               Log.i("Token","Failed to get the token");
                future.completeExceptionally(e);
                return null;
            });

        }
        else{
            if(isTokenValid(token))
                future.complete(token);
            else
                Utils.getUserToken().thenAccept(token->{
                    setToken(token);
                    future.complete(token);
                }).exceptionally(e->{
                    e.printStackTrace();
                    Log.i("Token","Failed to get the token");
                    future.completeExceptionally(e);
                    return null;
                });
        }
        return future;
    }

    public static void setToken(String token) {
        TokenManager.token = token;
    }

    public static boolean isTokenValid(String token) {
        try {
            // Split the token into header, payload, and signature
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return false;
            }

            // Decode the payload
            String payload = new String(Base64.decode(parts[1], Base64.URL_SAFE));
            JSONObject payloadJson = new JSONObject(payload);

            // Get the expiration time
            long exp = payloadJson.getLong("exp");

            // Check if the token is expired
            long currentTime = System.currentTimeMillis() / 1000;
            return exp > currentTime;
        } catch (JSONException | IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }
}