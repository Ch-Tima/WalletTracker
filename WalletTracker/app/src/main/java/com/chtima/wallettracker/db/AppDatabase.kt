package com.chtima.wallettracker.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.chtima.wallettracker.R
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

         fun isExist(context: Context):Boolean{
             val checkDB:SQLiteDatabase?;

             try {
                 checkDB = SQLiteDatabase.openDatabase(context.getDatabasePath(DATABASE_NAME).getAbsolutePath(), null,
                     SQLiteDatabase.OPEN_READONLY);
                 checkDB.close();
             } catch (e: SQLiteException) {
                 return false
             }

             return checkDB != null
         }

         fun defaultCategories(context: Context): Array<Category> {
             return arrayOf(
                 Category("Paycheck", context.resources.getResourceName(R.drawable.payments_24dp), Category.CategoryType.INCOME),
                 Category("Gift", context.resources.getResourceName(R.drawable.gift_24dp), Category.CategoryType.INCOME),

                 Category("Clothes", context.resources.getResourceName(R.drawable.clothes_24dp), Category.CategoryType.EXPENSE),
                 Category("Health", context.resources.getResourceName(R.drawable.ecg_heart_24dp), Category.CategoryType.EXPENSE),
                 Category("Food", context.resources.getResourceName(R.drawable.food_24dp), Category.CategoryType.EXPENSE),
                 Category("Car", context.resources.getResourceName(R.drawable.car_24dp), Category.CategoryType.EXPENSE),
                 Category("Education", context.resources.getResourceName(R.drawable.school_24dp), Category.CategoryType.EXPENSE),
                 Category("Vacation", context.resources.getResourceName(R.drawable.surfing_24dp), Category.CategoryType.EXPENSE),

                 Category("Other", context.resources.getResourceName(R.drawable.help_24dp), Category.CategoryType.MIX)
             )
         }

     }


}