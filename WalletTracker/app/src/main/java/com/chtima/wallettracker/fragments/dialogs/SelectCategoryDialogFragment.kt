package com.chtima.wallettracker.fragments.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chtima.wallettracker.R
import com.chtima.wallettracker.adapters.CategoryRecycleAdapter
import com.chtima.wallettracker.models.Category
import com.chtima.wallettracker.models.Category.CategoryType
import com.chtima.wallettracker.models.DialogObserver
import com.chtima.wallettracker.viewModels.CategoryViewModel


class SelectCategoryDialogFragment private constructor() : DialogFragment() {

    private lateinit var adapter: CategoryRecycleAdapter
    private lateinit var categoryViewModel: CategoryViewModel
    private var selectCategoryListener:DialogObserver<Category>? = null
    private var categoryType:CategoryType? = null
    private var isShowSelectCategory = false

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
        val v = inflater.inflate(R.layout.fragment_select_category_dialog, container, false);

        adapter = CategoryRecycleAdapter(requireContext(), ArrayList(), isShowSelectCategory)
        recyclerView = v.findViewById(R.id.list_category)

        recyclerView.setAdapter(adapter)
        recyclerView.layoutManager = GridLayoutManager(this.requireContext(), 3)
        recyclerView.addItemDecoration(CategoryRecycleAdapter.GridSpacingItemDecoration (3,  Math.round(16 * this.resources.displayMetrics.density), true))

        //set click listener for RecyclerView items
        adapter.setOnClickListener(object : CategoryRecycleAdapter.OnClickListener{
            override fun onClick(category: Category) {
                if(selectCategoryListener != null) selectCategoryListener?.onSuccess(category)
                dialog!!.dismiss();//dismiss the dialog after category selection
            }
        })

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categoryViewModel = ViewModelProvider(requireActivity())[CategoryViewModel::class]
        categoryViewModel.getByType(this.categoryType).observe(this){
            adapter.updateList(it)
        }
    }

    override fun onStart() {
        super.onStart()
        //adjust dialog window attributes on dialog start
        if (dialog != null) {
            val window = dialog!!.window
            val params = window!!.attributes
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            window.setBackgroundDrawableResource(R.drawable.rounded_8dp_ashen35)
            params.verticalMargin = resources.getDimension(R.dimen.m8)
            params.horizontalMargin = resources.getDimension(R.dimen.m8)
            window.attributes = params
        }
    }

    /**
     * Setter method for setting the SelectCategoryListener.
     * @param selectCategoryListener The listener to be set.
     */
    fun setSelectCategoryListener(selectCategoryListener : DialogObserver<Category>){
        this.selectCategoryListener = selectCategoryListener;
    }

}