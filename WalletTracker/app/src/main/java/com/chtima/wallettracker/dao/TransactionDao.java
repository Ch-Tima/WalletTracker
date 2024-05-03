package com.chtima.wallettracker.dao;

import androidx.room.*;

import com.chtima.wallettracker.models.Transaction;

import java.util.List;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;

@Dao
public interface TransactionDao {
    @Insert
    Maybe<Long> insert (Transaction transaction);

    @Query("SELECT * FROM transactions")
    Observable<List<Transaction>> getAll();
}
