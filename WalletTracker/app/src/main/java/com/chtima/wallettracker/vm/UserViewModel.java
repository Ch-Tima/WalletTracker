package com.chtima.wallettracker.vm;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;

import com.chtima.wallettracker.dao.repositories.UserRepository;
import com.chtima.wallettracker.fragments.TopUpDialogFragment;
import com.chtima.wallettracker.models.User;

import java.util.List;

import autodispose2.AutoDispose;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableSource;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.functions.Predicate;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UserViewModel extends AndroidViewModel {

    private final UserRepository repository;
    private final MutableLiveData<User> liveData;

    public UserViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository(application);
        liveData = new MutableLiveData<>();
        loadUser();
    }

    public LiveData<User> getUser(){
        return liveData;
    }

    public Single<User> getFirst(){
        return repository.getFirst();
    }

    public Completable update(User user){
        return repository.update(user)
                .doOnComplete(() -> {
                    Log.d("UserViewModel", "User updated: " + user);
                    liveData.postValue(user);
                });
    }

    public Maybe<Long> insert(User user){
        return repository.insert(user)
                .doAfterSuccess(aLong -> {
                    user.id = aLong;
                    liveData.setValue(user);
                });
    }

    public Flowable<List<User>> getUsers(){
        return repository.getUsers();
    }


    @SuppressLint("CheckResult")
    private void loadUser() {
        repository.getFirst()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        user -> {
                            Log.d("UserViewModel", "User loaded: " + user);
                            liveData.setValue(user);
                        },
                        throwable -> liveData.setValue(User.empty())
                );
    }

}
