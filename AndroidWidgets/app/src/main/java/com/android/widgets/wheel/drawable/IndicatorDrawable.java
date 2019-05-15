package com.android.widgets.wheel.drawable;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;

import com.android.widgets.R;
import com.android.widgets.wheel.WheelView;


public class IndicatorDrawable extends WheelDrawable {
    private Rect mHead = new Rect();
    private Rect mFoot = new Rect();
    private int mIndicatorSize;

    public IndicatorDrawable(WheelView view) {
        super(view);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(view.getResources().getColor(R.color.colorPrimary));
        mIndicatorSize = view.getResources().getDimensionPixelSize(R.dimen.size_5);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.drawRect(mHead, paint);
        canvas.drawRect(mFoot, paint);
    }

    public void setIndicatorSize(int indicatorSize) {
        mIndicatorSize = indicatorSize;
    }

    @Override
    public void computeBounds() {
        super.computeBounds();
        int halfIndicatorSize = mIndicatorSize / 2;
        int wheelCenterY = parentViewPort.centerY();
        int indicatorHeadCenterY = wheelCenterY + getHaftItemHeight();
        int indicatorFootCenterY = wheelCenterY - getHaftItemHeight();

        mHead.set(parentViewPort.left, indicatorHeadCenterY - halfIndicatorSize,
                parentViewPort.right, indicatorHeadCenterY + halfIndicatorSize);
        mFoot.set(parentViewPort.left, indicatorFootCenterY - halfIndicatorSize,
                parentViewPort.right, indicatorFootCenterY + halfIndicatorSize);
    }
}
