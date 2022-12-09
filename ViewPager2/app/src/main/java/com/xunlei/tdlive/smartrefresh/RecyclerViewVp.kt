package com.xunlei.tdlive.smartrefresh

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.recyclerview.widget.RecyclerView
import com.xunlei.tdlive.pulltorefresh.LogTag
import kotlin.math.abs


class RecyclerViewVp(context: Context, attrs: AttributeSet) : RecyclerView(context, attrs) {
    private var startX: Float = 0f
    private var startY: Float = 0f
    private var mTouchSlop = 0

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {

        if (mTouchSlop == 0) {
            val viewConfiguration = ViewConfiguration.get(context)
            mTouchSlop = viewConfiguration.scaledTouchSlop
        }

        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = ev.x
                startY = ev.y
                parent.requestDisallowInterceptTouchEvent(true)
                Log.d(
                    LogTag.TAG_LIVE_SMOOTH,
                    "${this.javaClass.simpleName} $this MotionEvent.ACTION_DOWN "
                )
            }
            MotionEvent.ACTION_MOVE -> {
                Log.d(
                    LogTag.TAG_LIVE_SMOOTH,
                    "${this.javaClass.simpleName} $this MotionEvent.ACTION_MOVE "
                )
                val endX = ev.x.toInt()
                val endY = ev.y.toInt()
                val disX = abs(endX - startX).toInt()
                val disY = abs(endY - startY).toInt()
                if (disX < disY) {
                    //如果是纵向滑动，告知父布局不进行拦截，交由子布局消费，　requestDisallowInterceptTouchEvent(true)
                    parent.requestDisallowInterceptTouchEvent(canScrollVertically((startY - endY).toInt()))
                } else {
                    parent.requestDisallowInterceptTouchEvent(false)
                }

               /* val layoutManager = layoutManager
                val canScrollVertically = layoutManager != null && layoutManager.canScrollVertically()
                if (disY > mTouchSlop && canScrollVertically) {
                    parent.requestDisallowInterceptTouchEvent(true)
                } else {
                    parent.requestDisallowInterceptTouchEvent(canScrollHorizontally((startX - endX).toInt()))
                }*/
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                Log.d(LogTag.TAG_LIVE_SMOOTH, "${this.javaClass.simpleName} $this " + ev.action)
                parent.requestDisallowInterceptTouchEvent(false)


            }
        }
        return super.dispatchTouchEvent(ev)
    }
}