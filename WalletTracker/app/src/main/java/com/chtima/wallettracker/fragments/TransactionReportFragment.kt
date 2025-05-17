package com.chtima.wallettracker.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ImageButton
import com.chtima.wallettracker.R
import com.chtima.wallettracker.fragments.dialogs.FilterDialogFragment
import com.chtima.wallettracker.fragments.simples.DisplayTransactionListFragment
import com.google.android.material.textfield.TextInputEditText

class TransactionReportFragment : Fragment() {

    private lateinit var filterBtn: ImageButton
    private lateinit var titleEditText : TextInputEditText
    private lateinit var displayTransactionListFragment: DisplayTransactionListFragment
    private lateinit var filterDialogFragment: FilterDialogFragment
    private var filterParams: FilterDialogFragment.FilterParams? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        filterDialogFragment = FilterDialogFragment.newInstance(
            { result ->
                setFilter(result)
            }, {
                setFilter(null)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_transaction_report, container, false)

        filterBtn = view.findViewById<ImageButton>(R.id.btn_filter)//-F* предется делать логику помещения данных в FilterDialogFragment
            /// 17.05.25  -F* ты очем  ?
        filterBtn.setOnClickListener {
            if (!filterDialogFragment.isAdded) {
                filterDialogFragment.show(childFragmentManager, "FilterDialog")
            } else {
                filterDialogFragment.dialog?.show()
            }
        }

        titleEditText = view.findViewById(R.id.title_edit)//TextInputEditText
        titleEditText.setOnEditorActionListener { v, actionId, keyEv ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                setFilter()
                displayTransactionListFragment.filter().byText(v.text.toString()).apply()
                true
            }
            false
        }

        return view
    }

    private fun setFilter(fp : FilterDialogFragment.FilterParams?){
        filterParams = fp
        setFilter()
    }

    private fun setFilter(){
        val f = this.displayTransactionListFragment.filter()
        f.clear().apply()
        val filterParams = this.filterParams
        filterParams?.let {
            filterParams.getListOfCategory().isNotEmpty().let { f.byCategory(filterParams.getListOfCategory()) }
            filterParams.getTransactionType()?.let { t -> f.byType(t) }
            if(filterParams.getDateStart()!=null && filterParams.getDateEnd()!=null)
                f.byDate(filterParams.getDateStart()!!, filterParams.getDateEnd()!!)
            f.apply()
        }?:run {
            f.clear().apply()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayTransactionListFragment = DisplayTransactionListFragment.newInstance()
        childFragmentManager.beginTransaction()
            .replace(R.id.display_transactions, displayTransactionListFragment)
            .commit()
    }

    companion object {
        fun newInstance() = TransactionReportFragment().apply {}
    }
}