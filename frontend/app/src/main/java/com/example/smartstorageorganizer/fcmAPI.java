package com.example.smartstorageorganizer;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface fcmAPI {
    @POST("/send")
    Call<Void> sendMessage(@Body SendNotification body);

    @POST("/broadcast")
    Call<Void> broadcast(@Body SendNotification body);
}