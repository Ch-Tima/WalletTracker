package com.chtima.wallettracker.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import com.chtima.wallettracker.R
import com.chtima.wallettracker.fragments.simples.DisplayTransactionListFragment
import com.chtima.wallettracker.viewModels.CategoryViewModel

class TransactionReportFragment : Fragment() {

    private lateinit var filterBtn: ImageButton
    private lateinit var displayTransactionListFragment: DisplayTransactionListFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_transaction_report, container, false);

        filterBtn = view.findViewById<ImageButton>(R.id.btn_filter)
        filterBtn.setOnClickListener {
            filterBtn.imageTintList = requireContext().getColorStateList(R.color.light_slate_blue)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayTransactionListFragment = DisplayTransactionListFragment.newInstance();
        childFragmentManager.beginTransaction()
            .replace(R.id.display_transactions, displayTransactionListFragment)
            .commit()
    }

    companion object {
        fun newInstance() = TransactionReportFragment().apply {}
    }
}