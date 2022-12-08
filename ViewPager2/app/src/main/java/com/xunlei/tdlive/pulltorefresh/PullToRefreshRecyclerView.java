package com.xunlei.tdlive.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.integration.testapp.R;


public class PullToRefreshRecyclerView extends PullToRefreshBase<RecyclerView> {

    RecyclerView recyclerView;

    private OnLastItemVisibleListener mOnLastItemVisibleListener;

    public PullToRefreshRecyclerView(Context context) {
        super(context);
    }

    public PullToRefreshRecyclerView(Context context, Mode mode) {
        super(context, mode);
    }

    public PullToRefreshRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public final Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

    public final void setOnLastItemVisibleListener(OnLastItemVisibleListener listener) {
        mOnLastItemVisibleListener = listener;
    }

    @Override
    protected RecyclerView createRefreshableView(Context context, AttributeSet attrs) {
        // 直接new出的对象会存在没有滚动条的问题，所以改用布局文件来
        android.view.View view  = LayoutInflater.from(context).inflate(R.layout.xllive_view_vetical_scrollbar_recyclerview, null);;
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                check(newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                check(recyclerView.getScrollState());
            }

            private void check(int newState) {
                if ((newState == RecyclerView.SCROLL_STATE_IDLE
                        || newState == RecyclerView.SCROLL_STATE_DRAGGING)
                        && mOnLastItemVisibleListener != null
                        && isReadyForPullEnd()) {
                    mOnLastItemVisibleListener.onLastItemVisible();
                }
            }
        });
        return recyclerView;
    }

    @Override
    protected boolean isReadyForPullStart() {
        if (mRefreshableView.getChildCount() <= 0) {
            return true;
        }

        try {
            return mRefreshableView.getChildAt(0).getTop() == mRefreshableView.getPaddingTop();
        } catch (Throwable e) {
            // e
        }

        return false;
    }

    @Override
    protected boolean isReadyForPullEnd() {
        try {
            int lastVisiblePosition = mRefreshableView.getChildAdapterPosition(
                    mRefreshableView.getChildAt(mRefreshableView.getChildCount() - 1));
            if (lastVisiblePosition >= mRefreshableView.getAdapter().getItemCount() - 1) {
                return mRefreshableView.getChildAt(mRefreshableView.getChildCount() - 1).getTop()
                        <= mRefreshableView.getBottom();
            }
        } catch (Throwable e) {
            // e
        }

        return false;
    }
}
