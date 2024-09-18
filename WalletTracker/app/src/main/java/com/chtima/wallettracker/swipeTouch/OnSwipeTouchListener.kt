package com.chtima.wallettracker.swipeTouch

import android.annotation.SuppressLint
import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import java.lang.Math.*
import kotlin.math.abs


class OnSwipeTouchListener(context: Context, private val swipe: onSwipe) : View.OnTouchListener {

    companion object {
        private const val SWIPE_DISTANCE_THRESHOLD = 100
        private const val SWIPE_VELOCITY_THRESHOLD = 100
    }

    private val gestureDetector = GestureDetector(context, GestureListener())


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    fun onSwipeLeft() {
        swipe.onSwipeLeft()
    }

    fun onSwipeRight() {
        swipe.onSwipeRight()
    }


    private inner class GestureListener : GestureDetector.SimpleOnGestureListener(){

        override fun onDown(e: MotionEvent): Boolean {
            return true;
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val distanceX = e2.x - (e1?.x?:0f)
            val distanceY = e2.y - (e1?.y?:0f)

            if (abs(distanceX) > abs(distanceY) &&
                abs(distanceX) > SWIPE_DISTANCE_THRESHOLD &&
                abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {

                if (distanceX > 0) onSwipeRight() else onSwipeLeft()
                return true
            }
            return false
        }
    }

    interface onSwipe {
        fun onSwipeLeft()
        fun onSwipeRight()
    }

}