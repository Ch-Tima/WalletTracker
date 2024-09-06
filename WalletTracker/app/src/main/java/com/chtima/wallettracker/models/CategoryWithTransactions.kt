package com.chtima.wallettracker.models

import androidx.room.Embedded
import androidx.room.Relation


class CategoryWithTransactions {

    @Embedded
    var category: Category? = null

    @Relation(parentColumn = "id", entityColumn = "categoryId")
    var transactions: List<Transaction>? = null

}