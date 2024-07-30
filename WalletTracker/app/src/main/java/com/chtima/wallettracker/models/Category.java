package com.chtima.wallettracker.models;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;


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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return id == category.id && Objects.equals(title, category.title) && Objects.equals(icon, category.icon) && categoryType == category.categoryType;
    }

    public enum CategoryType{
        INCOME,
        EXPENSE,
        MIX
    }
}
