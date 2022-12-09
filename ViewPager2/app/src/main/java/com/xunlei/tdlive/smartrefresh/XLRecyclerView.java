package com.xunlei.tdlive.smartrefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.xunlei.tdlive.pulltorefresh.LogTag;

/*
 *  @author      : xunlei
 *  @date        : 2022/12/9
 *  @desc        : 1.0
 */
public class XLRecyclerView extends RecyclerView {
    public XLRecyclerView(@NonNull Context context) {
        super(context);
    }

    public XLRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public XLRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int startX, startY;
    private int mTouchSlop;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mTouchSlop == 0) {
            ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
            mTouchSlop = viewConfiguration.getScaledTouchSlop();

            Log.e(LogTag.TAG_LIVE_SMOOTH, "dispatchTouchEvent touch event = " + mTouchSlop);
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) ev.getX();
                startY = (int) ev.getY();
                getParent().requestDisallowInterceptTouchEvent(true);

                Log.e(LogTag.TAG_LIVE_SMOOTH, "dispatchTouchEvent ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                int endX = (int) ev.getX();
                int endY = (int) ev.getY();
                int disX = Math.abs(endX - startX);
                int disY = Math.abs(endY - startY);
                LayoutManager layoutManager = getLayoutManager();
                boolean canScrollVertically = layoutManager != null && layoutManager.canScrollVertically();
                if (disY > mTouchSlop && canScrollVertically) {
                    getParent().requestDisallowInterceptTouchEvent(true);

                    Log.e(LogTag.TAG_LIVE_SMOOTH, "dispatchTouchEvent requestDisallowInterceptTouchEvent(true)");
                } else {

                    Log.e(LogTag.TAG_LIVE_SMOOTH, "dispatchTouchEvent requestDisallowInterceptTouchEvent("+canScrollHorizontally(startX - endX)+")");
                    getParent().requestDisallowInterceptTouchEvent(canScrollHorizontally(startX - endX));
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                Log.e(LogTag.TAG_LIVE_SMOOTH, "dispatchTouchEvent requestDisallowInterceptTouchEvent(false)");
                break;
        }

        return super.dispatchTouchEvent(ev);
    }

}
