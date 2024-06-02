package com.chtima.wallettracker.vm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;

import com.chtima.wallettracker.dao.repositories.CategoryRepository;
import com.chtima.wallettracker.dao.repositories.TransactionRepository;
import com.chtima.wallettracker.models.Category;
import com.chtima.wallettracker.models.Transaction;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class TransactionViewModel extends AndroidViewModel {
    private final TransactionRepository repository;
    private final LiveData<List<Transaction>> liveData;

    public TransactionViewModel(@NonNull Application application) {
        super(application);
        repository = new TransactionRepository(application);
        liveData = LiveDataReactiveStreams.fromPublisher(repository.getAll());
    }

    public LiveData<List<Transaction>> getAll() {
        return liveData;
    }

    public Single<Long> insert(Transaction transaction){
        return repository.insert(transaction)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
