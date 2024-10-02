package com.chtima.wallettracker.fragments.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.IntDef
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.chtima.wallettracker.R
import com.chtima.wallettracker.models.Category
import com.chtima.wallettracker.models.DialogObserver
import com.chtima.wallettracker.models.Transaction
import com.chtima.wallettracker.models.User
import com.chtima.wallettracker.viewModels.TransactionViewModel
import com.chtima.wallettracker.viewModels.UserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.DecimalFormat
import java.util.Calendar


class TopUpBottomDialogFragment private constructor (): BottomSheetDialogFragment() {

    private lateinit var user: User

    private var onDoneListener: DialogObserver<Transaction>? = null

    //VM
    private lateinit var userVM: UserViewModel
    private lateinit var transactionVM: TransactionViewModel

    //UI
    private lateinit var textView: TextView
    private lateinit var btnDot: Button
    private lateinit var btnDone: Button
    private lateinit var title: TextView

    private var visibilityTitle = View.VISIBLE

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_top_up_bottom_dialog, container, false)

        title = v.findViewById(R.id.title)
        title.visibility = this.visibilityTitle

        val numpad: GridLayout = v.findViewById(R.id.numpad_grid)

        // Initialize numpad buttons and set click listeners
        for (i in 0..<numpad.childCount) {
            val numpadBtn = numpad.getChildAt(i)
            if (numpadBtn.tag != null && numpadBtn.tag.toString() == getString(R.string.number_btn)) {
                val btn = (numpadBtn as Button)
                btn.setOnClickListener{ _ ->
                    putNumber(btn.getText().toString())
                }
            }
        }

        // Initialize input fields
        textView = v.findViewById(R.id.number_text)//input fields
        textView.addTextChangedListener(afterTextChanged = {
            btnDone.setEnabled(it.toString().isNotEmpty() && it!![it.length - 1] != '.' && toDouble(it.toString()) > 0.009)
        })


        // Initialize 'Dot' button for adding decimal point
        btnDot = v.findViewById(R.id.btn_dot)
        btnDot.setOnClickListener {
            if(!textView.getText().toString().contains(".") && textView.text.isNotEmpty())
                textView.text = ((textView.text.toString().plus( ".")))
        }

        // Initialize 'Clear' button for removing the last character
        v.findViewById<ImageButton>(R.id.btn_clear).setOnClickListener {
            val text = StringBuilder(textView.getText().toString())
            if (text.toString().isEmpty()) return@setOnClickListener

            text.deleteCharAt(text.length - 1)

            if (text.indexOf(".") != -1) {
                textView.text = text
                return@setOnClickListener
            }

            textView.text = toFormat(text.toString())
        }

        // Initialize 'Done' button for finalizing the transaction
        btnDone = v.findViewById(R.id.btn_done)
        btnDone.setEnabled(false)

        btnDone.setOnClickListener{// Show category selection dialog
            val fragment = CategoryAndDetailsDialogFragment.newInstance()

            fragment.setListener(object : CategoryAndDetailsDialogFragment.OnCategoryAndDetailsDialogListener{
                override fun onDone(category: Category, title: String, note: String) {

                    val transaction = Transaction(
                        category.id, user.id,
                        toDouble(textView.getText().toString()),
                        title, note,
                        Calendar.getInstance().time,
                        Transaction.TransactionType.INCOME)

                    transactionVM.insert(transaction)
                        .flatMapCompletable {
                            user.addToBalance(transaction.sum)
                            userVM.update(user)
                        }.subscribe()

                    onDoneListener?.onSuccess(transaction)
                    dismiss() // Close
                }

            })

            fragment.show(childFragmentManager, null)
        }

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userVM = ViewModelProvider(requireActivity())[UserViewModel::class]
        userVM.getUser().observe(this) { user = it }
        transactionVM = ViewModelProvider(requireActivity())[TransactionViewModel::class]
    }

    companion object{
        fun newInstance():TopUpBottomDialogFragment{
            return TopUpBottomDialogFragment()
        }
    }

    /**
     * Appends the clicked number to the input field, handling formatting and decimal points.
     * @param i The number to append.
     */
    private fun putNumber(i: String) {
        val text = StringBuilder(textView.text.toString())

        if (text.isEmpty() && i == "0") {
            textView.text = text.append("0.")
            return
        }

        val indexDot = text.indexOf(".")
        if (indexDot != -1 && text.length < 14 && text.length - indexDot < 3) {
            textView.text = text.append(i).toString()
            return
        }

        if (text.length < 12 && indexDot == -1) {
            textView.text = toFormat(text.append(i).toString())
        }
    }

    /**
     * Formats the input string to include thousand separators.
     * @param s The input string.
     * @return A formatted string with thousand separators.
     */
    private fun toFormat(s: String): String {
        if (s.isBlank()) return ""
        val text: java.lang.StringBuilder = java.lang.StringBuilder(s)

        var indexForDelete: Int
        while ((text.indexOf(",").also { indexForDelete = it }) != -1)
            text.deleteCharAt(indexForDelete)

        return DecimalFormat("#,###").format(text.toString().toInt())
    }

    /**
     * Converts the input string to a double, removing thousand separators.
     * @param s The input string.
     * @return The double representation of the input string.
     */
    private fun toDouble(s: String): Double {
        if (s.isBlank()) return 0.0

        val text = java.lang.StringBuilder(s)

        var indexForDelete: Int
        while ((text.indexOf(",").also { indexForDelete = it }) != -1)
            text.deleteCharAt(indexForDelete)

        return text.toString().toDouble()
    }

    /**
     * Sets a listener to be invoked when a transaction is completed.
     *
     * @param onDoneListener The listener to notify when the transaction is done.
     * This listener should handle the completion of the transaction.
     */
    fun setOnDoneListener(onDoneListener: DialogObserver<Transaction>) {
        this.onDoneListener = onDoneListener
    }

    /**
     * Sets the visibility of the title view.
     *
     * @param visibilityTitle The visibility status for the title view.
     *                        It should be one of the following values: {@link View#VISIBLE}, {@link View#INVISIBLE}, or {@link View#GONE}.
     */
    fun setVisibilityTitle(@Visibility visibilityTitle: Int) {
        this.visibilityTitle = visibilityTitle
        title.visibility = this.visibilityTitle
    }

    /**
     * Annotation to restrict the allowable values for the visibility parameter.
     * This ensures that only [View.VISIBLE], [View.INVISIBLE], or [View.GONE]
     * can be passed to methods that accept visibility parameters.
     *
     * @hide This annotation is internal and should not be part of the public API documentation.
     */
    @IntDef(View.VISIBLE, View.INVISIBLE, View.GONE)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Visibility

}