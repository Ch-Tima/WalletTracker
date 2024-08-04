package com.chtima.wallettracker.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.chtima.wallettracker.R;

public class HelpActivity extends AppCompatActivity {

    private int  i = 0;

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

        //close
        findViewById(R.id.btn_back).setOnClickListener(x -> finish());

        //support
        findViewById(R.id.support).setOnClickListener(x -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setPrimaryClip(ClipData.newPlainText(getText(R.string.app_name), getText(R.string.help_developer_forge_gmail_com)));
            Toast.makeText(this, getString(R.string.support_email_copy), Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.privacy_policy).setOnClickListener(x -> openWebpage("https://github.com/Ch-Tima/WalletTracker"));//TODO

        //about
        findViewById(R.id.github).setOnClickListener(x -> openWebpage("https://github.com/Ch-Tima"));
        findViewById(R.id.google_play_store).setOnClickListener(x -> openWebpage("https://play.google.com/store/apps/developer?id=Ch-Tima"));

        //help_us
        findViewById(R.id.buy_coffee).setOnClickListener(x -> openWebpage("https://ko-fi.com/chtima/"));
        findViewById(R.id.rate_us).setOnClickListener(x -> openWebpage("https://github.com/Ch-Tima/WalletTracker"));//TODO

    }

    public void openWebpage(String uriString) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uriString)));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) finish();
        return super.onKeyDown(keyCode, event);
    }
}