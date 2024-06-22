package com.chtima.wallettracker.vm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;

import com.chtima.wallettracker.dao.repositories.UserRepository;
import com.chtima.wallettracker.models.User;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

public class UserViewModel extends AndroidViewModel {

    private final UserRepository repository;
    private final LiveData<User> liveData;

    public UserViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository(application);
        liveData = LiveDataReactiveStreams.fromPublisher(repository.getFirst().toFlowable().onErrorReturnItem(User.empty()));
    }

    public LiveData<User> getUser(){
        return liveData;
    }

    public Single<User> getFirst(){
        return repository.getFirst();
    }

    public Completable update(User user){
        return repository.update(user);
    }

    public Maybe<Long> insert(User user){
        return repository.insert(user);
    }

}
