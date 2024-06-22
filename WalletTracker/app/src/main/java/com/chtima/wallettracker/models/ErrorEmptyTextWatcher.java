package com.chtima.wallettracker.models;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputLayout;

public class ErrorEmptyTextWatcher implements TextWatcher {

    private final Context context;
    private final TextInputLayout textLayout;
    private final int resId;

    public ErrorEmptyTextWatcher(Context context, TextInputLayout textLayout, int resId) {
        this.context = context;
        this.textLayout = textLayout;
        this.resId = resId;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s){
        if(s.toString().trim().isEmpty())
            textLayout.setError(context.getString(resId));
        else
            textLayout.setError("");
    }
}
