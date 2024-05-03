package com.chtima.wallettracker.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(defaultValue = "guest")
    public String firstname;
    @ColumnInfo(defaultValue = "")
    public String lastname;

    public TransactionType type;

}
