package com.xunlei.tdlive.smartrefresh;

import android.content.Context;
import android.util.AttributeSet;

public class XLSmartRefreshLayout extends SmartRefreshLayoutWrapper {
    public XLSmartRefreshLayout(Context context) {
        super(context);
        init(context);
    }

    public XLSmartRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        if (isInEditMode()) return;

        setEnableRefresh(true);
        setEnableLoadMore(true);
        setEnableAutoLoadMore(true);
        setEnableFooterFollowWhenLoadFinished(true);
        setEnableLoadMoreWhenContentNotFull(false);

    }

    @Override
    public boolean autoRefresh() {
        // 自动刷新时不需要回弹效果：先去掉，然后再设置回去，防止影响手动下拉的效果
        setHeaderMaxDragRate(1f);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                setHeaderMaxDragRate(2.5f);
            }
        }, 1000);

        return super.autoRefresh(400);
    }
}
