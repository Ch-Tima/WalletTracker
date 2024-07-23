package com.chtima.wallettracker.models;


import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "categories")
public class Category {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String title;
    public String icon;
    public CategoryType categoryType;

    public Category(String title, String icon, CategoryType categoryType) {
        this.title = title;
        this.icon = icon;
        this.categoryType = categoryType;
    }

    public enum CategoryType{
        INCOME,
        EXPENSE,
        MIX
    }
}
