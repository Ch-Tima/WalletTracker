package com.chtima.wallettracker.dao;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.chtima.wallettracker.R;
import com.chtima.wallettracker.models.Category;
import com.chtima.wallettracker.models.Transaction;
import com.chtima.wallettracker.models.TransactionType;
import com.chtima.wallettracker.models.User;
import com.chtima.wallettracker.vm.CategoryViewModel;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

@Database(entities = {Category.class, User.class, Transaction.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CategoryDao categoryDao();
    public abstract TransactionDao transactionDao();
    public abstract UserDao userDao();

    private static final String DATABASE_NAME = "wallettracker.db";
    private static final Object LOCK = new Object();
    private static AppDatabase instance;

    public static AppDatabase getInstance(Context context){

        if(instance != null) return AppDatabase.instance;

        synchronized (LOCK){

            if(instance != null) return AppDatabase.instance;

            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class,
                            DATABASE_NAME)
                    .build();

            return instance;
        }

    }

    public static Category[] defaultCategories(Context context) {
        return new Category[]{
                new Category("Paycheck", context.getResources().getResourceName(R.drawable.payments_24px), Category.CategoryType.INCOME),
                new Category("Gift",  context.getResources().getResourceName(R.drawable.gift_24px), Category.CategoryType.INCOME),

                new Category("Clothes", context.getResources().getResourceName(R.drawable.clothes_24px), Category.CategoryType.EXPENSE),
                new Category("Health", context.getResources().getResourceName(R.drawable.ecg_heart_24px), Category.CategoryType.EXPENSE),
                new Category("Food", context.getResources().getResourceName(R.drawable.food_24px), Category.CategoryType.EXPENSE),
                new Category("Car", context.getResources().getResourceName(R.drawable.car_24px), Category.CategoryType.EXPENSE),
                new Category("Education", context.getResources().getResourceName(R.drawable.school_24px), Category.CategoryType.EXPENSE),
                new Category("Vacation", context.getResources().getResourceName(R.drawable.surfing_24px), Category.CategoryType.EXPENSE),

                new Category("Other", context.getResources().getResourceName(R.drawable.help_24px), Category.CategoryType.MIX)

        };
    }

}
