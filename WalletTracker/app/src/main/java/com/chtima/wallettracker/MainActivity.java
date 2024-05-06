package com.chtima.wallettracker;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.chtima.wallettracker.dao.AppDatabase;
import com.chtima.wallettracker.fragments.HomeFragment;
import com.chtima.wallettracker.models.Category;
import com.chtima.wallettracker.models.CategoryWithTransactions;
import com.chtima.wallettracker.models.Transaction;
import com.chtima.wallettracker.models.User;


import org.reactivestreams.Subscriber;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.MaybeObserver;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private User user;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = AppDatabase.getInstance(this.getApplicationContext());
        getUser(db);
        //LOADING...
    }



    private void getUser(AppDatabase db){
        db.userDao().getFirst()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<User>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull User user) {
                        MainActivity.this.user = user;
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.main_fragment_container, HomeFragment.newInstance(user), HomeFragment.class.getName())
                                .commit();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                });
    }
}