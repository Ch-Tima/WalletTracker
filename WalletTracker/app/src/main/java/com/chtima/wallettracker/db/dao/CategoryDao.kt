package com.chtima.wallettracker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.chtima.wallettracker.models.Category
import com.chtima.wallettracker.models.Category.CategoryType;
import com.chtima.wallettracker.models.CategoryWithTransactions
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe


@Dao
interface CategoryDao {

    @Insert
    fun insert(category: Category): Maybe<Long>

    @Insert
    fun insertAll(vararg categories: Category): Completable

    @Query("SELECT * FROM categories")
    fun getAll(): Flowable<List<Category>>

    @Query("SELECT * FROM categories WHERE categoryType = :type")
    fun getByType(type: CategoryType?): Flowable<List<Category>>

    @Query("SELECT * FROM categories")
    fun getCategoriesWithTransactions(): Flowable<List<CategoryWithTransactions>>

    @Transaction
    @Query("SELECT * FROM categories WHERE id IN (SELECT `categoryId` FROM `transaction` WHERE userId = :userId)")
    fun getCategoriesWithTransactionsByUserId(userId: Long): Flowable<List<CategoryWithTransactions>>

}