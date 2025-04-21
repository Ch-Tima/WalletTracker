package com.chtima.wallettracker.domain

import android.annotation.SuppressLint
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chtima.wallettracker.adapters.CategoryRecycleAdapter
import com.chtima.wallettracker.models.Category
import com.chtima.wallettracker.models.DialogObserver
import com.chtima.wallettracker.swipeTouch.OnSwipeTouchListener
import com.chtima.wallettracker.viewModels.CategoryViewModel

class SelectCategoryGridLogic(
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

    @SuppressLint("ClickableViewAccessibility")
    override fun setupUI() {
        adapter = CategoryRecycleAdapter(fragment.requireContext(), ArrayList(), isShowSelectCategory)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(fragment.requireContext(), 3) // 3 col
        recyclerView.addItemDecoration(
            CategoryRecycleAdapter.GridSpacingItemDecoration(
                3, // span count
                24, // spacing in pixels
                true // include edge spacing
            )
        )
        adapter.setOnClickListener(object : CategoryRecycleAdapter.OnClickListener{
            override fun onClick(category: Category) {
                selectedListCategoryListener?.onSuccess(adapter.getSelectedCategories())
            }
        })
        //just processing swipe left/right
        onSwipeTouchListener = OnSwipeTouchListener(fragment.requireContext(), object : OnSwipeTouchListener.onSwipe {
            override fun onSwipeLeft() {
                adapter.nextPage()
            }
            override fun onSwipeRight() {
                adapter.previousPage()
            }
        })

        recyclerView.setOnTouchListener(onSwipeTouchListener)
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.overScrollMode = View.OVER_SCROLL_NEVER
        categoryViewModel = ViewModelProvider(fragment.requireActivity())[CategoryViewModel::class.java]
        categoryViewModel.getByType(this.categoryType).observe(fragment) {
            adapter.updateList(it)
        }
    }

}