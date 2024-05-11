package com.chtima.wallettracker.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.chtima.wallettracker.models.User;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface UserDao {

    @Insert
    Maybe<Long> insert (User user);

    @Query("SELECT * FROM users LIMIT 1")
    Single<User> getFirst ();

}
