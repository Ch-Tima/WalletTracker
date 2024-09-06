package com.chtima.wallettracker.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "users")
class User(
    @ColumnInfo(defaultValue = "guest") var firstname: String?,
    @ColumnInfo(defaultValue = "") var lastname: String?,
    @ColumnInfo(defaultValue = "0.0") var balance: Double,
    @ColumnInfo(defaultValue = "USD") var currency: String?
) {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    override fun toString(): String {
        return "User(id=$id, firstname=$firstname, lastname=$lastname, balance=$balance, currency=$currency)"
    }


}