package com.android.widgets.wheel;

import android.os.Build;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.Scroller;

public class WheelScroller extends Scroller implements Runnable {
    private final WheelView mView;
    private int mMinimumVelocity = 50, mMaximumVelocity = 8000;
    private int mScrollOffsetY;
    private VelocityTracker mTracker;
    private boolean isForceFinishScroll;
    private int mItemHeight;
    private final Handler mHandler = new Handler();
    private boolean isCyclic;
    private int mMinFlingY, mMaxFlingY;

    private Runnable onScrollFinishListener;
    private Runnable onScrollingListener;

    public void setOnScrollFinishListener(Runnable onScrollFinishListener) {
        this.onScrollFinishListener = onScrollFinishListener;
    }

    public void setOnScrollingListener(Runnable onScrollingListener) {
        this.onScrollingListener = onScrollingListener;
    }

    public WheelScroller(WheelView wheelView) {
        super(wheelView.getContext());
        mView = wheelView;
    }

    public void startTracking(MotionEvent event) {
        if (null == mTracker)
            mTracker = VelocityTracker.obtain();
        else
            mTracker.clear();
        mTracker.addMovement(event);
    }

    public void tracking(MotionEvent event) {
        mTracker.addMovement(event);
    }

    public void finishIfNeeded() {
        if (!isFinished()) {
            abortAnimation();
            isForceFinishScroll = true;
        }
    }

    public boolean isForceFinish() {
        return isForceFinishScroll;
    }

    public void scroll() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT)
            mTracker.computeCurrentVelocity(1000, mMaximumVelocity);
        else mTracker.computeCurrentVelocity(1000);

        isForceFinishScroll = false;
        int velocity = (int) mTracker.getYVelocity();
        if (Math.abs(velocity) > mMinimumVelocity) {
            fling(0, mScrollOffsetY, 0, velocity, 0, 0, mMinFlingY, mMaxFlingY);
            setFinalY(getFinalY() + computeDistanceToEndPoint(getFinalY() % mItemHeight));
        } else {
            startScroll(0, mScrollOffsetY, 0, computeDistanceToEndPoint(mScrollOffsetY % mItemHeight));
        }
        if (!isCyclic) {
            if (getFinalY() > mMaxFlingY)
                setFinalY(mMaxFlingY);
            else if (getFinalY() < mMinFlingY)
                setFinalY(mMinFlingY);
        }
        mHandler.post(this);
    }

    void setMinVelocity(int minimumFlingVelocity) {
        this.mMinimumVelocity = minimumFlingVelocity;
    }

    void setMaxVelocity(int maximumFlingVelocity) {
        this.mMaximumVelocity = maximumFlingVelocity;
    }

    private int computeDistanceToEndPoint(int remainder) {
        if (Math.abs(remainder) > mItemHeight / 2)
            if (mScrollOffsetY < 0)
                return -mItemHeight - remainder;
            else return mItemHeight - remainder;
        else return -remainder;
    }

    void setItemHeight(int itemHeight) {
        mItemHeight = itemHeight;
    }

    @Override
    public void run() {
        if (isFinished() && !isForceFinishScroll) {
            if (onScrollFinishListener != null) onScrollFinishListener.run();
        }
        if (computeScrollOffset()) {
            if (onScrollingListener != null) onScrollingListener.run();
            mScrollOffsetY = getCurrY();
            mView.postInvalidate();
            mHandler.postDelayed(this, 16);
        }
    }

    void setOffsetY(int i) {
        mScrollOffsetY = i;
    }

    int getOffsetY() {
        return mScrollOffsetY;
    }

    void stopTracking() {
        if (null != mTracker) {
            mTracker.recycle();
            mTracker = null;
        }
    }

    void addMove(float move) {
        mScrollOffsetY += move;
    }

    boolean isCyclic() {
        return isCyclic;
    }

    void scrollBy(int itemDifference) {
        startScroll(0, getCurrY(), 0, (-itemDifference) * mItemHeight);
        mHandler.post(this);
    }

    void abortAnimationIfNeeded() {
        if (!isFinished()) abortAnimation();
    }

    void computeFlingLimitY(int currentItemOffset) {
        mMinFlingY = isCyclic ? Integer.MIN_VALUE : -mItemHeight * (mView.getData().size() - 1) + currentItemOffset;
        mMaxFlingY = isCyclic ? Integer.MAX_VALUE : currentItemOffset;
    }

    void setCyclic(boolean isCyclic) {
        this.isCyclic = isCyclic;
    }
}
