package com.example.smartstorageorganizer;

import android.os.Bundle;
import android.content.Intent;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.TextView;
import android.view.View;
import android.text.method.LinkMovementMethod;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_help);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView helpResetPasswordTextView = findViewById(R.id.help_reset_password_text_view);

        // Create a clickable span for "Reset Password"
        String text = helpResetPasswordTextView.getText().toString();
        SpannableString spannableString = new SpannableString(text);
        int start = text.indexOf("Forgot Password");
        int end = start + "Forgot Password".length();

        if (start >= 0) {
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    // Handle click event, e.g., navigate to ResetPassword activity
                    Intent intent = new Intent(HelpActivity.this, ResetPasswordActivity.class);
                    startActivity(intent);
                }
            };
            spannableString.setSpan(clickableSpan, start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        helpResetPasswordTextView.setText(spannableString);
        helpResetPasswordTextView.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());

        TextView helpAddCategoryTextView = findViewById(R.id.help_add_category_text_view);
        String message = helpAddCategoryTextView.getText().toString();
        SpannableString spannableStr = new SpannableString(message);
        int first = message.indexOf("Homepage");
        int last = start + "Homepage".length();

        if (first >= 0) {
            ClickableSpan clickSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    // Handle click event, e.g., navigate to ResetPassword activity
                    Intent intent = new Intent(HelpActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
            };
            spannableStr.setSpan(clickSpan, first, last, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        helpAddCategoryTextView.setText(spannableStr);
        helpAddCategoryTextView.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());

        TextView helpEditProfileTextView = findViewById(R.id.help_edit_profile_text_view);
        String note = helpEditProfileTextView.getText().toString();
        SpannableString spanString = new SpannableString(note);
        int initial = note.indexOf("EDIT PROFILE");
        int latest = start + "EDIT PROFILE".length();

        if (initial >= 0) {
            ClickableSpan onclickSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    // Handle click event, e.g., navigate to ResetPassword activity
                    Intent intent = new Intent(HelpActivity.this, ProfileManagementActivity.class);
                    startActivity(intent);
                }
            };
            spanString.setSpan(onclickSpan, initial, latest, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        helpEditProfileTextView.setText(spanString);
        helpEditProfileTextView.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
    }
}