package com.chtima.wallettracker.models;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


@Entity(tableName = "categories")
public class Category {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String title;
    public String icon;

    @Ignore
    public Category(String title, int icon) {
        this.title = title;
        this.icon = "res://" + icon;
    }

    public Category(String title, String icon) {
        this.title = title;
        this.icon = icon;
    }
}
