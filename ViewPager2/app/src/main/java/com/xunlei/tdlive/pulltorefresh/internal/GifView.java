package com.xunlei.tdlive.pulltorefresh.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.AttributeSet;

public class GifView extends android.view.View {

    /**
     * 默认为1秒
     */
    private static final int DEFAULT_MOVIE_DURATION = 1000;

    private int mMovieResourceId;

    private Movie mMovie;

    private long mMovieStart;

    private int mCurrentAnimationTime = 0;

    private float mLeft;

    private float mTop;

    private float mScale;

    private int mMeasuredMovieWidth;

    private int mMeasuredMovieHeight;

    private boolean byUser ;

    private boolean mVisible = true;

    private volatile boolean mPaused = false;
    private ColorDrawable mColorDrawable;

    public GifView(Context context) {
        this(context, null);
    }

    public GifView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GifView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setViewAttributes(context, attrs, defStyle);
    }

    @SuppressLint("NewApi")
    private void setViewAttributes(Context context, AttributeSet attrs,
                                   int defStyle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(android.view.View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    /**
     * 设置gif图资源
     *
     * @param movieResId
     */
    public void setMovieResource(int movieResId) {
        this.mMovieResourceId = movieResId;
        mMovie = Movie.decodeStream(getResources().openRawResource(
                mMovieResourceId));
        requestLayout();
    }

    public void setMovie(Movie movie) {
        this.mMovie = movie;
        requestLayout();
    }

    public Movie getMovie() {
        return mMovie;
    }

    public void setMovieTime(int time) {
        mCurrentAnimationTime = time;
        invalidate();
    }

    public int getDuration(){
        if(mMovie!=null){
            return mMovie.duration();
        }
        return 0;
    }

    /**
     * 设置暂停
     *
     * @param paused
     */
    public void setPaused(boolean paused) {
        this.mPaused = paused;
        if (!paused) {
            mCurrentAnimationTime = 0;
            mMovieStart = android.os.SystemClock.uptimeMillis()
                    - mCurrentAnimationTime;
        }
        invalidate();
    }

    public void setByUser(boolean byUser) {
        this.byUser = byUser;
    }

    public boolean isByUser() {
        return byUser;
    }

    /**
     * 判断gif图是否停止了
     *
     * @return
     */
    public boolean isPaused() {
        return this.mPaused;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mMovie != null) {
            int movieWidth = mMovie.width();
            int movieHeight = mMovie.height();
            int maximumWidth = MeasureSpec.getSize(widthMeasureSpec);
            float scaleW = (float) movieWidth / (float) maximumWidth;
            mScale = 1f / scaleW;
            mMeasuredMovieWidth = maximumWidth;
            mMeasuredMovieHeight = (int) (movieHeight * mScale);
            setMeasuredDimension(mMeasuredMovieWidth, mMeasuredMovieHeight);
            if(mColorDrawable != null){
                mColorDrawable.setBounds(0, 0,  mMeasuredMovieWidth ,mMeasuredMovieHeight);
            }
        } else {
            setMeasuredDimension(getSuggestedMinimumWidth(),
                    getSuggestedMinimumHeight());
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mLeft = (getWidth() - mMeasuredMovieWidth) / 2f;
        mTop = (getHeight() - mMeasuredMovieHeight) / 2f;
        mVisible = getVisibility() == android.view.View.VISIBLE;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mMovie != null) {
            if (!mPaused) {
                if(!byUser){
                    updateAnimationTime();
                }
                drawMovieFrame(canvas);
                invalidateView();
            } else {
                drawMovieFrame(canvas);
            }
        }
    }

    @SuppressLint("NewApi")
    private void invalidateView() {
        if (mVisible) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                postInvalidateOnAnimation();
//            } else {
                invalidate();
 //           }
        }
    }

    private void updateAnimationTime() {
        long now = android.os.SystemClock.uptimeMillis();
        // 如果第一帧，记录起始时间
        if (mMovieStart == 0) {
            mMovieStart = now;
        }
        // 取出动画的时长
        int dur = mMovie.duration();
        if (dur == 0) {
            dur = DEFAULT_MOVIE_DURATION;
        }
        // 算出需要显示第几帧
        mCurrentAnimationTime = (int) ((now - mMovieStart) % dur);
    }

    private void drawMovieFrame(Canvas canvas) {
        if(mColorDrawable != null){
            mColorDrawable.draw(canvas);
        }
        // 设置要显示的帧，绘制即可
        mMovie.setTime(mCurrentAnimationTime);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG |
                Paint.FILTER_BITMAP_FLAG));
        canvas.save();
        canvas.scale(mScale, mScale);
        mMovie.draw(canvas, mLeft / mScale, mTop / mScale);
        canvas.restore();
    }

    @SuppressLint("NewApi")
    @Override
    public void onScreenStateChanged(int screenState) {
        super.onScreenStateChanged(screenState);
        mVisible = screenState == SCREEN_STATE_ON;
        invalidateView();
    }

    @SuppressLint("NewApi")
    @Override
    protected void onVisibilityChanged(android.view.View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        mVisible = visibility == android.view.View.VISIBLE;
        invalidateView();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        mVisible = visibility == android.view.View.VISIBLE;
        invalidateView();
    }

    public void setBgColor(int res){
        int color = getContext().getResources().getColor(res);
        mColorDrawable = new ColorDrawable(color);
        mColorDrawable.setBounds(0, 0,  mMeasuredMovieWidth ,mMeasuredMovieHeight);
    }

}