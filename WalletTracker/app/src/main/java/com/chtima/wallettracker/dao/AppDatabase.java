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
                    //.fallbackToDestructiveMigration()
                    .build();

            return instance;
        }

    }

    public static Category[] defaultCategories() {
        return new Category[]{
                new Category("Gift", R.drawable.gift_24px),
                new Category("Clothes", R.drawable.clothes_24px),
                new Category("Fitness", R.drawable.fitness_24px),
                new Category("Food", R.drawable.food_24px),
                new Category("Car", R.drawable.car_24px)
        };
    }

}
