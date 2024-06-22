package com.chtima.wallettracker.models;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputLayout;

/**
 * ErrorEmptyTextWatcher is a custom implementation of the TextWatcher interface.
 * It sets an error message on a TextInputLayout if the associated text field is empty.
 */
public class ErrorEmptyTextWatcher implements TextWatcher {

    // Context to access resources and application-specific assets
    private final Context context;
    // TextInputLayout to display the error message on
    private final TextInputLayout textLayout;
    // Resource ID of the error message to be shown when the text field is empty
    private final int resId;

    /**
     * Constructor to initialize the ErrorEmptyTextWatcher.
     * @param context     Context for accessing resources and application-specific data.
     * @param textLayout  The TextInputLayout that will show the error message.
     * @param resId       Resource ID of the error message string to be shown when the text field is empty.
     */
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
        if(s.toString().trim().isEmpty())//Trim the input text and check if it's empty.
            textLayout.setError(context.getString(resId));//If the text is empty, set an error message on the TextInputLayout.
        else
            textLayout.setError("");//Clear the error message if the text is not empty.
    }
}
