package com.chtima.wallettracker.fragments.dialogs

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.chtima.wallettracker.R
import com.chtima.wallettracker.components.SwitchTransactionView
import com.chtima.wallettracker.models.Category
import com.chtima.wallettracker.models.DialogObserver
import com.chtima.wallettracker.models.SharedPreferencesKeys
import com.chtima.wallettracker.models.Transaction
import com.chtima.wallettracker.watchers.ErrorEmptyTextWatcher
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputLayout
import java.lang.Double.parseDouble
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


/** BottomSheetDialogFragment for adding a new transaction. */
class AddTransactionDialogFragment constructor(): BottomSheetDialogFragment() {

    private var dialogObservers : MutableList<DialogObserver<Transaction>> = ArrayList()
    private lateinit var dateFromPicker: Date
    private lateinit var datePickerDialog: DatePickerDialog
    private var selectedCategory: Category? = null

    //UI
    private lateinit var selectCategoryBtn: Button
    private lateinit var dataBtn: Button

    private lateinit var title: EditText
    private lateinit var titleLayout: TextInputLayout
    private lateinit var note: EditText
    private lateinit var noteLayout: TextInputLayout
    private lateinit var sum: EditText
    private lateinit var sumLayout: TextInputLayout
    private lateinit var type: SwitchTransactionView

    companion object {
        fun newInstance():AddTransactionDialogFragment{
            return AddTransactionDialogFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val v = inflater.inflate(R.layout.fragment_add_transaction_dialog, container, false);

        //dismiss dialog on cancel
        dialog!!.setOnCancelListener { _: DialogInterface? ->
            dialogObservers.forEach{it.onCancel()}
            dismiss()
        }

        //handle click on add button
        v.findViewById<Button>(R.id.add_button).setOnClickListener {
            val transaction = makeTransaction()

            transaction?:return@setOnClickListener

            dialogObservers.forEach{ it.onSuccess(transaction) }
            dismiss()
        }

        //initialize select category button and set click listener
        selectCategoryBtn = v.findViewById<Button>(R.id.select_category_btn)
        selectCategoryBtn.setOnClickListener{ _ -> selectCategory()}

        //initialize date picker dialog
        val c : Calendar = Calendar.getInstance()
        dateFromPicker = c.time
        datePickerDialog = DatePickerDialog(requireContext(), { view, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            dateFromPicker = calendar.time
            dataBtn.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))

        //initialize UI elements
        title = v.findViewById(R.id.title_transaction)
        titleLayout = v.findViewById(R.id.title_transaction_layout)
        title.addTextChangedListener(ErrorEmptyTextWatcher(requireContext(), titleLayout, R.string.please_enter_title))

        note = v.findViewById(R.id.note_transaction)
        noteLayout = v.findViewById(R.id.note_transaction_layout)

        sum = v.findViewById(R.id.sum_transaction);
        sumLayout = v.findViewById(R.id.sum_transaction_layout)
        sum.addTextChangedListener(ErrorEmptyTextWatcher(requireContext(), sumLayout, R.string.please_enter_sum))

        type = v.findViewById(R.id.type_transaction)
        dataBtn = v.findViewById(R.id.date_picker_transaction)

        //set date picker dialog listener on date button click
        dataBtn.setOnClickListener{_ -> datePickerDialog.show()}
        dataBtn.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(System.currentTimeMillis())

        return v

    }

    override fun onStart() {
        super.onStart()
        //adjust dialog window attributes on dialog start
        val window = dialog?.window?:return
        val params: WindowManager.LayoutParams = window.attributes;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
    }

    /**
     * Set the DialogObserver to subscribe to transaction events.
     * @param observer The observer to set.
     */
    fun addDialogObserver(observer: DialogObserver<Transaction>){
        this.dialogObservers.add(observer)
    }

    /** Launch SelectCategoryDialogFragment to allow user to select a category.*/
    private fun selectCategory(){
        val dialogFragment = SelectCategoryDialogFragment.newInstance()
        dialogFragment.setSelectCategoryListener(object : DialogObserver<Category>{
            override fun onSuccess(result: Category) {
                selectedCategory = result
                selectCategoryBtn.text = result.title
            }

            override fun onCancel() {}

        })
        dialogFragment.show(this.getChildFragmentManager(), SelectCategoryDialogFragment::class.simpleName)
    }

    /**
     * Create a Transaction object from user input.
     * @return The created Transaction object, or null if input is invalid.
     */
    private fun makeTransaction():Transaction?{
        //check availability of the selected category
        if(selectedCategory == null){
            Toast.makeText(requireContext(), R.string.please_select_a_category, Toast.LENGTH_SHORT).show()
            selectCategory()
            return null
        }

        var err:Boolean = false
        var dSum: Double = 0.0

        //validate and retrieve title
        if (title.getText().toString().trim().isEmpty()){
            titleLayout.setError(getString(R.string.please_enter_title));
            err = true;
        }

        //validate and retrieve sum
        if (sum.getText().toString().trim().isEmpty()){
            sumLayout.setError(getString(R.string.please_enter_sum));
            err = true;
        }else {
            try {
                dSum = parseDouble(sum.getText().toString());
            }catch (e:Exception){
                sumLayout.setError(getString(R.string.wrong_format));
                err = true;
            }
        }

        if(err) {
            //handle error state (e.g., vibrate)
            return null;
        }

        val userID = SharedPreferencesKeys.getSharedPreferences(requireContext()).getLong(SharedPreferencesKeys.SELECTED_USER_ID, -1)

        if(userID == -1L){
            Toast.makeText(requireContext(), "Who you?", Toast.LENGTH_LONG).show()
            return null
        }

        //create and return Transaction object
        return Transaction(selectedCategory!!.id,
            userID,
            dSum,
            this.title.text.toString(),
            note.getText().toString(),
            dateFromPicker,
            type.getTransactionType())
    }

}