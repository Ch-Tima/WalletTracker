package com.chtima.wallettracker.db.repositories

import android.app.Application
import com.chtima.wallettracker.db.AppDatabase
import com.chtima.wallettracker.db.dao.TransactionDao
import com.chtima.wallettracker.models.Transaction
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class TransactionRepository (application: Application) {

    private val transactionDao: TransactionDao = AppDatabase.getInstance(application).transactionDao()

    fun insert(transaction: Transaction): Single<Long> {
        return transactionDao.insert(transaction)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

}