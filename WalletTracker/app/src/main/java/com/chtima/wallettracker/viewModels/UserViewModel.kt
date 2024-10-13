package com.chtima.wallettracker.viewModels

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import autodispose2.AutoDispose
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider
import com.chtima.wallettracker.db.repositories.UserRepository
import com.chtima.wallettracker.models.SharedPreferencesKeys
import com.chtima.wallettracker.models.User
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe

@SuppressLint("CheckResult")
class UserViewModel(private val app:Application) : AndroidViewModel(app) {

    private var userRepository: UserRepository = UserRepository(app)
    private var liveData : MutableLiveData<User> = MutableLiveData()

    init {
        val userID = SharedPreferencesKeys.getSharedPreferences(app).getLong(SharedPreferencesKeys.SELECTED_USER_ID, -999L)
        if(userID != -999L){
            userRepository.getUserById(userID)
                .subscribe({ user ->
                    Log.d("UserViewModel", "User loaded: $user")
                    liveData.setValue(user)
                },{
                    throw Exception("NOT FOUND USER!")
                })
        }
    }

    fun insert(user: User) : Maybe<Long>{
        return userRepository.insert(user);
    }

    fun getUser():MutableLiveData<User>{
        return liveData
    }

    fun update(user: User): Completable {
        return userRepository.update(user)
            .doOnComplete{
                Log.d("UserViewModel", "User updated: $user");
                liveData.postValue(user);
            }
    }

}