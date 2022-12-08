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
    private boolean forbidHorizontalScroll = false;

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

    public void setForbidHorizontalScroll(boolean forbidHorizontalScroll) {
        this.forbidHorizontalScroll = forbidHorizontalScroll;
    }

    public PullToRefreshRecyclerView2(Context context, Mode mode) {
        super(context, mode);
        init();
    }

    public PullToRefreshRecyclerView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private int getViewPagerOrientation(Object object) {
        if (object != null) {
            try {
               Method method = object.getClass().getMethod("getOrientation");
                method.setAccessible(true);
                return (int) method.invoke(object);
            } catch (Throwable e) {
                e.printStackTrace();
                android.util.Log.e(LogTag.TAG_LIVE_SMOOTH, "updateUserInputFiledValue error " + e.getMessage());
            }
        }
        return RecyclerView.HORIZONTAL;
    }

    private Boolean canChildScroll(int orientation, float delta) {
        int direction = (int)  Math.signum(-delta);
        android.view.View child = getChild();
        if (child == null) {
            return false;
        }
        switch (orientation) {
            case RecyclerView.HORIZONTAL:
                return child.canScrollHorizontally(direction);
            case RecyclerView.VERTICAL:
                return child.canScrollVertically(direction);
            default:
                return false;
        }
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

    public ViewParent getParent2() {
        return getViewPager2();
    }

    private boolean handleInterceptTouchEvent(MotionEvent e) {
        ViewParent parent = getParent2();
        if (parent == null) {
            return false;
        }
        int orientation = getViewPagerOrientation(parent);
        // Early return if child can't scroll in same direction as parent
        if (!canChildScroll(orientation, -1f) && !canChildScroll(orientation, 1f)) {
            android.util.Log.e(LogTag.TAG_LIVE_SMOOTH, "handleInterceptTouchEvent return false");
            return false;
        }
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            initialX = e.getX();
            initialY = e.getY();
            parent.requestDisallowInterceptTouchEvent(true);
            return false;
        } else if (e.getAction() == MotionEvent.ACTION_MOVE) {
            int dx = (int) (e.getX() - initialX);
            int dy = (int) (e.getY() - initialY);
            boolean isVpHorizontal = (orientation == RecyclerView.HORIZONTAL);

            // assuming ViewPager2 touch-slop is 2x touch-slop of child
            float scaledDx = Math.abs(dx) * (isVpHorizontal ? .5f : 1.0f);
            float scaledDy = Math.abs(dy) * (isVpHorizontal ? 1f : .5f);

            if ((scaledDx > touchSlop || scaledDy > touchSlop)) {
                if (isVpHorizontal == (scaledDy > scaledDx)) {
                    // Gesture is perpendicular, allow all parents to intercept
                    //如果viewPager2是横向滑动但手势是竖直方向滑动，则允许所有父类拦截
                    parent.requestDisallowInterceptTouchEvent(false);
                    android.util.Log.e(LogTag.TAG_LIVE_SMOOTH, "垂直 requestDisallowInterceptTouchEvent(false)");
                } else {
                    // Gesture is parallel, query child if movement in that direction is possible
                    if (forbidHorizontalScroll) {
                        //全部禁止vp2滑动
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                    else if (canChildScroll(orientation, isVpHorizontal ? dx : dy)) {
                        // Child can scroll, disallow all parents to intercept
                        //手势滑动方向和viewPage2是同方向的，需要询问子RecyclerView是否在同方向能滑动
                        //子RecyclerView能滑动直接禁止父view拦截事件
                        parent.requestDisallowInterceptTouchEvent(true);
                        android.util.Log.e(LogTag.TAG_LIVE_SMOOTH, "水平 requestDisallowInterceptTouchEvent(true)");
                    } else {
                        // Child cannot scroll, allow all parents to intercept
                        //子RecyclerView不能滑动(划到第一个Item还往右滑或者划到最后面一个Item还往左划的时候)允许父view拦截
                        parent.requestDisallowInterceptTouchEvent(false);
                        android.util.Log.e(LogTag.TAG_LIVE_SMOOTH, "水平 requestDisallowInterceptTouchEvent(false)");
                    }
                }
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent e)  {
//         handleInterceptTouchEvent(e);
        return super.onInterceptTouchEvent(e);
    }
}
