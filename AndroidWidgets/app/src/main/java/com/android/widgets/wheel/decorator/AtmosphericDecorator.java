package com.android.widgets.wheel.decorator;

import com.kantek.eechat.widgets.wheel.drawable.TextWheelDrawable;

public class AtmosphericDecorator extends WheelDecorator {

    @Override
    public void decorate(TextWheelDrawable textDrawable) {
        int mDrawnCenterY = textDrawable.getDrawnCenterY();
        int alpha = (int) ((mDrawnCenterY - Math.abs(mDrawnCenterY - textDrawable.getOffsetDrawY())) *
                1.0F / mDrawnCenterY * 255);
        alpha = alpha < 0 ? 0 : alpha;
        textDrawable.setAlpha(alpha);
    }
}
