package com.chtima.wallettracker.models;

import androidx.room.*;

import com.chtima.wallettracker.converters.DateConverter;

import java.util.Date;


@Entity(tableName = "transactions", foreignKeys = {
        @ForeignKey(entity = Category.class, parentColumns = "id", childColumns = "categoryId", onDelete = ForeignKey.CASCADE)
})
public class Transaction {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long categoryId;


    public double sum;
    public String title;

    public String note;

    @TypeConverters(DateConverter.class)
    public Date dateTime;

    public TransactionType type;


    public Transaction(long categoryId, double sum, String title, String note, Date dateTime, TransactionType type) {
        this.categoryId = categoryId;
        this.sum = sum;
        this.title = title;
        this.note = note;
        this.dateTime = dateTime;
        this.type = type;
    }
}
