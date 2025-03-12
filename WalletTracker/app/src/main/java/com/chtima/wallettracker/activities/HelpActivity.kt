package com.chtima.wallettracker.activities

import com.chtima.wallettracker.R
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HelpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_help)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        //close
        findViewById<View>(R.id.btn_back).setOnClickListener { x: View? -> finish() }


        //support
        findViewById<View>(R.id.support).setOnClickListener { x: View? ->
            val clipboard: ClipboardManager =
                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(
                ClipData.newPlainText(
                    getText(R.string.app_name),
                    getText(R.string.help_developer_forge_gmail_com)
                )
            )
            Toast.makeText(this, getString(R.string.support_email_copy), Toast.LENGTH_SHORT)
                .show()
        }

        findViewById<View>(R.id.privacy_policy).setOnClickListener { x: View? ->
            openWebpage(
                "https://github.com/Ch-Tima/WalletTracker"
            )
        } //TODO


        //about
        findViewById<View>(R.id.github).setOnClickListener { x: View? ->
            openWebpage(
                "https://github.com/Ch-Tima"
            )
        }
        findViewById<View>(R.id.google_play_store).setOnClickListener { x: View? ->
            openWebpage(
                "https://play.google.com/store/apps/developer?id=Ch-Tima"
            )
        }


        //help_us
        findViewById<View>(R.id.buy_coffee).setOnClickListener { x: View? ->
            openWebpage(
                "https://ko-fi.com/chtima/"
            )
        }
        findViewById<View>(R.id.rate_us).setOnClickListener { x: View? ->
            openWebpage(
                "https://github.com/Ch-Tima/WalletTracker"
            )
        } //TODO

    }

    private fun openWebpage(uriString: String?) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uriString)))
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) finish()
        return super.onKeyDown(keyCode, event)
    }
}