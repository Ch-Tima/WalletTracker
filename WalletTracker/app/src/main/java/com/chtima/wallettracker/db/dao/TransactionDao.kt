package com.chtima.wallettracker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.chtima.wallettracker.models.Transaction
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

@Dao
interface TransactionDao {
    @Insert
    fun insert(transaction: Transaction): Single<Long>

    @Query("SELECT * FROM `transaction`")
    fun getAll(): Flowable<List<Transaction>>
}