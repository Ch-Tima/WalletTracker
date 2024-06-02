package com.chtima.wallettracker.dao.repositories;

import android.app.Application;

import com.chtima.wallettracker.dao.AppDatabase;
import com.chtima.wallettracker.dao.CategoryDao;
import com.chtima.wallettracker.dao.TransactionDao;
import com.chtima.wallettracker.models.Category;
import com.chtima.wallettracker.models.Transaction;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

public class TransactionRepository {

    private TransactionDao transactionDao;
    private AppDatabase database;

    public TransactionRepository(Application application) {
        this.database = AppDatabase.getInstance(application);
        this.transactionDao = database.transactionDao();
    }

    public Flowable<List<Transaction>> getAll(){
        return transactionDao.getAll();
    }

    public Single<Long> insert(Transaction transaction){
        return transactionDao.insert(transaction);
    }

}
