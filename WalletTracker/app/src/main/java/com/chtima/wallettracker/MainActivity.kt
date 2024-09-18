package com.chtima.wallettracker

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.chtima.wallettracker.activities.WelcomeActivity
import com.chtima.wallettracker.db.AppDatabase
import com.chtima.wallettracker.fragments.HomeFragment


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if(AppDatabase.isExist(this)){
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, HomeFragment.newInstance())
                .commit()
        }else{
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish();
        }

    }
}