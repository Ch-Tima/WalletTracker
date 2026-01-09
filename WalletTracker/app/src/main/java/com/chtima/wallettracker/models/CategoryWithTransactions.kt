package com.chtima.wallettracker.models

import androidx.room.Embedded
import androidx.room.Relation

class CategoryWithTransactions(
    @Embedded val category: Category,
    @Relation(parentColumn = "id", entityColumn = "categoryId") val transactions: List<Transaction>
) {
    fun copy(
        category: Category = this.category,
        transactions: List<Transaction> = this.transactions
    ): CategoryWithTransactions {
        return CategoryWithTransactions(category, transactions)
    }
}
