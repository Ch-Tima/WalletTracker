package com.chtima.wallettracker.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.chtima.wallettracker.models.User;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface UserDao {

    @Insert
    Maybe<Long> insert (User user);

    @Query("SELECT * FROM users LIMIT 1")
    Single<User> getFirst ();

    @Query("SELECT * FROM users")
    Flowable<List<User>> getUsers();

    @Update
    Completable update(User user);

}
