package com.chtima.wallettracker.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "categories")
class Category(var title: String, var icon: String, var categoryType: CategoryType) {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    enum class CategoryType {
        INCOME,
        EXPENSE,
        MIX
    }

}