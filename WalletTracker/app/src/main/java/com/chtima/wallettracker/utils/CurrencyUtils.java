package com.chtima.wallettracker.utils;

import android.content.Context;
import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class CurrencyUtils {

    // Private constructor to prevent instantiation of this utility class
    private CurrencyUtils(){};

    /**
     * Returns the currency symbol based on the provided currency code.
     *
     * @param currencyCode The currency code (e.g., "USD", "EUR"). It is expected to be in uppercase.
     * @param context The context used to access the resources.
     * @return The corresponding currency symbol. If the currency code or context is null, or the symbol cannot be found, a default value <em>'$'</em> is returned.
     */
    public static String getCurrencyChar(@CurrencyCode String currencyCode, Context context){
        if(currencyCode == null || context == null)
            return "$";

        String resName = String.format("currency_%s", currencyCode.toLowerCase());
        int resId = context.getResources().getIdentifier(resName, "string", context.getPackageName());

        if(resId == -1){
            return "$";
        }

        return context.getString(resId);
    }

    @StringDef({"EUR", "USD", "PLN", "RUB", "UAH"})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CurrencyCode {}

}
