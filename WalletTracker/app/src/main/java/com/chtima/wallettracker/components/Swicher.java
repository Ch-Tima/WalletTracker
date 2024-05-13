package com.chtima.wallettracker.components;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chtima.wallettracker.R;

public class Swicher extends ViewGroup {

    private View thumb;
    private TextView income;
    private TextView expanse;
    private boolean isChecked = false;

    public Swicher(Context context) {
        super(context);
        init();
    }

    public Swicher(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Swicher(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public Swicher(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        // parent size
        int parentWidth = right - left;
        int parentHeight = bottom - top;

        // size
        int thumbWidth = parentWidth / 2;
        int thumbHeight = (int) (parentHeight * 0.85);

        //Place your thumb in the vertical center and horizontally along the left edge.
        int thumbLeft = (int)((parentWidth - thumbWidth) * 0.05);
        int thumbRight = thumbLeft + thumbWidth;

        int thumbTop = (parentHeight - thumbHeight) / 2;
        int thumbBottom = thumbTop + thumbHeight;

        thumb.layout(thumbLeft, thumbTop, thumbRight, thumbBottom);


        //INCOME

        // size
        int incomeWidth = parentWidth / 2;
        int incomeHeight = parentHeight / 2;

        //Place your thumb in the vertical center and horizontally along the left edge.
        int incomeLeft = (int)((parentWidth - incomeWidth) * 0.1);
        int incomeRight = (int)((incomeLeft + incomeWidth) * 0.9);

        int incomeTop = (parentHeight - incomeHeight) / 2;
        int incomeBottom = incomeTop + incomeHeight;

        income.layout(incomeLeft, incomeTop, incomeRight, incomeBottom);


        //EXPANSE

        // size
        int expanseWidth = parentWidth / 2;
        int expanseHeight = parentHeight / 2;

        //Place your thumb in the vertical center and horizontally along the left edge.
        int expanseLeft = (int)((parentWidth - expanseWidth) * 0.95);
        int expanseRight = (int)((expanseLeft + expanseWidth));

        int expanseTop = (parentHeight - expanseHeight) / 2;
        int expanseBottom = expanseTop + expanseHeight;

        expanse.layout(expanseLeft, expanseTop, expanseRight, expanseBottom);

    }

    private void init(){
        this.setBackgroundResource(R.drawable.swicher_background);

        thumb = new View(getContext());
        thumb.setBackgroundResource(R.drawable.swicher_thumb);
        addView(thumb);

        income = new TextView(getContext());
        income.setText(R.string.income);
        income.setTextColor(getResources().getColor(R.color.white, null));
        income.setGravity(Gravity.CENTER);
        addView(income);

        expanse = new TextView(getContext());
        expanse.setText(R.string.expanse);
        expanse.setTextColor(getResources().getColor(R.color.white, null));
        expanse.setGravity(Gravity.CENTER);
        addView(expanse);

        this.setOnClickListener(x -> {
            int parentWidth = getWidth();
            int thumbWidth = parentWidth / 2;

            int startX = 0;
            int endX = 0;

            //Place your thumb in the vertical center and horizontally along the left edge.
            if(isChecked){
                startX = thumb.getLeft();
                endX = (int)((getWidth() - thumb.getWidth()) * 0.05);
            }else {
                startX = thumb.getLeft();
                endX = (int )((getWidth() - thumb.getWidth()) * 0.95);
            }

            ValueAnimator animator = ValueAnimator.ofInt(startX, endX);
            animator.setDuration(250);
            animator.setInterpolator(new AccelerateInterpolator());
            animator.addUpdateListener(animation -> {
                int value = (int) animation.getAnimatedValue();
                thumb.layout(value, thumb.getTop(), value + thumb.getWidth(), thumb.getBottom());
            });

            animator.start();

            isChecked = !isChecked;
        });

    }


}
