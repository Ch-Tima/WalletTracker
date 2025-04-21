package com.chtima.wallettracker.fragments.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.chtima.wallettracker.R
import com.chtima.wallettracker.models.Category
import com.chtima.wallettracker.models.DialogObserver
import com.chtima.wallettracker.models.DisplayType
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * FilterDialogFragment is a BottomSheetDialogFragment that displays
 * a category filter UI using a nested fragment (SelectCategoryDialogFragment).
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
        val v = inflater.inflate(R.layout.fragment_filter_dialog, container, false)
        //creating a "Select Category DialogFragment" as a grid with multiple selections
        val selectCategoryDF = SelectCategoryDialogFragment.newInstance(null, true, DisplayType.GRID)
        selectCategoryDF.setSelectCategoryListListener(object: DialogObserver<List<Category>>{
            override fun onSuccess(result: List<Category>) {
                //!here we get the list of selected categories!
            }
        })
        childFragmentManager.beginTransaction()
            .replace(R.id.category_fragment, selectCategoryDF)
            .commit()
        return v
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
}