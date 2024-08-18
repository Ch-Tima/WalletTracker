package com.chtima.wallettracker.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.chtima.wallettracker.R;
import com.chtima.wallettracker.fragments.welcome.WelcomeFragment;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView textPP = findViewById(R.id.privacy_policy);
        textPP.setText(Html.fromHtml(textPP.getText().toString(), Html.FROM_HTML_MODE_LEGACY));
        textPP.setOnClickListener(x -> openWebpage("https://github.com/Ch-Tima/WalletTracker"));//TODO

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment_container, WelcomeFragment.newInstance(), WelcomeFragment.class.getName())
                .commit();
    }

    private void openWebpage(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }
}