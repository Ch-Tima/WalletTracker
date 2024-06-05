package com.chtima.wallettracker.dao.repositories;

import android.app.Application;

import com.chtima.wallettracker.dao.AppDatabase;
import com.chtima.wallettracker.dao.UserDao;
import com.chtima.wallettracker.models.User;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class UserRepository {

    private final UserDao userDao;
    private final AppDatabase database;

    public UserRepository(Application application) {
        this.database = AppDatabase.getInstance(application);
        this.userDao = database.userDao();
    }

    public Single<User> getFirst (){
        return userDao.getFirst();
    }

    public Completable update(User user){
        return userDao.update(user);
    }

}
