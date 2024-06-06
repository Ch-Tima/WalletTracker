package com.chtima.wallettracker;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.chtima.wallettracker.fragments.HomeFragment;
import com.chtima.wallettracker.models.User;
import com.chtima.wallettracker.vm.UserViewModel;

import java.util.Date;

import autodispose2.AutoDispose;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;

public class MainActivity extends AppCompatActivity {

    private User user;

    public static final Date nowDate = new Date((1716500076L*1000));//2024-05-23 23:34:36; // only test
    private UserViewModel userVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //init
        userVM = new ViewModelProvider(this).get(UserViewModel.class);
        //LOADING...
        loadUser();
        //LOADING...
    }

    private void loadUser(){
        userVM.getFirst()
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(user -> {
                    if(user == null || User.isEmpty(user))
                        signIn();

                    MainActivity.this.user = user;
                    HomeFragment homeFragment = HomeFragment.newInstance();
                    homeFragment.setUser(user);

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_fragment_container, homeFragment, HomeFragment.class.getName())
                            .commit();
                }, er -> signIn());
    }

    private void signIn() {
        //TODO...
    }
}