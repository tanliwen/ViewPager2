package com.xunlei.tdlive.smartrefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;

import androidx.recyclerview.widget.RecyclerView;

import com.xunlei.tdlive.pulltorefresh.LogTag;

import java.lang.reflect.Field;

public class XLSmartRefreshLayout extends SmartRefreshLayoutWrapper {

    public XLSmartRefreshLayout(Context context) {
        super(context);
        init();
    }

    public XLSmartRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        Object view = getViewPager2(this);
        if (view != null && view.getClass().isAssignableFrom(clazz)) {
            changeSlop((ViewParent) view);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        return super.dispatchTouchEvent(e);
    }

    private static Class clazz;

    static {
        try {
            clazz = Class.forName("androidx.viewpager2.widget.ViewPager2");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    public static void changeSlop(ViewParent vp){
        try {
            final Field recyclerViewField = clazz.getDeclaredField("mRecyclerView");
            recyclerViewField.setAccessible(true);

            final RecyclerView recyclerView = (RecyclerView) recyclerViewField.get(vp);//vb.viewpagerHome为要改变滑动距离的viewpager2控件

            final Field touchSlopField = RecyclerView.class.getDeclaredField("mTouchSlop");
            touchSlopField.setAccessible(true);

            final int touchSlop = (int) touchSlopField.get(recyclerView);
            touchSlopField.set(recyclerView, touchSlop * 4);//通过获取原有的最小滑动距离 *n来增加此值
            //touchSlopField.set(recyclerView, 200);//自己写一个值
        } catch (Exception ignore) {
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
}
