package com.chtima.wallettracker.components

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.TextView
import com.chtima.wallettracker.R
import com.chtima.wallettracker.models.Transaction.TransactionType


class SwitchTransactionView @JvmOverloads constructor (
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    private var isChecked: Boolean = false
    private var thumb: View
    private var income: TextView
    private var expanse: TextView

    private val listeners : MutableList<SwitchTransactionListener> = ArrayList()

    init {
        this.setBackgroundResource(R.drawable.swicher_background);

        thumb = View(getContext());
        thumb.setBackgroundResource(R.drawable.swicher_thumb);
        addView(thumb);

        income = TextView(getContext());
        income.setTypeface(context.resources.getFont(R.font.outfit_medium));
        income.setText(R.string.expanse);
        income.setTextColor(context.getColor(R.color.white));
        income.setGravity(Gravity.CENTER);
        addView(income);

        expanse = TextView(getContext());
        expanse.setText(R.string.income);
        expanse.setTypeface(context.resources.getFont(R.font.outfit_medium));
        expanse.setTextColor(context.getColor(R.color.white));
        expanse.setGravity(Gravity.CENTER);
        addView(expanse);

        this.setOnClickListener{ swap() }
    }

    private fun swap(){
        var startX = 0;
        var endX = 0;

        //Place your thumb in the vertical center and horizontally along the left edge.
        if (isChecked) {
            startX = thumb.left
            endX = ((width - thumb.width) * 0.05).toInt()
        } else {
            startX = thumb.left;
            endX = ((width - thumb.width) * 0.95).toInt()
        }

        val animator : ValueAnimator = ValueAnimator.ofInt(startX, endX);
        animator.setDuration(250);
        animator.interpolator = AccelerateInterpolator();
        animator.addUpdateListener { animation ->
            val value = animation.getAnimatedValue() as Int;
            thumb.layout(value, thumb.top, value + thumb.width, thumb.bottom);
        };

        animator.start();

        isChecked = !isChecked;
        listeners.forEach { f -> f.onChangedSelection(getTransactionType())}
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        // parent size
        val parentWidth = right - left
        val parentHeight = bottom - top

        // size
        val thumbWidth = parentWidth / 2
        val thumbHeight = (parentHeight * 0.85).toInt()

        //Place your thumb in the vertical center and horizontally along the left edge.
        val thumbLeft = (((parentWidth - thumbWidth) * (if(isChecked) 0.95 else 0.05))).toInt()
        val thumbRight = thumbLeft + thumbWidth

        val thumbTop = (parentHeight - thumbHeight) / 2
        val thumbBottom = thumbTop + thumbHeight

        thumb.layout(thumbLeft, thumbTop, thumbRight, thumbBottom)

        //income
        val incomeWidth = parentWidth / 2
        val incomeHeight = parentHeight / 2

        //Place your thumb in the vertical center and horizontally along the left edge.
        val incomeLeft = ((parentWidth - incomeWidth) * 0.1).toInt()
        val incomeRight = ((incomeLeft + incomeWidth) * 0.9).toInt()

        val incomeTop = (parentHeight - incomeHeight) / 2;
        val incomeBottom = incomeTop + incomeHeight;

        income.layout(incomeLeft, incomeTop, incomeRight, incomeBottom);

        //expanse
        val expanseWidth = parentWidth / 2
        val expanseHeight = parentHeight / 2

        //Place your thumb in the vertical center and horizontally along the left edge.
        val expanseLeft = ((parentWidth - expanseWidth) * 0.95).toInt()
        val expanseRight = ((expanseLeft + expanseWidth))

        val expanseTop = (parentHeight - expanseHeight) / 2;
        val expanseBottom = expanseTop + expanseHeight

        expanse.layout(expanseLeft, expanseTop, expanseRight, expanseBottom);
    }

    fun getTransactionType() : TransactionType{
        return if(isChecked) TransactionType.INCOME else TransactionType.EXPENSE
    }

    fun addSwitchTransactionListener(s : SwitchTransactionListener){
        listeners.add(s)
    }

    interface SwitchTransactionListener {
        fun onChangedSelection(type: TransactionType)
    }
}