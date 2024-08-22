package com.example.smartstorageorganizer;

import static androidx.compose.runtime.SnapshotStateKt.mutableStateOf;

import android.os.Handler;
import android.os.Looper;

import androidx.compose.runtime.MutableState;
import androidx.lifecycle.ViewModel;

import com.example.smartstorageorganizer.ui.notifications.NotificationsFragment;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class ChatViewModel extends ViewModel {

    private MutableState<ChatState> state = mutableStateOf(new ChatState(), null);
    private final fcmAPI api;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());



    public ChatViewModel(NotificationsFragment notificationsFragment) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(MoshiConverterFactory.create(new Moshi.Builder().build()))
                .build();
        api = retrofit.create(fcmAPI.class);

        // Subscribe to the "chat" topic
//        CoroutineScope scope = viewModelScope();
//        scope.launch(Dispatchers.IO, () -> {
//            try {
//                FirebaseMessaging.getInstance().subscribeToTopic("chat").wait();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
    }

    public ChatState getState() {
        return state.getValue();
    }

    public void onRemoteTokenChange(String newToken) {
        ChatState newState = new ChatState(getState());
        newState.setRemoteToken(newToken);
        state.setValue(newState);
    }

    public void onSubmitRemoteToken() {
        ChatState newState = new ChatState(getState());
        newState.setEnteringToken(false);
        state.setValue(newState);
    }

    public void onMessageChange(String message) {
        ChatState newState = new ChatState(getState());
        newState.setMessageText(message);
        state.setValue(newState);
    }

//    public void sendMessage(boolean isBroadcast) {
//        CoroutineScope scope = viewModelScope();
//        scope.launch(Dispatchers.IO, () -> {
//            SendNotification messageDto = new SendNotification(
//                    isBroadcast ? null : getState().getRemoteToken(),
//                    new NotificationBody("New message!", getState().getMessageText())
//            );
//
//            try {
//                if (isBroadcast) {
//                    api.broadcast(messageDto).execute();
//                } else {
//                    api.sendMessage(messageDto).execute();
//                }
//
//                ChatState newState = new ChatState(getState());
//                newState.setMessageText("");
//                state.setValue(newState);
//
//            } catch (HttpException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//    }
public void sendMessage(boolean isBroadcast) {
    CompletableFuture.runAsync(() -> {
        SendNotification messageDto = new SendNotification(
                isBroadcast ? null : getState().getRemoteToken(),
                new NotificationBody("New message!", getState().getMessageText())
        );

        try {
            if (isBroadcast) {
                api.broadcast(messageDto).execute();
            } else {
                api.sendMessage(messageDto).execute();
            }

            // Update the state (assuming a method to update the state)
            ChatState newState = new ChatState(getState());
            newState.setMessageText("");
            // Assuming there's a method to set the state
            state.setValue(newState);

        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }, executorService);
}
//    private CoroutineScope viewModelScope() {
//        // In Java, you can use a CoroutineScope with Dispatchers.Main or Dispatchers.IO.
//        // This simulates the ViewModel's coroutine scope.
//        return new CoroutineScope(Dispatchers.Main);
//    }

    // Method to execute background tasks
    public void executeInBackground(Runnable task) {
        executorService.execute(task);
    }

    // Method to execute tasks on the main thread
    public void executeOnMainThread(Runnable task) {
        mainThreadHandler.post(task);
    }

    // Shutdown the executor when you're done
    public void shutdown() {
        executorService.shutdown();
    }
}