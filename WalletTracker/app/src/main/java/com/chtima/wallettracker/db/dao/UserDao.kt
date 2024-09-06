package com.chtima.wallettracker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.chtima.wallettracker.models.User
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe

@Dao
interface UserDao {

    @Insert
    fun insert(user: User): Maybe<Long>

    @Query("SELECT * FROM users")
    fun getUsers(): Flowable<List<User>>

    @Update
    fun update(user: User): Completable

}