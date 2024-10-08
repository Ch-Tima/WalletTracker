package com.chtima.wallettracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.chtima.wallettracker.activities.WelcomeActivity;
import com.chtima.wallettracker.dao.AppDatabase;
import com.chtima.wallettracker.fragments.HomeFragment;
import com.chtima.wallettracker.fragments.ProfileFragment;
import com.chtima.wallettracker.fragments.TransactionReportFragment;
import com.chtima.wallettracker.models.User;
import com.chtima.wallettracker.vm.CategoryViewModel;
import com.chtima.wallettracker.vm.UserViewModel;

import java.util.Date;

import autodispose2.AutoDispose;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;

public class MainActivity extends AppCompatActivity {

    private User user;

    public static final Date nowDate = new Date(System.currentTimeMillis()); //new Date((1716500076L*1000));//2024-05-23 23:34:36; // only test
    private UserViewModel userVM;
    private CategoryViewModel categoryVM;
    private RadioGroup radioGroup;
    private FragmentTransaction transaction;
    private HomeFragment homeFragment;
    private TransactionReportFragment activityFragment;
    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //init
        userVM = new ViewModelProvider(this).get(UserViewModel.class);
        categoryVM = new ViewModelProvider(this).get(CategoryViewModel.class);
        //LOADING...
        loadUser();
        //LOADING...

        radioGroup = (RadioGroup)findViewById(R.id.bottom_menu_btns);

    }

    private void loadUser(){
        userVM.getFirst()
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(user -> {
                    if(user == null || User.isEmpty(user)){
                        signIn();
                        return;
                    }

                    MainActivity.this.user = user;

                    homeFragment = HomeFragment.newInstance();
                    activityFragment = TransactionReportFragment.newInstance(user.id);
                    profileFragment = ProfileFragment.newInstance();

                    homeFragment.setUser(user);

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_fragment_container, homeFragment)
                            .commit();


                    setUpBottomMenu();

                }, er -> signIn());
    }

    private void setUpBottomMenu(){
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Fragment selectedFragment = null;
            if (checkedId == R.id.radioHome){
                selectedFragment = homeFragment;
            }else if(checkedId == R.id.radioActivity){
                selectedFragment = activityFragment;
            } else if (checkedId == R.id.radioPerson) {
                selectedFragment = profileFragment;
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment_container, selectedFragment)
                        .commit();
            }

        });
    }

    private void signIn() {
        categoryVM.insertAll(AppDatabase.defaultCategories(this))
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(() -> {}, throwable -> {
                    Toast.makeText(this, R.string.unexpected_error, Toast.LENGTH_SHORT).show();
                    Log.e("ERR", throwable.toString());
                });


        startActivity(new Intent(this, WelcomeActivity.class));
        finish();

    }

}