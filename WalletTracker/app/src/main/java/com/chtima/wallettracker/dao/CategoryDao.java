package com.chtima.wallettracker.dao;

import androidx.room.*;

import com.chtima.wallettracker.models.Category;
import com.chtima.wallettracker.models.CategoryWithTransactions;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Completable;

@Dao
public interface CategoryDao {

    @Insert
    Maybe<Long> insert (Category category);

    @Insert()
    Completable insertAll(Category...categories);

    @Query("SELECT * FROM categories")
    Flowable<List<Category>> getAll();

    @Query("SELECT * FROM categories")
    Flowable<List<CategoryWithTransactions>> getCategoriesWithTransactions();

    @Transaction
    @Query("SELECT * FROM categories WHERE id IN (SELECT categoryId FROM transactions WHERE userId = :userId)")
    Flowable<List<CategoryWithTransactions>> getCategoriesWithTransactionsByUserId(long userId);

}
