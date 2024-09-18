package com.chtima.wallettracker.db.repositories

import android.app.Application
import com.chtima.wallettracker.db.AppDatabase
import com.chtima.wallettracker.db.dao.CategoryDao
import com.chtima.wallettracker.models.Category
import com.chtima.wallettracker.models.CategoryWithTransactions
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers


class CategoryRepository(val app: Application) {

    private val dao: CategoryDao = AppDatabase.getInstance(app).categoryDao()

    fun insertAll(vararg categories: Category): Completable {
        return dao.insertAll(*categories)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getByType(type: Category.CategoryType): Flowable<List<Category>> {
        return dao.getByType(type)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getCategoriesWithTransactionsByUserId(userId: Long): Flowable<List<CategoryWithTransactions>> {
        return dao.getCategoriesWithTransactionsByUserId(userId)
    }

    fun getAll(): Flowable<List<Category>> {
        return dao.getAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

}