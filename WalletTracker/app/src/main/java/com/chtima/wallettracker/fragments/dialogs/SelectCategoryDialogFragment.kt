package com.chtima.wallettracker.fragments.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.chtima.wallettracker.R
import com.chtima.wallettracker.domain.SelectCategoryLogic
import com.chtima.wallettracker.models.Category
import com.chtima.wallettracker.models.Category.CategoryType
import com.chtima.wallettracker.models.DialogObserver


class SelectCategoryDialogFragment constructor() : DialogFragment() {

    private var selectCategoryListener:DialogObserver<Category>? = null
    private var categoryType:CategoryType? = null
    private var isShowSelectCategory = false
    private lateinit var selectCategoryLogic: SelectCategoryLogic

    //UI
    private lateinit var recyclerView: RecyclerView

    companion object {
        private const val CATEGORY_TYPE = "CATEGORY_TYPE";
        private const val IS_SHOW_SELECTED_CATEGORY = "IS_SHOW_SELECTED_CATEGORY";

        /**
         * Static factory method to create a new instance of SelectCategoryDialogFragment.
         * @return A new instance of SelectCategoryDialogFragment.
         */
        public fun newInstance(): SelectCategoryDialogFragment {
            return newInstance(null, false);
        }

        /**
         * Static factory method to create a new instance of SelectCategoryDialogFragment.
         * @param categoryType - use to filter and show only a specific type category
         * @return A new instance of SelectCategoryDialogFragment.
         */
        public fun newInstance(categoryType: CategoryType?, isShowSelectCategory: Boolean): SelectCategoryDialogFragment {
            val fragment = SelectCategoryDialogFragment()
            val bundle = Bundle()
            bundle.putString(CATEGORY_TYPE, categoryType?.name)
            bundle.putBoolean(IS_SHOW_SELECTED_CATEGORY, isShowSelectCategory)
            fragment.arguments = bundle
            return fragment

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ct = arguments?.getString(CATEGORY_TYPE)

        if(ct != null) categoryType = CategoryType.valueOf(ct)
        isShowSelectCategory = arguments?.getBoolean(IS_SHOW_SELECTED_CATEGORY, false) ?: false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_category_selection, container, false)

        recyclerView = v.findViewById(R.id.list_category)

        selectCategoryLogic = SelectCategoryLogic(
            this,
            recyclerView,
            selectCategoryListener,
            categoryType,
            isShowSelectCategory
        )

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectCategoryLogic.setupUI()
    }

    override fun onStart() {
        super.onStart()
        //adjust dialog window attributes on dialog start
        if (dialog == null || dialog?.window == null) return

        val window = dialog!!.window
        val params: WindowManager.LayoutParams = window!!.attributes
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        window.attributes = params
    }

    /**
     * Setter method for setting the SelectCategoryListener.
     * @param selectCategoryListener The listener to be set.
     */
    fun setSelectCategoryListener(selectCategoryListener : DialogObserver<Category>){
        this.selectCategoryListener = selectCategoryListener;
    }

}