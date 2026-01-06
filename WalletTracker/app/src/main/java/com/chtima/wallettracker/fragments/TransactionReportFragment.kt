package com.chtima.wallettracker.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ImageButton
import android.widget.Toast
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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_transaction_report, container, false)

        filterBtn = view.findViewById<ImageButton>(R.id.btn_filter)
        filterBtn.setOnClickListener {
            val existing = childFragmentManager.findFragmentByTag("FilterDialog") as? FilterDialogFragment
            if (existing == null || !existing.isVisible) {
                val dialog = FilterDialogFragment.newInstance()
                dialog.setOnChangedListener({result ->
                    Toast.makeText(requireContext(), "OK", Toast.LENGTH_SHORT).show()
                    setFilter(result)
                }, {
                    Toast.makeText(requireContext(), "Clear", Toast.LENGTH_SHORT).show()
                    setFilter(null)
                })
                dialog.show(childFragmentManager, "FilterDialog")
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
            filterParams.getTransactionType()?.let { f.byType(it) }
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
        filterDialogFragment = FilterDialogFragment.newInstance()

        childFragmentManager.beginTransaction()
            .replace(R.id.display_transactions, displayTransactionListFragment)
            .commit()
    }

    companion object {
        fun newInstance() = TransactionReportFragment().apply {}
    }
}