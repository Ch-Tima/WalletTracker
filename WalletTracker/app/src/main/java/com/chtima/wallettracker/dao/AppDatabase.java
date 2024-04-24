package com.chtima.wallettracker.dao;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.chtima.wallettracker.models.Category;
import com.chtima.wallettracker.models.Transaction;
import com.chtima.wallettracker.models.User;

@Database(entities = {Category.class, User.class, Transaction.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CategoryDao categoryDao();
    public abstract TransactionDao transactionDao();
}
