package com.xunlei.tdlive.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewParent;


import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Method;

public class PullToRefreshRecyclerView2 extends PullToRefreshRecyclerView {

    private int touchSlop = 0;
    private float initialX = 0f;
    private float initialY = 0f;
    private ViewParent cacheViewPager2;
    private static Class clazz;

    private android.view.View child;

    static {
        try {
            clazz = Class.forName("androidx.viewpager2.widget.ViewPager2");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public android.view.View getChild() {
        if (child == null) {
            child = recyclerView;
        }
        return child;
    }

    public void init() {
        touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }


    public PullToRefreshRecyclerView2(Context context) {
        super(context);
        init();
    }


    public PullToRefreshRecyclerView2(Context context, Mode mode) {
        super(context, mode);
        init();
    }

    public PullToRefreshRecyclerView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public Object getViewPager2(ViewParent parent) {
        try {
            if (parent != null && parent.getClass().isAssignableFrom(clazz)) {
                android.util.Log.e(LogTag.TAG_LIVE_SMOOTH, "parent = " + parent.getClass());
                return parent;
            } else if (parent != null && parent.getParent() != null) {
                return getViewPager2(parent.getParent());
            } else {
                android.util.Log.e(LogTag.TAG_LIVE_SMOOTH, "parent = null");
                return null;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            android.util.Log.e(LogTag.TAG_LIVE_SMOOTH, "getViewPager2 error " + e.getMessage());
        }
        return null;
    }

    public ViewParent getViewPager2() {
        if (cacheViewPager2 == null) {
            Object result = getViewPager2(getParent());
            if (result instanceof ViewParent) {
                cacheViewPager2 = (ViewParent) result;
            }
        }
        return cacheViewPager2;
    }
}
