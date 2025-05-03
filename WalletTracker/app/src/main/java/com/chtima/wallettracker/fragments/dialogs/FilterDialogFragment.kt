package com.chtima.wallettracker.fragments.dialogs

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.chtima.wallettracker.R
import com.chtima.wallettracker.components.SwitchTransactionView
import com.chtima.wallettracker.models.Category
import com.chtima.wallettracker.models.DialogObserver
import com.chtima.wallettracker.models.DisplayType
import com.chtima.wallettracker.models.Transaction
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * FilterDialogFragment is a BottomSheetDialogFragment that displays
 * a category filter UI using a nested fragment (SelectCategoryDialogFragment).
 */
class FilterDialogFragment : BottomSheetDialogFragment() {

    private lateinit var dateRangePickerTextView: TextView
    private lateinit var sendСhanges : (f: FilterParams) -> Unit
    private lateinit var sendClear : () -> Unit
    private var filterParams: FilterParams = FilterParams()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_filter_dialog, container, false)
        //creating a "Select Category DialogFragment" as a grid with multiple selections
        val selectCategoryDF = SelectCategoryDialogFragment.newInstance(null, true, DisplayType.GRID)
        selectCategoryDF.setSelectCategoryListListener(object: DialogObserver<List<Category>>{
            override fun onSuccess(result: List<Category>) {
               filterParams.setListOfCategory(result)
            }
        })
        childFragmentManager.beginTransaction()
            .replace(R.id.category_fragment, selectCategoryDF)
            .commit()
        //
        dateRangePickerTextView = v.findViewById<TextView>(R.id.text_date)
        //Get SwitchTransactionView and subscription to wiretapping
        val switchTransactionView = v.findViewById<SwitchTransactionView>(R.id.swicher_transaction_type)
        switchTransactionView.addSwitchTransactionListener(object: SwitchTransactionView.SwitchTransactionListener{
            override fun onChangedSelection(type: Transaction.TransactionType) {
                filterParams.setTransactionType(type)
            }
        })
        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
        dateRangePicker.setTitleText("Select dates")
        //
        val constraintsBuilder = CalendarConstraints.Builder()
        dateRangePicker.setCalendarConstraints(constraintsBuilder.build())
        //
        val picker = dateRangePicker.build()
        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        picker.addOnPositiveButtonClickListener { selection ->
            filterParams.setDate(Date(selection.first), Date(selection.second))
            dateRangePickerTextView.text ="${sdf.format(Date(selection.first))}-${sdf.format(Date(selection.second))}"
        }
        //
        v.findViewById<ImageButton>(R.id.btn_date_picker).setOnClickListener({ _ ->
            picker.show((requireContext() as AppCompatActivity).supportFragmentManager, picker.toString())
        })
        //Button to clear filters
        v.findViewById<Button>(R.id.btn_clear).setOnClickListener({_ ->
            sendClear()
            filterParams = FilterParams()
        })
        //Button to accept filters & close
        v.findViewById<Button>(R.id.btn_done).setOnClickListener({_ ->
            sendСhanges(filterParams)
            this.dismiss()
        })

        return v
    }

    public fun setOnChangedListener(filter: (fp: FilterParams?) -> Unit, clear: () -> Unit){
        this.sendСhanges = filter
        this.sendClear = clear
    }

    override fun onStart() {
        super.onStart()
        //adjust dialog window attributes on dialog start
        val window = dialog?.window?:return
        val params: WindowManager.LayoutParams = window.attributes;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.attributes = params;
    }

    companion object {
        /**
         * Factory method to create a new instance of FilterDialogFragment.
         */
        @JvmStatic
        fun newInstance() = FilterDialogFragment().apply {}
    }

    public class FilterParams(){
        private var listOfCategory : List<Category> = ArrayList<Category>()
        private var transactionType : Transaction.TransactionType? = null
        private var dateStart : Date? = null
        private var dateEnd : Date? = null

        fun getListOfCategory(): List<Category> {
            return listOfCategory
        }

        fun setListOfCategory(listOfCategory: List<Category>) {
            this.listOfCategory = listOfCategory
        }

        fun getTransactionType(): Transaction.TransactionType? {
            return transactionType
        }

        fun setTransactionType(transactionType: Transaction.TransactionType?) {
            this.transactionType = transactionType
        }

        fun getDateStart(): Date? {
            return dateStart
        }

        fun setDate(dateStart: Date?, dateEnd: Date?) {
            this.dateStart = dateStart
            this.dateEnd = dateEnd
        }

        fun getDateEnd(): Date? {
            return dateEnd
        }

    }
}