package com.chtima.wallettracker.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.chtima.wallettracker.converters.DateConverter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "transaction", foreignKeys = [
    ForeignKey(entity = Category::class, parentColumns = ["id"], childColumns = ["categoryId"], onDelete = ForeignKey.CASCADE),
    ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["userId"], onDelete = ForeignKey.CASCADE)
])
@TypeConverters(DateConverter::class)
class Transaction(
    var categoryId: Long,
    var userId: Long,
    var sum: Double,
    var title: String,
    var note: String?,
    var dateTime: Date,
    var type: TransactionType
) {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    fun getDate():String{
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(this.dateTime.time);
    }

    enum class TransactionType {
        INCOME,
        EXPENSE
    }
}