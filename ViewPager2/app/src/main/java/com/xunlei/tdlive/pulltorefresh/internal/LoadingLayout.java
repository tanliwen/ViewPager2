/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.xunlei.tdlive.pulltorefresh.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.viewpager2.integration.testapp.R;

import com.airbnb.lottie.LottieAnimationView;
import com.xunlei.tdlive.pulltorefresh.ILoadingLayout;
import com.xunlei.tdlive.pulltorefresh.PullToRefreshBase;
import com.xunlei.tdlive.pulltorefresh.PullToRefreshBase.Mode;
import com.xunlei.tdlive.pulltorefresh.PullToRefreshBase.Orientation;

@SuppressLint("ViewConstructor")
public abstract class LoadingLayout extends FrameLayout implements ILoadingLayout {

    static final android.view.animation.Interpolator ANIMATION_INTERPOLATOR = new LinearInterpolator();

    protected android.view.View mInnerLayout;

    protected final ImageView mPullingStatic;
    protected final ImageView mRefreshingGif;
    protected final AnimationDrawable mAnimationDrawable;
    protected final GifView mPullingGif;
    protected final LottieAnimationView mPullingView;

    private boolean mUseIntrinsicAnimation;

    protected final android.widget.TextView mHeaderText;

    protected final Mode mMode;
    protected final Orientation mScrollDirection;

    private CharSequence mPullLabel = "下拉刷新";
    private CharSequence mRefreshingLabel = "正在刷新...";
    private CharSequence mReleaseLabel;

    public LoadingLayout(Context context, final Mode mode, final Orientation scrollDirection, TypedArray attrs) {
        super(context);
        mMode = mode;
        mScrollDirection = scrollDirection;

        switch (scrollDirection) {
            case HORIZONTAL:
                LayoutInflater.from(context).inflate(R.layout.xllive_ptr_header_horizontal, this);
                break;
            case VERTICAL:
            default:
                LayoutInflater.from(context).inflate(R.layout.xllive_ptr_header_vertical, this);
                break;
        }

        mInnerLayout = findViewById(R.id.fl_inner);
        mHeaderText = mInnerLayout.findViewById(R.id.pull_to_refresh_text);

        mPullingStatic = mInnerLayout.findViewById(R.id.pull_to_refresh_pulling_static);
        mPullingView = mInnerLayout.findViewById(R.id.pull_to_refresh_refreshing2);

        mPullingGif = mInnerLayout.findViewById(R.id.pull_to_refresh_pulling);
        //mPullingGif.setByUser(true);
        //mPullingGif.setMovieResource(R.raw.xllive_pulling);
        //mPullingGif.setPaused(true);

        mRefreshingGif = mInnerLayout.findViewById(R.id.pull_to_refresh_refreshing);
        //if ("GiONEE".equals(Build.BRAND) && "E7".equals(Build.MODEL)) {
            //mRefreshingGif.setImageResource(R.drawable.xllive_loading_animate2);
        //}

        mAnimationDrawable = (AnimationDrawable) mRefreshingGif.getDrawable();
        LayoutParams lp = (LayoutParams) mInnerLayout.getLayoutParams();

        switch (mode) {
            case PULL_FROM_END:
                lp.gravity = scrollDirection == Orientation.VERTICAL ? Gravity.TOP : Gravity.LEFT;
                initFromEnd();
                break;

            case PULL_FROM_START:
            default:
                lp.gravity = scrollDirection == Orientation.VERTICAL ? Gravity.BOTTOM : Gravity.RIGHT;
                initFromStart();
                break;
        }

        if (attrs.hasValue(R.styleable.PullRefresh_prHeaderBackground)) {
            android.graphics.drawable.Drawable background = attrs.getDrawable(R.styleable.PullRefresh_prHeaderBackground);
            if (null != background) {
                ViewCompat.setBackground(this, background);
            }
        }

        if (attrs.hasValue(R.styleable.PullRefresh_prHeaderTextAppearance)) {
            TypedValue styleID = new TypedValue();
            attrs.getValue(R.styleable.PullRefresh_prHeaderTextAppearance, styleID);
            setTextAppearance(styleID.data);
        }

        // Text Color attrs need to be set after TextAppearance attrs
        if (attrs.hasValue(R.styleable.PullRefresh_prHeaderTextColor)) {
            ColorStateList colors = attrs.getColorStateList(R.styleable.PullRefresh_prHeaderTextColor);
            if (null != colors) {
                setTextColor(colors);
            }
        }

        // Try and get defined drawable from Attrs
        android.graphics.drawable.Drawable imageDrawable = null;
        if (attrs.hasValue(R.styleable.PullRefresh_prDrawable)) {
            imageDrawable = attrs.getDrawable(R.styleable.PullRefresh_prDrawable);
        }

        // Check Specific Drawable from Attrs, these overrite the generic
        // drawable attr above
        switch (mode) {
            case PULL_FROM_START:
            default:
                if (attrs.hasValue(R.styleable.PullRefresh_prDrawableStart)) {
                    imageDrawable = attrs.getDrawable(R.styleable.PullRefresh_prDrawableStart);
                } else if (attrs.hasValue(R.styleable.PullRefresh_prDrawableTop)) {
                    imageDrawable = attrs.getDrawable(R.styleable.PullRefresh_prDrawableTop);
                }
                break;

            case PULL_FROM_END:
                if (attrs.hasValue(R.styleable.PullRefresh_prDrawableEnd)) {
                    imageDrawable = attrs.getDrawable(R.styleable.PullRefresh_prDrawableEnd);
                } else if (attrs.hasValue(R.styleable.PullRefresh_prDrawableBottom)) {
                    imageDrawable = attrs.getDrawable(R.styleable.PullRefresh_prDrawableBottom);
                }
                break;
        }
        // If we don't have a user defined drawable, load the default
        if (null == imageDrawable) {
            imageDrawable = context.getResources().getDrawable(getDefaultDrawableResId());
        }
        // Set Drawable, and save width/height
        setLoadingDrawable(imageDrawable);
        reset();
    }

    public void hideLoadingInfoView() {
        mPullingGif.setVisibility(GONE);
        mRefreshingGif.setVisibility(GONE);
        mHeaderText.setVisibility(GONE);
    }

    private void initFromStart() {
        setClipChildren(false);
        mPullLabel = "下拉刷新";
        mRefreshingLabel = "正在刷新...";
        mReleaseLabel = "松开立即刷新";
    }

    private void initFromEnd() {
        mPullLabel = "上拉加载";
        mRefreshingLabel = "正在加载";
        mReleaseLabel = "松开立即加载";
        mInnerLayout.setPadding(mInnerLayout.getPaddingLeft(), mInnerLayout.getPaddingBottom(), mInnerLayout.getPaddingRight(), mInnerLayout.getPaddingTop());
    }


    public final void setHeight(int height) {
        ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) getLayoutParams();
        lp.height = height;
        requestLayout();
    }

    public final void setWidth(int width) {
        ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) getLayoutParams();
        lp.width = width;
        requestLayout();
    }

    public final void setPaddingTop(int top) {
        int left = mInnerLayout.getPaddingLeft();
        int right = mInnerLayout.getPaddingRight();
        int bottom = mInnerLayout.getPaddingBottom();
        mInnerLayout.setPadding(left, top, right, bottom);
    }


    public final int getContentSize() {
        switch (mScrollDirection) {
            case HORIZONTAL:
                return mInnerLayout.getWidth();
            case VERTICAL:
            default:
                return mInnerLayout.getHeight();
        }
    }


    public final int getInnerTop() {
        return mInnerLayout.getTop();
    }

    public final void hideAllViews() {

    }

    public final void onPull(float scaleOfLayout) {
        if (!mUseIntrinsicAnimation) {
            onPullImpl(scaleOfLayout);
        }
    }

    public final void pullToRefresh() {
        if (null != mHeaderText) {
            mHeaderText.setText(mPullLabel);
        }
        invalidate();
        // Now call the callback
        pullToRefreshImpl();
    }

    public final void refreshing() {
        if (null != mHeaderText) {
            mHeaderText.setText(mRefreshingLabel);
        }

        if (mUseIntrinsicAnimation) {
            //((AnimationDrawable) mHeaderImage.getDrawable()).start();
        } else {
            // Now call the callback
            refreshingImpl();
        }
    }

    public final void releaseToRefresh() {
        releaseToRefreshImpl();
    }

    public final void reset() {
        if (mUseIntrinsicAnimation) {
        } else {
            resetImpl();
        }
    }

    @Override
    public void setLastUpdatedLabel(CharSequence label) {

    }

    public final void setLoadingDrawable(android.graphics.drawable.Drawable imageDrawable) {
        // Set Drawable
        mUseIntrinsicAnimation = (imageDrawable instanceof AnimationDrawable);

        // Now call the callback
        onLoadingDrawableSet(imageDrawable);
    }

    public void setPullLabel(CharSequence pullLabel) {
        mPullLabel = pullLabel;
    }

    public void setRefreshingLabel(CharSequence refreshingLabel) {
        mRefreshingLabel = refreshingLabel;
    }

    public void setReleaseLabel(CharSequence releaseLabel) {
        mReleaseLabel = releaseLabel;
    }

    @Override
    public void setTextTypeface(Typeface tf) {
        mHeaderText.setTypeface(tf);
    }

    public final void showInvisibleViews() {

    }

    /**
     * Callbacks for derivative Layouts
     */

    protected abstract int getDefaultDrawableResId();

    protected abstract void onLoadingDrawableSet(android.graphics.drawable.Drawable imageDrawable);

    protected abstract void onPullImpl(float scaleOfLayout);

    protected abstract void pullToRefreshImpl();

    protected abstract void refreshingImpl();

    protected abstract void releaseToRefreshImpl();

    protected abstract void resetImpl();

    public abstract void onHeaderScroll(int scrollValue, PullToRefreshBase.State currentState, Mode currentMode);


    private void setTextAppearance(int value) {
        if (null != mHeaderText) {
            mHeaderText.setTextAppearance(getContext(), value);
        }
    }

    private void setTextColor(ColorStateList color) {

    }

    public void setBgColorForRefreshingView(int res) {
        mPullingStatic.setBackgroundResource(res);
        //mRefreshingGif.setBgColor(res);
    }

}
