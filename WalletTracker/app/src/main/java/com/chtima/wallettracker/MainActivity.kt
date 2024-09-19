package com.chtima.wallettracker

import android.content.Intent
import android.os.Bundle
import android.widget.RadioGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.chtima.wallettracker.activities.WelcomeActivity
import com.chtima.wallettracker.db.AppDatabase
import com.chtima.wallettracker.fragments.HomeFragment
import com.chtima.wallettracker.fragments.ProfileFragment
import com.chtima.wallettracker.fragments.TransactionReportFragment


class MainActivity : AppCompatActivity() {

    private lateinit var homeFragment: HomeFragment
    private lateinit var activityFragment: TransactionReportFragment
    private lateinit var profileFragment: ProfileFragment

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
            homeFragment = HomeFragment.newInstance()
            //activityFragment = TransactionReportFragment.newInstance(user.id);
            profileFragment = ProfileFragment.newInstance();

            supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, homeFragment)
                .commit()

            findViewById<RadioGroup>(R.id.bottom_menu_btns).setOnCheckedChangeListener { group, checkedId ->
                var selectedFragment: Fragment? = null
                when (checkedId) {
                    R.id.radioHome -> {
                        selectedFragment = homeFragment
                    }
                    R.id.radioActivity -> {
                        selectedFragment = activityFragment
                    }
                    R.id.radioPerson -> {
                        selectedFragment = profileFragment
                    }
                }

                if (selectedFragment != null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_fragment_container, selectedFragment)
                        .commit();
                }
            }

        }else{
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish();
        }

    }
}