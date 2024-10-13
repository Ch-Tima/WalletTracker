package com.chtima.wallettracker.db

import android.content.Context
import android.database.sqlite.SQLiteException
import android.net.Uri
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
import com.google.rpc.Code
import net.sqlcipher.database.SupportFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

@Database(entities = [Category::class, Transaction::class, User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun userDao(): UserDao

    companion object{
        private const val DATABASE_NAME: String = "wallet-tracker.db" // Database name
        private val LOCK: Any = Object() // Synchronization lock to ensure a single instance of the database
        private var instance: AppDatabase? = null // Singleton instance of the database

        /**
         * Retrieves an instance of the encrypted database using SQLCipher.
         * This function uses a synchronized block to ensure thread-safety during initialization.
         * @param context The application context.
         * @return An instance of the AppDatabase.
        */
         fun getInstance(context: Context) : AppDatabase {
              return instance ?: synchronized(LOCK) {
                  instance ?: Room.databaseBuilder(
                      context.applicationContext,
                      AppDatabase::class.java,
                      DATABASE_NAME
                  ).openHelperFactory(getFactory(context))// Using SQLCipher for encryption
                      .build() .also { appDatabase: AppDatabase -> instance = appDatabase }
              }
         }

        /**
         * Restores the database from a backup located at the given `Uri`.
         * @param context The application context.
         * @param targetUri The `Uri` where the backup is located.
         * @return A `ResultAction` containing the result of the restore operation.
         */
        fun restoreDatabase(context: Context, targetUri: Uri): ResultAction {

            return try {
                val newDBFile = File(context.getDatabasePath(DATABASE_NAME).absolutePath)
                if (newDBFile.exists()) {
                    newDBFile.delete()
                }

                context.contentResolver.openInputStream(targetUri).use { inputStream ->
                    FileOutputStream(newDBFile).use { outputStream ->
                        inputStream?.copyTo(outputStream)
                    }
                }

                getInstance(context)// Ensure database gets loaded after restoration
                ResultAction("Success", "Database restored successfully", code = Code.OK)
            }catch (e: Exception){
                ResultAction("Error", message = e.toString(), ex = e, code = Code.UNKNOWN)
            }
        }

        /**
         * Provides a `SupportFactory` to use SQLCipher encryption for the database.
         * @param context The application context.
         * @return A `SupportFactory` initialized with an encryption key.
         */
        private fun getFactory(context: Context) : SupportFactory{
            //AndroidKeyStore
            val passphraseBytes = net.sqlcipher.database.SQLiteDatabase.getBytes("20eW8UjSosUok1hPf84bn1rSkxTwVyAL4EL2p1916vIjmRA5J6".toCharArray())
            return SupportFactory(passphraseBytes)
        }

        /**
         * Checks if the database file exists.
         * @param context The application context.
         * @return `true` if the database exists, `false` otherwise.
         */
        fun isExist(context: Context):Boolean{
             return try {
                 context.getDatabasePath(DATABASE_NAME).exists()
             } catch (e: SQLiteException) {
                 false
             }
         }

        /**
         * Returns a list of default categories to be used in the application.
         * @param context The application context.
         * @return An array of `Category` objects.
         */
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

        /**
         * Backs up the database to the provided URI.
         * @param context The application context.
         * @param uri The `Uri` where the backup should be saved.
         * @return A `ResultAction` containing the result of the backup operation.
         */
         fun backupDatabase(context: Context, uri: Uri) : ResultAction {
             return getInstance(context).backupDatabaseToUri(context, uri)
         }

        /**
         * Deletes the current database file if it exists.
         * @param context The application context.
         */
        fun delete(context: Context) {
            val newDBFile = File(context.getDatabasePath(DATABASE_NAME).absolutePath)
            if (newDBFile.exists()) {
                newDBFile.delete()
            }
        }

    }

    /**
     * Internal function to perform the backup operation. It writes the main database file
     * and associated WAL and SHM files to the target `Uri`.
     * @param context The application context.
     * @param targetUri The `Uri` where the backup should be written.
     * @return A `ResultAction` containing the result of the backup operation.
     */
    private fun backupDatabaseToUri(context: Context, targetUri: Uri) : ResultAction{
        // Force writing data from the WAL log to disk
        getInstance(context).openHelper.writableDatabase.query("PRAGMA wal_checkpoint(FULL);")

        val dbFile = context.getDatabasePath(DATABASE_NAME)
        val walFile = File(dbFile.absolutePath + "-wal")
        val shmFile = File(dbFile.absolutePath + "-shm")

        if (!dbFile.exists()) {
            return ResultAction(
                title = "Error",
                message = "Database $DATABASE_NAME does not exist.",
            )
        }

        try {
            context.contentResolver.openOutputStream(targetUri).use { out ->
                FileInputStream(dbFile).use { inp ->
                    inp.copyTo(out!!)
                }

                if (walFile.exists())
                    FileInputStream(walFile).use { inp -> inp.copyTo(out!!) }

                if (shmFile.exists())
                    FileInputStream(shmFile).use { inp -> inp.copyTo(out!!) }

                return ResultAction("Success", message = "Database backup created", code = Code.OK)
            }
        }catch (e: IOException){
            return ResultAction(title = "Error", message = e.message ?: "Unknown error occurred", ex = e)
        }
    }

    /**
     * Helper class to store the result of operations, including success or failure details.
     * @param title The title of the result (e.g., "Error", "Success").
     * @param message A message explaining the result.
     * @param ex Optional exception, if any occurred.
     * @param code Status code representing the result.
     */
    class ResultAction(val title: String,
                       val message: String,
                       val ex: Exception? = null,
                       val code: Code = Code.UNKNOWN){
        /**
         * Returns a string representation of the `ResultAction`.
         *
         * @return A string combining the title, message, exception (if any), and code.
         */
        override fun toString(): String {
            return "ResultAction(title='$title', message='$message', exception=${ex?.message}, code=$code)"
        }
    }

}