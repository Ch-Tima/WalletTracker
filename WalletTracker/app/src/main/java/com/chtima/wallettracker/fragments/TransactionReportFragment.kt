package com.chtima.wallettracker.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.chtima.wallettracker.R

class TransactionReportFragment : Fragment() {

    private lateinit var filterBtn: ImageButton

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

        return view;
    }

    companion object {
        fun newInstance() = TransactionReportFragment().apply {}
    }
}