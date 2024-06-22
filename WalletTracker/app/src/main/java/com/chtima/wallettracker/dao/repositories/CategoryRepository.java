package com.chtima.wallettracker.dao.repositories;

import android.app.Application;

import com.chtima.wallettracker.dao.AppDatabase;
import com.chtima.wallettracker.dao.CategoryDao;
import com.chtima.wallettracker.models.Category;
import com.chtima.wallettracker.models.CategoryWithTransactions;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CategoryRepository {
    private CategoryDao categoryDao;
    private AppDatabase database;

    public CategoryRepository(Application application) {
        this.database = AppDatabase.getInstance(application);
        this.categoryDao = database.categoryDao();
    }

    public Completable insertAll(Category...categories){
        return categoryDao.insertAll(categories)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Flowable<List<Category>> getAll(){
        return categoryDao.getAll();
    }

    public Flowable<List<CategoryWithTransactions>> getCategoriesWithTransactions() {
        return categoryDao.getCategoriesWithTransactions();
    }

}
