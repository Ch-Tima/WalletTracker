package com.chtima.wallettracker.components;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.chtima.wallettracker.R;

public class ShadowConstraintLayout extends ConstraintLayout {
    public ShadowConstraintLayout(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ShadowConstraintLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ShadowConstraintLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public ShadowConstraintLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context){
        int i = 0;

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (getViewTreeObserver().isAlive())
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);

                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) getLayoutParams();
                params.setMargins(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));
                setBackgroundResource(R.drawable.round_layout);
                setElevation(dpToPx(5));
                setLayoutParams(params);
            }
        });

    }

    private int dpToPx(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

}
