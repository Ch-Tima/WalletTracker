package com.chtima.wallettracker;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.chtima.wallettracker.dao.AppDatabase;
import com.chtima.wallettracker.fragments.HomeFragment;
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
                    HomeFragment homeFragment = HomeFragment.newInstance();
                    homeFragment.setUser(user);

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_fragment_container, homeFragment, HomeFragment.class.getName())
                            .commit();
                }, er -> signIn());
    }

    private void signIn() {
        //TODO...
        categoryVM.insertAll(AppDatabase.defaultCategories())
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe();
        userVM.insert(new User("Tima", "Ch", 832.34, "USD"))
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(aLong -> {
                    loadUser();
                }, throwable -> {
                    Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_LONG).show();
                });

    }
}