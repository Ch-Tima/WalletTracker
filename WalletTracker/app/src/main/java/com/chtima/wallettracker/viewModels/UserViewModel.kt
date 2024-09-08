package com.chtima.wallettracker.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.chtima.wallettracker.db.repositories.UserRepository
import com.chtima.wallettracker.models.User
import io.reactivex.rxjava3.core.Maybe

class UserViewModel(var app:Application) : AndroidViewModel(app) {

    private var userRepository: UserRepository = UserRepository(app)
    private var liveData : MutableLiveData<User> = MutableLiveData()


    fun insert(user: User) : Maybe<Long>{
        return userRepository.insert(user);
    }

}