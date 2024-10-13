package com.chtima.wallettracker.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.toLiveData
import com.chtima.wallettracker.db.DatabaseUpdatedEvent
import com.chtima.wallettracker.db.repositories.TransactionRepository
import com.chtima.wallettracker.models.Transaction
import io.reactivex.rxjava3.core.Single
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class TransactionViewModel(private var app: Application) : AndroidViewModel(app) {

    private var transactionRepository: TransactionRepository = TransactionRepository(app)

    fun insert(transaction: Transaction) : Single<Long>{
        return transactionRepository.insert(transaction)
    }


}
