package com.android.widgets.wheel.decorator;

import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;

import com.kantek.eechat.widgets.wheel.drawable.TextWheelDrawable;

public class CurvedDecorator extends WheelDecorator {
    private TextWheelDrawable textDrawable;
    private Camera mCamera;
    private Matrix mMatrixRotate, mMatrixDepth;
    private int mDistanceToCenter = 0;

    public CurvedDecorator() {
        mCamera = new Camera();
        mMatrixRotate = new Matrix();
        mMatrixDepth = new Matrix();
    }

    private int computeSpace(int degree) {
        int haftHeight = textDrawable.parentViewPort.height() / 2;
        return (int) (Math.sin(Math.toRadians(degree)) * haftHeight);
    }

    private int computeDepth(int degree) {
        int haftHeight = textDrawable.parentViewPort.height() / 2;
        return (int) (haftHeight - Math.cos(Math.toRadians(degree)) * haftHeight);
    }

    @Override
    public void decorate(TextWheelDrawable textDrawable) {
        this.textDrawable = textDrawable;
        int drawnCenterY = textDrawable.getDrawnCenterY();
        int drawnItemCenterY = textDrawable.getOffsetDrawY();

        float ratio = (drawnCenterY - Math.abs(drawnCenterY - drawnItemCenterY) -
                textDrawable.parentViewPort.top) * 1.0F / (drawnCenterY - textDrawable.parentViewPort.top);

        int unit = 0;
        if (drawnItemCenterY > drawnCenterY)
            unit = 1;
        else if (drawnItemCenterY < drawnCenterY)
            unit = -1;

        float degree = (-(1 - ratio) * 90 * unit);
        if (degree < -90) degree = -90;
        if (degree > 90) degree = 90;
        mDistanceToCenter = computeSpace((int) degree);

        int transX = textDrawable.getTranslateX();
        int transY = textDrawable.parentViewPort.centerY() - mDistanceToCenter;

        mCamera.save();
        mCamera.rotateX(degree);
        mCamera.getMatrix(mMatrixRotate);
        mCamera.restore();
        mMatrixRotate.preTranslate(-transX, -transY);
        mMatrixRotate.postTranslate(transX, transY);

        int deep = computeDepth((int) degree);
        mCamera.save();
        mCamera.translate(0, 0, deep);
        mCamera.getMatrix(mMatrixDepth);
        mCamera.restore();
        mMatrixDepth.preTranslate(-transX, -transY);
        mMatrixDepth.postTranslate(transX, transY);

        mMatrixRotate.postConcat(mMatrixDepth);
    }

    public int getDistanceToCenter() {
        return mDistanceToCenter;
    }

    public void apply(Canvas canvas) {
        canvas.concat(mMatrixRotate);
    }
}
