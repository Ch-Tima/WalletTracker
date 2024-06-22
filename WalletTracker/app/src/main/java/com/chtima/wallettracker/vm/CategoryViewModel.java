package com.chtima.wallettracker.vm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;

import com.chtima.wallettracker.dao.repositories.CategoryRepository;
import com.chtima.wallettracker.models.Category;
import com.chtima.wallettracker.models.CategoryWithTransactions;

import java.io.Closeable;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;

public class CategoryViewModel extends AndroidViewModel {

    private final CategoryRepository repository;
    private final LiveData<List<Category>> categoriesLiveData;
    private final LiveData<List<CategoryWithTransactions>> categoriesWithTransactionsLiveData;

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        repository = new CategoryRepository(application);
        categoriesLiveData = LiveDataReactiveStreams.fromPublisher(repository.getAll());
        categoriesWithTransactionsLiveData = LiveDataReactiveStreams.fromPublisher(repository.getCategoriesWithTransactions());
    }

    public Completable insertAll(Category...categories){
        return repository.insertAll(categories);
    }

    public LiveData<List<Category>> getAll() {
        return categoriesLiveData;
    }

    public LiveData<List<CategoryWithTransactions>> getCategoriesWithTransactions() {
        return categoriesWithTransactionsLiveData;
    }
}
