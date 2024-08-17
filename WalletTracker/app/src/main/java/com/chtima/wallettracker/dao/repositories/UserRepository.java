package com.chtima.wallettracker.dao.repositories;

import android.app.Application;

import com.chtima.wallettracker.dao.AppDatabase;
import com.chtima.wallettracker.dao.UserDao;
import com.chtima.wallettracker.models.User;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UserRepository {

    private final UserDao userDao;
    private final AppDatabase database;

    public UserRepository(Application application) {
        this.database = AppDatabase.getInstance(application);
        this.userDao = database.userDao();
    }

    public Maybe<Long> insert(User user){
        return this.userDao.insert(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<User> getFirst (){
        return userDao.getFirst()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Flowable<List<User>> getUsers(){
        return userDao.getUsers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable update(User user){
        return userDao.update(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
