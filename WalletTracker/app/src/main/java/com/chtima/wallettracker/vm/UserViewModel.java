package com.chtima.wallettracker.vm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;

import com.chtima.wallettracker.dao.repositories.UserRepository;
import com.chtima.wallettracker.models.User;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UserViewModel extends AndroidViewModel {

    private final UserRepository repository;
    private final LiveData<User> liveData;

    public UserViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository(application);
        liveData = LiveDataReactiveStreams.fromPublisher(repository.getFirst().toFlowable());
    }

    public LiveData<User> getUser(){
        return liveData;
    }

    public Completable update(User user){
        return repository.update(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
