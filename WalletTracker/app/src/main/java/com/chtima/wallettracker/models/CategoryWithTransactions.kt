package com.chtima.wallettracker.models

import androidx.room.Embedded
import androidx.room.Relation


class CategoryWithTransactions {

    @Embedded
    lateinit var category: Category

    @Relation(parentColumn = "id", entityColumn = "categoryId")
    lateinit var transactions: List<Transaction>

}