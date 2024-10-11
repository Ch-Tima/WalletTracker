package com.chtima.wallettracker.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.net.toFile
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.chtima.wallettracker.R
import com.chtima.wallettracker.db.dao.CategoryDao
import com.chtima.wallettracker.db.dao.TransactionDao
import com.chtima.wallettracker.db.dao.UserDao
import com.chtima.wallettracker.models.Category
import com.chtima.wallettracker.models.Transaction
import com.chtima.wallettracker.models.User
import com.google.protobuf.Internal.BooleanList
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

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
                  ).build().also { appDatabase: AppDatabase ->
                      instance = appDatabase
                  }
              }
         }

        fun getInstance(context: Context, targetUri: Uri): Boolean {

            try {
                val newDBFile = File(context.getDatabasePath(DATABASE_NAME).absolutePath)
                if (newDBFile.exists()) {
                    newDBFile.delete()
                }

                context.contentResolver.openInputStream(targetUri).use { inputStream ->
                    FileOutputStream(newDBFile).use { outputStream ->
                        inputStream?.copyTo(outputStream)
                    }
                }

                getInstance(context)

                return true

            }catch (e: Exception){
                return false
            }
        }

         fun isExist(context: Context):Boolean{
             return try {
                 context.getDatabasePath(DATABASE_NAME).exists()
             } catch (e: SQLiteException) {
                 false
             }
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

         fun backupDatabase(context: Context, uri: Uri) {
             getInstance(context).backupDatabaseToUri(context, uri)
         }

        fun delete(context: Context) {
            val newDBFile = File(context.getDatabasePath(DATABASE_NAME).absolutePath)
            if (newDBFile.exists()) {
                newDBFile.delete()
            }
        }

    }

    private fun backupDatabaseToUri(context: Context, targetUri: Uri){
        val dbFile = context.getDatabasePath(DATABASE_NAME)

        if (!dbFile.exists()) {
            throw SQLiteException("Database $DATABASE_NAME does not exist.")
        }

        try {
            context.contentResolver.openOutputStream(targetUri).use { out ->
                FileInputStream(dbFile).use { inp ->
                    inp.copyTo(out!!)
                }
            }
        }catch (e: IOException){
            Log.e("WTF", e.toString())
        }catch (e: Exception){
            Log.e("WTF", e.toString())
        }

    }


}