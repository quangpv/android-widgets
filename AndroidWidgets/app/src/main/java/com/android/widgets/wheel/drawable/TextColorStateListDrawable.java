package com.android.widgets.wheel.drawable;

import android.graphics.Canvas;
import android.graphics.Region;
import android.support.annotation.NonNull;

import com.android.widgets.wheel.WheelView;

public class TextColorStateListDrawable extends TextWheelDrawable {
    private WheelDrawable mCurrentItemDrawable;
    private int mDefaultColor;
    private int mSelectedTextColor = -1;

    public TextColorStateListDrawable(WheelView view, CurtainDrawable curtainDrawable) {
        super(view);
        mCurrentItemDrawable = curtainDrawable;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        paint.setColor(mDefaultColor);
        int drawnCenterY = getItemDrawCenterY();
        canvas.save();
        if (hasCurved()) curvedDecorator.apply(canvas);
        canvas.clipRect(mCurrentItemDrawable.getBounds(), Region.Op.DIFFERENCE);
        canvas.drawText(text, getDrawnCenterX(), drawnCenterY, paint);
        canvas.restore();

        paint.setColor(mSelectedTextColor);
        canvas.save();
        if (hasCurved()) curvedDecorator.apply(canvas);
        canvas.clipRect(mCurrentItemDrawable.getBounds());
        canvas.drawText(text, getDrawnCenterX(), drawnCenterY, paint);
        canvas.restore();
    }

    @Override
    public void setColor(int color) {
        super.setColor(color);
        mDefaultColor = color;
    }

    public void setSelectedColor(int color) {
        mSelectedTextColor = color;
    }
}
