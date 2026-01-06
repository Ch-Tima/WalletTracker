package com.chtima.wallettracker.domain

import android.annotation.SuppressLint
import android.view.View
import android.widget.LinearLayout
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

    private lateinit var pageIndicator: LinearLayout

    @SuppressLint("ClickableViewAccessibility")
    override fun setupUI() {
        adapter = CategoryRecycleAdapter(fragment.requireContext(), ArrayList(), isShowSelectCategory)
        adapter.setShowAll(false)
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
        this.pageIndicator = fragment.requireView().findViewById<LinearLayout>(R.id.page_indicator)


        val pageString = fragment.requireContext().getText(R.string.tag_page).toString()
        //just processing swipe left/right
        onSwipeTouchListener = OnSwipeTouchListener(fragment.requireContext(), object : OnSwipeTouchListener.onSwipe {
            override fun onSwipeLeft() {
                if(adapter.getCurrentPage() < adapter.getCountPages()){
                    adapter.nextPage()
                    setParamForBigDot(pageIndicator.getChildAt(adapter.getCurrentPage()))
                    setParamForDot(pageIndicator.getChildAt(adapter.getCurrentPage()-1))
                }
            }
            override fun onSwipeRight() {
                if(adapter.getCurrentPage() >= 0){
                    adapter.previousPage()
                    setParamForBigDot(pageIndicator.getChildAt(adapter.getCurrentPage()))
                    setParamForDot(pageIndicator.getChildAt(adapter.getCurrentPage()+1))
                }
            }
        })

        recyclerView.setOnTouchListener(onSwipeTouchListener)
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.overScrollMode = View.OVER_SCROLL_NEVER
        categoryViewModel = ViewModelProvider(fragment.requireActivity())[CategoryViewModel::class.java]
        categoryViewModel.getByType(this.categoryType).observe(fragment) {
            adapter.updateList(it)
            pageIndicator.visibility = View.VISIBLE
            pageIndicator.removeAllViews()
            for (i in 0..<adapter.getCountPages()){
                val dot = View(fragment.requireContext())
                setParamForDot(dot)
                pageIndicator.addView(dot)
            }
            setParamForBigDot(pageIndicator.getChildAt(0))
        }
    }

    private fun setParamForDot(v: View) {
        v.setBackgroundResource(R.drawable.dot_background)
        val params = LinearLayout.LayoutParams(24, 24)
        params.marginEnd = 16
        params.bottomMargin = 0
        v.background.setTint(fragment.requireContext().getColor(R.color.silver_sand))
        v.layoutParams =  params
    }
    private fun setParamForBigDot(v: View) {
        v.setBackgroundResource(R.drawable.dot_background)
        val params = LinearLayout.LayoutParams(28, 28)
        params.marginEnd = 16
        params.bottomMargin = 0
        v.background.setTint(fragment.requireContext().getColor(R.color.light_slate_blue))
        v.layoutParams =  params
    }

}