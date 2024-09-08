package com.chtima.wallettracker.watchers

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputLayout

/**
 * Constructor to initialize the ErrorEmptyTextWatcher.
 * @param context     Context for accessing resources and application-specific data.
 * @param textInputLayout  The TextInputLayout that will show the error message.
 * @param resId       Resource ID of the error message string to be shown when the text field is empty.
 */
class ErrorEmptyTextWatcher(private var context: Context,
                            private var textInputLayout: TextInputLayout,
                            private var resId: Int) : TextWatcher {

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable?) {
        if(s.toString().trim().isEmpty()) // Trim the input text and check if it's empty.
            textInputLayout.error = context.getText(resId);
        // Clear the error message if the text is not empty.
        else textInputLayout.error = "";
    }
}