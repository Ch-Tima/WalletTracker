package com.chtima.wallettracker.domain

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.chtima.wallettracker.adapters.CategoryRecycleAdapter
import com.chtima.wallettracker.models.Category
import com.chtima.wallettracker.models.DialogObserver
import com.chtima.wallettracker.swipeTouch.OnSwipeTouchListener
import com.chtima.wallettracker.viewModels.CategoryViewModel

abstract class BaseSelectCategoryLogic constructor(
    protected val fragment: Fragment,
    protected val recyclerView: RecyclerView,
    protected val selectCategoryListener: DialogObserver<Category>?,
    protected val categoryType: Category.CategoryType?,
    protected val isShowSelectCategory: Boolean
) {

    protected open lateinit var adapter: CategoryRecycleAdapter
    protected open lateinit var categoryViewModel: CategoryViewModel
    protected open lateinit var onSwipeTouchListener: OnSwipeTouchListener

    public abstract fun setupUI();
}