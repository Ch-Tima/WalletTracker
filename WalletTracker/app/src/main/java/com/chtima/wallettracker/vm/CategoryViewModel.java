package com.chtima.wallettracker.vm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;

import com.chtima.wallettracker.dao.repositories.CategoryRepository;
import com.chtima.wallettracker.models.Category;
import com.chtima.wallettracker.models.CategoryWithTransactions;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;

public class CategoryViewModel extends AndroidViewModel {

    private final CategoryRepository repository;
    private final LiveData<List<Category>> categoriesLiveData;
    private final LiveData<List<CategoryWithTransactions>> categoriesWithTransactionsLiveData;
    private LiveData<List<CategoryWithTransactions>> categoriesWithTransactionsByUserIdLiveData;

    private long userId;

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        repository = new CategoryRepository(application);
        categoriesLiveData = LiveDataReactiveStreams.fromPublisher(repository.getAll());
        categoriesWithTransactionsLiveData = LiveDataReactiveStreams.fromPublisher(repository.getCategoriesWithTransactions());
        categoriesWithTransactionsByUserIdLiveData = null;
    }

    public Completable insertAll(Category...categories){
        return repository.insertAll(categories);
    }

    public LiveData<List<Category>> getAll() {
        return categoriesLiveData;
    }

    public LiveData<List<Category>> getByType(Category.CategoryType categoryType) {
        if(categoryType == null) return this.categoriesLiveData;
        return LiveDataReactiveStreams.fromPublisher(repository.getByType(categoryType));
    }

    public LiveData<List<CategoryWithTransactions>> getCategoriesWithTransactions() {
        return categoriesWithTransactionsLiveData;
    }

    public LiveData<List<CategoryWithTransactions>> getCategoriesWithTransactionsByUserId(long userId) {

        if(categoriesWithTransactionsByUserIdLiveData == null || this.userId != userId){
            this.userId = userId;
            categoriesWithTransactionsByUserIdLiveData = LiveDataReactiveStreams.fromPublisher(repository.getCategoriesWithTransactionsByUserId(userId));
        }

        return categoriesWithTransactionsByUserIdLiveData;
    }
}
