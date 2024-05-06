package com.chtima.wallettracker.dao;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.chtima.wallettracker.models.Category;
import com.chtima.wallettracker.models.Transaction;
import com.chtima.wallettracker.models.User;

@Database(entities = {Category.class, User.class, Transaction.class}, version = 2)
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

            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class,
                            DATABASE_NAME)
                    .createFromAsset("database/wallettracker.db",
                            new PrepackagedDatabaseCallback() {
                                @Override
                                public void onOpenPrepackagedDatabase(@NonNull SupportSQLiteDatabase db) {
                                    super.onOpenPrepackagedDatabase(db);
                                }
                            })
                    .addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build();

            return instance;
        }

    }

    public interface OnCreateListener{
        void onCreated();
    }

}
