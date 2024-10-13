package com.chtima.wallettracker.models

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesKeys {

    const val SELECTED_USER_ID = "selected_user_id"

    private const val NAME = "com.chtima.wallettracker.sp"

    fun getSharedPreferences(application: Application) : SharedPreferences{
        return application.getSharedPreferences(NAME, Context.MODE_PRIVATE)
    }

    fun getSharedPreferences(context: Context) : SharedPreferences{
        return context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
    }

}