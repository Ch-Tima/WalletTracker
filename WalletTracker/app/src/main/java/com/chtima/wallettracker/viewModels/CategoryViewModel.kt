package com.chtima.wallettracker.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.toLiveData
import com.chtima.wallettracker.db.DatabaseUpdatedEvent
import com.chtima.wallettracker.db.repositories.CategoryRepository
import com.chtima.wallettracker.models.AppConstants
import com.chtima.wallettracker.models.Category
import com.chtima.wallettracker.models.CategoryWithTransactions
import com.chtima.wallettracker.models.SharedPreferencesKeys
import io.reactivex.rxjava3.core.Completable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class CategoryViewModel(private val app: Application) : AndroidViewModel(app) {

    private val categoryRepository: CategoryRepository = CategoryRepository(app)
    private var categoriesWithTransactionsByUserLiveData : LiveData<List<CategoryWithTransactions>>? = null

    private var userID: Long = -1;

    fun insertAll(vararg categories: Category): Completable {
        return categoryRepository.insertAll(*categories)
    }

    fun getByType(type: Category.CategoryType?):LiveData<List<Category>>{
        if(type == null) return categoryRepository.getAll().toLiveData()
        return categoryRepository.getByType(type).toLiveData()
    }

    fun getCategoriesWithTransactionsByUser(): LiveData<List<CategoryWithTransactions>>{
        val userIdNow = SharedPreferencesKeys.getSharedPreferences(app).getLong(AppConstants.SELECTED_USER_ID, -1)
        if(categoriesWithTransactionsByUserLiveData == null || userID != userIdNow){
            userID = userIdNow
            categoriesWithTransactionsByUserLiveData = categoryRepository.getCategoriesWithTransactionsByUserId(userID).toLiveData()
            Log.d("CategoryViewModel", "create:LiveData")
        }

        Log.d("CategoryViewModel", "userID:$userID")

        return this.categoriesWithTransactionsByUserLiveData!!
    }

    override fun onCleared() {
        super.onCleared()
        EventBus.getDefault().unregister(this)
    }

}