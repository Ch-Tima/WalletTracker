package com.chtima.wallettracker.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Objects


@Entity(tableName = "categories")
class Category(var title: String, var icon: String, var categoryType: CategoryType) {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val category = o as Category
        return id == category.id && Objects.equals(title, category.title) && Objects.equals(
            icon,
            category.icon
        ) && categoryType == category.categoryType
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + icon.hashCode()
        result = 31 * result + categoryType.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }

    enum class CategoryType {
        INCOME,
        EXPENSE,
        MIX
    }

}