package com.example.smartstorageorganizer;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class ChatScreenActivity extends AppCompatActivity {

    private EditText messageEditText;
    private ImageButton sendButton;
    private ImageButton broadcastButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);

        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        broadcastButton = findViewById(R.id.broadcastButton);

        sendButton.setOnClickListener(v -> onMessageSend());
        broadcastButton.setOnClickListener(v -> onMessageBroadcast());
    }

    private void onMessageSend() {
        String message = messageEditText.getText().toString();
        // Handle message sending logic here
    }

    private void onMessageBroadcast() {
        String message = messageEditText.getText().toString();
        // Handle message broadcasting logic here
    }
}