package com.xunlei.tdlive.smartrefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class XRecyclerView extends SmartRefreshLayoutWrapper {

    private final RecyclerView recyclerView;

    public XRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public XRecyclerView(@NonNull Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XRecyclerView(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        addView(recyclerView = createRecyclerView(context, attrs, defStyleAttr), -1, -1);
    }

    private RecyclerView getRecyclerView() {
        return recyclerView;
    }

    private static RecyclerView createRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        return new RecyclerView(context, attrs, defStyleAttr) {
            private int startX, startY;
            private int mTouchSlop;

            @Override
            public boolean dispatchTouchEvent(MotionEvent ev) {
                if (mTouchSlop == 0) {
                    ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
                    mTouchSlop = viewConfiguration.getScaledTouchSlop();
                }

                switch (ev.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) ev.getX();
                        startY = (int) ev.getY();
                        getParent().requestDisallowInterceptTouchEvent(true);
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
                        } else {
                            getParent().requestDisallowInterceptTouchEvent(canScrollHorizontally(startX - endX));
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                return super.dispatchTouchEvent(ev);
            }
        };
    }
}
