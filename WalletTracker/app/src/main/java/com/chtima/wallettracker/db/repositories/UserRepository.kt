package com.chtima.wallettracker.db.repositories

import android.app.Application
import com.chtima.wallettracker.db.AppDatabase
import com.chtima.wallettracker.db.dao.UserDao
import com.chtima.wallettracker.models.User
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers


class UserRepository(val app: Application){

    private var dao: UserDao = AppDatabase.getInstance(app).userDao()

    fun insert(user: User): Maybe<Long> {
        return dao.insert(user)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getUsers(): Flowable<List<User>> {
        return dao.getUsers()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun update(user: User): Completable {
        return dao.update(user)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

}