package com.chtima.wallettracker.fragments.dialogs

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.chtima.wallettracker.R
import com.chtima.wallettracker.components.SwitchTransactionView
import com.chtima.wallettracker.models.Category
import com.chtima.wallettracker.models.DialogObserver
import com.chtima.wallettracker.models.DisplayType
import com.chtima.wallettracker.models.Transaction
import com.chtima.wallettracker.viewModels.TransactionReportViewModel
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


    private lateinit var sendСhanges : (f: FilterParams) -> Unit
    private lateinit var sendClear : () -> Unit
    private lateinit var filterParams: FilterParams
    private lateinit var transactionReportVM : TransactionReportViewModel
    private lateinit var selectCategoryDF: SelectCategoryDialogFragment

    //UI
    private lateinit var switchTransactionView: SwitchTransactionView
    private lateinit var dateRangePickerTextView: TextView
    private lateinit var btnDatePicker: ImageButton
    private lateinit var btnClear: Button
    private lateinit var btnDone: Button


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

        //Get SwitchTransactionView
        switchTransactionView = v.findViewById<SwitchTransactionView>(R.id.swicher_transaction_type)
        //DateTextView
        dateRangePickerTextView = v.findViewById<TextView>(R.id.text_date)
        //DatePicker
        btnDatePicker = v.findViewById<ImageButton>(R.id.btn_date_picker);
        //Button to clear filters
        btnClear = v.findViewById<Button>(R.id.btn_clear)
        //Button to accept filters & close
        btnDone = v.findViewById<Button>(R.id.btn_done)

        return v
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        transactionReportVM = ViewModelProvider(requireActivity())[TransactionReportViewModel::class]
        filterParams = transactionReportVM.getFilter() ?: FilterParams()

        //creating a "Select Category DialogFragment" as a grid with multiple selections
        selectCategoryDF = SelectCategoryDialogFragment.newInstance(null, true, DisplayType.GRID)
        childFragmentManager.beginTransaction()
            .replace(R.id.category_fragment, selectCategoryDF)
            .commit()

        //SwitchTransactionView subscription to wiretapping
        filterParams.getTransactionType()?.let {
            switchTransactionView.setSelectedType(it)
        }
        switchTransactionView.addSwitchTransactionListener(object: SwitchTransactionView.SwitchTransactionListener{
            override fun onChangedSelection(type: Transaction.TransactionType) {
                filterParams.setTransactionType(type)
            }
        })

        //

        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
        dateRangePicker.setTitleText("Select dates")
        //
        val constraintsBuilder = CalendarConstraints.Builder()
        dateRangePicker.setCalendarConstraints(constraintsBuilder.build())
        //
        val picker = dateRangePicker.build()
        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        filterParams.getDateStart()?.let { s ->
            filterParams.getDateEnd()?.let { e ->
                dateRangePickerTextView.text ="${sdf.format(s)}-${sdf.format(e)}"
            }
        }
        picker.addOnPositiveButtonClickListener { selection ->
            filterParams.setDate(Date(selection.first), Date(selection.second))
            dateRangePickerTextView.text ="${sdf.format(Date(selection.first))}-${sdf.format(Date(selection.second))}"
        }
        btnDatePicker.setOnClickListener({ _ ->
            picker.show((requireContext() as AppCompatActivity).supportFragmentManager, picker.toString())
        })

        btnClear.setOnClickListener{_ ->
            sendClear()
            dismiss()
        }

        btnDone.setOnClickListener{_ ->
            sendСhanges(filterParams)
            this.dialog?.hide()
        }

        this.isCancelable = false

    }

    override fun onCancel(dialog: DialogInterface) {
        //super.onCancel(dialog)
        this.dialog?.hide()
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
        fun newInstance(onFilterChanged: (FilterParams?) -> Unit,
                        onFilterCleared: () -> Unit) : FilterDialogFragment{
            return FilterDialogFragment().apply {
                setOnChangedListener(onFilterChanged, onFilterCleared)
            }
        }
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