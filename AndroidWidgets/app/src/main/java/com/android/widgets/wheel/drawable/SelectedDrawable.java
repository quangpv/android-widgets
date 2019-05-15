package com.android.widgets.wheel.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;


public class SelectedDrawable extends Drawable {
    private final Paint mPaint;
    private int mIndicatorHeight;
    private int mHeight;
    private Rect mHead = new Rect();
    private Rect mFoot = new Rect();

    public SelectedDrawable() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.drawRect(mHead, mPaint);
        canvas.drawRect(mFoot, mPaint);
    }

    @Override
    public void setAlpha(int i) {
        mPaint.setAlpha(i);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public int getIntrinsicHeight() {
        return mHeight;
    }

    public void measure(int measuredWidth, int measuredHeight) {
        mHeight = measuredHeight;
    }

    public void layout(int l, int t, int r, int b) {
        mHead.set(l, t, r, t + mIndicatorHeight);
        mFoot.set(l, b - mIndicatorHeight, r, b);
    }

    public void setColor(int color) {
        mPaint.setColor(color);
    }

    public void setIndicatorHeight(int height) {
        mIndicatorHeight = height;
    }
}
