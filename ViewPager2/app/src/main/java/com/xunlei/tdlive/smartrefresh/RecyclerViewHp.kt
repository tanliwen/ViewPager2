package com.xunlei.tdlive.smartrefresh

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import com.xunlei.tdlive.pulltorefresh.LogTag
import kotlin.math.abs


class RecyclerViewHp(context: Context, attrs: AttributeSet) : RecyclerView(context, attrs) {
    private var startX: Float = 0f
    private var startY: Float = 0f

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = ev.x
                startY = ev.y
                parent.requestDisallowInterceptTouchEvent(true)
                Log.d(
                    LogTag.TAG_LIVE_SMOOTH,
                    "${this.javaClass.simpleName} MotionEvent.ACTION_DOWN "
                )
            }
            MotionEvent.ACTION_MOVE -> {
                val endX = ev.x.toInt()
                val endY = ev.y.toInt()
                val disX = abs(endX - startX).toInt()
                val disY = abs(endY - startY).toInt()
                if (disX > disY) {
//                分类页tab滑动容易切换到其他页面,故屏蔽-21.4.26
//                    parent.requestDisallowInterceptTouchEvent(canScrollHorizontally((startX - endX).toInt()))
                    parent.requestDisallowInterceptTouchEvent(true)
                    Log.e(
                        LogTag.TAG_LIVE_SMOOTH,
                        "${this.javaClass.simpleName} MotionEvent.ACTION_MOVE true"
                    )
                } else {
                    parent.requestDisallowInterceptTouchEvent(false)
                    Log.d(
                        LogTag.TAG_LIVE_SMOOTH,
                        "${this.javaClass.simpleName} MotionEvent.ACTION_MOVE false"
                    )
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                Log.d(LogTag.TAG_LIVE_SMOOTH, "${this.javaClass.simpleName} " + ev.action)
                parent.requestDisallowInterceptTouchEvent(false)
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}