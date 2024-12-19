package com.chtima.wallettracker.components

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ViewOutlineProvider
import android.view.ViewTreeObserver
import androidx.annotation.AttrRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.chtima.wallettracker.R

class ShadowConstraintLayout @JvmOverloads constructor(
    context: Context,
    attrRes: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = R.style.ShadowConstraintLayoutStyle
) : ConstraintLayout(context, attrRes, defStyleAttr, defStyleRes) {

    init {
        getViewTreeObserver().addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                if (viewTreeObserver.isAlive)
                    viewTreeObserver.removeOnGlobalLayoutListener(this)

                val params = layoutParams as ConstraintLayout.LayoutParams;
                params.setMargins(dpToPx(16f), dpToPx(16f), dpToPx(16f), dpToPx(16f))
                elevation = dpToPx(10f).toFloat()
                outlineProvider = ViewOutlineProvider.BACKGROUND
                clipToOutline = true
                setLayoutParams(params)
            }
        })
    }

    fun dpToPx(dp: Float):Int{
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()
    }


}