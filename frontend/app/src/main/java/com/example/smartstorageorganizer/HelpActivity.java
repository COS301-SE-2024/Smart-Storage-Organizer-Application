package com.example.smartstorageorganizer;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

public class HelpActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        setupHelpLinks();
    }

    private void setupHelpLinks() {
        TextView help1 = findViewById(R.id.help_reset_password_text_view);
        TextView help2 = findViewById(R.id.help_add_category_text_view);
        TextView help3 = findViewById(R.id.help_edit_profile_text_view);
        TextView help4 = findViewById(R.id.help_unit_view);

        makeLink(help1, "Forgot Password", new Intent(this, ResetPasswordActivity.class));
        makeLink(help2, "Homepage", new Intent(this, HomeActivity.class));
        makeLink(help3, "EDIT PROFILE", new Intent(this, ProfileManagementActivity.class));
        makeLink(help4, "Unit", new Intent(this, UnitActivity.class));
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
    }
}