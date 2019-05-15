package com.android.widgets.wheel.drawable;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.NonNull;

import com.android.widgets.wheel.WheelView;
import com.android.widgets.wheel.decorator.CurvedDecorator;
import com.android.widgets.wheel.decorator.WheelDecorator;

import java.util.ArrayList;
import java.util.List;

public class TextWheelDrawable extends WheelDrawable {
    public static final int ALIGN_CENTER = 0, ALIGN_LEFT = 1, ALIGN_RIGHT = 2;

    protected String text;
    private int mAlign;
    protected CurvedDecorator curvedDecorator;
    private List<WheelDecorator> mDecorators = new ArrayList<>();
    private int mScrollOffsetY = 0;
    private int mDrawOffsetPos = 0;
    private int mDrawnCenterY = 0;
    private int mDrawnCenterX = 0;

    public TextWheelDrawable(WheelView view) {
        super(view);
        paint.setStyle(Paint.Style.FILL);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.save();
        canvas.clipRect(parentViewPort);
        if (hasCurved()) curvedDecorator.apply(canvas);
        canvas.drawText(text, mDrawnCenterX, getItemDrawCenterY(), paint);
        canvas.restore();
    }

    int getItemDrawCenterY() {
        return hasCurved() ? getDrawnCenterY() - curvedDecorator.getDistanceToCenter() : getOffsetDrawY();
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public boolean hasCurved() {
        return curvedDecorator != null;
    }

    public void seek(int drawnOffsetPos, int scrollOffsetY) {
        mDrawOffsetPos = drawnOffsetPos;
        mScrollOffsetY = scrollOffsetY;
        for (WheelDecorator decorator : mDecorators) {
            decorator.decorate(this);
        }
    }

    public void setAlignment(int align) {
        mAlign = align;
        switch (align) {
            case ALIGN_LEFT:
                paint.setTextAlign(Paint.Align.LEFT);
                break;
            case ALIGN_RIGHT:
                paint.setTextAlign(Paint.Align.RIGHT);
                break;
            default:
                paint.setTextAlign(Paint.Align.CENTER);
                break;
        }
    }

    @Override
    public void computeBounds() {
        super.computeBounds();
        switch (mAlign) {
            case ALIGN_LEFT:
                mDrawnCenterX = parentViewPort.left;
                break;
            case ALIGN_RIGHT:
                mDrawnCenterX = parentViewPort.right;
                break;
            default:
                mDrawnCenterX = parentViewPort.centerX();
                break;
        }
        mDrawnCenterY = (int) (parentViewPort.centerY() - ((paint.ascent() + paint.descent()) / 2));
    }

    public int getTranslateX() {
        switch (mAlign) {
            case ALIGN_LEFT:
                return parentViewPort.left;
            case ALIGN_RIGHT:
                return parentViewPort.right;
        }
        return parentViewPort.centerX();
    }

    public int getDrawnCenterY() {
        return mDrawnCenterY;
    }

    public int getDrawnCenterX() {
        return mDrawnCenterX;
    }

    public int getOffsetDrawY() {
        return mDrawnCenterY + (mDrawOffsetPos * getItemHeight()) + mScrollOffsetY % getItemHeight();
    }

    public void setTypeface(Typeface tf) {
        paint.setTypeface(tf);
    }

    public Typeface getTypeface() {
        return paint.getTypeface();
    }

    public void setTextSize(int size) {
        paint.setTextSize(size);
    }

    public void setFontPath(String string) {
        if (string == null) return;
        Typeface typeface = Typeface.createFromAsset(view.getContext().getAssets(), string);
        paint.setTypeface(typeface);
    }

    public void addDecorator(WheelDecorator wheelDecorator) {
        mDecorators.add(wheelDecorator);
        if (wheelDecorator instanceof CurvedDecorator)
            curvedDecorator = (CurvedDecorator) wheelDecorator;
    }

    public float measureText(String text) {
        return paint.measureText(text);
    }

    public float getMaxHeight() {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return fontMetrics.bottom - fontMetrics.top;
    }
}
