package com.chtima.wallettracker.domain

import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
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

/**
 * This class handles the logic for displaying categories in a flexible list (using FlexboxLayoutManager).
 * It connects the UI with the ViewModel and sets up click interactions and item decorations.
 */
class SelectCategoryLogic(
    f: Fragment,
    rv: RecyclerView,
    scl: DialogObserver<Category>?,
    selectedListCategoryListener: DialogObserver<List<Category>>?,
    cType: Category.CategoryType?,
    isShowSelect: Boolean
) : BaseSelectCategoryLogic(fragment = f,
    recyclerView = rv,
    selectCategoryListener = scl,
    categoryType = cType,
    isShowSelectCategory = isShowSelect,
    selectedListCategoryListener = selectedListCategoryListener) {

    override fun setupUI() {
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

        categoryViewModel = ViewModelProvider(fragment.requireActivity())[CategoryViewModel::class]
        categoryViewModel.getByType(this.categoryType).observe(fragment){
            adapter.updateList(it)
        }

        recyclerView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        recyclerView.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
    }

}