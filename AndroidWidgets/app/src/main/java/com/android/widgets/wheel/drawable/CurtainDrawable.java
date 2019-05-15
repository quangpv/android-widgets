package com.android.widgets.wheel.drawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;

import com.android.widgets.wheel.WheelView;

public class CurtainDrawable extends WheelDrawable {
    private boolean mHasCurtain;
    private int mPosition = 0;

    public CurtainDrawable(WheelView view) {
        super(view);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (mHasCurtain) canvas.drawRect(getBounds(), paint);
    }

    public void setCurtain(boolean curtain) {
        mHasCurtain = curtain;
    }

    @Override
    public void computeBounds() {
        super.computeBounds();
        if (!mHasCurtain || paint.getColor() == -1) return;
        int halfItemHeight = getHaftItemHeight();
        int centerY = parentViewPort.centerY();
        setBounds(parentViewPort.left,
                centerY - halfItemHeight,
                parentViewPort.right,
                centerY + halfItemHeight);
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setCurtainColor(int color) {
    }
}
