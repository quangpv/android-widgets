package com.android.widgets.wheel.drawable;

import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import com.android.widgets.wheel.WheelView;

public abstract class WheelDrawable extends Drawable {
    protected final Paint paint;
    protected final WheelView view;
    public Rect parentViewPort = new Rect();
    private int mItemHeight;
    private int mHaftItemHeight;

    public WheelDrawable(WheelView view) {
        paint = new Paint();
        this.view = view;
    }

    public void setColor(int color) {
        paint.setColor(color);
    }

    @Override
    public void setAlpha(int i) {
        paint.setAlpha(i);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void invalidateSelf() {
        this.view.invalidate();
    }

    public int getColor() {
        return paint.getColor();
    }

    public int getItemHeight() {
        return mItemHeight;
    }

    public int getHaftItemHeight() {
        return mHaftItemHeight;
    }

    public void computeBounds() {
        parentViewPort.set(view.getPaddingLeft(),
                view.getPaddingTop(),
                view.getMeasuredWidth() - view.getPaddingRight(),
                view.getMeasuredHeight() - view.getPaddingBottom());
        mItemHeight = parentViewPort.height() / view.getVisibleItemCount();
        mHaftItemHeight = mItemHeight / 2;
    }
}
