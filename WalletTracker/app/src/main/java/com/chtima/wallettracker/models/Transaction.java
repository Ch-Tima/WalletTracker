package com.chtima.wallettracker.models;

import androidx.room.*;

import com.chtima.wallettracker.converters.DateConverter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


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
        this.dateTime = normalizeDate(dateTime);
        this.type = type;
    }

    public String getDate(){
        if(this.dateTime == null) return "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(this.dateTime.getTime());
    }

    private Date normalizeDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

}
