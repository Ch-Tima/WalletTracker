package com.chtima.wallettracker.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "categories")
public class Category {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String title;
    public int icon;


    public Category(String title, int icon) {
        this.title = title;
        this.icon = icon;
    }
}
