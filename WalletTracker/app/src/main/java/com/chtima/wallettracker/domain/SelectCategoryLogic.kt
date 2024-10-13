package com.chtima.wallettracker.domain

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chtima.wallettracker.R
import com.chtima.wallettracker.adapters.CategoryRecycleAdapter
import com.chtima.wallettracker.models.Category
import com.chtima.wallettracker.models.DialogObserver
import com.chtima.wallettracker.swipeTouch.OnSwipeTouchListener
import com.chtima.wallettracker.viewModels.CategoryViewModel
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class SelectCategoryLogic(
    private val fragment: Fragment,
    private val recyclerView: RecyclerView,
    private val selectCategoryListener: DialogObserver<Category>?,
    private val categoryType: Category.CategoryType?,
    private val isShowSelectCategory: Boolean
){

    private lateinit var adapter: CategoryRecycleAdapter
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var onSwipeTouchListener: OnSwipeTouchListener

    fun setupUI() {
        adapter = CategoryRecycleAdapter(fragment.requireContext(), ArrayList(), isShowSelectCategory)

        recyclerView.setAdapter(adapter)
        recyclerView.layoutManager = FlexboxLayoutManager(fragment.requireContext()).apply {
            justifyContent = JustifyContent.CENTER
            alignItems = AlignItems.FLEX_START
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
        }
        recyclerView.addItemDecoration(CategoryRecycleAdapter.FlexboxItemDecoration(16))
        //set click listener for RecyclerView items
        adapter.setOnClickListener(object : CategoryRecycleAdapter.OnClickListener{
            override fun onClick(category: Category) {
                selectCategoryListener?.onSuccess(category)
                if (fragment is DialogFragment) {
                    fragment.dialog?.dismiss()
                }
            }
        })

        onSwipeTouchListener = OnSwipeTouchListener(fragment.requireContext(), object: OnSwipeTouchListener.onSwipe{
            override fun onSwipeLeft() {
                adapter.nextPage()
            }

            override fun onSwipeRight() {
                adapter.previousPage()
            }
        })

        categoryViewModel = ViewModelProvider(fragment.requireActivity())[CategoryViewModel::class]
        categoryViewModel.getByType(this.categoryType).observe(fragment){
            adapter.updateList(it)
        }

    }

}