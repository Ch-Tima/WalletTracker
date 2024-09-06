package com.chtima.wallettracker.db

import android.R
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.chtima.wallettracker.db.dao.CategoryDao
import com.chtima.wallettracker.db.dao.TransactionDao
import com.chtima.wallettracker.db.dao.UserDao
import com.chtima.wallettracker.models.Category
import com.chtima.wallettracker.models.Transaction
import com.chtima.wallettracker.models.User


@Database(entities = [Category::class, Transaction::class, User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun userDao(): UserDao

     companion object{
         private const val DATABASE_NAME: String = "wallet-tracker.db"
         private val LOCK: Any = Object()
         private var instance: AppDatabase? = null

          fun getInstance(context: Context) : AppDatabase {
              return instance ?: synchronized(LOCK) {
                  instance ?: Room.databaseBuilder(
                      context.applicationContext,
                      AppDatabase::class.java,
                      DATABASE_NAME
                  ).build().also { appDatabase: AppDatabase -> instance = appDatabase }
              }
         }

     }



}