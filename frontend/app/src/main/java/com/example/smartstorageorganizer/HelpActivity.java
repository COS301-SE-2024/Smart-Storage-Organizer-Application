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
import android.text.Spanned;
import android.widget.TextView;
import android.view.View;
import android.text.method.LinkMovementMethod;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        setupHelpLinks();
    }

    private void setupHelpLinks() {
        TextView Help1 = findViewById(R.id.help_reset_password_text_view);
        TextView Help2 = findViewById(R.id.help_add_category_text_view);
        TextView Help3 = findViewById(R.id.help_edit_profile_text_view);

        makeLink(Help1, "Forgot Password", new Intent(this, ResetPasswordActivity.class));
        makeLink(Help2, "Homepage", new Intent(this, HomeActivity.class));
        makeLink(Help3, "EDIT PROFILE", new Intent(this, ProfileManagementActivity.class));
    }

    private void makeLink(TextView textView, String linkText, Intent intent) {
        String text = textView.getText().toString();
        int start = text.indexOf(linkText);
        int end = start + linkText.length();

        if (start == -1) return;

        SpannableString spannableString = new SpannableString(text);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                startActivity(intent);
            }
        };

        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    };

//        TextView helpAddCategoryTextView = findViewById(R.id.help_add_category_text_view);
//        String message = helpAddCategoryTextView.getText().toString();
//        SpannableString spannableStr = new SpannableString(message);
//        int first = message.indexOf("Homepage");
//        int last = start + "Homepage".length();
//
//        if (first >= 0) {
//            ClickableSpan clickSpan = new ClickableSpan() {
//                @Override
//                public void onClick(View widget) {
//                    // Handle click event, e.g., navigate to ResetPassword activity
//                    Intent intent = new Intent(HelpActivity.this, HomeActivity.class);
//                    startActivity(intent);
//                }
//            };
//            spannableStr.setSpan(clickSpan, first, last, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
//        }
//
//        helpAddCategoryTextView.setText(spannableStr);
//        helpAddCategoryTextView.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());

//        TextView helpEditProfileTextView = findViewById(R.id.help_edit_profile_text_view);
//        String note = helpEditProfileTextView.getText().toString();
//        SpannableString spanString = new SpannableString(note);
//        int initial = note.indexOf("EDIT PROFILE");
//        int latest = start + "EDIT PROFILE".length();
//
//        if (initial >= 0) {
//            ClickableSpan onclickSpan = new ClickableSpan() {
//                @Override
//                public void onClick(View widget) {
//                    // Handle click event, e.g., navigate to ResetPassword activity
//                    Intent intent = new Intent(HelpActivity.this, ProfileManagementActivity.class);
//                    startActivity(intent);
//                }
//            };
//            spanString.setSpan(onclickSpan, initial, latest, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
//        }
//
//        helpEditProfileTextView.setText(spanString);
//        helpEditProfileTextView.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
    
}