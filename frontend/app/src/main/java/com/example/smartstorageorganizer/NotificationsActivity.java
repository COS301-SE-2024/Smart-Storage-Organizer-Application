package com.example.smartstorageorganizer;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class NotificationsActivity extends AppCompatActivity {
    EditText ed1, ed2, ed3;
    Button b1, bSaveDraft;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        ed1 = findViewById(R.id.editText);
        ed2 = findViewById(R.id.editText2);
        ed3 = findViewById(R.id.editText3);
        b1 = findViewById(R.id.button);
        bSaveDraft = findViewById(R.id.button_save_draft);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("NotificationsPrefs", Context.MODE_PRIVATE);

        // Load draft if it exists
        loadDraft();

        b1.setOnClickListener(v -> {
            String title = ed1.getText().toString().trim();
            String subject = ed2.getText().toString().trim();
            String body = ed3.getText().toString().trim();

            if (!title.isEmpty() && !subject.isEmpty() && !body.isEmpty()) {
                NotificationManager notif = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                Notification notify = new Notification.Builder(getApplicationContext())
                        .setContentTitle(subject)
                        .setContentText(body)
                        .setSmallIcon(R.drawable.ic_notification)
                        .build();

                notify.flags |= Notification.FLAG_AUTO_CANCEL;
                notif.notify(0, notify);

                // Save notification after it's sent
                saveNotification(title, subject, body);
                clearFields();
                Toast.makeText(NotificationsActivity.this, "Notification sent and saved!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(NotificationsActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        bSaveDraft.setOnClickListener(v -> {
            String title = ed1.getText().toString().trim();
            String subject = ed2.getText().toString().trim();
            String body = ed3.getText().toString().trim();

            if (!title.isEmpty() && !subject.isEmpty() && !body.isEmpty()) {
                saveDraft(title, subject, body);
                Toast.makeText(NotificationsActivity.this, "Draft saved!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(NotificationsActivity.this, "Please fill all fields to save as draft", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveNotification(String title, String subject, String body) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("notification_title", title);
        editor.putString("notification_subject", subject);
        editor.putString("notification_body", body);
        editor.apply();
    }

    private void saveDraft(String title, String subject, String body) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("draft_title", title);
        editor.putString("draft_subject", subject);
        editor.putString("draft_body", body);
        editor.apply();
    }

    private void loadDraft() {
        String draftTitle = sharedPreferences.getString("draft_title", "");
        String draftSubject = sharedPreferences.getString("draft_subject", "");
        String draftBody = sharedPreferences.getString("draft_body", "");

        ed1.setText(draftTitle);
        ed2.setText(draftSubject);
        ed3.setText(draftBody);
    }

    private void clearFields() {
        ed1.setText("");
        ed2.setText("");
        ed3.setText("");
    }
}
