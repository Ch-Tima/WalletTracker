package com.chtima.wallettracker.dao;

import androidx.room.*;

import com.chtima.wallettracker.models.Transaction;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface TransactionDao {
    @Insert
    Single<Long> insert (Transaction transaction);

    @Query("SELECT * FROM transactions")
    Flowable<List<Transaction>> getAll();
}
