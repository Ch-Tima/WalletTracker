package com.chtima.wallettracker.utils

import android.content.Context
import androidx.annotation.StringDef


object CurrencyUtils{

    /**
     * Returns the currency symbol based on the provided currency code.
     *
     * @param currencyCode The currency code (e.g., "USD", "EUR"). It is expected to be in uppercase.
     * @param context The context used to access the resources.
     * @return The corresponding currency symbol. If the currency code or context is null, or the symbol cannot be found, a default value <em>'$'</em> is returned.
     */
    @JvmStatic
    fun getCurrencyChar(@CurrencyCode currencyCode:String, context: Context):String{

        val resName:String = String.format("currency_%s", currencyCode.lowercase())
        val resId: Int = context.resources.getIdentifier(resName, "string", context.packageName)

        if(resId < 1) return "$"

        return context.getString(resId);
    }

    @StringDef("EUR", "USD", "PLN", "RUB", "UAH")
    @Retention(AnnotationRetention.SOURCE)
    annotation class CurrencyCode

}