package com.chtima.wallettracker.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import org.jetbrains.annotations.NotNull;

import java.util.List;

@Entity(tableName = "categories")
public class Category {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String title;
    public int icon;

    public TransactionType type;

    public Category(String title, int icon, TransactionType type) {
        this.title = title;
        this.icon = icon;
        this.type = type;
    }
}
