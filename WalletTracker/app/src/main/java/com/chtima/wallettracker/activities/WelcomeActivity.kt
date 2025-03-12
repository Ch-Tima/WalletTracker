package com.chtima.wallettracker.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.chtima.wallettracker.R
import com.chtima.wallettracker.fragments.welcome.WelcomeFragment
import org.w3c.dom.Text


class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_welcome)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val textPP = findViewById<TextView>(R.id.privacy_policy)
        textPP.text = Html.fromHtml(textPP.text.toString(), Html.FROM_HTML_MODE_LEGACY)
        textPP.setOnClickListener { x: View? ->
            openWebpage(
                "https://github.com/Ch-Tima/WalletTracker"
            )
        } //TODO

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_container, WelcomeFragment.newInstance(), WelcomeFragment::class.simpleName)
            .commit()

    }

    private fun openWebpage(url:String){
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }
}