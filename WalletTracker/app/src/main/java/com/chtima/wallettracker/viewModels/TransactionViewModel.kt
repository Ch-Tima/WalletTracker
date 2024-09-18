package com.chtima.wallettracker.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.chtima.wallettracker.db.repositories.TransactionRepository
import com.chtima.wallettracker.models.Transaction
import io.reactivex.rxjava3.core.Single

class TransactionViewModel(private var app: Application) : AndroidViewModel(app) {

    private val transactionRepository: TransactionRepository = TransactionRepository(app)

    fun insert(transaction: Transaction) : Single<Long>{
        return transactionRepository.insert(transaction)
    }

}
