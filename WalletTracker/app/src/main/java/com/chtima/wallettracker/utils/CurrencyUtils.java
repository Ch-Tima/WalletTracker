package com.chtima.wallettracker.utils;

import android.content.Context;

import com.chtima.wallettracker.R;

public class CurrencyUtils {

    private CurrencyUtils(){};

    public static String getCurrencyChar(String currencyCode, Context context){
        if(currencyCode == null || context == null)
            return "$";

        String resName = String.format("currency_%s", currencyCode.toLowerCase());
        int resId = context.getResources().getIdentifier(resName, "string", context.getPackageName());

        if(resId == -1){
            return context.getString(R.string.currency_usd);
        }

        return context.getString(resId);
    }

}
