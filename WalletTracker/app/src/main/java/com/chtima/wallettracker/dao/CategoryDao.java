package com.chtima.wallettracker.dao;

import androidx.room.*;

import com.chtima.wallettracker.models.Category;
import com.chtima.wallettracker.models.CategoryWithTransactions;

import java.util.List;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;

@Dao
public interface CategoryDao {

    @Insert
    Maybe<Long> insert (Category category);

    @Query("SELECT * FROM categories")
    Observable<List<Category>> getAll();

    @Query("SELECT * FROM categories")
    Observable<List<CategoryWithTransactions>> getCategoriesWithTransactions();

}
