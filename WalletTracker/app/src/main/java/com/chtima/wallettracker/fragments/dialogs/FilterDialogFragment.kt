package com.chtima.wallettracker.fragments.dialogs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.chtima.wallettracker.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * A simple [Fragment] subclass.
 * Use the [FilterDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FilterDialogFragment : BottomSheetDialogFragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_filter_dialog, container, false)
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
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         */
        @JvmStatic
        fun newInstance() = FilterDialogFragment().apply {}
    }
}