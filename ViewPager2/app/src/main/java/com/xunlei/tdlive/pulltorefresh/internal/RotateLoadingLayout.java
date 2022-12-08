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

import android.content.Context;
import android.content.res.TypedArray;

import androidx.viewpager2.integration.testapp.R;

import com.xunlei.tdlive.pulltorefresh.PullToRefreshBase;
import com.xunlei.tdlive.pulltorefresh.PullToRefreshBase.Mode;
import com.xunlei.tdlive.pulltorefresh.PullToRefreshBase.Orientation;

public class RotateLoadingLayout extends LoadingLayout {
    static final int ROTATION_ANIMATION_DURATION = 1200;

    //private final Animation mRotateAnimation;
    private final android.graphics.Matrix mHeaderImageMatrix;
    private float mRotationPivotX, mRotationPivotY;
    private final boolean mRotateDrawableWhilePulling;

    public RotateLoadingLayout(Context context, Mode mode, Orientation scrollDirection, TypedArray attrs) {
        super(context, mode, scrollDirection, attrs);

        mRotateDrawableWhilePulling = attrs.getBoolean(R.styleable.PullRefresh_prRotateDrawableWhilePulling, true);

        mHeaderImageMatrix = new android.graphics.Matrix();
        //mRefreshingGif.setVisibility(VISIBLE);

        //mRotateAnimation = new RotateAnimation(0, 720, Animation.RELATIVE_TO_SELF, 0.5f,
        //        Animation.RELATIVE_TO_SELF, 0.5f);
        //mRotateAnimation.setInterpolator(ANIMATION_INTERPOLATOR);
        //mRotateAnimation.setDuration(ROTATION_ANIMATION_DURATION);
        //mRotateAnimation.setRepeatCount(Animation.INFINITE);
        //mRotateAnimation.setRepeatMode(Animation.RESTART);
    }

    public void onLoadingDrawableSet(android.graphics.drawable.Drawable imageDrawable) {
        if (null != imageDrawable) {
            mRotationPivotX = Math.round(imageDrawable.getIntrinsicWidth() / 2f);
            mRotationPivotY = Math.round(imageDrawable.getIntrinsicHeight() / 2f);
        }
    }

    protected void onPullImpl(float scaleOfLayout) {
        float angle;
        if (mRotateDrawableWhilePulling) {
            angle = scaleOfLayout * 90f;
        } else {
            angle = Math.max(0f, Math.min(180f, scaleOfLayout * 360f - 180f));
        }

        mHeaderImageMatrix.setRotate(angle, mRotationPivotX, mRotationPivotY);
    }

    @Override
    protected void refreshingImpl() { //刷新中
        //mRefreshingGif.setVisibility(VISIBLE);
        //mAnimationDrawable.start();
        //mPullingStatic.setVisibility(GONE);
        //mPullingGif.setVisibility(GONE);

        mPullingView.playAnimation();
    }

    @Override
    protected void resetImpl() {
        //mRefreshingGif.setVisibility(GONE);
        //mAnimationDrawable.stop();

        //mPullingGif.setVisibility(VISIBLE);
        //mHeaderText.setVisibility(VISIBLE);
        mPullingView.cancelAnimation();
    }

    @Override
    protected void pullToRefreshImpl() {
        // NO-OP
    }

    @Override
    protected void releaseToRefreshImpl() {
        // NO-OP
    }

    @Override
    protected int getDefaultDrawableResId() {
        return R.drawable.xllive_default_ptr_rotate;
    }

    @Override
    public void onHeaderScroll(int scrollValue, PullToRefreshBase.State currentState, Mode currentMode) {
        int abs = Math.abs(scrollValue);
        int headerSize = getContentSize();
        mHeaderText.setVisibility(VISIBLE);
        if (mMode == Mode.PULL_FROM_START) { //当前LoadingLayout表示的是 Header
            if (abs >= headerSize) {
                int diff = (abs - headerSize) / 2;
                scrollTo(0, diff);
            } else {
                //mRefreshingGif.setVisibility(GONE);

                //if (mPullingGif.getVisibility() == GONE && currentState == PullToRefreshBase.State.PULL_TO_REFRESH) {
                //    mPullingGif.setVisibility(VISIBLE);
                //}

                //int currentTime = (int) ((abs * 1.0 / headerSize) * mPullingGif.getDuration());
                //mPullingGif.setMovieTime(currentTime);

                int scrollY = getScrollY();
                if (scrollY != 0) {
                    scrollTo(0, 0);
                }
            }

            if (currentState == PullToRefreshBase.State.REFRESHING ||
                    currentState == PullToRefreshBase.State.MANUAL_REFRESHING) {
                //mRefreshingGif.setVisibility(VISIBLE);
                //mPullingStatic.setVisibility(GONE);
            }
        }

        if (currentMode == Mode.PULL_FROM_END) {
            if (currentState == PullToRefreshBase.State.PULL_TO_REFRESH) {
                //mRefreshingGif.setVisibility(GONE);
                //mPullingStatic.setVisibility(VISIBLE);
            }
        }
    }

}
