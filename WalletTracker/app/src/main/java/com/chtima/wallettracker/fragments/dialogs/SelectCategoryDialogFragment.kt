package com.chtima.wallettracker.fragments.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.chtima.wallettracker.R
import com.chtima.wallettracker.domain.BaseSelectCategoryLogic
import com.chtima.wallettracker.domain.SelectCategoryGridLogic
import com.chtima.wallettracker.domain.SelectCategoryLogic
import com.chtima.wallettracker.models.Category
import com.chtima.wallettracker.models.Category.CategoryType
import com.chtima.wallettracker.models.DialogObserver
import com.chtima.wallettracker.models.DisplayType

class SelectCategoryDialogFragment constructor() : DialogFragment() {

    private var selectedLastCategoryListener:DialogObserver<Category>? = null
    private var selectedListCategoryListener:DialogObserver<List<Category>>? = null
    private var categoryType:CategoryType? = null
    private var isShowSelectCategory = false
    private lateinit var selectCategoryLogic: BaseSelectCategoryLogic
    private lateinit var displayType: DisplayType

    //UI
    private lateinit var recyclerView: RecyclerView

    companion object {
        private const val CATEGORY_TYPE = "CATEGORY_TYPE";
        private const val IS_SHOW_SELECTED_CATEGORY = "IS_SHOW_SELECTED_CATEGORY";
        private const val DISPLAY_LIST_TYPE = "DISPLAY_OF_LIST_TYPE"

        /**
         * Static factory method to create a new instance of SelectCategoryDialogFragment.
         * @return A new instance of SelectCategoryDialogFragment.
         */
        public fun newInstance(): SelectCategoryDialogFragment {
            return newInstance(null, false, DisplayType.LIST);
        }

        /**
         * Static factory method to create a new instance of SelectCategoryDialogFragment.
         * @param categoryType - use to filter and show only a specific type category
         * @return A new instance of SelectCategoryDialogFragment.
         * @param displayType Display layout type (GRID or LIST)
         */
        public fun newInstance(categoryType: CategoryType?, isShowSelectCategory: Boolean, displayType: DisplayType): SelectCategoryDialogFragment {
            val fragment = SelectCategoryDialogFragment()
            val bundle = Bundle()
            bundle.putString(CATEGORY_TYPE, categoryType?.name)
            bundle.putBoolean(IS_SHOW_SELECTED_CATEGORY, isShowSelectCategory)
            bundle.putString(DISPLAY_LIST_TYPE, displayType.name)
            fragment.arguments = bundle
            return fragment

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retrieve arguments passed to the fragment and initialize fields
        val ct = arguments?.getString(CATEGORY_TYPE)

        if(ct != null) categoryType = CategoryType.valueOf(ct)
        isShowSelectCategory = arguments?.getBoolean(IS_SHOW_SELECTED_CATEGORY, false) ?: false

        displayType = DisplayType.valueOf(arguments?.getString(DISPLAY_LIST_TYPE, DisplayType.LIST.name) ?: DisplayType.LIST.name)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_category_selection, container, false)
        // Initialize RecyclerView for showing categories
        recyclerView = v.findViewById(R.id.list_category)
        // Choose logic class based on selected display type (GRID or LIST)
        when(displayType){
            DisplayType.GRID -> {
                selectCategoryLogic = SelectCategoryGridLogic(
                    this,
                    recyclerView,
                    selectedLastCategoryListener,
                    selectedListCategoryListener,
                    categoryType,
                    isShowSelectCategory
                )
            }else ->{
                selectCategoryLogic = SelectCategoryLogic(
                    this,
                    recyclerView,
                    selectedLastCategoryListener,
                    null,
                    categoryType,
                    isShowSelectCategory
                )
            }
        }
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectCategoryLogic.setupUI()// Set up UI components and data binding
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
        this.selectedLastCategoryListener = selectCategoryListener
    }

    fun setSelectCategoryListListener(selectCategoriesListener : DialogObserver<List<Category>>){
        this.selectedListCategoryListener = selectCategoriesListener
    }

}